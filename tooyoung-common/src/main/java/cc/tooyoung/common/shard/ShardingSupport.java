/**
 * 
 */
package cc.tooyoung.common.shard;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Dao Sharding Support inteface
 * 
 * @author yangwm Jan 6, 2013 9:01:23 PM
 */
public interface ShardingSupport<T> {

    /**
     * get client by id 
     * 
     * @param id
     * @return client
     */
    T getClient(long id);
    T getClientByDb(Integer db);
    
    /**
     * 
     */
    DbTable getDbTable(long id);
    DbTable getDbTable(long id, Date date);
    
    /**
     * key: dbIndex
     * value: splitIds 
     */
    Map<Integer, List<Long>> getDbSharding(long[] ids);

    // ----------------- will move -------------------
    
    int DEFAULT_HASH_GENE = 256;
    int DEFAULT_TBL_PER_DB = 8;
    
    String TIME_UNIT_YEAR = "year";
    String TIME_UNIT_MONTH = "month";
    String TIME_UNIT_DAY = "day";
    ThreadLocal<SimpleDateFormat> DATE_FORMAT_YEAR = new ThreadLocal<SimpleDateFormat>(){
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy");
        }
    };
    ThreadLocal<SimpleDateFormat> DATE_FORMAT_MONTH = new ThreadLocal<SimpleDateFormat>(){
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy_MM");
        }
    };
    ThreadLocal<SimpleDateFormat> DATE_FORMAT_DAY = new ThreadLocal<SimpleDateFormat>(){
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy_MM_dd");
        }
    };
    
}
