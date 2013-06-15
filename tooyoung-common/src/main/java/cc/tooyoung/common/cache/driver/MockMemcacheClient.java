/**
 * 
 */
package cc.tooyoung.common.cache.driver;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cc.tooyoung.common.cache.MemcacheClient;
import cc.tooyoung.memcache.vika.CasValue;


/**
 * 
 * a mock MemcacheClient implement. 
 * 
 * @author modify by yangwm Jun 15, 2013 12:28:02 AM
 */
public class MockMemcacheClient implements MemcacheClient {
    
    private Map<String,Object> map = new HashMap<String,Object>();
    
    private int minSpareConnections;
    private int maxSpareConnections;
    
    public void init() {
    }
    
    @Override
    public void setServerPort(String serverPort) {
    }

    @Override
    public void setServerPortList(String[] serverPort) {
    }

    @Override
    public Object get(String key) {
        return map.get(key);
    }
    @Override
    public Map<String, Object> getMulti(String[] keys) {
        Map<String,Object> result = new HashMap<String,Object>();
        for(String key:keys){
            result.put(key, this.get(key));
        }
        return result;
    }

    @Override
    public boolean set(String key, Object value) {
        map.put(key, value);
        return true;
    }
    @Override
    public boolean set(String key, Object value, Date expdate) {
        map.put(key, value);
        return true;
    }

    @Override
    public boolean delete(String key) {
        map.remove(key);
        return true;
    }

    @Override
    public boolean add(String key, Object value) {
        this.map.put(key, value);
        return true;
    }
    @Override
    public boolean add(String key, Object value, Date expdate) {
        this.map.put(key, value);
        return true;
    }

    @Override
    public boolean append(String key, Object value) {
        map.put(key, value);
        return true;
    }

    @Override
    public CasValue<Object> gets(String key) {
        return new CasValue<Object>(map.get(key));
    }
    
    @Override
    public boolean cas(String key, CasValue<Object> casValue) {
        map.put(key, casValue.getValue());
        return true;
    }
    @Override
    public boolean cas(String key, CasValue<Object> casValue, Date expdate) {
        map.put(key, casValue.getValue());
        return true;
    }

    public int getMinSpareConnections() {
        return minSpareConnections;
    }
    public void setMinSpareConnections(int minSpareConnections) {
        this.minSpareConnections = minSpareConnections;
    }
    public int getMaxSpareConnections() {
        return maxSpareConnections;
    }
    public void setMaxSpareConnections(int maxSpareConnections) {
        this.maxSpareConnections = maxSpareConnections;
    }

}
