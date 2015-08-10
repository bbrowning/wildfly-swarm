package org.wildfly.swarm.netflix.ribbon.runtime;

import org.wildfly.clustering.dispatcher.Command;

/**
 * @author Bob McWhirter
 */
public class RequestAdvertisementsCommand implements Command<Void, ClusterManager> {

    public RequestAdvertisementsCommand() {
    }

    @Override
    public Void execute(ClusterManager context) throws Exception {
        context.advertiseAll();
        return null;
    }
}
