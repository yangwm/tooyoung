/**
 * 
 */
package cc.tooyoung.web.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author yangwm May 22, 2013 11:55:34 PM
 */
public class WebApiException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -2558444636565841019L;

    private final Map<String, Object> parameters = new HashMap<String, Object>();
    private ExcepFactor factor;

    public WebApiException(ExcepFactor factor) {
        this(factor, factor.getErrorMsg());
    }

    public WebApiException(ExcepFactor factor, Object message) {
        super(message == null ? factor.getErrorMsg() : message.toString());
        this.factor = factor;
    }

    public WebApiException(ExcepFactor factor, Object[] args) {
        this(factor, factor.getErrorMsg(args));
    }

    public WebApiException(Exception e) {
        this(ExcepFactor.E_DEFAULT, e.getMessage());
    }

    public WebApiException(String message) {
        this(ExcepFactor.E_DEFAULT, message);
    }

    public void setTraceHeader(String name, Object value) {
        getTraceHeaders().put(name, value);
    }

    public Map<String, Object> getTraceHeaders() {
        return parameters;
    }

    public ExcepFactor getFactor() {
        return factor;
    }

}
