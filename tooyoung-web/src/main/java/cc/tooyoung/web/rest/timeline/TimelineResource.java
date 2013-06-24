/**
 * 
 */
package cc.tooyoung.web.rest.timeline;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

/**
 * 
 * @author yangwm May 24, 2013 4:35:29 PM
 */
@Path("/timeline")
@Component
public class TimelineResource {
    
    @Path("/user")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String sayHello() {
        return "{\"user\":\"Hello yangwm with Jersey\"}";
    }
    
    @Path("/friends")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String friends(
            @QueryParam("uid") long uid
            ) {
        long[] ids = new long[] { uid + 1, uid + 2, uid + 3};

        return ids.toString();
    }
    
    @Path("/home")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public String getTimeline(
            @QueryParam("uid") long uid
            ) {
        
        return "{\"uid\":" + uid + "}";
    }
    
}
