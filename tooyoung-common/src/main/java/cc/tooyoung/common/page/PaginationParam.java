package cc.tooyoung.common.page;

/**
 * Pagination Param
 * 
 * @author yangwm Jun 15, 2012 12:53:24 AM
 */
public class PaginationParam {

	private long sinceId;
	private long maxId;
	private int count;
	private int page;
	
	public PaginationParam() {
	}
	public PaginationParam(long sinceId, long maxId, int count, int page){
		this.sinceId = sinceId;
		this.maxId = maxId;
		this.count = count;
		this.page = page;
	}
	
    public static class Builder {
        private long sinceId;
        private long maxId;
        private int count;
        private int page;
        
        public Builder sinceId(long sinceId) {
            this.sinceId = sinceId;
            return this;
        }
        public Builder maxId(long maxId) {
            this.maxId = maxId;
            return this;
        }
        public Builder count(int count) {
            this.count = count;
            return this;
        }
        public Builder page(int page) {
            this.page = page;
            return this;
        }
        
        public PaginationParam build() {
            return new PaginationParam(this);
        }
    }
    private PaginationParam(Builder builder) {
        this.sinceId = builder.sinceId;
        this.maxId = builder.maxId;
        this.count = builder.count;
        this.page = builder.page;
    }

	@Override
    public String toString() {
        return "PageParam [sinceId=" + sinceId + ", maxId=" + maxId + ", count=" + count + ", page=" + page + "]";
    }

    public long getSinceId() {
		return sinceId;
	}
	public long getMaxId() {
		return maxId;
	}
	public int getCount() {
		return count;
	}
	public int getPage() {
		return page;
	}
	
}
