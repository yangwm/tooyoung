/**
 * 
 */
package cc.tooyoung.web.rest.hello;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;


/**
 * 
 * @author yangwm May 24, 2013 4:35:29 PM
 */
@Path("/hello")
@Component
public class HelloResource {
    
    @Path("/say")
    @GET
    @Produces({ MediaType.TEXT_PLAIN })
    public String sayHello() {
        return "Hello yangwm with Jersey";
    }
    
    /**/
    @Path("/to")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({ MediaType.APPLICATION_JSON })
    public String update(
            //@Context AuthResponse authResponse,
            @DefaultValue("0") @FormParam("visible") int visible
            ){
        long uid = 0L;//authResponse.getUid();
        return "" + uid + "";
    }

}
