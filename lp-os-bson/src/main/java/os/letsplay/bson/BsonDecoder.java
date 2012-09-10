package os.letsplay.bson;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import os.letsplay.bson.binary.Binary;
import os.letsplay.utils.Types;


public class BsonDecoder {
	
	BsonByteArray bson;
	
	public BsonDecoder(){
		bson = new BsonByteArray();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T decode( byte[] document){
		return (T) decode(document,null);
	}
	
	public <T> T decode( byte[] document, Class<T> type){
		
		bson.writeBytes(document);
		bson.position(0);
		T result = (T)readDocument(BSON.DOCUMENT,type);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T readDocument(byte type, Class<T> clazz){
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
				
				Types.Type rType = Types.getType(clazz);
				if(rType.isMap()){
					Types.Type kType = Types.getType(rType.getKeyType());
					Types.Type vType = Types.getType(rType.getValueType());
					Map<Object,Object> map  = rType.newInstance();
					while(true){
						
						type        = bson.readByte();
						Object key  = bson.readCString();
						if(type!=0 && key.toString().length()>0){
							if(kType.isEnum()){
								key = toEnum(key,kType);
							}else if(kType.isBean()){
								key = toBean(key,kType);
							}
							map.put(key, readElement(type,vType.getType()));
						}
						
						if(bson.readByte()==BSON.TERMINATOR){
							break;
						}else{
							bson.backward();
						}
					}
					result = map;
				}else
				if(rType.isBean()){
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
						
						if(	rType.getProperties().containsKey(key)){
							Types.Property property = rType.getProperties().get(key);
							Object value = readElement(type,property.getType());
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
						System.out.println("Unknown Properties on <"+rType.getType()+">\n"+unknownProperties.toString());
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
					list.add(index,readElement(type,null));
					if(bson.readByte()==BSON.TERMINATOR){
						break;
					}else{
						bson.backward();
					}
				}
				result = list.size()>0?list:null;
			}else{
				Types.Type rType = Types.getType(clazz);
				Object obj  = rType.newInstance();
				if(Set.class.isAssignableFrom(obj.getClass())){
					Set<Object> list = (Set<Object>)obj;
					while(true){
						type 			= bson.readByte();
						if(type==BSON.TERMINATOR){
							break;
						}
						bson.readCString();// IGNORE KEY;
						list.add(readElement(type,rType.getValueType()));
						if(bson.readByte()==BSON.TERMINATOR){
							break;
						}else{
							bson.backward();
						}
					}
					result = list.size()>0?list:null;
				}else if(List.class.isAssignableFrom(obj.getClass())){
					List<Object> list = (List<Object>)obj;
					while(true){
						type 			= bson.readByte();
						if(type==BSON.TERMINATOR){
							break;
						}
						String key 		= bson.readCString();
						Integer index 	= Integer.parseInt(key);
						list.add(index,readElement(type,rType.getValueType()));
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
	
	public <T> Object readElement(byte type, Class<T> clazz){
		Types.Type t = clazz!=null?Types.getType(clazz):null;
		switch( type ) {
			case BSON.NULL:
				return null;
			case BSON.INT32:
				if(t!=null&&t.isSimple() && !(t.getType().equals(Integer.class) || t.getType().equals(int.class))){
					return t.newInstance(bson.readInt());
				}else{
					return bson.readInt();
				}
			case BSON.INT64:
				if(t!=null&&t.isSimple() && !(t.getType().equals(Long.class) || t.getType().equals(long.class))){
					return t.newInstance(bson.readLong());
				}else{
					return bson.readLong();
				}
			case BSON.DOUBLE:
				if(t!=null&&t.isSimple() && !(t.getType().equals(Double.class) || t.getType().equals(double.class))){
					return t.newInstance(bson.readDouble());
				}else{
					return bson.readDouble();
				}
			case BSON.STRING:
				if(t!=null&&t.isSimple() && !t.getType().equals(String.class)){
					return t.newInstance(bson.readString());
				}else if(t!=null&&t.isEnum()){
					return toEnum(bson.readString(), t);
				}else{
					return bson.readString();
				}
			case BSON.OBJECTID:
				if(t!=null&&t.isSimple( ) && BsonId.class.isAssignableFrom(t.getType())){
					return t.newInstance(bson.readBytes(12));
				}else{
					return new BsonId(bson.readBytes(12));
				}
			case BSON.BINARY:
				if(t!=null&&BsonBinary.class.isAssignableFrom(t.getType())){
					BsonBinary bin = t.newInstance();
					bin.setData(bson.readBinary());
				}if(t!=null&&t.isSimple()){
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
	private <T> T toEnum(Object data, Types.Type type){
		Object[] list = type.getType().getEnumConstants();
		for(Object item:list){
			Enum<?> en = (Enum<?>)item;
			if(en.name().toUpperCase().equals(data.toString().toUpperCase())){
				return (T) en;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T toBean(Object data, Types.Type type){
		return (T) type.newInstance(data);
	}
}
