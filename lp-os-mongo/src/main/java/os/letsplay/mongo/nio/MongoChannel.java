package os.letsplay.mongo.nio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import os.letsplay.mongo.Message;
import os.letsplay.mongo.Messages;
import os.letsplay.mongo.ops.OpReply;
import os.letsplay.utils.BytesUtil;

public class MongoChannel {
	
	
    private Socket 		 socket = null;
    private InputStream  input  = null;
    private OutputStream output = null;
    
    private Boolean available;
    
    public MongoChannel(){
    	available = true;
    }
    
    public MongoChannel(String host, Integer port){
    	this();
    	connect(host, port);
    }
    
    public Boolean available() {
    	return available;
    }
    
    public OpReply send(Message message) {
    	return send(message,10000);
    }
    
    public synchronized OpReply send(Message message, int timeout) {
    	available = false;
		OpReply reply=null;
		
	    try {
	    	byte[] data = message.toBinaryMsg();
			output.write(data, 0, data.length);	
			
			if(message.isSafe()){
				long startTime = System.currentTimeMillis();
				while(input.available() < 4){
					if((System.currentTimeMillis()-startTime)>timeout){
						return null; // Timeout Exception Must be Thrown
					}
				}
				
				byte[] lengthBytes = new byte[4]; 
				input.read(lengthBytes);
				int packageBytesLength 		= BytesUtil.readInt(lengthBytes, 0);
				int availableBytesLength  	= input.available();
				while(availableBytesLength<packageBytesLength-4){
					if((System.currentTimeMillis()-startTime)>timeout){
						return null; // Timeout Exception Must be Thrown
					}
				}
				byte[] buf = new byte[packageBytesLength];
		        input.read(buf, 4, availableBytesLength);
				BytesUtil.writeInt(buf, 0, packageBytesLength);
				reply = (OpReply)Messages.valueOf(buf);
			}
	    }catch (Exception e){
	    	e.printStackTrace(System.err);
	    }
	    available = true;
	    if(reply.isFailed()){
	    	System.err.print(reply.getDocuments());
	    	return null;
	    }
	    return reply;
    }
    
    public void close(){
    	try {
		    socket.close();
		}catch (IOException e){
		    e.printStackTrace();
		}
    }
    
    public void connect(String serverHostname, int serverPort){
    	try {
		    socket 		= new Socket(serverHostname, serverPort);
		    input 	= socket.getInputStream();
		    output 	= socket.getOutputStream();
		    System.out.println("Connected to "+serverHostname+":"+serverPort);
		} catch (IOException e){
		    e.printStackTrace(System.err);
		    return;
		}
    }
}
