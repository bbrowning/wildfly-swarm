package org.wildfly.swarm.integration.base;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.wildfly.swarm.container.Container;
import org.wildfly.swarm.logging.LoggingFraction;

/**
 * @author Bob McWhirter
 */
public abstract class AbstractWildFlySwarmTestCase {

    protected Container newContainer() throws Exception {
        return newContainer(false);
    }

    protected Container newContainer(boolean trace) throws Exception {
        return new Container()
                .fraction((trace ?
                        LoggingFraction.createTraceLoggingFraction() :
                        LoggingFraction.createDefaultLoggingFraction()));
    }

    protected String fetch(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        StringBuffer buffer = new StringBuffer();
        try (InputStream in = url.openStream()) {
            int numRead = 0;
            while (numRead >= 0) {
                byte[] b = new byte[1024];
                numRead = in.read(b);
                if (numRead < 0) {
                    break;
                }
                buffer.append(new String(b, 0, numRead));
            }
        }

        return buffer.toString();
    }


}
