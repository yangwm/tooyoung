package com.tooyoung.common.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tooyoung.common.util.ApiLogger;


/**
 * 
 * @author yangwm May 21, 2013 8:42:41 PM
 */
public class JsonWrapper {
	
	private JsonNode root;
	
	public JsonWrapper(String json) throws Exception {
		if (json != null)
			try {
			    //JsonParser jp = new JsonFactory().createParser(json);
			    ObjectMapper objectMapper = new ObjectMapper();
			    root = objectMapper.readTree(json);
			} catch (Exception e) {				
				ApiLogger.warn(new StringBuilder(64).append("Error: msg=").append(json), e);
				throw e;
			}
	}
	public JsonWrapper(JsonNode root) {
		this.root = root;
	}
    
    /*
     *  base   
     */
    public JsonNode getJsonNode(String name) {
        if (name == null || root == null) {
            return null;
        }
        return root.get(name);
    }
    public JsonNode pathJsonNode(String name) {
        if (name == null || root == null) {
            return null;
        }
        return root.path(name);
    }
    
    public String toString() {
        return root.toString();
    }

	/*
	 *  get 
	 */
    public String[] getStringArr(String name) {
        List<JsonNode> result = getList(name);
        String[] strs = new String[result.size()];
        for (int i = 0; i < result.size(); i++) {
            JsonNode node = result.get(i);
            strs[i] = node.asText();
        }
        return strs;
    }
    public long[] getLongArr(String name) {
        List<JsonNode> result = getList(name);
        long[] longs = new long[result.size()];
        for (int i = 0; i < result.size(); i++) {
            JsonNode node = result.get(i);
            longs[i] = node.asLong();
        }
        return longs;
    }
    private List<JsonNode> getList(String name){
        JsonNode node = getJsonNode(name);
        if (null == node) {
            return Collections.emptyList();
        }
        
        List<JsonNode> result = new ArrayList<JsonNode>();
        Iterator<JsonNode> iter = node.elements();
        while (iter.hasNext()) {
            JsonNode next = iter.next();
            if (next != null) {
                result.add(next);
            }
        }
        return result;
    }
    
    public String getString(String name) {
        JsonNode node = root.get(name);
        return (node == null ? null : node.asText());
    }
    public long getInt(String name){
        JsonNode node = root.get(name);
        return (node == null ? null : node.asInt());
    }
    public long getLong(String name){
        JsonNode node = root.get(name);
        return (node == null ? null : node.asLong());
    }
    public double getDouble(String name){
        JsonNode node = root.get(name);
        return (node == null ? null : node.asDouble());
    }
    public boolean getBoolean(String name){
        JsonNode node = root.get(name);
        return (node == null ? null : node.asBoolean());
    }
    
    /*
     *  has 
     */
    public boolean has(String name) {
        return root.has(name);
    }

    /*
     *  is 
     */
    public boolean isEmpty() {
        return (root == null || root.size() == 0);
    }
    public boolean isValueNode() {
        return root.isValueNode();
    }
    public boolean isContainerNode() {
        return root.isContainerNode();
    }
    public boolean isMissingNode() {
        return root.isMissingNode();
    }

    public boolean isPojo() {
        return root.isPojo();
    }
    public boolean isArray() {
        return root.isArray();
    }
    public boolean isBinary() {
        return root.isBinary();
    }
    public boolean isObject() {
        return root.isObject();
    }
    public boolean isInt() {
        return root.isInt();
    }
    public boolean isLong() {
        return root.isLong();
    }
    public boolean isDouble() {
        return root.isDouble();
    }
    public boolean isBigDecimal() {
        return root.isBigDecimal();
    }
    public boolean isString() {
        return root.isTextual();
    }
    public boolean isBoolean() {
        return root.isBoolean();
    }
    public boolean isNull() {
        return root.isNull();
    }
    
    public boolean isNumber() {
        return root.isNumber();
    }
    public boolean isIntegralNumber() {
        return root.isIntegralNumber();
    }
    public boolean isFloatingPointNumber() {
        return root.isFloatingPointNumber();
    }

}
