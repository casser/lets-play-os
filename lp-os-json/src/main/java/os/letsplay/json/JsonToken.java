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

import os.letsplay.utils.StringUtils;

	
public final class JsonToken {
	
	
	private static final Character NOT = (char)0;
	public static enum Type {
		
		COMMA(','),
		COLON(':'),
		OBJECT_START('{'),
		OBJECT_END('}'),
		ARRAY_START('['),
		ARRAY_END(']'),
		
		STRING(NOT);
		
		
		public Boolean isValue(){
			return character==NOT;
		}
		public final Character character;
		private Type(Character character) {
			this.character = character;
		}
	}
	
	
	static JsonToken create( Type type ){
		return new JsonToken(type, null);
	}
	
	static JsonToken create( Type type, String value){
		return new JsonToken(type, value);
	}
	
	private Type 	type;
	private String 	value;
	private String 	comment;
	private int 	level;
	private int 	sLoc;
	private int 	eLoc;
	private int 	sLine;
	private int 	eLine;
	private int 	sPos ;
	private int 	ePos ;
	
	public JsonToken comment(String comment) {
		this.comment = comment;
		return this;
	}
	public JsonToken level(int level) {
		this.level = level;
		return this;
	}
	
	public Type type() {
		return type;
	}
	
	
	
	public JsonToken( Type type, String value){
		this.type 	= type;
		this.value 	= value;
	}
	
	public void positions(int sLoc, int eLoc, int sLine, int eLine, int sPos, int ePos) {
		this.sLoc 	= sLoc;
		this.eLoc 	= eLoc;
		this.sLine 	= sLine;
		this.eLine 	= eLine;
		this.sPos 	= sPos;
		this.ePos 	= ePos;
	}
	
	public Boolean isString() {
		return (eLoc-sLoc==value.length()+2);
	}
	
	public Boolean isBoolean() {
		return 
			value.toLowerCase().equals("true") ||
			value.toLowerCase().equals("false");
	}
	public Boolean isNumber() {
		if(value.matches("-?\\d*\\.?\\d+")||value.matches("0x\\d{1,8}")){
			return true;
		}
		return false;
	}
	public Boolean convertBoolean() {
		return value.equals("true");
	}
	
	public Number convertNumber() {
		int radix = 10;
		if(value.indexOf('.')>0){
			return Double.parseDouble(value);
		}
		if(value.matches("0x\\d{1,8}")){
			value = value.substring(2);
			radix = 16;
		}
		try{
			return Integer.parseInt(value,radix);
		}catch (NumberFormatException e) {
			return Long.parseLong(value,radix);
		}
	}
	
	public Object value() {
		if(value==null){
			return null;
		}if(isString()){
			return value;
		}if(value.equals("null")){
			return null;
		}else if(isBoolean()){
			return convertBoolean();
		}else if(isNumber()){
			return convertNumber();
		}
		return value;
	}
	
	public String raw() {
		return value;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(StringUtils.repeat("  ",level));
		sb.append(this.type.name()+" ");
		sb.append("[line "+sLine+","+eLine+" pos:"+sPos+","+ePos+" loc:"+sLoc+","+eLoc+"] ");
		if(this.type.isValue()){
			sb.append(this.value.getClass().getSimpleName());
			sb.append("("+this.value+") ");
		}
		if(this.comment!=null && this.comment.length()>0){
			sb.append("/*"+this.comment+"*/");
		}
		return sb.toString();
	}
}
