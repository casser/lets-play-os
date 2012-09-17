package os.letsplay.pongo;

import java.net.UnknownHostException;

import os.letsplay.mongo.Collection;
import os.letsplay.mongo.Database;
import os.letsplay.mongo.Mongo;
import os.letsplay.utils.StringUtils;
import play.Configuration;
import play.Logger;
import play.Play;


public class Pongo {

    private static volatile Pongo INSTANCE = null;
    
    private Database database = null;
    
    private Pongo() throws UnknownHostException {
    	Configuration config = Play.application().configuration();
    	
    	String[]  urls   = config.getString("pongo.urls").split(",");
    	Mongo.init(urls);
    	
    	Logger.info("Starting pongo <"+StringUtils.join(urls,',')+">");
    	
    	database = Mongo.db();
    	
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
        	INSTANCE.database.getMongo().close();
        	INSTANCE.database	= null;
        	INSTANCE 		  	= null;
        }
    }
    
    public static Database database() {
        return getInstance().database;
    }
    
    public static <T> Collection<T> getCollection(Class<T> type) {
        return database().getCollection(type);
    }

}