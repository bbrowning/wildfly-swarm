/**
 * Copyright 2015-2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.swarm.container;

import org.wildfly.swarm.SwarmProperties;

public class Environment {

    public static boolean openshift() {
        return System.getenv("OPENSHIFT_BUILD_NAME") != null ||
                System.getenv("OPENSHIFT_BUILD_REFERENCE") != null ||
                "openshift".equalsIgnoreCase(System.getProperty(SwarmProperties.ENVIRONMENT));
    }

    public static String serviceHost(String serviceName) {
        String envName = serviceName.replace("-", "_").toUpperCase() + "_SERVICE_HOST";
        return System.getenv(envName);
    }

    public static int servicePort(String serviceName) {
        String envName = serviceName.replace("-", "_").toUpperCase() + "_SERVICE_PORT";
        String envPort = System.getenv(envName);
        if (envPort == null) {
            return -1;
        }

        return Integer.parseInt(envPort);
    }
}
