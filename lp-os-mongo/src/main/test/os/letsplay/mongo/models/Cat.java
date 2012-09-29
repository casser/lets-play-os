package os.letsplay.mongo.models;

import os.letsplay.bson.annotations.BsonDocument;

@BsonDocument(collection="animals")
public class Cat extends Animal {
	@Override
	public AnimalType type() {
		return AnimalType.CAT;
	}
}
