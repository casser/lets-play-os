package os.letsplay.bson;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import os.letsplay.bson.annotations.BsonDocument;
import os.letsplay.bson.binary.Binary;
import os.letsplay.utils.reflection.Definition;
import os.letsplay.utils.reflection.DefinitionProperty;
import os.letsplay.utils.reflection.Definitions;
import os.letsplay.utils.reflection.Simple;
import os.letsplay.utils.reflection.exceptions.ReflectionException;


public class BsonEncoder {
	private static class EmptyObjectException extends Exception{
		private static final long serialVersionUID = -59659902743709624L;
	}
	BsonByteArray bson;
	
	public BsonEncoder(){
		bson = new BsonByteArray();
	}
	
	private byte parseType ( Object object ) throws ReflectionException {
		
		
		if(object instanceof byte[]){
			return BSON.BINARY;
		}
		if(object == null){
			return BSON.NULL;
		}
		Definition def = Definitions.get(object.getClass());
	
		if ( 
			String.class.isAssignableFrom(object.getClass()) || 
			def.type().equals(Definitions.ENUM) 
		) {
			return BSON.STRING;
		} else if ( Boolean.class.isAssignableFrom(object.getClass()) ) {
			return BSON.BOOLEAN;
		} else if ( Integer.class.isAssignableFrom(object.getClass()) ) {
			return BSON.INT32;
		} else if ( Long.class.isAssignableFrom(object.getClass()) ) {
			return BSON.INT64;
		} else if ( Double.class.isAssignableFrom(object.getClass()) ) {
			return BSON.DOUBLE;
		} else if ( BsonId.class.isAssignableFrom(object.getClass()) ) {
			return BSON.OBJECTID;
		} else if ( Simple.class.isAssignableFrom(object.getClass()) ) {
			return BSON.STRING;
		} else if ( Date.class.isAssignableFrom(object.getClass()) ) {
			return BSON.UTC;
		} else if ( 
			UUID.class.isAssignableFrom(object.getClass()) 			|| 
			BsonBinary.class.isAssignableFrom(object.getClass()) 	||
			Binary.class.isAssignableFrom(object.getClass())
		) {
			return BSON.BINARY;
		} else if ( 
			object.getClass().isArray() 							|| 
			List.class.isAssignableFrom(object.getClass()) 			|| 
			Set.class.isAssignableFrom(object.getClass()) 			|| 
			def.type().equals(Definitions.ARRAY) 
		) {
			return BSON.ARRAY;
		} else if ( 
			Map.class.isAssignableFrom(object.getClass()) 			|| 
			def.type().equals(Definitions.BEAN) 
		) {
			return BSON.DOCUMENT;
		} 
		return BSON.NULL;
	}
	
	public byte[] encode( Object document ){
		if(document instanceof byte[]){
			return (byte[]) document;
		}
		
		if(document instanceof BsonEncodable){
			return ((BsonEncodable) document).encodeBson();
		}
		try{
			writeMap(document);
			return bson.array();
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	private void writeMap(Object document) throws Exception {
		if(document instanceof byte[]){
			 bson.writeBytes((byte[]) document);
			 return;
		}
		int sPos = bson.position();
		bson.writeInt(0);
		Definition def = Definitions.get(document.getClass());
		Boolean isModel = document.getClass().isAnnotationPresent(BsonDocument.class);
		if(def.type().equals(Definitions.BEAN)){
			for(DefinitionProperty property:def.properties()){
				String key  			= (String)property.name();
				if(isModel && key.equals("id")){
					key = "_id";
				}
				if(property.hasScope("bson")){
					Object val 	= property.invokeGetter(document);
					writeElement(parseType(val),key,val);
				}
			}
		}else if(def.type().equals(Definitions.MAP)){
			if(Map.class.isAssignableFrom(document.getClass())){
				Map<?,?> map = (Map<?,?>)document;
				if(map.size()>0){
					for(Map.Entry<?,?> entry:map.entrySet()){
						Object key  = entry.getKey();
						Object val  = entry.getValue();
						writeElement(parseType(val),key.toString(),val);
					}
				}else{
					throw new EmptyObjectException();
				}
			}
		}else if(def.type().equals(Definitions.ARRAY)){
			if(List.class.isAssignableFrom(document.getClass())){
				List<?> list = (List<?>)document;
				if(list.size()>0){
					for(int i=0;i<list.size();i++){
						Object key  = new Integer(i).toString();
						Object val  = list.get(i);
						writeElement(parseType(val),key.toString(),val);
					}
				}else{
					throw new EmptyObjectException();
				}
			}else
			if(Set.class.isAssignableFrom(document.getClass())){
				Set<?> list = (Set<?>)document;
				if(list.size()>0){
					int i=0;
					for(Object val:list){
						Object key  = new Integer(i).toString();
						writeElement(parseType(val),key.toString(),val);
						i++;
					}
				}else{
					throw new EmptyObjectException();
				}
			}else
			if(document.getClass().isArray()){
				Object[] list = (Object[])document;
				if(list.length>0){
					for(int i=0;i<list.length;i++){
						String key  = new Integer(i).toString();
						Object val  = list[i];
						writeElement(parseType(val),key,val);
					}
				}else{
					throw new EmptyObjectException();
				}
			}
		}
		
		
		bson.writeByte(BSON.TERMINATOR);
		int ePos    = bson.position();
		int length  = ePos-sPos;
		bson.position(sPos);
		bson.writeInt(length);
		bson.position(ePos);
		
	}
	
	private void writeElement(byte type, String key, Object val) throws Exception {
		if(val==null){
			return;
		}
		int sPos = bson.position();
		bson.writeByte(type);
		bson.writeCString(key.toString());
		switch(type){
			case BSON.NULL     : break;
			case BSON.STRING   : bson.writeString(val.toString());break;
			case BSON.BOOLEAN  : bson.writeBoolean((Boolean)val);break;
			case BSON.INT32    : bson.writeInt((Integer)val);break;
			case BSON.INT64    : bson.writeLong((Long)val);break;
			case BSON.DOUBLE   : bson.writeDouble((Double)val);break;
			case BSON.OBJECTID : bson.writeBytes(((BsonId)val).toByteArray());break;
			case BSON.UTC      : bson.writeDate((Date)val);break;
			case BSON.BINARY   : bson.writeBinary(val); break;
			case BSON.ARRAY    :
			case BSON.DOCUMENT : try{
				writeMap(val); break;
			}catch(EmptyObjectException ex){
				bson.position(sPos);
			}
		}
	}
}
