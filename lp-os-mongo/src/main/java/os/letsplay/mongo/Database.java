package os.letsplay.mongo;

import java.util.Map;

import os.letsplay.mongo.ops.OpQuery;
import os.letsplay.mongo.ops.OpReply;
import os.letsplay.utils.MD5;



public class Database {
	
	private Mongo mongo;
	private String name;
	
	public Database(Mongo mongo, String name) {
		this.mongo 	= mongo;
		this.name 	= name;
	}

	public String getName() {
		return name;
	}
	
	public Mongo getMongo() {
		return mongo;
	}
	
	public <T> Collection<T> getCollection(String name) {
		return new Collection<T>(this, name);
	}
	public <T> Collection<T> getCollection(Class<T> clazz) {
		return new Collection<T>(this, clazz);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> command(Object query) throws Exception {
		if(query!=null && IQuery.class.isAssignableFrom(query.getClass())){
			query =((IQuery)query).getQuery();
		}
    	OpQuery q = new OpQuery(name+".$cmd", query, null, -1, 0, 0);
    	OpReply r = mongo.send(q);
    	return (Map<String,Object>) r.getResults(null).get(0);
    }
    
	public Boolean authenticate(String username, String password){
		Boolean result=false;
		try{
			Map<String, Object> nonceReply = command(Query.start("getnonce").is(1));
			if(nonceReply.containsKey("ok") && nonceReply.get("ok").equals(1.0)){
				String nonce  = (String)nonceReply.get("nonce");
				String digest = getAuthenticationDigest(nonce,username,password);
				Map<String, Object> authReply  = command(
					Query.
						start("authenticate").is(1).
						and("user").is(username).
						and("nonce").is(nonce).
						and("key").is(digest)
				);
				if(authReply.containsKey("ok") && authReply.get("ok").equals(1.0)){
					return true; 
				}
			}
		}catch (Exception e) {}
		return result;
	}
	
	private String getAuthenticationDigest(String nonce, String username, String password) throws Exception{
		if(nonce==null||nonce.length()<=0){
			throw new Exception("Authentication Failed");
		}
		String preHash = nonce + username + MD5.hex(username + ":mongo:" + password);

		return  MD5.hex(preHash);
	}
}
