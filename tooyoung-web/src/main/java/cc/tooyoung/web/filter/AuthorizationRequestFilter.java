/**
 * 
 */
package cc.tooyoung.web.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * 
 * 
 * @author yangwm Jun 23, 2013 12:46:04 AM
 */
public class AuthorizationRequestFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        System.out.println("AuthorizationRequestFilter filter　headers:" + requestContext.getHeaders()
                + ", uriInfo:" + requestContext.getUriInfo() + ", requestMethod:" + requestContext.getRequest().getMethod());
        
        /*
        final SecurityContext securityContext = requestContext.getSecurityContext();
        if (securityContext == null || !securityContext.isUserInRole("privileged")) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).entity("User cannot access the resource.").build()
                    );
        }
        */
    }
}

