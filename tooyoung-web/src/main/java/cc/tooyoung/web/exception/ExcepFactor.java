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

    /** 默认错误 */
    public static final ExcepFactor E_DEFAULT = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.INTERNAL_SERVER_ERROR, 50001, 1,
            "system error!", "系统错误!");
    
    /** 服务端资源不可用 */
    public static final ExcepFactor E_SERVICE_UNAVAILABLE = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.SERVICE_UNAVAILABLE, 50301, 2,
            "service unavailable!", "服务端资源不可用!");
    public static final ExcepFactor E_SYSTEM_BUSY = new ExcepFactor(ERROR_LEVEL_SYSTEM, 0,
            HttpStatus.SERVICE_UNAVAILABLE, 50302,9,
            "Too many pending tasks, system is busy!", "任务过多，系统繁忙!");
    public static final ExcepFactor E_JOB_EXPIRED = new ExcepFactor(ERROR_LEVEL_SYSTEM, 0,
            HttpStatus.SERVICE_UNAVAILABLE, 50303,10, "Job Expired", "任务超时。");
    public static final ExcepFactor E_RPC_ERROR = new ExcepFactor(ERROR_LEVEL_SYSTEM, 0,
            HttpStatus.SERVICE_UNAVAILABLE, 50304,11, "RPC ERROR", "RPC错误。");
    

    /** 接口错误 */
    public static final ExcepFactor E_SERVICE_ERROR= new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.SERVICE_UNAVAILABLE, 40301, 3,
            "remote service error!", "远程服务出错");
    /** IP限制，不能请求该资源 */
    public static final ExcepFactor E_IP_LIMIT = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.BAD_REQUEST, 400302, 4,
            "IP limit!", "IP限制，不能请求该资源!");
    /** 来源级别错误 */
    public static final ExcepFactor E_SOURCE_LEVEL_ERROR = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.FORBIDDEN, 40303, 5,
            "permission denied! Need a high level appkey!",
            "该资源需要appkey拥有更高级的授权!");
    public static final ExcepFactor E_ILLEGAL_REQUEST = new ExcepFactor(ERROR_LEVEL_SYSTEM, 0,
            HttpStatus.BAD_REQUEST, 40392,12, "Illegal Request!", "非法请求！");
    
    
    /** 来源不对 */
    public static final ExcepFactor E_SOURCE_ERROR = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.BAD_REQUEST, 40022, 6,
            "source paramter(appkey) is missing", "缺少 source参数(appkey)!");
    
    public static final ExcepFactor E_UNSUPPORT_MEDIATYPE_ERROR = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.UNSUPPORTED_MEDIA_TYPE, 40001, 7,
            "unsupport mediatype (%s)", "不支持的 MediaType (%s).");

    public static final ExcepFactor E_PARAM_ERROR = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.BAD_REQUEST, 40001, 8,
            "param error, see doc for more info.", "错误:参数错误，请参考API文档!");

    /** 用户没有开通服务 */
    public static final ExcepFactor E_USER_NOTOPEN = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.FORBIDDEN, 40313, 13,
            "invalid weibo user!", "不合法的用户!");
    /** 应用访问api接口权限受限制 */
    public static final ExcepFactor E_APP_API_LIMIT = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.FORBIDDEN, 40070, 14,
            "Insufficient app permissions!", "第三方应用访问api接口权限受限制");

    
    public static final ExcepFactor E_PARAM_MISS_ERROR = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.BAD_REQUEST, 40002, 16,
            "miss required parameter (%s), see doc for more info.",
            "错误:缺失必选参数:%s，请参考API文档.");
    public static final ExcepFactor E_PARAM_INVALID_ERROR = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.BAD_REQUEST, 40001, 17,
            "parameter (%s)'s value invalid,expect (%s), but get (%s), see doc for more info.",
            "错误:参数值非法,希望得到 (%s),实际得到 (%s)，请参考API文档.");

    public static final ExcepFactor E_POST_BODY_LENGTH_LIMIT = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.BAD_REQUEST, 40001, 18,
            "request boday length over limit.", "请求长度超过限制!");

    /** 接口不存在 */
    public static final ExcepFactor E_API_NOT_EXIST = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.NOT_FOUND, 0, 20,
            "Request Api not found!",
            "接口不存在!");
    /** Http 方法错误 */
    public static final ExcepFactor E_METHOD_ERROR = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.METHOD_NOT_ALLOWED, 40307, 21,
            "HTTP METHOD is not suported for this request!",
            "请求的HTTP METHOD不支持!");
    /** 用户IP次数达到限制 */
    public static final ExcepFactor E_IP_OUTOFLIMIT = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.FORBIDDEN, 40312, 22,
            "IP requests out of rate limit!", "IP请求超过上限!");

    /** 用户请求次数达到限制 */
    public static final ExcepFactor E_USER_OUTOFLIMIT = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.FORBIDDEN, 40310, 23,
            "User requests out of rate limit!", "用户请求超过上限!");
    
    /** 用户对特定接口的请求次数达到限制 */
    public static final ExcepFactor E_API_OUTOFLIMIT = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.FORBIDDEN, 0, 24,
            "User requests for %s out of rate limit!", "用户请求接口%s超过上限!");
    
    /**
     * 调用内部服务 接口参数错误
     */
    public static final ExcepFactor E_SINA_PARAM_ERROR = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.BAD_REQUEST, 0, 25,
            "internal service param error.", "内部接口参数错误");
    
    /**
     * 接口已经废弃
     */
    public static final ExcepFactor E_API_DEPRECATED_ERROR = new ExcepFactor(
            ERROR_LEVEL_SYSTEM, 0, HttpStatus.SERVICE_UNAVAILABLE, 0, 26,
            "api is deprecated.", "该接口已经废弃");

    /** 用户不存在 */
    public static final ExcepFactor E_USER_NOTEXIST = new ExcepFactor(
            ERROR_LEVEL_SERVICE, 0, HttpStatus.BAD_REQUEST, 40023, 3,
            "User does not exists!", "用户不存在!");


    /** 图片格式不对 */
    public static final ExcepFactor E_INPUT_IMAGEERROR = new ExcepFactor(
            ERROR_LEVEL_SERVICE, 0, HttpStatus.BAD_REQUEST, 40045, 5,
            "unsupported image type, only suport JPG, GIF, PNG!",
            "不支持的图片类型,仅仅支持JPG,GIF,PNG!");
    /** 图片太大 */
    public static final ExcepFactor E_INPUT_IMAGESIZEERROR = new ExcepFactor(
            ERROR_LEVEL_SERVICE, 0, HttpStatus.BAD_REQUEST, 4008, 6,
            "image size too large.", "图片太大。");
    /** 没有上传图片 */
    public static final ExcepFactor E_INPUT_NOIMAGE = new ExcepFactor(
            ERROR_LEVEL_SERVICE, 0, HttpStatus.BAD_REQUEST, 4009, 7,
            "does multipart has image?", "请确保使用multpart上传了图片!");
    

    /** 没有填写发布内容 */
    public static final ExcepFactor E_INPUT_TEXTNULL = new ExcepFactor(
            ERROR_LEVEL_SERVICE, 0, HttpStatus.BAD_REQUEST, 20108, 8,
            "content is null!", "内容为空");
    
    public static final ExcepFactor E_INPUT_SAFE_PLUS4 = new ExcepFactor(
            ERROR_LEVEL_SERVICE,
            0,
            HttpStatus.BAD_REQUEST,
            40097,
            31,
            " test and verify!",
            "需要弹出验证码!");

    public static final ExcepFactor E_INPUT_SAFE_PLUS7 = new ExcepFactor(
            ERROR_LEVEL_SERVICE,
            0,
            HttpStatus.BAD_REQUEST,
            0,
            33,
            "login stat abnormal.",
            "登陆状态异常.");
    
    public static final ExcepFactor E_INPUT_SAFE_MINUS12 = new ExcepFactor(
            ERROR_LEVEL_SERVICE,
            0,
            HttpStatus.FORBIDDEN,
            40322,
            34,
            "account is locked.",
            "帐号处于锁定状态");

    private final HttpStatus httpStatus;
    private final int level;
    private final int serviceId;
    private final int errorCode;
    private final String errorMsg;
    private final String errorMsgCn;

    private ExcepFactor(int level, int serviceId, HttpStatus httpStatus,
            int errorCodeV1, int errorCode, String errorMsg, String errorMsgCn) {
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

    protected ExcepFactor(int serviceId, HttpStatus httpStatus,
            int errorCodeV4, String errorMsg, String errorMsgCn) {
        this(ERROR_LEVEL_SERVICE, serviceId, httpStatus, 0, errorCodeV4,
                errorMsg, errorMsgCn);
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
