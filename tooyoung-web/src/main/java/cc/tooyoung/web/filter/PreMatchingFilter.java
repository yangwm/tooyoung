/**
 * 
 */
package cc.tooyoung.web.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

/**
 * 
 * 
 * @author yangwm Jun 23, 2013 1:08:23 AM
 */
public class PreMatchingFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        System.out.println("PreMatchingFilter filter requestMethod:" + requestContext.getMethod());
        
        // change all PUT methods to POST
        if (requestContext.getMethod().equals("PUT")) {
            requestContext.setMethod("POST");
        }
    }
}

