/**
 * 
 */
package cc.tooyoung.web.base;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.support.XmlWebApplicationContext;


public class ContextLoaderListener implements ServletContextListener {

    public static final String CONFIG_LOCATION_PARAM = "contextConfigLocation";

    public static final String ALLOW_BEAN_DEFINITION_OVERRIDING_PARAM = "allowBeanDefinitionOverriding";
    /**
     * Initialize the root web application context.
     */
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("begin init spring context.");
        ServletContext sc = event.getServletContext();
        String configLocation = sc.getInitParameter(CONFIG_LOCATION_PARAM);
        if(configLocation == null) {
            System.out.println("can not find servlet init parameter:"+CONFIG_LOCATION_PARAM);
            //throw new Exception("can not find servlet init parameter:"+CONFIG_LOCATION_PARAM); // SystemInitException 
        }
        String beanOverriding = sc.getInitParameter(ALLOW_BEAN_DEFINITION_OVERRIDING_PARAM);
        //为了兼容，默认允许 beanOverriding
        boolean allowBeanOverriding = "false".equalsIgnoreCase(beanOverriding)?false:true;
        System.out.println(ALLOW_BEAN_DEFINITION_OVERRIDING_PARAM+":"+allowBeanOverriding);
        //通过 jvm进程的 -Dweb.profile 参数决定是否启用profile
        if("true".equalsIgnoreCase(System.getProperty("web.profile","false"))){
            System.out.println("web.profile is true,config:spring/profile.xml");
            if(!configLocation.endsWith(";")){
                configLocation = configLocation +";";
            }
            configLocation = configLocation +"classpath:spring/profile.xml";
        }
        
        try{
            XmlWebApplicationContext ctx = new XmlWebApplicationContext();
            ctx.setParent(null);
            ctx.setServletContext(sc);
            //是否允许 bean overriding
            ctx.setAllowBeanDefinitionOverriding(allowBeanOverriding);
            ctx.setConfigLocation(configLocation);
            ctx.refresh();
            ApplicationContextHolder.setApplicatioinContext(ctx);
        } catch(RuntimeException e) {
            e.printStackTrace();
            throw e;
        } catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println("spring context init finished.");
    }


    /**
     * Close the root web application context.
     */
    public void contextDestroyed(ServletContextEvent event) {
        ApplicationContextHolder.getApplicatioinContext().close();
    }

}
