package org.wildfly.swarm.netflix.ribbon.secured.client;

import com.netflix.loadbalancer.reactive.ExecutionContext;
import com.netflix.loadbalancer.reactive.ExecutionInfo;
import com.netflix.loadbalancer.reactive.ExecutionListener;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import org.keycloak.KeycloakSecurityContext;
import org.wildfly.swarm.keycloak.runtime.KeycloakSecurityContextAssociation;

/**
 * @author Bob McWhirter
 */
public class BearerHeaderAdder implements ExecutionListener<HttpClientRequest<ByteBuf>, HttpClientResponse<ByteBuf>> {
    @Override
    public void onExecutionStart(ExecutionContext<HttpClientRequest<ByteBuf>> context) throws AbortExecutionException {
    }

    @Override
    public void onStartWithServer(ExecutionContext<HttpClientRequest<ByteBuf>> context, ExecutionInfo info) throws AbortExecutionException {
        KeycloakSecurityContext securityContext = KeycloakSecurityContextAssociation.get();
        if ( securityContext != null ) {
            HttpClientRequest<ByteBuf> request = context.getRequest();
            request.withHeader("Authorization", "Bearer " + securityContext.getTokenString());
            context.put( KeycloakSecurityContextAssociation.class.getName(), securityContext );
        } else {
            KeycloakSecurityContextAssociation.disassociate();
        }
    }

    @Override
    public void onExceptionWithServer(ExecutionContext<HttpClientRequest<ByteBuf>> context, Throwable exception, ExecutionInfo info) {
        KeycloakSecurityContext securityContext = (KeycloakSecurityContext) context.get(KeycloakSecurityContextAssociation.class.getName());
        if ( securityContext != null ) {
            KeycloakSecurityContextAssociation.associate( securityContext );
        } else {
            KeycloakSecurityContextAssociation.disassociate();
        }
    }

    @Override
    public void onExecutionSuccess(ExecutionContext<HttpClientRequest<ByteBuf>> context, HttpClientResponse<ByteBuf> response, ExecutionInfo info) {
        KeycloakSecurityContext securityContext = (KeycloakSecurityContext) context.get(KeycloakSecurityContextAssociation.class.getName());
        if ( securityContext != null ) {
            KeycloakSecurityContextAssociation.associate( securityContext );
        } else {
            KeycloakSecurityContextAssociation.disassociate();
        }
    }

    @Override
    public void onExecutionFailed(ExecutionContext<HttpClientRequest<ByteBuf>> context, Throwable finalException, ExecutionInfo info) {
        KeycloakSecurityContext securityContext = (KeycloakSecurityContext) context.get(KeycloakSecurityContextAssociation.class.getName());
        if ( securityContext != null ) {
            KeycloakSecurityContextAssociation.associate( securityContext );
        } else {
            KeycloakSecurityContextAssociation.disassociate();
        }
    }
}
