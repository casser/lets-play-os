package os.letsplay.facebook;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import os.letsplay.json.JSON;
import os.letsplay.utils.StringUtils;



public class GraphAPI {
	public static final String API_URL = "https://graph.facebook.com";
	
	public static class Request {
		public static enum Method {
			GET,POST,DELETE;
		}
		public static enum ResponseFormat {
			JSON,XML,QSTRING;
		}
		public static class Params extends HashMap<String, String>{
			private static final long serialVersionUID = -838074270083998655L;
		}
		
		private GraphAPI 		api;
		private Method 	 		method;
		private ResponseFormat	format;
		private String 	 		path;
		private Params 	 		params;
		private String   		body;
		private String   		response;
		
		public Request(GraphAPI api) {
			this.api 	= api;
			this.method = Method.GET;
			this.format = ResponseFormat.JSON;
			this.params = new Params();
		}
		
		public String param(String key){
			return params.get(key);
		}
		
		public Request param(String key, String value){
			params.put(key, value);
			return this;
		}
		
		public String body(){
			return body;
		}
		
		public Request body(String value){
			body = value;
			return this;
		}
		
		public String path(){
			return path;
		}
		
		public Request path(String value){
			path = value;
			return this;
		}
		
		public Method method(){
			return method;
		}
		
		public Request method(Method value){
			method = value;
			return this;
		}
		
		public ResponseFormat format(){
			return format;
		}
		
		public Request format(ResponseFormat value){
			format = value;
			return this;
		}
		
		@SuppressWarnings("unchecked")
		public Map<String, Object> call() throws GraphApiException{
			return call(HashMap.class);
		}
		
		@SuppressWarnings("unchecked")
		public <T> T call(Class<T> type) throws GraphApiException{
			try {
				int status = execute();
				if(status==200){
					switch (format) {
						case JSON:
							return JSON.decode(response,type);
						case QSTRING:
							return (T)getQStringMap(response);
						default:
							throw new GraphApiException("Unknown response format <"+format+">");
					}
				}else{
					throw new GraphApiException().json(response);	
				}
			}catch (GraphApiException e) {
				throw e;
			}catch (Exception e) {
				throw new GraphApiException("Invalid JSON Data",e);
			}
		}
		
		private Integer execute() throws GraphApiException{
			if(api.token!=null){
				params.put("access_token", api.getToken().getValue());	
			}
			String url =  api.getApiUrl()+path+getUrlParams();
			
			HttpsURLConnection conn;
			try{
				conn = (HttpsURLConnection) new URL(url).openConnection();
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setUseCaches(false);
				conn.setAllowUserInteraction(false);
				conn.setRequestProperty("Content-Type","application/json");
				conn.setRequestMethod(method.name());
			}catch (Exception e) {
				throw new GraphApiException("Connection to <"+url+"> failed",e);
			}
			try {
				response = readInputStreamAsString(conn.getInputStream());
				return conn.getResponseCode();
			} catch (IOException e) {
				try {
					response = readInputStreamAsString(conn.getErrorStream());
					return conn.getResponseCode();
				} catch (IOException e1) {
					throw new GraphApiException("Reading Error Stream Failed",e1);
				}				
			}
		}
		
		private Map<String, String> getQStringMap(String string){
			Map<String, String> map = new HashMap<String, String>();
			String[] pairs = string.split("&");
			for(String p:pairs){
				String[] pair = p.split("=");
				map.put(pair[0],pair[1]);
			}
			return map;
		}
		
		private String getUrlParams(){
			if(params==null || params.size()==0){
				return "";
			}
			Set<String> keys = params.keySet();
			ArrayList<String> pairs = new ArrayList<String>();
			for(String key: keys){
				pairs.add(key+"="+params.get(key));
			}
			return "?"+StringUtils.join(pairs.toArray(),'&');
		}
		
		public static String readInputStreamAsString(InputStream in) throws IOException {

		    BufferedInputStream bis = new BufferedInputStream(in);
		    ByteArrayOutputStream buf = new ByteArrayOutputStream();
		    int result = bis.read();
		    while(result != -1) {
		      byte b = (byte)result;
		      buf.write(b);
		      result = bis.read();
		    }        
		    return buf.toString();
		}
	}
	
	public static class Token {
		public static enum Type {
			USER,APP;
		}
		
		private Type type;
		private String value;
		
		public Token(Type type,String value) {
			this.type  = type;
			this.value = value;
		}
		public Type getType() {
			return type;
		}
		public String getValue() {
			return value;
		}
		
	}
	
	
	
	private String apiUrl;
	private Token  token;
	
    public String getApiUrl() {
		return apiUrl==null ? API_URL:apiUrl;
	}
    
    public Token getToken() {
		return token;
	}
    
	public GraphAPI(String accessToken){
		this.token = new Token(Token.Type.USER, accessToken);
    }
	
	public GraphAPI(String appId, String appSecret) throws GraphApiException{
		Request request = new Request(this)
			.method(Request.Method.GET)
			.format(Request.ResponseFormat.QSTRING)
			.path("/oauth/access_token")
			.param("client_id",appId)
			.param("client_secret",appSecret)
			.param("grant_type","client_credentials");
		Map<String, Object> params = request.call();
		token = new Token(Token.Type.APP,(String)params.get("access_token"));
    }
	
	public Request get(String path) {
		return new Request(this).method(Request.Method.GET).path(path);
	}
}