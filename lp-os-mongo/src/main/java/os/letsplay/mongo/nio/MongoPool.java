package os.letsplay.mongo.nio;

import os.letsplay.mongo.Message;
import os.letsplay.mongo.ops.OpReply;

public class MongoPool {
	
	protected String 			host;
	protected Integer 			port;
    protected MongoChannel[] 	channels;
    
    private int availableChannelsCount = 0;
	private int openChannelsCount   = 0;
	
	public int getAvailableChannelsCount(){
		return availableChannelsCount;
	}
	public int getTotalChannelsCount(){
		return channels.length;
	}
	public int getOpenChannelsCount(){
		return openChannelsCount;
	}
	
    public MongoPool(String host, int port, int poolSize){
    	connect(host, port, poolSize);
    }
    
    public MongoPool(String host, int port) {
		this(host,port,20);
	}

	private synchronized MongoChannel channel() {
    	MongoChannel channel = null;
    	availableChannelsCount = 0;
    	for(int i=0;i<channels.length;i++){
    		if(channels[i]==null){
    			channels[i] = new MongoChannel(host,port);
    			openChannelsCount++;
    		}
    		if(channels[i].available()){
    			availableChannelsCount++;
    			channel = channels[i];
    			break;
    		}
    	}
    	if(channel==null){
    		channel = channels[(int)(Math.random()*channels.length)];
    	}
    	return channel;
    }
	
    public OpReply send(Message message) {
    	return send(message,2000);
    }
    
    public OpReply send(Message message, int timeout) {
    	return channel().send(message,timeout);
    }
    
    public void close(){
    	if(channels!=null){
    		for(int i=0;i<channels.length;i++){
        		if(channels[i]!=null){
        			channels[i].close();
        		}
        	}
    		this.channels = new MongoChannel[channels.length];
    	}
    }
    
    public void connect(String host,Integer port, Integer poolSize){
    	close();
    	this.host		= host;
    	this.port		= port;
    	this.channels 	= new MongoChannel[poolSize];
    }
   
}
