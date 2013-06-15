/**
 * 
 */
package com.tooyoung.common.db;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

/**
 * 
 * @author yangwm Sep 29, 2011 11:27:15 AM
 */
public class JdbcTemplateTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:mysql://s3306.tooyoung.com:3306/fans?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true";
        String jdbcName = StringUtils.substringBetween(jdbcUrl, "://", "/");
        System.out.println("jdbcName is=" + jdbcName);
        
        System.out.println(StringUtils.substringBefore("0.0", "."));
        System.out.println(StringUtils.substringBefore("0", "."));
        System.out.println(StringUtils.substringBefore(null, "."));
    }
    
    @Test
    public void reservedTest(){
    	
    }

}
