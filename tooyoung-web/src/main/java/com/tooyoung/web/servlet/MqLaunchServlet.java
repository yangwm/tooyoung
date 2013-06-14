/**
 * 
 */
package com.tooyoung.web.servlet;

import javax.servlet.http.HttpServlet;

/**
 * 
 * @author yangwm May 7, 2012 11:14:24 AM
 */
public class MqLaunchServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 8450466885151977909L;

    @Override
    public void init() {
        
        System.out.println("ActivityMqLaunch init -sucess!");
    }
    
    @Override
    public void destroy() {
        
        System.out.println("ActivityMqLaunch destroy -sucess!");
    }
    
}
    
