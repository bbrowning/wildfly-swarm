/**
 * Copyright 2015 Red Hat, Inc, and individual contributors.
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
package org.wildfly.swarm.netflix.ribbon.runtime;

import com.netflix.loadbalancer.Server;
import org.wildfly.clustering.dispatcher.Command;

/**
 * @author Bob McWhirter
 */
public class UnadvertiseCommand implements Command<Void,ClusterManager> {

    private final String appName;
    private final String nodeKey;

    public UnadvertiseCommand(String nodeKey, String appName) {
        this.nodeKey = nodeKey;
        this.appName = appName;
    }

    @Override
    public Void execute(ClusterManager context) throws Exception {
        context.unregister( this.nodeKey, this.appName );
        return null;
    }
}
