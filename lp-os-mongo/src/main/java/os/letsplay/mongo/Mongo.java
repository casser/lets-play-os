
package os.letsplay.mongo;

import os.letsplay.mongo.nio.MongoPool;

public class Mongo extends MongoPool {
	
	
	public String getHost(){
		return host;
	}
	
	public Integer getPort(){
		return port;	
	}
	
	public Mongo(){
		this("127.0.0.1",27017,20);
	}
	
	public Mongo( String host, Integer port){
		this(host,port,20);
	}
	
	public Mongo( String host, Integer port, Integer poolSize) {
		super(host,port,poolSize);
	}
	
	public Database getDB(String name) {
		return new Database(this, name);
	}
	
}

