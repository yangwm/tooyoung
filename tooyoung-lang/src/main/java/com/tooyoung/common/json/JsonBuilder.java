package com.tooyoung.common.json;

import java.util.List;


public class JsonBuilder {
	
	private StringBuilder sb;
	
	public JsonBuilder() {
		sb = new StringBuilder();
		sb.append("{");
	}
	
	public JsonBuilder(int initCapacity) {
		sb = new StringBuilder(initCapacity);
		sb.append("{");
	}
	
	/**
	 * 
	 * @param value
	 * @param isRawData true 则不编码
	 */
	public JsonBuilder(String value, boolean isRawData) {
		sb = new StringBuilder();
		if (isRawData) {
			sb.append(value);
		} else {
			sb.append("\"").append(JsonUtil.toJsonStr(value)).append("\"");
		}
	}

	public JsonBuilder append(String name, String value) {
		if (name == null) {
			return this;
		}
		
		if (sb.length() > 1)
			sb.append(",");
		sb.append("\"").append(name).append("\":");
		if (value != null)
			sb.append("\"").append(JsonUtil.toJsonStr(value)).append("\"");
		else
			sb.append("null");
		
		return this;
	}
	public JsonBuilder appendStrArr(String name, String[] value) {
		if (name == null) {
			return this;
		}
		
		if (sb.length() > 1)
			sb.append(",");
		sb.append("\"").append(name).append("\":");
		if (value != null){
			sb.append("[");
			int i=0;
			for(String s :value){
				if(i++>0){
					sb.append(",");
				}
				sb.append("\"").append(JsonUtil.toJsonStr(s)).append("\"");
			}
			sb.append("]");
		}else{
			sb.append("[]");
		}
		
		return this;
	}
	
	public JsonBuilder appendJsonArr(String name, List<JsonBuilder> jsonArr) {
		if (name == null) {
			return this;
		}
		
		if (sb.length() > 1)
			sb.append(",");
		sb.append("\"").append(name).append("\":");
		if (jsonArr != null){
			sb.append("[");
			int i=0;
			for(JsonBuilder s :jsonArr){
				if(i++>0){
					sb.append(",");
				}
				sb.append(s.toString());
			}
			sb.append("]");
		}else{
			sb.append("[]");
		}
		
		return this;
	}
	
	/**
	 * 附加long数组到buffer中
	 * @param name 待添加的属性名称
	 * @param values	long数组
	 * @return	如果数组不为空，则返回按照","分割的字符串，否则返回"[]"
	 */
	public JsonBuilder appendLongArr(String name, long[] values) {
		if (name == null) {
			return this;
		}
		
		if (sb.length() > 1) {
			sb.append(",");
		}
		sb.append("\"").append(name).append("\":");
		if (values != null){
			sb.append("[");
			for(int i = 0; i < values.length; i++){
				if(i>0){
					sb.append(",");
				}
				sb.append(JsonUtil.toJsonStr(String.valueOf(values[i])));
			}
			sb.append("]");
		}else{
			sb.append("[]");
		}
		
		return this;
	}
	
	public JsonBuilder appendStrArr(String name, long[] values) {
        if (name == null)
            return this;
        
        if (sb.length() > 1) {
            sb.append(",");
        }
        sb.append("\"").append(name).append("\":");
        if (values != null){
            sb.append("[");
            int i=0;
            for(long val : values){
                if(i++>0){
                    sb.append(", ");
                }
                sb.append("\"").append(String.valueOf(val)).append("\"");
            }
            sb.append("]");
        }else{
            sb.append("[]");
        }
        
        return this;
    }
	
	public JsonBuilder appendArr(String name, Jsonable[] values) {
        if (name == null)
            return this;
        
        if (sb.length() > 1) {
            sb.append(",");
        }
        sb.append("\"").append(name).append("\":");
        sb.append(JsonUtil.toJson(values));
        
        return this;
    }
	
	/**
	 * 附加Long列表到buffer中
	 * @param name 待添加的属性名称
	 * @param values	long列表
	 * @return	如果List不为空，则返回按照","分割的字符串，否则返回"[]"
	 */
	public JsonBuilder appendLongList(String name, List<Long> values) {
		if (name == null)
			return this;
		
		if (sb.length() > 1) {
			sb.append(",");
		}
		sb.append("\"").append(name).append("\":");
		if (values != null){
			sb.append("[");
			int i = 0;
			for(long value : values){
				if(i++>0){
					sb.append(",");
				}
				sb.append(JsonUtil.toJsonStr(String.valueOf(value)));
			}
			sb.append("]");
		}else{
			sb.append("[]");
		}
		
		return this;
	}
	
	public JsonBuilder appendObject(String name, String[] value) {
		if (name == null)
			return this;
		
		if (sb.length() > 1) {
			sb.append(",");
		}
		sb.append("\"").append(name).append("\":");
		if (value != null){
			sb.append("{");
			int i=0;
			for(String s :value){
				if(i++>0){
					sb.append(",");
				}
				sb.append("\"").append(s).append("\":");
				sb.append("\"").append(JsonUtil.toJsonStr(s)).append("\"");
			}
			sb.append("}");
		}else{
			sb.append("{}");
		}
		
		return this;
	}
	
	public JsonBuilder append(String name, boolean value) {
		if (name == null)
			return this;
		
		if (sb.length() > 1) {
			sb.append(",");
		}
		sb.append("\"").append(name).append("\":").append(value);
		return this;
	}
	
	public JsonBuilder append(String name, int value) {
		if (sb.length() > 1) {
			sb.append(",");
		}
		sb.append("\"").append(name).append("\":").append(value);
		return this;
	}

	public JsonBuilder append(String name, long value) {
		if (sb.length() > 1) {
			sb.append(",");
		}
		sb.append("\"").append(name).append("\":").append(value);
		return this;
	}
	public JsonBuilder append(String name, double value) {
		if (sb.length() > 1) {
			sb.append(",");
		}
		sb.append("\"").append(name).append("\":").append(value);
		return this;
	}
	
	public JsonBuilder append(String name, JsonBuilder value) {
		return appendJsonValue(name, value.toString());
	}
	
	/**
	 * append Json
	 * @param name
	 * @param jsonValue
	 * @return
	 */
	public JsonBuilder appendJsonValue(String name, String jsonValue) {
		if (name == null || jsonValue == null)
			return this;
		
		if (sb.length() > 1) {
			sb.append(",");
		}
		sb.append("\"").append(name).append("\":").append(jsonValue);
		return this;
	}
	
	public JsonBuilder flip() {
		sb.append('}');
		return this;
	}
	
	public JsonBuilder reset() {
		sb.setLength(0);
		sb.append("{");
		return this;
	}
	
	@Override
	public String toString() {
		/* 去掉，因为嵌套插入PostSource调用可能不含{}, 如 "source": "web"
		if (sb.charAt(sb.length() - 1) != '}')
			sb.append('}');
			*/
		return sb.toString();
	}
	
	public void setLength(int length) {
		sb.setLength(length);
	}
	
	public int length() {
		return sb.length();
	}
}
