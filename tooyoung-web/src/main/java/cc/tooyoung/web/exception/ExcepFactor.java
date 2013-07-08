package cc.tooyoung.web.exception;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExcepFactor {

    // 系统级异常
    public static final int ERROR_LEVEL_SYSTEM = 1;
    // 服务级异常
    public static final int ERROR_LEVEL_SERVICE = 2;

    private static final Set<ExcepFactor> excepFactors = new HashSet<ExcepFactor>();

    /** 系统默认错误 */
    public static final ExcepFactor E_SYSTEM_DEFAULT = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.INTERNAL_SERVER_ERROR, 1,
            "system error!", "系统错误!");
    
    /** 服务端资源不可用 */
    public static final ExcepFactor E_SERVICE_UNAVAILABLE = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.SERVICE_UNAVAILABLE, 2,
            "service unavailable!", "服务端资源不可用!");
    /** 接口错误 */
    public static final ExcepFactor E_SERVICE_ERROR= new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.SERVICE_UNAVAILABLE, 3,
            "remote service error!", "远程服务出错");
    
    /** IP限制，不能请求该资源 */
    public static final ExcepFactor E_IP_LIMIT = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.BAD_REQUEST, 4,
            "IP limit!", "IP限制，不能请求该资源!");
    public static final ExcepFactor E_ILLEGAL_REQUEST = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.BAD_REQUEST, 5,
            "Illegal Request!", "非法请求！");
    
    /** 来源不对 */
    public static final ExcepFactor E_APPKEY_ERROR = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.BAD_REQUEST, 6,
            "appkey paramter is missing", "缺少 appkey参数!");
    public static final ExcepFactor E_ACCESS_TOKEN_ERROR = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.BAD_REQUEST, 7,
            "invalid accessToken", "无效accessToken!");
    
    public static final ExcepFactor E_PARAM_ERROR = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.BAD_REQUEST, 15,
            "param error, see doc for more info.", "错误:参数错误，请参考API文档!");
    public static final ExcepFactor E_PARAM_MISS_ERROR = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.BAD_REQUEST, 16,
            "miss required parameter (%s), see doc for more info.", "错误:缺失必选参数:%s，请参考API文档.");
    public static final ExcepFactor E_PARAM_INVALID_ERROR = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.BAD_REQUEST, 17,
            "parameter (%s)'s value invalid,expect (%s), but get (%s), see doc for more info.",
            "错误:参数值非法,希望得到 (%s),实际得到 (%s)，请参考API文档.");
    public static final ExcepFactor E_POST_BODY_LENGTH_LIMIT = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.BAD_REQUEST, 19,
            "request boday length over limit.", "请求长度超过限制!");

    /** 接口不存在 */
    public static final ExcepFactor E_API_NOT_EXIST = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.NOT_FOUND, 20,
            "Request Api not found!", "接口不存在!");
    /** Http 方法错误 */
    public static final ExcepFactor E_METHOD_ERROR = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.METHOD_NOT_ALLOWED, 21,
            "HTTP METHOD is not suported for this request!", "请求的HTTP METHOD不支持!");

    /*
     * ----------------------------------------- 
     */
    /** 服务默认错误 */
    public static final ExcepFactor E_SERVICE_DEFAULT = new ExcepFactor(
            ERROR_LEVEL_SERVICE, 0, HttpStatus.BAD_REQUEST, 1,
            "service error!", "服务错误!");
    
    public static final ExcepFactor E_ILLEGAL_UPDATE = new ExcepFactor(
            ERROR_LEVEL_SERVICE, 0, HttpStatus.BAD_REQUEST, 2,
            "invalid update!", "无效更新操作!");
    /** 用户不存在 */
    public static final ExcepFactor E_USER_NOT_EXIST = new ExcepFactor(
            ERROR_LEVEL_SERVICE, 0, HttpStatus.BAD_REQUEST, 3,
            "User does not exists!", "用户不存在!");
    public static final ExcepFactor E_USER_INVALID = new ExcepFactor(
            ERROR_LEVEL_SERVICE, 0, HttpStatus.BAD_REQUEST, 4,
            "invalid User!", "无效用户!");
    
    /** 图片格式不对 */
    public static final ExcepFactor E_INPUT_IMAGE_ERROR = new ExcepFactor(
            ERROR_LEVEL_SERVICE, 0, HttpStatus.BAD_REQUEST, 15,
            "unsupported image type, only suport JPG, GIF, PNG!", "不支持的图片类型,仅仅支持JPG,GIF,PNG!");
    /** 图片太大 */
    public static final ExcepFactor E_INPUT_IMAGESIZE_ERROR = new ExcepFactor(
            ERROR_LEVEL_SERVICE, 0, HttpStatus.BAD_REQUEST, 16,
            "image size too large.", "图片太大。");
    /** 没有上传图片 */
    public static final ExcepFactor E_INPUT_NO_IMAGE = new ExcepFactor(
            ERROR_LEVEL_SERVICE, 0, HttpStatus.BAD_REQUEST, 17,
            "does multipart has image?", "请确保使用multpart上传了图片!");

    private final HttpStatus httpStatus;
    private final int level;
    private final int serviceId;
    private final int errorCode;
    private final String errorMsg;
    private final String errorMsgCn;

    private ExcepFactor(int level, int serviceId, HttpStatus httpStatus,
            int errorCode, String errorMsg, String errorMsgCn) {
        if (errorCode <= 0 || errorCode > 99) {
            throw new IllegalArgumentException("errorCode must between 1~99 .");
        }
        if (serviceId < 0 || serviceId > 99) {
            throw new IllegalArgumentException("serviceId must between 1~99 .");
        }
        this.level = level;
        this.serviceId = serviceId;
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.errorMsgCn = errorMsgCn;
        if(excepFactors.contains(this)){
            throw new IllegalArgumentException("this error exist: "+this.getErrorCode());
        }
        excepFactors.add(this);
    }

    public HttpStatus getStatus() {
        return httpStatus;
    }

    public int getErrorCode() {
        return this.level * 10000 + this.serviceId * 100 + this.errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getErrorMsg(Object... args) {
        if(args == null||args.length == 0){
            return errorMsg;
        }
        return String.format(errorMsg, args);
    }

    public String getErrorMsgCn() {
        return errorMsgCn;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.getErrorCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ExcepFactor other = (ExcepFactor) obj;
        if (this.getErrorCode() != other.getErrorCode()) {
            return false;
        }
        return true;
    }
    
    public String toString(){
        return String.format("%s\t%s\t%s", this.getErrorCode(),this.getErrorMsg(),this.getErrorMsgCn());
    }

    

    public static void main(String[] args) {
        printException(new PrintWriter(System.out));
    }
    
    public static void printException(PrintWriter out){
        List<ExcepFactor> excepList = new ArrayList<ExcepFactor>(excepFactors);
        Collections.sort(excepList, new Comparator<ExcepFactor>() {
            @Override
            public int compare(ExcepFactor o1, ExcepFactor o2) {
                return o1.getErrorCode() - o2.getErrorCode();
            }

        });
        for(ExcepFactor e:excepList){
            out.println(e.getErrorCode()+" "+e.toString());
            out.flush();
        }
    }


}
