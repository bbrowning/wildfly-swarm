package org.wildfly.swarm.logstash.runtime;

import java.util.Collections;
import java.util.List;

import org.jboss.dmr.ModelNode;
import org.wildfly.swarm.container.runtime.AbstractServerConfiguration;
import org.wildfly.swarm.logstash.LogstashFraction;

/**
 * @author Bob McWhirter
 */
public class LogstashConfiguration extends AbstractServerConfiguration<LogstashFraction> {

    public LogstashConfiguration() {
        super(LogstashFraction.class);
    }

    @Override
    public boolean isIgnorable() {
        return true;
    }

    @Override
    public LogstashFraction defaultFraction() {
        String hostname = System.getProperty( "swarm.logstash.hostname" );
        String port = System.getProperty("swarm.logstash.port");

        if ( hostname != null && port != null ) {
            return new LogstashFraction()
                    .hostname(hostname)
                    .port(port);
        }

        return null;
    }

    @Override
    public List<ModelNode> getList(LogstashFraction fraction) {
        return Collections.emptyList();
    }

}
