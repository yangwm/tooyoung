/**
 * 
 */
package cc.tooyoung.common.page;

import java.util.Arrays;

import cc.tooyoung.common.json.JsonBuilder;
import cc.tooyoung.common.json.Jsonable;
import cc.tooyoung.common.util.ApiLogger;
import cc.tooyoung.common.util.ArrayUtil;


/**
 * Page Wrapper Util
 * 
 * from SortTask and AggregatorUtil 
 * @author yangwm Jun 13, 2012 4:13:35 PM
 */
public class PageWrapperUtil {
    
    private static int MAX_ID_ADJECT = -1;
    
    
    public static <T> String toJson(PageWrapper<T> pageWrapper, String name, long[] values) {
        JsonBuilder json = new JsonBuilder();
        if (values == null) {
            json.append(name, "[]");
        } else {
            json.appendStrArr(name, values);
        }
        appendJson(json, pageWrapper);
        
        json.flip();
        return json.toString();
    }
    
    public static <T> String toJson(PageWrapper<T> pageWrapper, String name, Jsonable[] values) {
        JsonBuilder json = new JsonBuilder();
        if (values == null) {
            json.append(name, "[]");
        } else {
            json.appendArr(name, values);
        }
        appendJson(json, pageWrapper);
        
        json.flip();
        return json.toString();
    }
    
    private static <T> JsonBuilder appendJson(JsonBuilder json, PageWrapper<T> pageWrapper) {
        json.append("previous_cursor", pageWrapper.previousCursor);
        json.append("next_cursor", pageWrapper.nextCursor);
        json.append("total_number", pageWrapper.totalNumber);
        return json;
    }
    
    /**
     * 
     * @param ids descending order with db pagination, so not need pagination
     * @return
     */
    public static PageWrapper<long[]> wrap(long[] ids, int count) {
        PageWrapper<long[]> wrapper = new PageWrapper<long[]>(0, 0, ids);
        wrapper.totalNumber = ids.length;
        
        if (ids.length > 1) {
            wrapper.nextCursor = ids[ids.length - 1] + MAX_ID_ADJECT;
        }
        
        wrapper.result = Arrays.copyOf(ids, Math.min(ids.length, count));
        return wrapper;
    }
    
    /**
     * 获取分页的offset
     * @param page
     * @param count
     * @return
     */
    public static int getPageOffset(int page, int count){
        if (page < 1) {
            page = 1;
        }
        if (count < 0) {
            count = 0;
        }
        
        int offset = (page - 1) * count;
        if(offset < 0){
            offset = 0;
        }
        return offset;
    }

    
    //  ---------- pagination by reverse ------------------------
    
    /**
     * 分页后结果为倒序, ids需要是升序排列的，不包含sinceId，包含maxId
     * 
     * @param ids
     * @param idsLen
     * @param sinceId
     * @param maxId
     * @param count
     * @param page
     * @return
     */
    public static PageWrapper<long[]> paginationAndReverse(long[] ids, PaginationParam paginationParam) {
        return paginationAndReverse(ids, ids.length, paginationParam.getSinceId(), paginationParam.getMaxId(), paginationParam.getCount(), paginationParam.getPage());
    }
    
    /**
     * 分页后结果为倒序, ids需要是升序排列的，不包含sinceId，包含maxId
     * 
     * @param ids
     * @param idsLen
     * @param sinceId
     * @param maxId
     * @param count
     * @param page
     * @return
     */
    public static PageWrapper<long[]> paginationAndReverse(long[] ids, int idsLen, long sinceId, long maxId, int count, int page){
        PageWrapper<long[]> wrapper = new PageWrapper<long[]>(0, 0, new long[0]);
        wrapper.totalNumber = idsLen;
        
        int[] pos = calculatePaginationByReverse(ids, idsLen, sinceId, maxId, count, page);
        if (pos == null) {
            return wrapper; 
        }
        int offset = pos[0];
        int limit = pos[1];
        if (limit <= 0) {
            return wrapper; 
        }
        
        //计算上一页下一页
        calculateCursorByReverse(wrapper, ids, offset, limit, count);
        
        //获取返回结果 
        wrapper.result = ArrayUtil.reverseCopyRange(ids, offset, offset + limit);
        return wrapper;
    }
    
    /**
     * 分页, ids需要是升序排列的，不包含sinceId，包含maxId
     * 
     * @param ids： id数组
     * @param sinceId：查询的起始id 
     * @param maxId：查询的最大id
     * @param count 每页数量
     * @param page：查询的页数
     * @return 数组, 0- offset, 1- limit count, 类似mysql分页参数
     */
    public static int[] calculatePaginationByReverse(long[] ids, int idsLen, long sinceId, long maxId, int count, int page) {
        // TODO, verify the algorithm
        int pos = idsLen;
        
        int startPos = 0;
        int endPos = pos;
        
        // src从小到大排序
        if (sinceId != 0) { // 比如src=101-110, since=102,max=108
            startPos = -1;
            for (int i = 0; i < idsLen; i++) {
                if (ids[i] > sinceId) {
                    startPos = i;
                    break;
                }
            }
            if (startPos == -1) { // not found?             
                //ApiLogger.warn("Illegal startPos, since/maxid: " + sinceId + "/" + maxId);
                return null;
            }
        }
        if (maxId != 0) {           
            for (int i = 0; i < idsLen; i++) {
                if (ids[i] > maxId) {
                    endPos = i;
                    break;
                }
            }
            if (endPos == 0) { // not found?
                ApiLogger.warn(new StringBuilder(64).append("Illegal maxId, since/maxid: ").append(sinceId).append("/").append(maxId).append(", for endPos=").append(0));
                return null;
            }
        }
        
        if (startPos >= endPos) {
            if(startPos != 0){
                ApiLogger.warn(new StringBuilder(64).append("Illegal startId & maxId since/maxid: ").append(sinceId).append("/").append(maxId).append(",start/end:").append(startPos).append("/").append(endPos));
            }           
            return null;
        }

        // 由于ids现在是从小到大排列，但是业务需要从大到小取，所以需要反着取，注意startPos计算。
        int trueCount = endPos - startPos;
        if (count > trueCount)
            count = trueCount;
        if (page > 0) {
            int pageCount = (trueCount - 1) / count + 1;// 页数
            if (page > pageCount)
                return null;
            
            // startPos = (page - 1) * count; // 正序取法
            // 反序，比如 endPos = 100, trueCount = 10, count = 2, page = 2, page = 5, startPos = 96 (page1: 98,99, P2: 96,97)
            int pageStartPos = endPos - page * count;
            if(pageStartPos < 0){
                //pageStartPos < 0, 说明起始点需要从0开始，而count也小于pageSize
                pageStartPos = 0;
                count = endPos - (page - 1) * count;
            }
            //如果取最后一页，则startPos不能从pageStartPos取，而要从第一个不小于sinceId的id位置取, fish 2010.2.24
            if(startPos < pageStartPos){
                startPos = pageStartPos;
            }else{
                count = count - (startPos - pageStartPos);
            }           
            //count = endPos - (page - 1) * count;
            // endPos = startPos + count;
        } else{
            startPos = endPos - count;
            if(startPos < 0){
                startPos = 0;
                count = endPos;
            }
        }
        
        return new int[]{startPos, count};
    }
    
    public static <T> void calculateCursorByReverse(PageWrapper<T> wrapper, long[] ids, int offset, int limit, int count) {
        calculateCursorByReverse(wrapper, ids, offset, limit, count, MAX_ID_ADJECT);
    }
    /**
     * 提供外部直接调用的计算游标位置（ids需要是升序排列的） 
     * 
     * @param offset
     * @param limit
     * @param count
     * @param ids
     * @param wrapper
     */
    private static <T> void calculateCursorByReverse(PageWrapper<T> wrapper, long[] ids, int offset, int limit, int count, int adject) {
        //ApiLogger.debug("offset:" + offset + ", limit:" + limit + ", :" + Arrays.toString(ids));
        int arrLen = ids.length;
        if (arrLen < 1) {
            return ;
        }
        
        // 计算下一页起始cursor
        int pos = offset - 1;
        if (pos >= 0) {
            wrapper.nextCursor = ids[pos];
        } else if (offset == 0) { // 修正offset==0时nextCursor计算    
            wrapper.nextCursor = ids[offset] + adject;
        } else {
            wrapper.nextCursor = 0;
        }
        //计算上一页起始cursor
        pos = pos + limit + count;
        if (pos <= arrLen - 1) {
            wrapper.previousCursor = ids[pos];
        } else {
            wrapper.previousCursor = 0;
        }
    }

}
