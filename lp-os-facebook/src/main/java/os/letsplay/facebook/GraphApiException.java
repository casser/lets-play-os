package os.letsplay.facebook;

import java.util.HashMap;

import os.letsplay.json.JSON;
import os.letsplay.json.JsonDecodable;
import os.letsplay.json.JsonParseError;


public class GraphApiException extends Exception implements JsonDecodable {
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

	public void decodeJson(String json) {
		try {
			HashMap<String,Object> obj  = JSON.decode(json,HashMap.class);
			if(obj.containsKey("error")){
				HashMap<String,String> err  = (HashMap<String,String>)obj.get("error");
				message = err.get("message");
				type 	= err.get("type");
				try{
					code 	= Integer.parseInt(err.get("code"));
					subCode	= Integer.parseInt(err.get("error_subcode"));
				}catch (Exception e) {
				}
			}
		} catch (JsonParseError e) {
			e.printStackTrace();
		}
	}

}
