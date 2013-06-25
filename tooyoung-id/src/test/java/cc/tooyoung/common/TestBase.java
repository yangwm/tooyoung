/**
 * 
 */
package cc.tooyoung.common;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author yangwm Nov 18, 2010 6:04:51 PM
 */
public class TestBase {
    
    /**
     * Logger for this class
     */
    private static final Logger log = Logger.getLogger(TestBase.class);

    public static ApplicationContext ctx = null;
    
    static {
        init();
    }

    public static void init() {
        if (ctx != null) {
            log.error("Error: try to init TestBase twice");
            throw new IllegalArgumentException("Error: try to init TestBase twice");
        }
        try {
            ctx = new ClassPathXmlApplicationContext(
                    new String[]{
                            "spring/tooyoung-id.xml",
                    });

            System.out.println("TestBase init -sucess!");
        } catch (Exception e) {
            log.error("init TestBase error", e);
        }
    }
}
