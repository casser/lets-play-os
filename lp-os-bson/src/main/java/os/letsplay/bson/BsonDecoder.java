package os.letsplay.bson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import os.letsplay.bson.annotations.BsonDocument;
import os.letsplay.bson.binary.Binary;
import os.letsplay.utils.StringUtils;
import os.letsplay.utils.reflection.Definition;
import os.letsplay.utils.reflection.Definitions;
import os.letsplay.utils.reflection.DefinitionProperty;
import os.letsplay.utils.reflection.definitions.ArrayDefinition;
import os.letsplay.utils.reflection.definitions.BeanDefinition;
import os.letsplay.utils.reflection.definitions.EnumDefinition;
import os.letsplay.utils.reflection.definitions.InternalDefinition;
import os.letsplay.utils.reflection.definitions.MapDefinition;
import os.letsplay.utils.reflection.definitions.SimpleDefinition;
import os.letsplay.utils.reflection.exceptions.ReflectionException;


public class BsonDecoder {
	
	BsonByteArray bson;
	
	private byte token;
	
	public BsonDecoder(){
		bson = new BsonByteArray();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T decode( byte[] document) throws BsonParseError{
		return (T) decode(document,null);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T decode( byte[] document, Class<T> type) throws BsonParseError {
		try{
			bson.writeBytes(document);
			bson.position(0);
			token = BSON.DOCUMENT;
			return (T) serialize(type);
		}catch(Exception ex){
			throw new BsonParseError(ex.getMessage(),ex);
		}
	}
	
	private synchronized Object serialize(Class<?> clazz) throws  ReflectionException, BsonParseError {
		Definition 	definition;
		Object		target;
		if(clazz==null){
			clazz = Object.class;
		}
		definition = Definitions.get(clazz);
		switch(definition.type()){
			case SIMPLE	:
				target = serializeSimple(definition);
			break;
			case ENUM	:
				target = serializeEnum(definition);
			break;
			case ARRAY	:
				target = serializeArray(definition);
			break;
			case MAP	:
				target = serializeMap(definition);
			break;
			case BEAN	:
				target = serializeBean(definition);
			break;
			case INTERNAL	:
				target = serializeInternal(definition);
			break;
			default:
				throw new BsonParseError("Invalid Definition Type <"+definition.type()+">");
		}
		return target;
	}
	
	private Object readValue() throws BsonParseError{
		switch (token) {
			case BSON.TERMINATOR:
				return bson.readCString();
			case BSON.NULL		:
				return null;
			case BSON.OBJECTID	:
				return new BsonId(bson.readBytes(12));
			case BSON.STRING	:
				return bson.readString();
			case BSON.INT32		:
				return bson.readInt();
			case BSON.TIMESTAMP	:
			case BSON.INT64		:
				return bson.readLong();
			case BSON.DOUBLE	:
				return bson.readDouble();
			case BSON.BOOLEAN	:
				return bson.readBoolean();
			case BSON.BINARY	:
				return new Binary(bson.readBinary());
			case BSON.UTC		:
				return bson.readDate();
			
			case BSON.JS		:
			case BSON.MAX_KEY	:
			case BSON.MIN_KEY	:
			case BSON.REGEXP	:
			case BSON.SYMBOL	:
				throw new BsonParseError("Type Not Supported Yet <"+token+">");
			default:
				throw new BsonParseError("Not Simple Value <"+token+">");
		}
	}
	
	private Object serializeInternal(Definition def) throws  ReflectionException, BsonParseError{
		InternalDefinition definition = (InternalDefinition) def;
		if(definition.clazz()!=Object.class){
			return null;
		}
		switch (token) {
			case BSON.DOCUMENT:
				return serialize(Map.class);
			case BSON.ARRAY:
				return serialize(List.class);
			default:
				return readValue();
		}
	}
	
	private Object serializeEnum(Definition def) throws BsonParseError{
		EnumDefinition definition = (EnumDefinition) def;
		return definition.newInstance(readValue());
	}
	
	private Object serializeSimple(Definition def) throws  ReflectionException, BsonParseError{
		SimpleDefinition definition = (SimpleDefinition) def;
		return definition.newInstance(readValue());
	}
	
	@SuppressWarnings("unused")
	private Object serializeArray(Definition def) throws  ReflectionException, BsonParseError{
		ArrayDefinition definition 	= (ArrayDefinition) def;
		Object target 				= definition.newInstance();
		Boolean exit = false;
		int size			= bson.readInt();
		do {
			token        	= bson.readByte();
			Integer key  	= Integer.parseInt(bson.readCString());
			Object value   	= serialize(definition.valueClazz());
			byte terminate	= bson.readByte();
			definition.add(target, value);
			if(terminate==BSON.TERMINATOR){
				exit = true;
			}else{
				bson.backward();
			}
		}while (!exit);
		return target;
	}
	
	@SuppressWarnings("unused")
	private Object serializeMap(Definition def) throws  ReflectionException, BsonParseError{
		MapDefinition definition 	= (MapDefinition) def;
		Map<Object, Object> target 	= definition.newInstance();
		Boolean exit = false;
		int size			= bson.readInt();
		do {
			byte token      = bson.readByte();
			this.token		= BSON.TERMINATOR;
			Object key  	= serialize(definition.keyClazz());
			this.token		= token;
			Object value   	= serialize(definition.valueClazz());
			target.put(key, value);
			if(bson.readByte()==BSON.TERMINATOR){
				exit = true;
			}else{
				bson.backward();
			}
		}while (!exit);
		return target;
	}
	
	@SuppressWarnings("unused")
	private Object serializeBean(Definition def) throws  ReflectionException, BsonParseError{
		BeanDefinition definition 	= (BeanDefinition) def;
		
		
		if(definition.isAbstract()){
			int lPos = bson.position();
			HashMap<String, Object> map = new HashMap<String, Object>();
			List<String> props 	= definition.getAbstractionProperties();
			
			Boolean exit 		= false;
			int size			= bson.readInt();
			do {
				token      			= bson.readByte();
				String key  		= bson.readCString();
				if(key.equals("_id") && definition.clazz().isAnnotationPresent(BsonDocument.class)){
					key = "id";
				}
				DefinitionProperty property	= definition.properties().get(key);
				if(property!=null){
					Object val = serialize(property.classFor(map));
					if(val!=null){
						if(props.contains(property.name())){
							map.put(property.name(), val);
						}
					}
				}
				if(bson.readByte()==BSON.TERMINATOR){
					exit = true;
				}else{
					bson.backward();
				}
			}while (!exit);
			
			if(props.size()==map.size()){
				bson.position(lPos);
				return serialize(definition.getAbstractionType(map));
			}else{
				throw new BsonParseError("Required properties for abstract class unavailable <"+StringUtils.join(props.toArray(),',')+">");
			}
			
		}
		
		
		Object target 		= definition.newInstance();
		Boolean exit = false;
		int size			= bson.readInt();
		do {
			token      			= bson.readByte();
			String key  		= bson.readCString();
			if(key.equals("_id") && definition.clazz().isAnnotationPresent(BsonDocument.class)){
				key = "id";
			}
			DefinitionProperty property	= definition.properties().get(key);
			if(property!=null){
				Object val = serialize(property.classFor(target));
				if(val!=null){
					property.invokeSetter(target, val);	
				}
			}else{
				System.out.print("Unknwon Bean Property "+key+" "+serialize(Object.class));
			}
			if(bson.readByte()==BSON.TERMINATOR){
				exit = true;
			}else{
				bson.backward();
			}
		}while (!exit);
		return target;
	}
	
}
