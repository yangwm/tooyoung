/**
 * 
 */
package cc.tooyoung.web.exception;


/**
 * 
 * @author yangwm Jul 4, 2013 12:01:26 AM
 */
public class WebExceptionFormat {
    
    private final static String xmlMsg = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
            + "  <hash>\r\n"
            + "    <error>%s</error>\r\n"
            + "    <error_code>%s</error_code>\r\n"
            + "    <request>%s</request>\r\n"
            +"  </hash>";
    private final static String strMsg = "request=%s&error_code=%s&error=%s&error_CN=%s";
    private final static String jsonMsg = "{\"request\":\"%s\",\"error_code\":%s,\"error\":\"%s\"}";
    
    public static String formatException(WebApiException e, String path, String type) {
        if (path == null) {
            throw new IllegalArgumentException(" path argument is null");
        }
        ExcepFactor factor = e.getFactor();
        if (type == null) {
            // type = getType(path);
        }

        String result;
        if ("xml".equals(type)) {
            result = String.format(xmlMsg, path, factor.getErrorCode(), e.getMessage());
        } else if ("str".equals(type)) {
            result = String.format(strMsg, path, factor.getErrorCode(), e.getMessage(), factor.getErrorMsgCn());
        } else {
            result = String.format(jsonMsg, path, factor.getErrorCode(), e.getMessage());
        }
        return result;
    }
    
}
