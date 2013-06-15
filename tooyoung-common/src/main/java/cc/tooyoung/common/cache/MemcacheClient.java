package cc.tooyoung.common.cache;

import java.util.Date;
import java.util.Map;

import cc.tooyoung.memcache.vika.CasValue;


/**
 * 
 * 
 * @author modify by yangwm Jun 15, 2013 12:28:02 AM
 */
public interface MemcacheClient {
    
	public void setServerPort(String serverPort);
	public void setServerPortList(String[] serverPort);
	   
    public Object get(String key);
    public Map<String, Object> getMulti(String[] keys);
    
	public boolean set(String key, Object value);
	public boolean set(String key, Object value, Date expdate);

    public boolean delete(String key);

    /**
     * Adds data to the server; only the key and the value are specified.
     *
     * @param key key to store data under
     * @param value value to store
     * @return true, if the data was successfully stored
     */
    public boolean add(String key, Object value);
    public boolean add(String key, Object value, Date expdate);
    
    public boolean append(String key, Object value);

    public CasValue<Object> gets(String key);
    
	public boolean cas(String key, CasValue<Object> value);
	public boolean cas(String key, CasValue<Object> value, Date expdate);

}
