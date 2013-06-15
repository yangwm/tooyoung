/**
 * 
 */
package cc.tooyoung.common.id;

import cc.tooyoung.common.id.UuidConst.BizFlag;

/**
 * Id Create interface 
 * 
 * @author yangwm May 29, 2013 2:39:58 PM
 */
public interface IdCreate {

    long generateId(BizFlag bizFlag);
    
}
