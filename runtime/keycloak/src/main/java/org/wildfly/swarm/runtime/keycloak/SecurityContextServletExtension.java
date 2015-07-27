package org.wildfly.swarm.runtime.keycloak;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.ServletExtension;
import io.undertow.servlet.api.DeploymentInfo;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.undertow.UndertowHttpFacade;

import javax.servlet.ServletContext;

/**
 * @author Bob McWhirter
 */
public class SecurityContextServletExtension implements ServletExtension {
    @Override
    public void handleDeployment(DeploymentInfo info, ServletContext context) {
        /*
        info.addThreadSetupAction(new ThreadSetupAction() {
            @Override
            public Handle setup(HttpServerExchange exchange) {
                if ( exchange == null ) {
                    return null;
                }
                System.err.println( "setup thread: " + Thread.currentThread() );
                new Exception().printStackTrace();
                KeycloakSecurityContext c = exchange.getAttachment(UndertowHttpFacade.KEYCLOAK_SECURITY_CONTEXT_KEY);
                KeycloakSecurityContextAssociation.associate(c);
                return new Handle() {
                    @Override
                    public void tearDown() {
                        KeycloakSecurityContextAssociation.disassociate();
                    }
                };
            }
        });
        */

        info.addInnerHandlerChainWrapper(new HandlerWrapper() {
            @Override
            public HttpHandler wrap(HttpHandler next) {
                return new HttpHandler() {
                    @Override
                    public void handleRequest(HttpServerExchange exchange) throws Exception {
                        System.err.println( "******* handle: " + Thread.currentThread() );
                        KeycloakSecurityContext c = exchange.getAttachment(UndertowHttpFacade.KEYCLOAK_SECURITY_CONTEXT_KEY);
                        KeycloakSecurityContextAssociation.associate(c);

                        try {
                            next.handleRequest(exchange);
                        } finally {
                            KeycloakSecurityContextAssociation.disassociate();
                        }

                    }
                };
            }
        });
    }
}
