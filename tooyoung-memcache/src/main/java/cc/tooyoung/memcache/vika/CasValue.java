package cc.tooyoung.memcache.vika;

public class CasValue<T> {

	protected T value;
	protected long casUnique;
	protected boolean hasCasUnique = false;
	
	public CasValue(){}
	
	public CasValue(T value, long casUnique){
		this.value = value;
		this.casUnique = casUnique;
		this.hasCasUnique = true;
	}
	
	public CasValue(T value){
		this.value = value;
	}
		
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	public long getCasUnique() {
		return casUnique;
	}

	public void setCasUnique(long casUnique) {
		this.casUnique = casUnique;
		this.hasCasUnique = true;
	}
	
	public void resetCas(){
		this.casUnique = 0;
		this.hasCasUnique = false;
	}

	public boolean hasCasUnique() {
		return hasCasUnique;
	}
	
	public boolean equals(Object o) {
		if (! (o instanceof CasValue)) {
			return false;
		}
		CasValue v = (CasValue) o;
		if(this.hasCasUnique != v.hasCasUnique) {
			return false;
		}
		
		if(this.hasCasUnique && this.casUnique != v.casUnique) {
			return false;
		}
		
		return this.value.equals(v.value);
	}
	
	public int hashCode() {
		int result = 17;
		result = 37 * result + (int) casUnique;
		result = 37 * result + (hasCasUnique ? 1 : 0);
		result = 37 * result + value.hashCode();
				
		return result;
	}
	
}
