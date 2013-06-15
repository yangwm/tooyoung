/**
 * 
 */
package cc.tooyoung.common.shard;

import org.junit.Assert;
import org.junit.Test;

import cc.tooyoung.common.shard.impl.ShardingSupportHash;
import cc.tooyoung.common.util.ApiLogger;

/**
 * 1821155363
 * 
 * @author yangwm Jan 24, 2013 11:24:03 AM
 */
public class ShardingSupportHashTest {

    @Test
    public void testGetDbTableUuid() {
        ShardingSupport<Object> shardingSupport = getShardingSupport();
        ApiLogger.debug("testGetDbTableUuid shardingSupport:" + shardingSupport);
        
        long[] ids = new long[] { 3217326138458167L, 3342818919841793L, 3533177904537736L, 3557855061995026L };
        DbTable[] dbTables = new DbTable[] { new DbTable(3, "18"), new DbTable(1, "10"), new DbTable(6, "11"), new DbTable(0, "30") };
        for (int i = 0; i < ids.length; i++) {
            long id = ids[i];
            DbTable dbTable = shardingSupport.getDbTable(id);
            ApiLogger.debug("testGetDbTableUuid id:" + id + ", dbTable:" + dbTable);
            Assert.assertEquals(dbTables[i].toString(), dbTable.toString());
        }
    }

    @Test
    public void testGetDbTableUid() {
        ShardingSupport<Object> shardingSupport = getShardingSupport("crc32", 1024, 64, "new");
        ApiLogger.debug("testGetDbTableUid shardingSupport:" + shardingSupport);
        
        long[] ids = new long[] { 1750715731L, 1821155363L, 1779195673L, 1734528095L, 10503L };
        DbTable[] dbTables = new DbTable[] { new DbTable(15, "59"), new DbTable(13, "22"), new DbTable(11, "50"), new DbTable(0, "62"), new DbTable(14, "59") };
        for (int i = 0; i < ids.length; i++) {
            long id = ids[i];
            DbTable dbTable = shardingSupport.getDbTable(id);
            ApiLogger.debug("testGetDbTableUid id:" + id + ", dbTable:" + dbTable);
            Assert.assertEquals(dbTables[i].toString(), dbTable.toString());
        }
    }
    
    @Test
    public void testGetDbTableUidOld() {
        ShardingSupport<Object> shardingSupport = getShardingSupport("crc32", 1024, 64, "old");
        ApiLogger.debug("testGetDbTableUidOld shardingSupport:" + shardingSupport);
        
        long[] ids = new long[] { 1750715731L, 1821155363L, 1779195673L, 1734528095L, 10503L };
        DbTable[] dbTables = new DbTable[] { new DbTable(2, "15"), new DbTable(6, "27"), new DbTable(3, "34"), new DbTable(0, "11"), new DbTable(8, "20") };
        for (int i = 0; i < ids.length; i++) {
            long id = ids[i];
            DbTable dbTable = shardingSupport.getDbTable(id);
            ApiLogger.debug("testGetDbTableUidOld id:" + id + ", dbTable:" + dbTable);
            Assert.assertEquals(dbTables[i].toString(), dbTable.toString());
        }
    }

    
    private ShardingSupport<Object> getShardingSupport() {
        return getShardingSupport("crc32", 256, 32, "new");
    }
    private ShardingSupport<Object> getShardingSupport(String hashAlg, int hashGene, int tablePerDb, String noneHash) {
        ShardingSupportHash<Object> shardingSupport = new ShardingSupportHash<Object>();
        //shardingSupport.setClients(clients);
        shardingSupport.setHashAlg(hashAlg);
        shardingSupport.setHashGene(hashGene);
        shardingSupport.setTablePerDb(tablePerDb);
        shardingSupport.setNoneHash(noneHash);
        return shardingSupport;
    }
    
}
