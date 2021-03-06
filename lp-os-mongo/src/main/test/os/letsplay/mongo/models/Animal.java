package os.letsplay.mongo.models;

import java.util.Map;

import os.letsplay.bson.BsonId;
import os.letsplay.bson.annotations.BsonDocument;
import os.letsplay.utils.reflection.annotations.Abstract;
import os.letsplay.utils.reflection.annotations.Property;

@Abstract(required={"type"})
@BsonDocument(collection="animals")
abstract public class Animal {
	
	@Property(index=1)
	private BsonId id;
	public  BsonId id(){
		return id;
	}
	public  Animal id(BsonId value){
		id = value; return this;
	}
	
	@Property(index=2)
	private String name;
	public  String name(){
		return name;
	}
	public  Animal name(String value){
		name = value; return this;
	}
	
	@Property(index=3)
	private AnimalType type;
	public  AnimalType type(){
		return type;
	}
	public  Animal     type(AnimalType value){
		type = value; return this;
	}
	
	public static Class<?> classFor(Map<String,Object> props){
		AnimalType type = (AnimalType)props.get("type");
		switch (type) {
			case PARROT	:return Parrot.class;
			case DOG	:return Dog.class;
			case CAT	:return Cat.class;
		}
		return null;
	}
	
}
