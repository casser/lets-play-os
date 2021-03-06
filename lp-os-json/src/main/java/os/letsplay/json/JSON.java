/*
  Copyright (c) 2008, Adobe Systems Incorporated
  All rights reserved.

  Redistribution and use in source and binary forms, with or without 
  modification, are permitted provided that the following conditions are
  met:

  * Redistributions of source code must retain the above copyright notice, 
    this list of conditions and the following disclaimer.
  
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the 
    documentation and/or other materials provided with the distribution.
  
  * Neither the name of Adobe Systems Incorporated nor the names of its 
    contributors may be used to endorse or promote products derived from 
    this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
  IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package os.letsplay.json;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import os.letsplay.utils.reflection.exceptions.ReflectionException;

public class JSON {
	
	public static interface Hack {
		public String key();
		public Object execute(String source);
	}
	
	public static Map<String, Hack> hacks = new LinkedHashMap<String, JSON.Hack>();
	static {
		try {
			@SuppressWarnings("rawtypes")
			final Class BsonIdClass = Class.forName("os.bson.BsonId");
			hacks.put("BsonId", new Hack() {
				public String key() {
					return "BsonId";
				}
				
				@SuppressWarnings("unchecked")
				public Object execute(String source) {
					try {
						return BsonIdClass.getConstructor(new Class[]{String.class}).newInstance(source);
					} catch (Exception e) {
						return source;
					}
				}
			});
		} catch (ClassNotFoundException e) {
		}
	}
	
	public static <T> T decode(String document) throws JsonParseError{
		return decode(document,null);
	}
	
	public static <T> T decode(File file) throws JsonParseError{
		return decode(file,null);
	}
	
	public static <T> T decode(File file, Class<T> type) throws JsonParseError{
		try{
			byte[] buffer = new byte[(int) file.length()];
		    BufferedInputStream f = null;
		    try {
		        f = new BufferedInputStream(new FileInputStream(file));
		        f.read(buffer);
		    } finally {
		        if (f != null) {
		        	try { f.close(); } 
		        	catch (IOException ignored) {}
		        }
		    }
		    return decode(new String(buffer),type);
		}catch(Exception ex){
			throw new JsonParseError(ex.getMessage(),ex);
		}
	}
	
	public static <T> T decode(String document, Class<T> type) throws JsonParseError{
		try{
			return (new JsonDecoder().decode(document,type));
		}catch(ReflectionException ex){
			throw new JsonParseError(ex.getMessage(),ex);
		}
	}
	
	public static void addHack(Hack hack){
		hacks.put(hack.key(), hack);
	}
	public static void removeHack(String key){
		hacks.remove(key);
	}
	public static Boolean hasHack(String key){
		return hacks.containsKey(key);
	}
	public static Hack getHack(String key){
		return hacks.get(key);
	}
	
	
	public static String schema(Class<?> document) throws ReflectionException, JsonParseError{
		return schema(document,false);
	}
	public static String schema(Class<?> document, Boolean formated) throws ReflectionException, JsonParseError{
		return new JsonSchemaEncoder(formated).encode(document);
	}
	
	public static String encode(Object document) throws JsonParseError{
		return encode(document,false,false);
	}
	public static String encode(Object document, Boolean formated) throws JsonParseError{
		return encode(document,formated,false);
	}
	public static String encode(Object document, Boolean formated, Boolean commented) throws JsonParseError{
		return new JsonEncoder(formated,commented).encode(document);
	}
	
	public static void print(Object obj) {
		print(obj,false);
	}
	
	public static void print(Object obj,Boolean comments) {
		try {
			System.out.println(encode(obj,true,comments));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

