package os.letsplay.mongo.models;

import os.letsplay.bson.annotations.BsonDocument;

@BsonDocument(collection="animals")
public class Dog extends Animal {
	@Override
	public AnimalType type() {
		return AnimalType.DOG;
	}
}
