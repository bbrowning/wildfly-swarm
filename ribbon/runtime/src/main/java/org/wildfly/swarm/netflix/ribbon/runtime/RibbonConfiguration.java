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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceActivator;
import org.jboss.shrinkwrap.api.Archive;
import org.wildfly.swarm.container.JARArchive;
import org.wildfly.swarm.container.runtime.AbstractServerConfiguration;
import org.wildfly.swarm.netflix.ribbon.RibbonFraction;

/**
 * @author Bob McWhirter
 */
public class RibbonConfiguration extends AbstractServerConfiguration<RibbonFraction> {

    public RibbonConfiguration() {
        super(RibbonFraction.class);
    }

    @Override
    public RibbonFraction defaultFraction() {
        return new RibbonFraction();
    }

    @Override
    public List<ModelNode> getList(RibbonFraction fraction) {
        return Collections.emptyList();
    }

    @Override
    public List<ServiceActivator> getServiceActivators(RibbonFraction fraction) {
        List<ServiceActivator> activators = new ArrayList<>();
        activators.add( new ClusterManagerActivator() );
        return activators;
    }

    @Override
    public void prepareArchive(Archive archive) {
        archive.as(JARArchive.class).addModule("org.wildfly.swarm.netflix.ribbon");
        archive.as(JARArchive.class).addModule("org.wildfly.swarm.netflix.ribbon", "runtime");
        archive.as(JARArchive.class).addModule("com.netflix.ribbon" );
        archive.as(JARArchive.class).addModule("io.reactivex.rxjava" );
        archive.as(JARArchive.class).addModule("io.netty" );
    }
}
