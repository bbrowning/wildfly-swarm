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
package org.wildfly.swarm.bootstrap.modules;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.modules.maven.ArtifactCoordinates;
import org.jboss.modules.maven.MavenResolver;

/**
 * @author Bob McWhirter
 */
public class UberJarMavenResolver implements MavenResolver {

    private static final Pattern snapshotPattern = Pattern.compile("-\\d{8}\\.\\d{6}-\\d+$");

    public static File copyTempJar(String artifactId, InputStream in, String packaging) throws IOException {
        Path tmp = Files.createTempFile(artifactId, "." + packaging);
        Files.copy(in, tmp, StandardCopyOption.REPLACE_EXISTING);
        return tmp.toFile();
    }

    static String relativeArtifactPath(char separator, String groupId, String artifactId, String version) {
        StringBuilder builder = new StringBuilder(groupId.replace('.', separator));
        builder.append(separator).append(artifactId).append(separator);
        String pathVersion;
        final Matcher versionMatcher = snapshotPattern.matcher(version);
        if (versionMatcher.find()) {
            // it's really a snapshot
            pathVersion = version.substring(0, versionMatcher.start()) + "-SNAPSHOT";
        } else {
            pathVersion = version;
        }
        builder.append(pathVersion).append(separator).append(artifactId).append('-').append(version);
        return builder.toString();
    }

    @Override
    public File resolveArtifact(ArtifactCoordinates coordinates, String packaging) throws IOException {

        String artifactRelativePath = "m2repo/" + relativeArtifactPath('/', coordinates.getGroupId(), coordinates.getArtifactId(), coordinates.getVersion());
        String classifier = "";
        if (coordinates.getClassifier() != null && !coordinates.getClassifier().trim().isEmpty()) {
            classifier = "-" + coordinates.getClassifier();
        }

        String jarPath = artifactRelativePath + classifier + "." + packaging;

        InputStream stream = UberJarMavenResolver.class.getClassLoader().getResourceAsStream(jarPath);

        if (stream != null) {
            return copyTempJar(coordinates.getArtifactId() + "-" + coordinates.getVersion(), stream, packaging);
        }

        return null;
    }
}
