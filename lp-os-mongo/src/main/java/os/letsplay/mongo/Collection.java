package os.letsplay.mongo;

import java.util.List;
import java.util.Map;

import os.letsplay.bson.BSON;
import os.letsplay.bson.BsonId;
import os.letsplay.bson.BsonModel;
import os.letsplay.bson.annotations.BsonDocument;
import os.letsplay.mongo.ops.OpDelete;
import os.letsplay.mongo.ops.OpInsert;
import os.letsplay.mongo.ops.OpQuery;
import os.letsplay.mongo.ops.OpReply;
import os.letsplay.mongo.ops.OpUpdate;
import os.letsplay.mongo.ops.Result;



public class Collection<T> {
	
	private Database 	database;
	private String 		name;
	private Class<T>	clazz;
	
	public Database getDatabase() {
		return database;
	}
	
	public String getName() {
		return name;
	}
	
	public Collection( Database database, String name) {
		this.database 	= database;
		this.name 		= name;
	}
	
	public Collection( Database database, Class<T> clazz) {
		this.database 	= database;
		this.name 		= clazz.getSimpleName().toLowerCase();
		this.clazz 		= clazz;
		
		if(clazz.isAnnotationPresent(BsonDocument.class)){
			BsonDocument entity = clazz.getAnnotation(BsonDocument.class);
			this.name = entity.collection();
		}
	}
	
	public T get(Object query) throws Exception {
		Page<T> page = find(query, null, 1, 0, true);
		if(page.size()>0){
			return page.get(0);
		}else {
			return null;
		}
	}
	
	public Page<T> find() throws Exception {
		return find(null);
	}
	
	public Page<T> find(Object query) throws Exception {
		return find(query, null, -1, -1);
	}
		
	public Page<T> find(Object query, String fields, int limit, int skip) throws Exception{
		return find(query,fields,limit,skip,false);
	}
	
	public Page<T> find(Object query, String fields, int limit, int skip, boolean single) throws Exception{
		if(query!=null && query instanceof String){
			String queryString = (String)query;
			if(queryString.charAt(0)=='{'){
				query = Query.js((String)query);
			}else if(queryString.toLowerCase().matches("[a-f0-9]{24}")){
				query = Query.start("_id").is(new BsonId(queryString));
			}
		}else 
		if(	query!=null &&
			!(query instanceof byte[]) &&
			!IQuery.class.isAssignableFrom(query.getClass()) && 
			!Map.class.isAssignableFrom(query.getClass())
		){
			query = Query.start("_id").is(query);
		}
		if(query!=null && (query instanceof Query)){
			Query q = (Query)query;
			if(fields!=null){
				q.select(fields);
			}
			if(limit>-1){
				q.limit(limit);
			}else{
				limit = 100;
			}
			if(skip>-1){
				q.skip(skip);
			}else{
				skip = 0;
			}
		}
	    if(query!=null && IQuery.class.isAssignableFrom(query.getClass())){
			IQuery q = (IQuery)query;
			return findInternal(q.getQuery(), q.getFields(), q.getLimit(), q.getSkip(), single);
		}else{
			return findInternal(query, Query.start().select(fields).getFields(), limit, skip, single);
		}
	}
	
	@SuppressWarnings("unchecked")
	private Page<T> findInternal(Object query, Object fields, int limit, int skip, boolean single) throws Exception{
		int l = limit ==-1?100:limit;
		int s = skip  ==-1?0  :skip;
		
		OpQuery q = new OpQuery(name(), query, fields, l, s, 0);
    	OpReply r = database.getMongo().send(q);
    	if(query instanceof byte[]){
    		query = BSON.decode((byte[])query);
		}
    	if(fields instanceof byte[]){
    		fields = BSON.decode((byte[])query);
		}
    	List<T> data = (List<T>) r.getResults(clazz);
    	if(single || r.getCursorID()==0){
    		return new Page<T>(
    			(Map<String, Object>) query,
    			(Map<String, Integer>) fields,
    			new Integer(s),
    			new Integer(l),
    			new Integer(data.size()),
    			data
    		);
    	}else{
    		return new Page<T>(
    			(Map<String, Object>) query,
    			(Map<String, Integer>) fields,
    			new Integer(s),
    			new Integer(l),
    			count(query),
    			data
    		);
    	}
    }
    
    public Boolean insert(Object[] documents) throws Exception {
    	OpInsert q = new OpInsert(name(),documents);
    	OpReply  r = call(q);
    	return !r.getResult(Result.class).hasError();
    }
    
    public Integer count() throws Exception {
    	return count(null);
    }
    
	public Integer count(Object query) throws Exception {
		Query q = Query.start("count").is(name);
		if(query!=null){
			q.and("query").is(query);
		};
		Map<String,Object> r = database.command(q);
		return ((Double)r.get("n")).intValue();
	}
	
    public Integer update(Object query, Object document) throws Exception {
    	Integer count = count(query);
		if(count>=0){
	    	OpUpdate q = new OpUpdate(name(),query,document);
	    	OpReply  r = call(q);
	    	return r.getResult(Result.class).hasError()?0:count;
		}
		return 0;
    }
    
    public Integer delete(Object query) throws Exception {
    	if(IQuery.class.isAssignableFrom(query.getClass())){
    		query = ((IQuery)query).getQuery();
		}
    	Integer count = count(query);
		if(count>0){
			OpDelete q = new OpDelete(name(),query);
    		OpReply  r = call(q);
    		return r.getResult(Result.class).hasError()?0:count;
		}
    	return 0;
    }
    
    public Boolean save(Map<String,Object> document) throws Exception {
		if(!document.containsKey("_id")){
			document.put("_id",BsonId.get());
		}
		
		byte[] bytes = BSON.encode(document);
		
		Object query = Query.start("_id").is(document.get("_id")).getQuery();
		OpUpdate q  = new OpUpdate(name(),query,bytes,true,1);
    	OpReply  r  = call(q);
    	Result   rs = r.getResult(Result.class);
    	
    	return !rs.hasError();
    }
    
	public <F extends BsonModel> Boolean save(F document) throws Exception {
		
		if(document.id()==null){
			document.id(BsonId.get());
		}
		
		byte[] bytes = BSON.encode(document);
		
		
		Object query = Query.start("_id").is(document.id()).getQuery();
		OpUpdate q  = new OpUpdate(name(),query,bytes,true,1);
    	OpReply  r  = call(q);
    	Result   rs = r.getResult(Result.class);
    	
    	return !rs.hasError();		
    }
        
	private OpReply call(Message message) throws Exception {
		return database.getMongo().send(message);
	}
	
	private String name(){
		return database.getName()+"."+name;
	}
	
	public Boolean drop() throws Exception {
		Map<String,Object> r = database.command(Query.start("drop").is(name));
		return r.get("ok").equals(1.0);
	}
	
}
