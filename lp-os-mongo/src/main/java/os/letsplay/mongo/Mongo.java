
package os.letsplay.mongo;

import java.util.concurrent.ConcurrentHashMap;

import os.letsplay.mongo.nio.MongoPool;

public class Mongo extends MongoPool {
	private static final String PREFIX = "mongodb://";
	
	public static final String DEFAULT_HOST = "127.0.0.1";
	public static final int    DEFAULT_PORT = 27017;
	
	private Database db;
	
	private String name;
	private String username;
	private String password;
	
	private Integer poolsize;
	
	public String getHost(){
		return host;
	}
	
	public Integer getPort(){
		return port;	
	}
	
	public String getName(){
		return name;	
	}
	public String getUsername(){
		return username;	
	}
	public String getPassword(){
		return password;	
	}
	
	public String getUrl(){
		return PREFIX+((username!=null&&password!=null)?username+":"+password:"")+host+port+"/"+name+"?poolsize="+poolsize;	
	}
	
	public Mongo(){
		this("mongodb://127.0.0.1:27017/test?poosize=20");
	}
	
	public Mongo(String url){
		
		if (!url.startsWith( PREFIX ) )
			throw new IllegalArgumentException( "uri needs to start with " + PREFIX );
		url = url.substring(PREFIX.length());
		
		String serverPart;
        String namePart;
        String optionsPart;

        int idx;
        idx = url.lastIndexOf( "/" );
        if ( idx < 0 ){
            serverPart 	= url;
            namePart 		= null;
            optionsPart = null;
        } else {
            serverPart = url.substring( 0 , idx );
            namePart = url.substring( idx + 1 );

            idx = namePart.indexOf( "?" );
            if ( idx >= 0 ){
                optionsPart = namePart.substring( idx + 1 );
                namePart = namePart.substring( 0 , idx );
            }
            else {
                optionsPart = null;
            }
        }
        
        idx = serverPart.indexOf( "@" );

        if ( idx > 0 ){
            String authPart = serverPart.substring( 0 , idx );
            serverPart = serverPart.substring( idx + 1 );
            idx = authPart.indexOf( ":" );
            
            this.username = authPart.substring( 0, idx );
            this.password = authPart.substring( idx + 1 );
        }
        else {
        	this.username = null;
        	this.password = null;
        }

        host = serverPart;
        if ( serverPart != null ){ // _database,_collection
            idx = serverPart.indexOf( ":" );
            if ( idx < 0 ){
                host = serverPart;
                port = DEFAULT_PORT;
            }
            else {
            	 host = serverPart.substring(0,idx);
                 port = Integer.parseInt(serverPart.substring( idx + 1 ));
            }
        }
        else {
        	 host = DEFAULT_HOST;
             port = DEFAULT_PORT;
        }
        
        if(optionsPart!=null){
        	for ( String _part : optionsPart.split( "&|;" ) ){
                idx = _part.indexOf( "=" );
                if ( idx >= 0 ){
                    String key = _part.substring( 0, idx ).toLowerCase();
                    if(key.equals("poolsize")){
                    	this.poolsize = Integer.parseInt(_part.substring( idx + 1 ));
                    }
                }
        	}
        }
        
        if(namePart!=null){
        	this.name = namePart;
        }else{
        	this.name = "test";
        }
        if(poolsize==null){
        	poolsize = 20;
        }
        
        connect(host, port, poolsize);
        db = getDB(name);
        
        if(username!=null&&password!=null){
        	db.authenticate(username, password);
        }
	}
	
	public Database getDB() {
		return getDB(name);
	}
	public Database getDB(String name) {
		return new Database(this, name);
	}
		
	private static class Instances extends ConcurrentHashMap<String, Mongo>{
		private static final long serialVersionUID = -6587988884365667045L;
		public Mongo add(Mongo mongo){
			return put(mongo.getName(), mongo);
		}
	}
	
	public static final Instances mongos = new Instances();
	
	public static void init(String ...urls){
		for(String url:urls){
			mongos.add(new Mongo(url));
		}
	}
	
	public static Database db(){
		return mongos.values().iterator().next().getDB();
	}
	
	public static Database db(String name){
		return mongos.get(name).getDB();
	}
	
}

