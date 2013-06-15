/**
 * 
 */
package cc.tooyoung.memcacheproxy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import cc.tooyoung.common.cache.MemcacheClient;
import cc.tooyoung.common.util.ApiLogger;
import cc.tooyoung.memcache.vika.CasValue;

/**
 * <b> To get MemCacheTemplate from m-s memcache. </b>
 * 
 * <p> 
 * m is master and masterL1List.
 * s is slave and slaveL1List.
 * </p>
 * 
 * @author yangwm Oct 13, 2012 6:35:46 PM
 */
public class MemCacheTemplate<T> implements CacheAble<T> {
    
    /*
     * offset expire unit is minute : 1440 * 30 == 43200 (2592000s) 
     * unix time for expire : will > 1000000000000(2001-09-09) 
     */
    private static final int maxLowerExpire = 43200;
    private static final Date maxLowerExpireDate = new Date(1000L * 60 * maxLowerExpire);
    private static final Date maxUpperExpireDate = new Date(1000000000000L);
    
    /*
     * TODO 
     * 1. data format : java serialization, java byte buffer, kryo, protocal buffers, 
     * 2. key type: 
     * 3. balance  : master vs slave 
     * 4. evenly distributed expire : master and slave's expireTime, masterL1List and slaveL1List's expireTimeL1 
     *      example configure: expireTimeL1 = expireTime / 2 (keep L1 data hot, so L1 capacity = master/slave capacity / 2) 
     * 5. masterAsOneL1 : master as one L1  
     * 6. setIfExist or  : set master and slave, but set masterL1List and slaveL1List IfExist (keep L1 data hot) 
     * 
     */

    private MemcacheClient master;
    private List<MemcacheClient> masterL1List;

    private MemcacheClient slave;
    private List<MemcacheClient> slaveL1List;
    
    private String wirtePolicy = "writeAll";
    private boolean setbackMaster = true;
    private boolean masterAsOneL1 = true;
    
    private Date expireTime;
    private Date expireTimeL1;  //  use expireTimeL1 or expireTime, example configure: expireTimeL1 = less than expireTime / 2 
    
    /*
     * serialize/deserialize type
     * 0 is java serialize/deserialize 
     * 1 is pb serialize/deserialize
     * ... 
     */
    //private int codecType;
    
    @Override
    public T get(String key) {
        T value = null;
        
        /*
         * get 
         */
        MemcacheClient oneL1 = null;
        if (masterL1List != null && masterL1List.size() > 0) {
            oneL1 = chooseOneL1Client();
            if (oneL1 != null) {
                value = get(key, oneL1);
            }
        }
        if (value == null) {
            value = get(key, master);
        }
        if (value == null && slave != null) {
            value = get(key, slave);
            
            // set back for master 
            if (value != null && setbackMaster == true) {
                set(key, value, expireTime, master);
            }
        }

        /*
         * set back for L1 
         */
        if (value != null && oneL1 != null) {
            set(key, value, getExpireTimeL1(), oneL1);
        }
        return value;
    }
    @Override
    public Map<String, T> getMulti(String[] keys) {
        Map<String, T> values = new HashMap<String, T>();
        
        /*
         * multi get 
         */
        MemcacheClient oneL1 = null;
        if (masterL1List != null && masterL1List.size() > 0) {
            oneL1 = chooseOneL1Client();       
            if (oneL1 != null) {
                values = getMulti(keys, oneL1);
            }
        }
        String[] leftkeys = null;
        if(keys.length > values.size()) {
            leftkeys = getMulti(values, keys, master);
        }
        if (keys.length > values.size() && slave != null) {
            String[] leftkeysSlave = getMulti(values, keys, slave);
            
            // set back for master 
            if (leftkeysSlave != null && setbackMaster == true) {
                set(values, leftkeysSlave, expireTime, master);
            }
        }
        
        /*
         * set back for L1 
         */
        if (oneL1 != null && leftkeys != null) {
            set(values, leftkeys, getExpireTimeL1(), oneL1);
        }
        return values;
    }
    @SuppressWarnings("unchecked")
    private T get(String key, MemcacheClient mc) {
        return (T) mc.get(key);
    }
    @SuppressWarnings("unchecked")
    private Map<String, T> getMulti(String[] keys, MemcacheClient mc) {
        Map<String, T> values = (Map<String, T>) mc.getMulti(keys);
        for (String key : keys) {
            if (values.get(key) == null) {
                values.remove(key);
            }
        }
        return values;
    }
    @SuppressWarnings("unchecked")
    private String[] getMulti(Map<String, T> results, String[] keys, MemcacheClient mc) {
        /*
         * leftKeys 
         */
        if (keys.length - results.size() <= 0) {
            return null;
        }
        List<String> leftKeyList = new ArrayList<String>();
        for (String key : keys) {
            if (!results.containsKey(key)) {
                leftKeyList.add(key);
            }
        }
        if(leftKeyList.size() == 0){
            return null;
        }
        String[] leftKeys = leftKeyList.toArray(new String[0]);
        
        /*
         * multi get
         */
        Map<String, T> values = (Map<String, T>) mc.getMulti(leftKeys);
        if (values.size() <= 0) {
            return null;
        }
        
        /*
         * values merge to results 
         */
        for (String key : leftKeys) {
            T obj = values.get(key);
            if (obj != null) {
                results.put(key, obj);
            }
        }
        return leftKeys;
    }
    
    @Override
    public boolean set(String key, T value) {
        return set(key, value, expireTime);
    }
    @Override
    public boolean set(String key, T value, Date expdate) {
        if (expdate != null && expdate.getTime() > maxLowerExpireDate.getTime() && expdate.getTime() < maxUpperExpireDate.getTime()) {
            ApiLogger.warn("MemCacheTemplate set invalid expdate, expdate'minute:" + (expdate.getTime() / 1000 / 60) + ", so use maxLowerExpire:" + maxLowerExpire);
            expdate = maxLowerExpireDate;
        }
        
        boolean rs = set(key, value, expdate, master);
        if (rs == false) {
            return rs;
        }
        if (slave != null) {
            set(key, value, expdate, slave);
        }
        writeWithPolicy(key, value, expdate, masterL1List);
        writeWithPolicy(key, value, expdate, slaveL1List);
        return rs;
    }
    
    private boolean writeWithPolicy(String key, T value, Date expdate, List<MemcacheClient> mcList) {
        if ("writeAll".equals(wirtePolicy)) {
            set(key, value, expdate, mcList);
        } else if ("writeAndDeleteL1".equals(wirtePolicy)) {
            delete(key, mcList);
        } else if ("writeAndIfExistL1".equals(wirtePolicy)) {
            setIfExist(key, value, expdate, mcList);
        } else {
            set(key, value, expdate, mcList);
        }
        return true;
    }
    private boolean set(String key, T value, Date expdate, MemcacheClient mc) {
        boolean result = mc.set(key, value, expdate);
        if (result == false) {
            ApiLogger.warn("MemCacheTemplate set key:" + key + ", result:" + result);
        }
        return result;
    }
    private boolean set(String key, T value, Date expdate, List<MemcacheClient> mcList) {
        boolean result = true;
        if (mcList != null) {
            for (MemcacheClient client : mcList) {
                boolean success = set(key, value, expdate, client);
                if (success == false) {
                    result = false;
                }
            }
        }
        return result;
    }
    private boolean set(Map<String, T> values, String[] leftkeys, Date expdate, MemcacheClient mc) {
        for (String key : leftkeys) {
            T value = values.get(key);
            if (value == null) {
                continue;
            }
            set(key, value, expdate, mc);
        }
        return true;
    }
    private boolean setIfExist(String key, T value, Date expdate, List<MemcacheClient> mcList) {
        boolean result = true;
        if (mcList != null) {
            for (MemcacheClient client : mcList) {
                if (get(key, client) == null) { // not IfExist 
                    continue;
                }
                boolean success = set(key, value, expdate, client);
                if (success == false) {
                    result = false;
                }
            }
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public CasValue<T> getCas(String key) {
        CasValue<T> value = (CasValue<T>) master.gets(key);
        /* is bug, because cas to master(is null) will be false 
        if (value == null && slave != null) {
            value = (CasValue<T>) slave.gets(key);
        }*/
        return value;
    }
    
    @Override
    public boolean cas(String key, CasValue<T> casValue) {
        boolean result = cas(key, casValue, expireTime);
        if (result == false) {
            ApiLogger.warn("MemCacheTemplate cas key:" + key + ", result:" + result);
        }
        return result;
    }
    @Override
    public boolean cas(String key, CasValue<T> casValue, Date expdate) {
        if (expdate != null && expdate.getTime() > maxLowerExpireDate.getTime() && expdate.getTime() < maxUpperExpireDate.getTime()) {
            ApiLogger.warn("MemCacheTemplate cas invalid expdate, expdate'minute:" + (expdate.getTime() / 1000 / 60) + ", so use maxLowerExpire:" + maxLowerExpire);
            expdate = maxLowerExpireDate;
        }
        
        boolean rs = cas(key, casValue, expdate, master);
        if (rs == false) {
            return rs;
        }
        //除master外，其他应该是set; 因为master的cas unique key,不一定等于slave的cas unique key          
        if (slave != null) {
            set(key, casValue.getValue(), expdate, slave);
        }
        writeWithPolicy(key, casValue.getValue(), expdate, masterL1List);
        writeWithPolicy(key, casValue.getValue(), expdate, slaveL1List);
        return rs;
    }
    @SuppressWarnings("unchecked")
    private boolean cas(String key, CasValue<T> casValue, Date expdate, MemcacheClient mc) {
        return mc.cas(key, (CasValue<Object>)casValue, expdate);
    }
    
    @Override
    public boolean delete(String key) {
        boolean rs = delete(key, master);
        if (rs == false) {
            return rs;
        }
        if(slave != null){
            delete(key, slave);
        }
        delete(key, masterL1List);
        delete(key, slaveL1List);
        return rs;
    }
    private boolean delete(String key, List<MemcacheClient> mcList) {
        boolean result = true;
        if (mcList != null) {
            for (MemcacheClient client : mcList) {
                boolean success = client.delete(key);
                if (success == false) {
                    result = false;
                }
            }
        }
        return result;
    }
    private boolean delete(String key, MemcacheClient mc) {
        boolean result = mc.delete(key);
        if (result == false) {
            ApiLogger.warn("MemCacheTemplate delete key:" + key + ", result:" + result);
        }
        return result;
    }
    
    // --------------------------- inner -------------------------- 
    /**
     * 以len/(len + 1)的概率调用L1，剩余请求穿透
     * @return
     */
    AtomicInteger point = new AtomicInteger(0);
    private MemcacheClient chooseOneL1Client() {
        int v = point.incrementAndGet();
        if (v > 1000000000) {
            point.set(0);
        }
        
        // FIXME 不符合方法设计原则，需要重构，by daoru, 2011-10-21
        int len = masterL1List.size();
        if (masterAsOneL1 == true) {
            v = v % (len + 1);
        } else {
            v = v % len;
        }
        
        if (v >= len) {
            return null;
        }
        return masterL1List.get(v);
    }

    /**
     * expire unit is minute 
     * 
     * @param expire
     */
    public void setExpire(long expire) {
        this.expireTime = null;
        if (expire >= 0 && expire <= maxLowerExpire) {
            this.expireTime = new Date(1000L * 60 * expire);
        } else {
            ApiLogger.warn("MemCacheTemplate setExpire invalid, expire:" + expire + ", so use maxLowerExpire:" + maxLowerExpire);
            this.expireTime = (Date) maxLowerExpireDate.clone();
        }
    }
    public void setExpireL1(long expireL1) {
        this.expireTimeL1 = null;
        if (expireL1 >= 0 && expireL1 <= maxLowerExpire) {
            this.expireTimeL1 = new Date(1000L * 60 * expireL1);
        } else {
            ApiLogger.warn("MemCacheTemplate setExpireL1 invalid, expireL1:" + expireL1 + ", so use maxLowerExpire:" + maxLowerExpire);
            this.expireTimeL1 = (Date) maxLowerExpireDate.clone();
        }
    }
    
    public Date getExpireTime() {
        return expireTime;
    }
    public Date getExpireTimeL1() {
        if (expireTimeL1 != null) {
            return expireTimeL1;
        }
        return expireTime;
    }
    
    // getter setter
    
    public MemcacheClient getMaster() {
        return master;
    }
    public List<MemcacheClient> getMasterL1List() {
        return masterL1List;
    }
    public MemcacheClient getSlave() {
        return slave;
    }
    public List<MemcacheClient> getSlaveL1List() {
        return slaveL1List;
    }
    public boolean isSetbackMaster() {
        return setbackMaster;
    }
    public void setMaster(MemcacheClient master) {
        this.master = master;
    }
    public void setMasterL1List(List<MemcacheClient> masterL1List) {
    	// 为了提供更灵活的L1配置方式，对LIST中各个item进行判空
    	List<MemcacheClient> notNullClientList = new ArrayList<MemcacheClient>();
    	if (masterL1List != null) {
    		for (MemcacheClient mc : masterL1List) {
    			if (mc != null) {
    				notNullClientList.add(mc);
    			}
    		}
    	}
        this.masterL1List = notNullClientList;
    }
    public void setSlave(MemcacheClient slave) {
        this.slave = slave;
    }
    public void setSlaveL1List(List<MemcacheClient> slaveL1List) {
    	// 为了提供更灵活的L1配置方式，对LIST中各个item进行判空
    	List<MemcacheClient> notNullClientList = new ArrayList<MemcacheClient>();
    	if (slaveL1List != null) {
    		for (MemcacheClient mc : slaveL1List) {
    			if (mc != null) {
    				notNullClientList.add(mc);
    			}
    		}
    	}
        this.slaveL1List = notNullClientList;
    }
    public void setSetbackMaster(boolean setbackMaster) {
        this.setbackMaster = setbackMaster;
    }
    public boolean isMasterAsOneL1() {
        return masterAsOneL1;
    }
    public void setMasterAsOneL1(boolean masterAsOneL1) {
        this.masterAsOneL1 = masterAsOneL1;
    }
    public String getWirtePolicy() {
        return wirtePolicy;
    }
    public void setWirtePolicy(String wirtePolicy) {
        this.wirtePolicy = wirtePolicy;
    }

}
