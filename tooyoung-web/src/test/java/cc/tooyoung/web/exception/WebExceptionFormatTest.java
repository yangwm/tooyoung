/**
 * 
 */
package cc.tooyoung.web.exception;

import org.junit.Test;

import cc.tooyoung.common.util.ApiLogger;

/**
 * 
 * @author yangwm Jul 4, 2013 9:18:35 AM
 */
public class WebExceptionFormatTest {

    @Test
    public void testFormatException(){
        WebApiException e = new WebApiException(ExcepFactor.E_PARAM_INVALID_ERROR, "param error");
        String result = WebExceptionFormat.formatException(e, "/user/test", "json");
        ApiLogger.debug("testFormatException result:" + result);
    }
    
}
