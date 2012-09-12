package os.letsplay.pongo;

import java.net.UnknownHostException;

import os.letsplay.bson.BsonModel;
import os.letsplay.mongo.Collection;
import os.letsplay.mongo.Database;
import os.letsplay.mongo.Mongo;
import play.Configuration;
import play.Logger;
import play.Play;


public class Pongo {

    private static volatile Pongo INSTANCE = null;

    private Mongo mongo = null;
    private Database database = null;
    
    private Pongo() throws UnknownHostException {
    	Configuration config = Play.application().configuration();
    	
    	String  dbHost   = config.getString("pongo.host");
    	Integer dbPort   = config.getInt   ("pongo.port");
    	String  dbName   = config.getString("pongo.name");
    	String  dbUser	 = config.getString("pongo.user");
    	String  dbPass   = config.getString("pongo.pass");
    	
    	Logger.info("Starting pongo <mongo://"+(dbUser!=null?dbUser+":"+dbPass+"@":"")+dbHost+":"+dbPort+"/"+dbName+">");
    	    	
    	mongo = new Mongo(dbHost,dbPort);
    	
    	database = mongo.getDB(dbName);
    	
    	if(dbUser!=null && dbPass!=null && dbUser.length()>0 && dbPass.length()>0){
    		database.authenticate(dbUser, dbPass);
    	}
    }

    public static Pongo getInstance() {
        if (INSTANCE == null) {
            synchronized (Pongo.class) {
                if (INSTANCE == null) {
                    try {
                        INSTANCE = new Pongo();
                    } catch (Exception e) {
                        Logger.error("UnknownHostException", e);
                    }
                }
            }
        }
        return INSTANCE;
    }

    public static void restart() {
    	shutdown();
        getInstance();
    }
    
    public static void shutdown() {
        if(INSTANCE!=null){
        	Pongo.mongo().close();
        	INSTANCE.database = null;
        	INSTANCE.mongo    = null;
        	INSTANCE 		  = null;
        }
    }
    
    public static Mongo mongo() {
        return getInstance().mongo;
    }
    
    public static Database database() {
        return getInstance().database;
    }
    
    public static <T extends BsonModel> Collection<T> getCollection(Class<T> type) {
        return database().getCollection(type);
    }

}