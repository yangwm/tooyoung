package cc.tooyoung.common.json;

import org.apache.commons.lang.StringUtils;


/**
 * 
 * @author yangwm May 21, 2013 9:37:34 PM
 */
public class JsonUtil {

	/**
	 * xml 1.0: 0-31,127控制字符为非法内容，转为空格
	 * @param value
	 * @return
	 */
	public static String toJsonStr(String value) {
		if (value == null)
			return null;
				
		StringBuilder buf = new StringBuilder(value.length());
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
            switch(c) {
                case '"':
                	buf.append("\\\"");
                    break;
                case '\\':
                    buf.append("\\\\");
                    break;
                case '\n':
                    buf.append("\\n");
                    break;
                case '\r':
                    buf.append("\\r");
                    break;
                case '\t':
                    buf.append("\\t");
                    break;
                case '\f':
                    buf.append("\\f");
                    break;
                case '\b':
                    buf.append("\\b");
                    break;
                    
                default:
                	if (c < 32 || c == 127) {
                		buf.append(" ");
                	} else {
                		buf.append(c);
                	}
            }
		}
		return buf.toString();
	}

	public static boolean isValidJsonObject(String json){
        return isValidJsonObject(json, false);
    }
    //TODO 优化效率 
    public static boolean isValidJsonObject(String json, boolean allowBlank){
        if(StringUtils.isBlank(json)){
            return allowBlank;
        }
        json = json.trim();
        if(!json.startsWith("{")||!json.endsWith("}")){
            return false;
        }
        try{
            JsonWrapper node = new JsonWrapper(json);
            return node.isObject();
        }catch(Exception e){
            return false;
        }
    }
    
    public static boolean isValidJsonArray(String json){
        return isValidJsonArray(json, false);
    }
    public static boolean isValidJsonArray(String json, boolean allowBlank){
        if(StringUtils.isBlank(json)){
            return allowBlank;
        }
        json = json.trim();
        if(!json.startsWith("[")||!json.endsWith("]")){
            return false;
        }
        try{
            JsonWrapper node = new JsonWrapper(json);
            return node.isArray();
        }catch(Exception e){
            return false;
        }
    }

    public static String toJson(long[] ids) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(ids[i]);
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * toJson Object need implements Jsonable
     * 
     * @param values
     * @return
     */
    public static String toJson(Jsonable[] values) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(values[i].toJson());
            }
        }
        sb.append("]");
        return sb.toString();
    }

}
