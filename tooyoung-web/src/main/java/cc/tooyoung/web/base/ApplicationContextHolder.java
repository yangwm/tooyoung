/**
 * 
 */
package cc.tooyoung.web.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 
 * @author yangwm May 24, 2013 6:49:42 PM
 */
public class ApplicationContextHolder {

    private static ConfigurableApplicationContext _context;
    
    private static Map<Class,Object> mockBeans;

    public static ConfigurableApplicationContext getApplicatioinContext() {
        return _context;
    }

    public static void setApplicatioinContext(ConfigurableApplicationContext context) {
        _context = context;
    }
    

    /**
     * 将该对象中的带有Autowired annotation的属性自动注入
     * 
     * @param obj
     */
    public static void autowireBean(Object obj) {
        if (_context != null) {
            AutowireCapableBeanFactory factory = _context.getAutowireCapableBeanFactory();
            factory.autowireBean(obj);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name){
        return (T) _context.getBean(name);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clazz){
        T bean = null;
        if(mockBeans != null){
            bean = (T)mockBeans.get(clazz);
        }
        if(bean != null){
            return bean;
        }
        String[] names = _context.getBeanNamesForType(clazz);
        if(names == null || names.length == 0){
            return null;
        }
        return (T)_context.getBean(names[0]);
    }
    public static <T> List<T> getBeans(Class<T> clazz){
        List<T> ret=new ArrayList<T>();
        if(_context==null)
            return ret;
        String[] names = _context.getBeanNamesForType(clazz);
        if(names == null || names.length == 0){
            return ret;
        }
        for(String name:names){
            ret.add((T) _context.getBean(name));
        }
        return ret;
    }
    
    public static void setMockBean(Class clazz,Object object){
        if(mockBeans == null){
            mockBeans = new HashMap<Class, Object>();
        }
        mockBeans.put(clazz, object);
    }
}
