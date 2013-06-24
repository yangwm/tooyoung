/**
 * 
 */
package cc.tooyoung.web.rest.sample;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

/**
 * 
 * @author yangwm Apr 27, 2012 11:50:32 AM
 */
@Path("sample")
@Component
public class SampleResource {
    
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public String frist() {
        return "{\"frist\":\"two\"}";
    }

}
