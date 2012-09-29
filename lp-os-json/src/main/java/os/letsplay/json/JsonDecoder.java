package os.letsplay.json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	
public class JsonDecoder {
	
	private static final Boolean DEBUG = false; 
	
	private JsonTokenizer 	tokenizer;
	private JsonToken  		token;
	
	@SuppressWarnings("unchecked")
	public <T> T decode(String document, Class<T> clazz) throws JsonParseError, ReflectionException{
		if(document!=null && document.length()>0){
			tokenizer	= new JsonTokenizer(document);
			
		}else{
			throw new JsonParseError("Invalid Json Data");
		}
		nextToken();
		return (T) serialize(clazz);
	}
	
	private static void print(Object o){
		if(DEBUG)System.out.println(o);
	}
		
	private Object serialize(Class<?> clazz) throws JsonParseError, ReflectionException{
		if(clazz==null){
			clazz = Object.class;
		}
		return serialize(Definitions.get(clazz));
	}
	
	private Object serialize(Definition definition) throws JsonParseError, ReflectionException{
		Object		target;
		if(definition==null){
			definition = Definitions.get(Object.class);
		}
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
				throw new JsonParseError("Invalid Definition Type <"+definition.type()+">");
		}
		return target;
	}
	
	public JsonToken nextToken() throws JsonParseError{
		token = tokenizer.nextToken();
		print(token);
		return token;
	}
	
	private Object serializeInternal(Definition def) throws JsonParseError, ReflectionException{
		InternalDefinition definition = (InternalDefinition) def;
		if(definition.clazz()!=Object.class){
			return null;
		}
		switch (token.type()) {
			case OBJECT_START:
				return serialize(Map.class);
			case ARRAY_START:
				return serialize(List.class);
			case STRING:
				return token.value();
			default:
				throw new JsonParseError("Invalid Json Data");
		}
	}
	
	private Object serializeEnum(Definition def) throws JsonParseError{
		EnumDefinition definition = (EnumDefinition) def;
		return definition.newInstance(token.value());
	}
	
	private Object serializeSimple(Definition def) throws JsonParseError, ReflectionException{
		SimpleDefinition definition = (SimpleDefinition) def;
		return definition.newInstance(token.raw());
	}
	
	private Object serializeArray(Definition def) throws JsonParseError, ReflectionException{
		ArrayDefinition definition 	= (ArrayDefinition) def;
		Object target 				= definition.newInstance();
		Boolean exit = true;
		do {
			@SuppressWarnings("unused")
			JsonToken valueToken 	= nextToken();
			Object value 			= serialize(definition.valueClazz());
			JsonToken commaToken 	= nextToken();
			switch(commaToken.type()){
				case ARRAY_END:
					exit=false;
				case COMMA:
					definition.add(target,value);
				break;
				default:
					throw new JsonParseError("Unexpected en of object, required ',' or ']' found "+token.toString());
					
			}
		}while (exit);
		return target;
	}
	
	@SuppressWarnings("unused")
	private Object serializeMap(Definition def) throws JsonParseError, ReflectionException{
		MapDefinition definition 	= (MapDefinition) def;
		Map<Object, Object> target 	= definition.newInstance();
		Boolean exit = true;
		do {
			JsonToken keyToken 		= nextToken();
			Object key 				= serialize(definition.keyClazz());
			JsonToken colonToken 	= nextToken();
			JsonToken valueToken 	= nextToken();
			Object value 			= serialize(definition.valueClazz());
			JsonToken commaToken 	= nextToken();
			switch(commaToken.type()){
				case OBJECT_END:
					exit=false;
				case COMMA:
					target.put(key, value);
				break;
				default:
					throw new JsonParseError("Unexpected en of object, required ',' or '}' found "+token.toString());
					
			}
		}while (exit);
		return target;
	}
	
	@SuppressWarnings("unused")
	private Object serializeBean(Definition def) throws JsonParseError, ReflectionException{
		BeanDefinition definition 	= (BeanDefinition) def;
		if(definition.isAbstract()){
			HashMap<String, Object> map = new HashMap<String, Object>();
			List<String> props 	= definition.getAbstractionProperties();
			tokenizer.mark();
			Boolean exit = true;
			do {
				if(props.size()==0){
					exit = false;
				}
				JsonToken keyToken 		= nextToken();
				JsonToken colonToken 	= nextToken();
				JsonToken valueToken 	= nextToken();
				DefinitionProperty property = definition.properties().get(
					keyToken.value().toString()
				);
				
				Object value = serialize(property.classFor(map));
				JsonToken commaToken 	= nextToken();
				switch(commaToken.type()){
					case OBJECT_END:
						exit=false;
					case COMMA:
						if(property!=null && value!=null){
							if(props.contains(property.name())){
								map.put(property.name(), value);
							}
						}
					break;
					default:
						throw new JsonParseError("Unexpected en of object, required ',' or '}' found "+token.toString());
				}
			}while (exit);
			
			if(props.size()==map.size()){
				tokenizer.reset();
				return serialize(definition.getAbstractionType(map));
			}else{
				throw new JsonParseError("Required properties for abstract class unavailable <"+StringUtils.join(props.toArray(),',')+">");
			}
			
		}
		
		Object target 				= definition.newInstance();
		Boolean exit = true;
		do {
			JsonToken keyToken 		= nextToken();
			JsonToken colonToken 	= nextToken();
			JsonToken valueToken 	= nextToken();
			DefinitionProperty property = definition.properties().get(
				keyToken.value().toString()
			);
			
			Object value = serialize(property.classFor(target));
			JsonToken commaToken 	= nextToken();
			switch(commaToken.type()){
				case OBJECT_END:
					exit=false;
				case COMMA:
					if(property!=null && value!=null){
						property.invokeSetter(target, value);
					}
				break;
				default:
					throw new JsonParseError("Unexpected en of object, required ',' or '}' found "+token.toString());
			}
		}while (exit);
		return target;
	}
	
	/*
	private JsonToken nextToken() throws JsonParseError {
		return token = tokenizer.getNextToken();
	}
	
	private JsonToken nextValidToken() throws JsonParseError {
		token = tokenizer.getNextToken();
		checkValidToken();
		return token;
	}
	
	private void checkValidToken() throws JsonParseError {
		if ( token == null ){
			tokenizer.parseError( "Unexpected end of input" );
		}
	}
	
	private <T> T parseArray(Class<T> cls) throws JsonParseError, ReflectionException {
		
		Definition type;
		if(cls==null){
			type = Definitions.get(ArrayList.class);
		}else{
			type = Definitions.get(cls);	
		}
		T a = type.newInstance();
		
		// grab the next token from the tokenizer to move
		// past the opening [
		nextValidToken();
		
		// check to see if we have an empty array
		if ( token.type == JsonToken.Type.RIGHT_BRACKET )
		{
			// we're done reading the array, so return it
			return (T)a;
		}
		
		// deal with elements of the array, and use an "infinite"
		// loop because we could have any amount of elements
		while ( true )
		{
			// read in the value and add it to the array
			readValue(a);
			// after the value there should be a ] or a ,
			nextValidToken();
			
			if ( token.type == JsonToken.Type.RIGHT_BRACKET )
			{
				// we're done reading the array, so return it
				return (T)a;
			}
			else if ( token.type == JsonToken.Type.COMMA )
			{
				// move past the comma and read another value
				nextToken();
			}
			else
			{
				tokenizer.parseError( "Expecting ] or , but found " + token.value );
			}
		}
		
		
	}
	
	@SuppressWarnings("unused")
	private Object convertKey(Object o, Class<?> cls) throws JsonParseError, ReflectionException {
		if(cls==null){
			return o;
		}
		Definition def = Definitions.get(cls);
		switch (def.type()) {
			case ENUM:
				return toEnum(o, def);
			case BEAN:
				return toBean(o, def);
			default:
				return o;
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> T toBean(Object data, Definition type){
		return (T) type.newInstance(data);
	}
	
	@SuppressWarnings("unchecked")
	private <T> T toEnum(Object data, Definition type){
		Object[] list = type.clazz().getEnumConstants();
		for(Object item:list){
			Enum<?> en = (Enum<?>)item;
			if(en.name().toUpperCase().equals(data.toString().toUpperCase())){
				return (T) en;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void readValue(Object o) throws JsonParseError, ReflectionException {
		Definition def = Definitions.get(o.getClass());
		switch(def.type()){
			case ARRAY:{
				ArrayDefinition adef = (ArrayDefinition)def;
				if(List.class.isAssignableFrom(o.getClass())){
					List<Object> list = (List<Object>)o;
					list.add(parseValue(adef.valueClazz()));
				}else
				if(Set.class.isAssignableFrom(o.getClass())){
					Set<Object> list = (Set<Object>)o;
					list.add(parseValue(adef.valueClazz()));
				}		
			}
			break;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void readValue(Object o, Object k) throws JsonParseError, ReflectionException {
		Definition def = Definitions.get(o.getClass());
		switch(def.type()){
			case MAP:{
				MapDefinition mdef = (MapDefinition)def;
				Map<Object,Object> map = (Map<Object,Object>)o;
				map.put(
					convertKey(k,mdef.keyClazz()),
					parseValue(mdef.valueClazz())
				);	
			}
			break;
			case BEAN:{
				BeanDefinition bdef = (BeanDefinition)def;
				Property property = bdef.properties().get(k.toString());
				if(property!=null){
					property.invokeSetter(o, parseValue(property.clazz()));
				}else{
					tokenizer.getObjectString();
				}
			}
			break;
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> T parseObject(Class<T> cls) throws JsonParseError, ReflectionException {
		if(cls!=null && JsonDecodable.class.isAssignableFrom(cls)){
			JsonDecodable value = null;
			try {
				value = (JsonDecodable) cls.newInstance();
				value.decodeJson(tokenizer.getObjectString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return (T)value;
		}
		Definition def;
		if(cls==null){
			def = Definitions.get(HashMap.class);
		}else{
			def = Definitions.get(cls);	
		}
		
		// create the object internally that we're going to
		// attempt to parse from the tokenizer
		T o = def.newInstance();
		
		// store the string part of an object member so
		// that we can assign it a value in the object
		String key;
		
		// grab the next token from the tokenizer
		nextValidToken();
		
		// check to see if we have an empty object
		if ( token.type == JsonToken.Type.RIGHT_BRACE )
		{
			// we're done reading the object, so return it
			return (T)o;
		}
		// in non-strict mode an empty object is also a comma
		// followed by a right bracket
		else if ( token.type == JsonToken.Type.COMMA )
		{
			// move past the comma
			nextValidToken();
			
			// check to see if we're reached the end of the object
			if ( token.type == JsonToken.Type.RIGHT_BRACE )
			{
				return (T)o;
			}
			else
			{
				tokenizer.parseError( "Leading commas are not supported.  Expecting '}' but found " + token.value );
			}
		}
		
		// deal with members of the object, and use an "infinite"
		// loop because we could have any amount of members
		while ( true )
		{
			if ( token.type == JsonToken.Type.STRING )
			{
				// the string value we read is the key for the object
				key = (String) token.value;
				
				// move past the string to see what's next
				nextValidToken();
				
				// after the string there should be a :
				if ( token.type == JsonToken.Type.COLON )
				{
					// move past the : and read/assign a value for the key
					nextToken();
					readValue(o, key);
					
					
					// move past the value to see what's next
					nextValidToken();
					
					// after the value there's either a } or a ,
					if ( token.type == JsonToken.Type.RIGHT_BRACE ){
						return (T)o;
					}
					else if ( token.type == JsonToken.Type.COMMA ){
						// skip past the comma and read another member
						nextToken();
					}
					else
					{
						tokenizer.parseError( "Expecting } or , but found " + token.value );
					}
				}
				else
				{
					tokenizer.parseError( "Expecting : but found " + token.value );
				}
			}
			else
			{
				tokenizer.parseError( "Expecting string but found " + token.value );
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> T parseValue(Class<T> type) throws JsonParseError, ReflectionException {
		
		checkValidToken();
		switch ( token.type ){
			case LEFT_BRACE:
				if(type==Object.class){
					return (T) parseObject(HashMap.class);	
				}else{
					return (T) parseObject(type);	
				}
			case LEFT_BRACKET:
				if(type==Object.class){
					return (T) parseArray(ArrayList.class);	
				}else{
					return (T) parseArray(type);	
				}
			case STRING:
			case NUMBER:
			case TRUE:
			case FALSE:
			case NULL:
				if(type==Object.class){
					return (T) token.value;
				}else{
					return (T) token.readValue(type);	
				}
			default:
				tokenizer.parseError( "Unexpected " + token.value );
		}
		return null;
	}
	*/
}

