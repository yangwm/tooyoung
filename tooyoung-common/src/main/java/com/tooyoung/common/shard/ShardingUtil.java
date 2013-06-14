/**
 * 
 */
package com.tooyoung.common.shard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sharding Util
 * 
 * <map>
 *  <entry key="0-29" value-ref="client_demo1"/>
 *  <entry key="30,31" value-ref="client_demo2"/>
 * </map>
 * after parseClients: 
 * <map>
 *  <entry key="0" value-ref="client_demo1"/>
 *  ...
 *  <entry key="29" value-ref="client_demo1"/>
 *  <entry key="30" value-ref="client_demo2"/>
 *  <entry key="31" value-ref="client_demo2"/>
 * </map> 
 * 
 * @author yangwm Mar 6, 2012 5:36:46 PM
 * @param <T>
 */
public class ShardingUtil<T> {

    private static final String KEY_SEPERATOR = ",";
    private static final String KEY_SUB_SEPERATOR = "-";
    
    /**
     * 解析db的hash id与client的对应关系, like: <entry key="0-31" value-ref="client_demo"/> 
     * 
     * @param clientsConfig
     * @return
     */
    public static<T> Map<Integer, T> parseClients(Map<String, T> clientsConfig){
        Map<Integer, T> shardingClients = new HashMap<Integer, T>();
        for(Map.Entry<String, T> entry : clientsConfig.entrySet()){
            List<Integer> dbIdxs = parseDbIdx(entry.getKey());
            T client = entry.getValue();
            for(Integer dbIdx : dbIdxs){
                shardingClients.put(dbIdx, client);
            }
        }
        return shardingClients;
    }
    
    /**
     * parse datasource hash ids, eg: 1,3-5,9-10 means the db with hash index of (1, 3,4,5,9,10). 
     * 
     * @param clientIdxStr
     * @return
     */
    private static List<Integer> parseDbIdx(String clientIdxStr){
        List<Integer> dbIdxs = new java.util.LinkedList<Integer>();
        String[] idsArr = clientIdxStr.split(KEY_SEPERATOR);
        for(String id : idsArr){
            id = id.trim();
            if(id.length() > 0){
                if(id.indexOf(KEY_SUB_SEPERATOR) < 0){                   
                    dbIdxs.add(Integer.parseInt(id));
                }else{
                    int startId = Integer.parseInt(id.substring(0, id.indexOf(KEY_SUB_SEPERATOR)));
                    int endId = Integer.parseInt(id.substring(id.indexOf(KEY_SUB_SEPERATOR) + 1));
                    for(int idx = startId; idx <= endId; idx++){
                        dbIdxs.add(idx);
                    }
                }
            }
        }
        return dbIdxs;
    }

}
