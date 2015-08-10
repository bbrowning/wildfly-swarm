package org.wildfly.swarm.netflix.ribbon.runtime;

import org.jboss.msc.service.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Bob McWhirter
 */
public class ApplicationAdvertiserActivator implements ServiceActivator {
    @Override
    public void activate(ServiceActivatorContext context) throws ServiceRegistryException {

        ServiceTarget target = context.getServiceTarget();

        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/netflix-ribbon-application.conf");

        if (in == null) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

            String appName = null;

            while ((appName = reader.readLine()) != null) {
                appName = appName.trim();
                if (!appName.isEmpty()) {
                    ApplicationAdvertiser advertiser = new ApplicationAdvertiser(appName);

                    target.addService(ServiceName.of("netflix", "ribbon", "advertise", appName), advertiser)
                            .addDependency(ClusterManager.SERVICE_NAME, ClusterManager.class, advertiser.getClusterManagerInjector())
                            .install();
                }
            }

        } catch (IOException e) {
            throw new ServiceRegistryException(e);
        }


    }
}
