/**
 * 
 */
package cc.tooyoung.web.exception;

import org.junit.Test;

/**
 * 
 * @author yangwm Jul 3, 2013 6:59:55 PM
 */
public class ExcepFactorTest {

    @Test
    public void test(){
        new WebApiException(ExcepFactor.E_PARAM_INVALID_ERROR, "param error");
    }
    
}
