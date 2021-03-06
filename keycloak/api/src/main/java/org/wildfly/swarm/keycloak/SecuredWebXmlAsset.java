package org.wildfly.swarm.keycloak;

import org.jboss.shrinkwrap.api.asset.NamedAsset;
import org.wildfly.swarm.container.util.XmlWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Bob McWhirter
 */
public class SecuredWebXmlAsset implements NamedAsset {

    public static final String NAME = "WEB-INF/web.xml";

    private List<SecurityConstraint> constraints = new ArrayList<>();

    public SecuredWebXmlAsset() {

    }

    public SecurityConstraint protect() {
        SecurityConstraint constraint = new SecurityConstraint();
        this.constraints.add(constraint);
        return constraint;
    }

    public SecurityConstraint protect(String urlPattern) {
        SecurityConstraint constraint = new SecurityConstraint(urlPattern);
        this.constraints.add(constraint);
        return constraint;
    }

    @Override
    public InputStream openStream() {
        StringWriter out = new StringWriter();
        XmlWriter writer = new XmlWriter(out);
        try {
            XmlWriter.Element webApp = writer.element("web-app");
            webApp.attr("xmlns", "http://java.sun.com/xml/ns/javaee");
            webApp.attr("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            webApp.attr("xsi:schemaLocation", "http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd");
            webApp.attr("version", "3.0");

            XmlWriter.Element param = webApp.element("context-param");
            param.element( "param-name" ).content( "resteasy.scan" ).end();
            param.element( "param-value" ).content( "true" ).end();
            param.end();

            Set<String> allRoles = new HashSet<>();

            for (SecurityConstraint each : this.constraints) {
                XmlWriter.Element securityConstraint = webApp.element("security-constraint");

                XmlWriter.Element webResourceCollection = securityConstraint.element("web-resource-collection");
                webResourceCollection.element("url-pattern").content(each.urlPattern()).end();
                if (each.method() != null) {
                    webResourceCollection.element("http-method").content(each.method()).end();
                }
                webResourceCollection.end();

                for (String eachRole : each.roles()) {
                    XmlWriter.Element authConstraint = securityConstraint.element("auth-constraint");
                    authConstraint.element("role-name").content(eachRole).end();
                    authConstraint.end();

                    allRoles.add(eachRole);
                }

                securityConstraint.end();
            }

            XmlWriter.Element loginConfig = webApp.element("login-config");
            loginConfig.element("auth-method").content("KEYCLOAK").end();
            loginConfig.element("realm-name").content("ignored").end();
            loginConfig.end();

            for (String eachRole : allRoles) {
                XmlWriter.Element securityRole = webApp.element("security-role");
                securityRole.element("role-name").content(eachRole).end();
                securityRole.end();
            }

            webApp.end();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(out.toString().getBytes());
    }

    @Override
    public String getName() {
        return NAME;
    }
}
