/**
 * 
 */
package cc.tooyoung.common.mcq;

/**
 * Msg Handler (实现业务逻辑) 
 * 
 * @author yangwm Nov 4, 2011 4:29:06 PM
 */
public interface MsgHandler {

	/** TODO yangwm fix */ 
    int handleMsq(String readKey, String msg, String msgJson);

}
