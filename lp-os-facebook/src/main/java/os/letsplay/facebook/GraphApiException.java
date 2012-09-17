package os.letsplay.facebook;

import java.util.HashMap;

import os.letsplay.json.JSON;
import os.letsplay.json.JsonParseError;


public class GraphApiException extends Exception {
	
	private static final long serialVersionUID = -2977436248098608076L;
	
	private String 	message;
	private String 	type;
	private Integer code;
	private Integer subCode;
	
	@Override
	public String getMessage() {
		return message==null?super.getMessage():message;
	}
	
	public String getType() {
		return type;
	}
	
	public Integer getCode() {
		return code;
	}
	
	public Integer getSubCode() {
		return subCode;
	}
	
	public GraphApiException(){
		message = "Unknown Exception";
	}
	
	public GraphApiException(String message, Exception e) {
		super(e);
	}

	public GraphApiException(String message) {
		super(message);
	}

	@SuppressWarnings("unchecked")
	public GraphApiException json(String json) {
		try {
			HashMap<String,Object> obj  = JSON.decode(json,HashMap.class);
			if(obj.containsKey("error")){
				HashMap<String,Object> err  = (HashMap<String,Object>)obj.get("error");
				if(err.containsKey("message")){
					message = (String)err.get("message");
				}
				if(err.containsKey("type")){
					type 	= (String)err.get("type");
				}
				if(err.containsKey("code")){
					code 	= (Integer)err.get("code");	
				}
				if(err.containsKey("error_subcode")){
					subCode	= (Integer)err.get("error_subcode");	
				}
			}
		} catch (JsonParseError e) {
			e.printStackTrace();
		}
		return this;
	}

}
