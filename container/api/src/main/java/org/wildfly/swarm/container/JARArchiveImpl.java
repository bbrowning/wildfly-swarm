package org.wildfly.swarm.container;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.impl.base.container.ContainerBase;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;

/**
 * @author Bob McWhirter
 */
public class JARArchiveImpl extends ContainerBase<JARArchive> implements JARArchive {

    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Path to the manifests inside of the Archive.
     */
    private static final ArchivePath PATH_MANIFEST = new BasicPath("META-INF");

    /**
     * Path to the resources inside of the Archive.
     */
    private static final ArchivePath PATH_RESOURCE = new BasicPath("/");

    /**
     * Path to the classes inside of the Archive.
     */
    private static final ArchivePath PATH_CLASSES = new BasicPath("/");

    // -------------------------------------------------------------------------------------||
    // Instance Members -------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Create a new JAXRS Archive with any type storage engine as backing.
     *
     * @param delegate The storage backing.
     */
    public JARArchiveImpl(Archive<?> delegate) {
        super(JARArchive.class, delegate);
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations -----------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * {@inheritDoc}
     *
     * @see ContainerBase#getManifestPath()
     */
    @Override
    protected ArchivePath getManifestPath() {
        return PATH_MANIFEST;
    }

    /**
     * {@inheritDoc}
     *
     * @see ContainerBase#getClassesPath()
     */
    @Override
    protected ArchivePath getClassesPath() {
        return PATH_CLASSES;
    }

    /**
     * {@inheritDoc}
     *
     * @see ContainerBase#getResourcePath()
     */
    @Override
    protected ArchivePath getResourcePath() {
        return PATH_RESOURCE;
    }

    /**
     * Libraries are not supported by JavaArchive.
     *
     * @throws UnsupportedOperationException
     *             Libraries are not supported by JavaArchive
     */
    @Override
    public ArchivePath getLibraryPath() {
        throw new UnsupportedOperationException("JavaArchive spec does not support Libraries");
    }

}
