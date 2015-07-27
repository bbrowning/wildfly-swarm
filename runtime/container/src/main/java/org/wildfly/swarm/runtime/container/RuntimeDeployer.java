package org.wildfly.swarm.runtime.container;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.impl.base.exporter.zip.ZipExporterImpl;
import org.jboss.vfs.TempFileProvider;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;
import org.wildfly.swarm.container.Deployer;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.*;

/**
 * @author Bob McWhirter
 */
public class RuntimeDeployer implements Deployer {

    private final ModelControllerClient client;

    private final SimpleContentProvider contentProvider;

    //private final ScheduledExecutorService executor;

    private final TempFileProvider tempFileProvider;
    private final List<Closeable> mountPoints = new ArrayList<>();

    public RuntimeDeployer(ModelControllerClient client, SimpleContentProvider contentProvider, TempFileProvider tempFileProvider) throws IOException {
        this.client = client;
        this.contentProvider = contentProvider;
        this.tempFileProvider = tempFileProvider;
        //this.executor = Executors.newSingleThreadScheduledExecutor();
        //this.tempFileProvider = TempFileProvider.create("wildfly-swarm", this.executor);
    }

    @Override
    public void deploy(Archive deployment) throws IOException {

        /*
        Map<ArchivePath, Node> c = deployment.getContent();
        for (Map.Entry<ArchivePath, Node> each : c.entrySet()) {
            if ( each.getValue().getAsset() != null ) {
                System.err.println( each.getKey() + " // " + each.getValue() );
            }
        }
        */


        VirtualFile mountPoint = VFS.getRootVirtualFile().getChild(deployment.getName());
        try (InputStream in = new ZipExporterImpl(deployment).exportAsInputStream()) {
            Closeable closeable = VFS.mountZipExpanded(in, deployment.getName(), mountPoint, tempFileProvider);
            this.mountPoints.add( closeable );
        }

        List<VirtualFile> children = mountPoint.getChildrenRecursively();
        for ( VirtualFile each : children ) {
            //System.err.println( "-> " + each.getPhysicalFile() );
            //each.delete();
            File f = each.getPhysicalFile();
            if ( f.getName().equals( "config.properties" ) ) {
                System.err.println( f );
                /*
                WatchService watcher = f.toPath().getFileSystem().newWatchService();
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        while ( true ) {
                            try {
                                WatchKey k = watcher.take();
                                List<WatchEvent<?>> events = k.pollEvents();
                                for ( WatchEvent<?> each : events ) {
                                    System.err.println( "saw: " + each );
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                };

                t.start();
                */
            }
        }



        byte[] hash = this.contentProvider.addContent(mountPoint);

        final ModelNode deploymentAdd = new ModelNode();

        deploymentAdd.get(OP).set(ADD);
        deploymentAdd.get(OP_ADDR).set("deployment", deployment.getName());
        deploymentAdd.get(RUNTIME_NAME).set(deployment.getName());
        deploymentAdd.get(ENABLED).set(true);

        ModelNode content = deploymentAdd.get(CONTENT).add();
        content.get(HASH).set(hash);

        System.setProperty("wildfly.swarm.current.deployment", deployment.getName());
        ModelNode result = client.execute(deploymentAdd);
    }

    void stop() {
        for ( Closeable each : this.mountPoints ) {
            try {
                System.err.println( "closing: " + each );
                each.close();
            } catch (IOException e) {
            }
        }

    }

}
