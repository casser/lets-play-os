package os.letsplay.mongo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import os.letsplay.bson.BSON;
import os.letsplay.json.JSON;
import os.letsplay.json.JsonParseError;





public class Query implements IQuery{
	public static class BasicDBObject extends LinkedHashMap<String, Object>{
		private static final long serialVersionUID = 346444651887422782L;
		public BasicDBObject(){
			super();
		}
		public BasicDBObject(String key,Object value){
			super();
			this.put(key, value);
		}
	}
	public static enum Operator{
		GT("$gt"),		GTE("$gte"),
		LT("$lt"),		LTE("$lte"),	NE("$ne"),
		IN("$in"),		NIN("$nin"),	EXISTS("$exists"),
		MOD("$mod"),	ALL("$all"),	SIZE("$size"),
		NEAR("$near"),	WHERE("$where"), WITHIN("$within");
		private String value;
		Operator(String value){
			this.value = value;
		}
		String value(){
			return value;
		}
	}
	
	public static enum Order{
		ASCENDING(1),DESCENDING(-1);
		private int value;
		Order(int value){
			this.value = value;
		}
		int value(){
			return value;
		}
	}
	
	public static Query start() {
        return new Query();
    }
	
	
    public static Query start(String key) {
        return (Query) (new Query()).put(key);
    }
    
    public static Query js(String query) {
        try {
			return new Query((BasicDBObject)JSON.decode(query,BasicDBObject.class));
		} catch (JsonParseError e) {
			return new Query();
		}
    }
    
    public Query() {
    	this(new BasicDBObject());
    }
    
    public Query(BasicDBObject query) {
        _query 	= query;
        _fields = new BasicDBObject();
        _sort 	= new BasicDBObject();
        _limit 	= 0;
        _skip  	= 0;
    }

	
	/**
     * Creates a <code>DBObject</code> query to be used for the driver's find operations
     * @return Returns a DBObject query instance
     */
    public Object getQuery() {
        for(String key : _query.keySet()) {
            if(_query.get(key) instanceof NullObject) {
                return null;
            }
        }
        if(_sort.size()>0){
        	Map<String,Object> qu = new BasicDBObject();
        	qu.put("query", _query);
        	qu.put("orderby", _sort);
        	return qu;
        }
        return _query;
    }
    
    /**
     * Creates a <code>DBObject</code> fields selection to be used for the driver's select fields
     * @return Returns a DBObject fields instance
     */
	public Object getFields(){
		if(_fields.size()>0) {
			return _fields;	
		}
		return null;
	}
		
	/**
     * Creates a limit for query results 
     * @return Returns a limit of result or 0 if not specified 
     */
	public int getSkip(){
		return _skip;
	}
	
	/**
     * Creates a limit for query results 
     * @return Returns a limit of result or -1 if not specified 
     */
	public int getLimit(){
		return _limit;
	}
	
	/**
	 * Skip results for specified offset
	 * @param offset items to skip
	 * @return Returns the current Query
	 */
	public Query skip(int offset){
		_skip = offset;
		return this;
	}
	
	/**
	 * Limit results for specified offset
	 * @param offset items to limit
	 * @return Returns the current Query
	 */
	public Query limit(int offset){
		_limit = offset;
		return this;
	}
	
	/**
	 * Skip results for specified offset and limit it for limit offset
	 * @param skip items to skip
	 * @param limit items to limit
	 * @return Returns the current Query
	 */
	public Query limit(int skip,int limit){
		skip(skip);
		limit(limit);
		return this;
	}
	
	/**
     * Adds a sort key by ASCENDING order
     * @param key MongoDB document field
     * @param order Order type
     * @return Returns the current Query
     */
	public Query sort(String key){
		_sort.put(key, Order.ASCENDING);
		return this;
	}
	
	/**
     * Adds a sort key
     * @param key MongoDB document field
     * @param order Order type
     * @return Returns the current Query
     */
	public Query sort(String key, Order order){
		_sort.put(key, order.value);
		return this;
	}
	
	/**
     * Adds a natural sort order
     * @param order Order type
     * @return Returns the current Query
     */
	public Query sort(Order order){
		_sort.put("$natural", order.value);
		return this;
	}
	
	/**
     * Adds a field to selection list 
     * @param field MongoDB document field
     * @return Returns the current Query
     */
	public Query select(String field){
		if(field!=null){
			field = field.replaceAll(" ", "");
			if(field.indexOf(',')>0){
				String[] fields = field.split(",");
				for (int i = 0; i < fields.length; i++) {
					if(fields[i].charAt(0)=='!'){
						_fields.put(fields[i].substring(1), 0);
					}else{
						_fields.put(fields[i], 1);
					}
				}
			}else{
				_fields.put(field, 1);
			}
		}
		return this;
	}
	
	
    /**
     * Adds a new key to the query if not present yet.
     * Sets this key as the current key.
     * @param key MongoDB document key
     * @return Returns the current Query
     */
    public Query put(String key) {
        _currentKey = key;
        if(_query.get(key) == null) {
            _query.put(_currentKey, new NullObject());
        }
        return this;
    }
	
    /**
     * Equivalent to <code>Query.put(key)</code>. Intended for compound query chains to be more readable
     * Example: Query.start("a").greaterThan(1).and("b").lessThan(3)
     * @param key MongoDB document key
     * @return Returns the current Query with an appended key operand
     */
    public Query and(String key) {
        return put(key);
    }
	
    /**
     * Equivalent to the $gt operator
     * @param object Value to query
     * @return Returns the current Query with an appended "greater than" query  
     */
    public Query greaterThan(Object object) {
        addOperand(Operator.GT, object);
        return this;
    }
	
    /**
     * Equivalent to the $gte operator
     * @param object Value to query
     * @return Returns the current Query with an appended "greater than or equals" query
     */
    public Query greaterThanEquals(Object object) {
        addOperand(Operator.GTE, object);
        return this;
    }
	
    /**
     * Equivalent to the $lt operand
     * @param object Value to query
     * @return Returns the current Query with an appended "less than" query
     */
    public Query lessThan(Object object) {
        addOperand(Operator.LT, object);
        return this;
    }
	
    /**
     * Equivalent to the $lte operand
     * @param object Value to query
     * @return Returns the current Query with an appended "less than or equals" query
     */
    public Query lessThanEquals(Object object) {
        addOperand(Operator.LTE, object);
        return this;
    }
	
    /**
     * Equivalent of the find({key:value})
     * @param object Value to query
     * @return Returns the current Query with an appended equality query
     */
    public Query is(Object object) {
        addOperand(null, object);
        return this;
    }
    
    /**
     * Equivalent of the find({})
     * @return Returns the current Query with an appended equality query
     */
    public Query empty() {
		return is(new HashMap<String, Object>());
	}
    
    /**
     * Equivalent of the $ne operand
     * @param object Value to query
     * @return Returns the current Query with an appended inequality query
     */
    public Query notEquals(Object object) {
        addOperand(Operator.NE, object);
        return this;
    }
	
    /**
     * Equivalent of the $in operand
     * @param object Value to query
     * @return Returns the current Query with an appended "in array" query
     */
    public Query in(Object object) {
        addOperand(Operator.IN, object);
        return this;
    }
	
    /**
     * Equivalent of the $nin operand
     * @param object Value to query
     * @return Returns the current Query with an appended "not in array" query
     */
    public Query notIn(Object object) {
        addOperand(Operator.NIN, object);
        return this;
    }
	
    /**
     * Equivalent of the $mod operand
     * @param object Value to query
     * @return Returns the current Query with an appended modulo query
     */
    public Query mod(Object object) {
        addOperand(Operator.MOD, object);
        return this;
    }
	
    /**
     * Equivalent of the $all operand
     * @param object Value to query
     * @return Returns the current Query with an appended "matches all array contents" query
     */
    public Query all(Object object) {
        addOperand(Operator.ALL, object);
        return this;
    }
	
    /**
     * Equivalent of the $size operand
     * @param object Value to query
     * @return Returns the current Query with an appended size operator
     */
    public Query size(Object object) {
        addOperand(Operator.SIZE, object);
        return this;
    }
	
    /**
     * Equivalent of the $exists operand
     * @param object Value to query
     * @return Returns the current Query with an appended exists operator
     */
    public Query exists(Object object) {
        addOperand(Operator.EXISTS, object);
        return this;
    }
	
    /**
     * Passes a regular expression for a query
     * @param regex Regex pattern object
     * @return Returns the current Query with an appended regex query
     */
    public Query regex(Pattern regex) {
        addOperand(null, regex);
        return this;
    }
	
    /**
     * Equivalent of the $within operand, used for geospatial operation
     * @param x x coordinate
     * @param y y coordinate
     * @param radius radius
     * @return
     */
    public Query withinCenter( double x , double y , double radius ){
        addOperand(Operator.WITHIN, new BasicDBObject( "$center" , new Object[]{ new Double[]{ x , y } , radius } ) );
        return this;
    }
	
    /**
     * Equivalent of the $near operand
     * @param x x coordinate
     * @param y y coordinate
     * @return
     */
    public Query near( double x , double y  ){
        addOperand(Operator.NEAR , new Double[]{ x , y } );
        return this;
    }

    /**
     * Equivalent of the $near operand
     * @param x x coordinate
     * @param y y coordinate
     * @param maxDistance max distance
     * @return
     */
    public Query near( double x , double y , double maxDistance ){
        addOperand(Operator.NEAR , new Double[]{ x , y , maxDistance } );
        return this;
    }
    
    /**
     * Equivalent to a $within operand, based on a bounding box using represented by two corners
     * 
     * @param x the x coordinate of the first box corner.
     * @param y the y coordinate of the first box corner.
     * @param x2 the x coordinate of the second box corner.
     * @param y2 the y coordinate of the second box corner.
     * @return
     */
    public Query withinBox(double x, double y, double x2, double y2) {
    	addOperand(Operator.WITHIN, new BasicDBObject( "$box" , new Object[] { new Double[] { x, y }, new Double[] { x2, y2 } } ) );
    	return this;
    }


    /**
     * Equivalent to a $or operand
     * @param ors
     * @return
     */
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Query or( Object ... ors ){
        List l = (List<Object>)_query.get( "$or" );
        if ( l == null ){
            l = new ArrayList<Object>();
            _query.put( "$or" , l );
        }
        for ( Object o : ors )
            l.add( o );
        return this;
    }

    /**
     * Equivalent to an $and operand
     * @param ands
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Query and( Object ... ands ){
        List l = (List<Object>)_query.get( "$and" );
        if ( l == null ){
            l = new ArrayList<Object>();
            _query.put( "$and" , l );
        }
        for ( Object o : ands )
            l.add( o );
        return this;
    }
    
    private void addOperand(Operator op, Object value) {
        if(op == null) {
            _query.put(_currentKey, value);
            return;
        }
        Object storedValue = _query.get(_currentKey);
        BasicDBObject operand;
        if(!(storedValue instanceof Map)) {
            operand = new BasicDBObject();
            _query.put(_currentKey, operand);
        } else {
            operand = (BasicDBObject)_query.get(_currentKey);
        }
        operand.put(op.value(), value);
    }
	
    private static class NullObject {}
	
    private Map<String,Object> _query;
    private Map<String,Object> _sort;
    private Map<String,Object> _fields;
    
    private int _skip;
    private int _limit;
    private String _currentKey;
	    
    @Override
    public boolean equals(Object obj) {
    	return (obj instanceof Query) && (obj.hashCode() == this.hashCode());
    }
    
    public Map<String, Object> toMap() {
		HashMap<String, Object> query = new HashMap<String, Object>();
    	if(getQuery()!=null){
    		query.put("query",getQuery());
    	}
    	if(getFields()!=null){
    		query.put("fields",getFields());
    	}
    	if(_limit>0 || _skip>0){
    		query.put("skip",_skip);
    		query.put("limit",_limit);
    	}
		return query;
	}
    
    public byte[] toBson() {
    	return BSON.encode(toMap());
    }
    
    public String toString(){
    	return JSON.encode(toMap());
    }
}
