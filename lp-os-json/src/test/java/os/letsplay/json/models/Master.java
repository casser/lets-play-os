package os.letsplay.json.models;

import os.letsplay.utils.reflection.annotations.Property;

public class Master {
	
	@Property(index=1)
	private AnimalType type;
	public AnimalType type(){
		return type;
	}
	public Master type(AnimalType value){
		type = value; return this;
	}
	
	@Property(index=1)
	private Animal animal;
	public Animal animal(){
		return animal;
	}
	public Master animal(Animal value){
		animal = value; return this;
	}
	
	public static Class<?> classFor(Master master, String key){
		if(key.equals("animal")){
			switch (master.type()) {
				case CAT    :return Cat.class;
				case DOG    :return Dog.class;
				case PARROT :return Parrot.class;
			}
		}
		return null;
	}
	
}
