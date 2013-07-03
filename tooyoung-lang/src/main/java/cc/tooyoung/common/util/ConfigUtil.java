/**
 * 
 */
package cc.tooyoung.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * configuratin files util 
 * 
 * @author yangwm Dec 28, 2010 1:47:13 PM
 */
public class ConfigUtil {

    /**
     * get file url by file name in class path 
     * 
     * @param fileName
     * @return
     * @throws IOException
     */
    public static URL getConfigUrl(String fileName) {
        ClassLoader cl = ConfigUtil.class.getClassLoader();
        return cl.getResource(fileName);
    }
    
    /**
     * get file input stream by file name in class path 
     * 
     * @param fileName
     * @return
     * @throws IOException
     */
    public static InputStream getConfigInputStream(String fileName) {
        ClassLoader cl = ConfigUtil.class.getClassLoader();
        return cl.getResourceAsStream(fileName);
    }

    /**
     * get file properties by file name in class path 
     * 
     * @param fileName
     * @return
     */
    public static Properties getConfigProperties(String fileName) {
        Properties env = new Properties();
        InputStream is = null;
        try {
            is = getConfigInputStream(fileName);
            env.load(is);
        } catch (Exception e) {
            ApiLogger.error("getConfigProperties(): error" + e);
            throw new RuntimeException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    ApiLogger.error("getConfigProperties(): Close InputStream error");
                }
            }
        }
        return env;
    }

}
