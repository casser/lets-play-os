package os.letsplay.mongo;


import java.util.List;
import java.util.Map;

public class Page<T> {
	
	private Integer from;
	private Integer limit;
	private Integer count;
	private Map<String, Object>  query;
	private Map<String, Integer> fields;
	private List<T> 			 data;
	
	public Page(){
		
	}
	
	public Page(Map<String, Object> query, Map<String, Integer> fields, Integer from, Integer limit, Integer count, List<T> data){
		this.query 		= query;
		this.fields 	= fields;
		this.from 		= from;
		this.limit 		= limit;
		this.count 		= count;
		this.data 		= data;
	}
	
	public Integer getFrom() {
		return from;
	}
	
	public Integer getLimit() {
		return limit;
	}
	
	public Integer getCount() {
		return count;
	}
	
	public Map<String, Object> getQuery() {
		return query;
	}
	
	public Map<String, Integer> getFields() {
		return fields;
	}
	
	public List<T> getData() {
		return data;
	}
		
	public int size() {
		return (getData()!=null)?getData().size():0;
	}

	public T get(int index) {
		return getData().get(index);
	}

	
}
