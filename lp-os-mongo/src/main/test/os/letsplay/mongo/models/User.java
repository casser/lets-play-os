package os.letsplay.mongo.models;

import os.letsplay.bson.BsonId;
import os.letsplay.bson.annotations.BsonDocument;


@BsonDocument(collection="users")
public class User {
	
	private BsonId 		id;
	private String 		name;
	

	public BsonId id() {
		return id;
	}
	
	public User id(BsonId value) {
		this.id=value; return this;
	}
	
	public String name() {
		return name;
	}
	
	public User name(String value) {
		this.name=value;return this;
	}
	
}
