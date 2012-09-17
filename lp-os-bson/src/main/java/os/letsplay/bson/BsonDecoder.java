package os.letsplay.bson;

import java.util.List;
import java.util.Map;

import os.letsplay.bson.annotations.BsonDocument;
import os.letsplay.bson.binary.Binary;
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
				Object val = serialize(property.clazz());
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
	/*
	
	@SuppressWarnings("unchecked")
	private <T> T readDocument(byte type, Class<T> clazz) throws ReflectionException{
		int sPos   = bson.position();
		int length = bson.readInt(); //length
		
		if(clazz!=null && clazz.isAnnotationPresent(BsonDecodable.Factory.class)){
			for(Method method:clazz.getDeclaredMethods()){
				if(Modifier.isStatic(method.getModifiers()) && method.isAnnotationPresent(BsonDecodable.Factory.class)){
					if(method.getGenericParameterTypes().length==1 && (method.getParameterTypes()[0]).equals(byte[].class)){
						bson.position(sPos);
						try{
							return (T)method.invoke(clazz, bson.readBytes(length));
						}catch (Exception e) {
							e.printStackTrace();
							return null;
						}
					}
				}
			}
		}
		
		if(clazz!=null && BsonDecodable.class.isAssignableFrom(clazz)){
			BsonDecodable value = null;
			try {
				bson.position(sPos);
				value = (BsonDecodable) clazz.newInstance();
				value.decodeBson(bson.readBytes(length));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return (T)value;
		}
		
		
		
		Object result = null;
		
		if(type==BSON.DOCUMENT){
			if(clazz==null){
				Map<Object,Object> map  = new LinkedHashMap<Object, Object>();
				while(true){
					type        = bson.readByte();
					Object key  = bson.readCString();
					map.put(key, readElement(type,null));
					if(bson.readByte()==BSON.TERMINATOR){
						break;
					}else{
						bson.backward();
					}
				}
				result = map;
			}else{
				
				Definition rType = Definitions.get(clazz);
				if(rType.type().equals(Definitions.MAP)){
					MapDefinition mDef = (MapDefinition)rType;
					Definition kType = Definitions.get(mDef.keyClazz());
					Definition vType = Definitions.get(mDef.valueClazz());
					Map<Object,Object> map  = rType.newInstance();
					while(true){
						
						type        = bson.readByte();
						Object key  = bson.readCString();
						if(type!=0 && key.toString().length()>0){
							if(kType.type().equals(Definitions.ENUM)){
								key = toEnum(key,kType);
							}else 
							if(kType.type().equals(Definitions.BEAN)){
								key = toBean(key,kType);
							}
							map.put(key, readElement(type,vType.clazz()));
						}
						
						if(bson.readByte()==BSON.TERMINATOR){
							break;
						}else{
							bson.backward();
						}
					}
					result = map;
				}else
				if(rType.type().equals(Definitions.BEAN)){
					T bean = rType.newInstance();
					
					if(BsonModel.class.isAssignableFrom(clazz)){
						BsonModel model = (BsonModel)bean;
						//bson.position(BsonModel.Info.LENGTH+4);
						byte   idType   = bson.readByte();
						String idKey  	= bson.readCString();
						if(idKey.equals("_id")){
							model.id(readElement(idType,null));
						}
					}
					Map<String,Object> unknownProperties = null; 
					while(true){
						type        = bson.readByte();
						if(type==BSON.TERMINATOR){
							break;
						}
						String key  = bson.readCString();
						
						if(	rType.properties().has(key)){
							Property property = rType.properties().get(key);
							Object value = readElement(type,property.clazz());
							try{
								property.invokeSetter(bean, value);
							}catch(Exception ex){
								ex.printStackTrace();
							}
						}else{
							if(unknownProperties==null){
								unknownProperties = new LinkedHashMap<String, Object>();
							}
							unknownProperties.put(key, readElement(type,null));
						}
						
						if(bson.readByte()==BSON.TERMINATOR){
							break;
						}else{
							bson.backward();
						}
					}
					if(unknownProperties!=null){
						System.out.println("Unknown Properties on <"+rType.type()+">\n"+unknownProperties.toString());
					}
					result = bean;
				}
			}
			
		}else 
		if(type==BSON.ARRAY){
			if(clazz==null || clazz.equals(Object.class)){
				clazz=null;
				List<Object> list = new ArrayList<Object>();
				while(true){
					type 			= bson.readByte();
					if(type==BSON.TERMINATOR){
						break;
					}
					String key 		= bson.readCString();
					Integer index 	= Integer.parseInt(key);
					while(index>list.size()){
						list.add(null);
					}
					list.add(index,readElement(type,null));
					if(bson.readByte()==BSON.TERMINATOR){
						break;
					}else{
						bson.backward();
					}
				}
				result = list.size()>0?list:null;
			}else{
				Definition rType = Definitions.get(clazz);
				Object obj  = rType.newInstance();
				if(Set.class.isAssignableFrom(obj.getClass())){
					ArrayDefinition aDef = (ArrayDefinition)rType;
					Set<Object> list = (Set<Object>)obj;
					while(true){
						type 			= bson.readByte();
						if(type==BSON.TERMINATOR){
							break;
						}
						bson.readCString();// IGNORE KEY;
						list.add(readElement(type,aDef.valueClazz()));
						if(bson.readByte()==BSON.TERMINATOR){
							break;
						}else{
							bson.backward();
						}
					}
					result = list.size()>0?list:null;
				}else 
				if(List.class.isAssignableFrom(obj.getClass())){
					ArrayDefinition aDef = (ArrayDefinition)rType;
					List<Object> list = (List<Object>)obj;
					while(true){
						type 			= bson.readByte();
						if(type==BSON.TERMINATOR){
							break;
						}
						String key 		= bson.readCString();
						Integer index 	= Integer.parseInt(key);
						list.add(index,readElement(type,aDef.valueClazz()));
						if(bson.readByte()==BSON.TERMINATOR){
							break;
						}else{
							bson.backward();
						}
					}
					result = list.size()>0?list:null;
				}
			}
			
			
		}
		return (T)result;
	}
	
	public <T> Object readElement(byte type, Class<T> clazz) throws ReflectionException{
		Definition t = clazz!=null?Definitions.get(clazz):null;
		switch( type ) {
			case BSON.NULL:
				return null;
			case BSON.INT32:
				if(t!=null&&t.type().equals(Definitions.SIMPLE) && !(t.type().equals(Integer.class) || t.type().equals(int.class))){
					return t.newInstance(bson.readInt());
				}else{
					return bson.readInt();
				}
			case BSON.INT64:
				if(t!=null&&t.type().equals(Definitions.SIMPLE) && !(t.type().equals(Long.class) || t.type().equals(long.class))){
					return t.newInstance(bson.readLong());
				}else{
					return bson.readLong();
				}
			case BSON.DOUBLE:
				if(t!=null&&t.type().equals(Definitions.SIMPLE) && !(t.type().equals(Double.class) || t.type().equals(double.class))){
					return t.newInstance(bson.readDouble());
				}else{
					return bson.readDouble();
				}
			case BSON.STRING:
				if(t!=null&&t.type().equals(Definitions.SIMPLE) && !t.type().equals(String.class)){
					return t.newInstance(bson.readString());
				}else if(t!=null&&t.type().equals(Definitions.ENUM)){
					return toEnum(bson.readString(), t);
				}else{
					return bson.readString();
				}
			case BSON.OBJECTID:
				if(t!=null&&t.type().equals(Definitions.SIMPLE) && BsonId.class.isAssignableFrom(t.clazz())){
					return t.newInstance(bson.readBytes(12));
				}else{
					return new BsonId(bson.readBytes(12));
				}
			case BSON.BINARY:
				if(t!=null&&BsonBinary.class.isAssignableFrom(t.clazz())){
					BsonBinary bin = t.newInstance();
					bin.setData(bson.readBinary());
				}if(t!=null&&t.type().equals(Definitions.SIMPLE)){
					return t.newInstance(bson.readBinary());
				}else{
					return new Binary(bson.readBinary());
				}
			case BSON.UTC:
				return bson.readDate();
			case BSON.BOOLEAN:
				return bson.readBoolean();
			case BSON.ARRAY:
				return readDocument(BSON.ARRAY,clazz);
			case BSON.DOCUMENT:
				return readDocument(BSON.DOCUMENT,clazz);
			default:
				return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> T toEnum(Object data, Definition def){
		EnumDefinition definition = (EnumDefinition)def;
		return (T) definition.newInstance(data);
	}
	
	@SuppressWarnings("unchecked")
	private <T> T toBean(Object data, Definition def) throws ReflectionException{
		return (T) def.newInstance(data);
	}
	*/
}
