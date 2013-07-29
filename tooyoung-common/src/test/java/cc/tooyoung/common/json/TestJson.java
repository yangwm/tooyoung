/**
 * 
 */
package cc.tooyoung.common.json;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * 
 * @author yangwm Jul 19, 2013 12:21:20 AM
 */
public class TestJson {
    private int db;
    private String table;
    private long id;
    
    /*public TestJson() {
    }*/
    @JSONCreator
    public TestJson(@JSONField(name = "db") int db, @JSONField(name = "table") String table){
        this.db = db;
        this.table = table;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TestJson){
            TestJson dbTable = (TestJson)obj;
            return dbTable.db == this.db && dbTable.table == this.table;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "db/table=" + db + "/" + table;
    }

    public int getDb() {
        return db;
    }
    public String getTable() {
        return table;
    }
    public void setDb(int db) {
        this.db = db;
    }
    public void setTable(String table) {
        this.table = table;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    
}
