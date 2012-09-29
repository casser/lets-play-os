package os.letsplay.mongo.models;


import java.util.Date;
import os.letsplay.bson.BsonId;
import os.letsplay.bson.annotations.BsonDocument;
import os.letsplay.utils.reflection.annotations.Property;

/**
 *
 * @author Sergey Mamyan <sergey.mamyan@gmail.com>
 */

@BsonDocument(collection = "messages")
public class Message {

    @Property(index = 1)
    protected BsonId   id;
    public    BsonId   id() {
        return id;
    }
    public    Message id(BsonId value) {
        id = (BsonId) value;return this;
    }
    
    @Property(index=2)
    private BsonId      sender;
    public  BsonId      sender() {
		return sender;
    }
    public  Message     sender(BsonId value) {
		sender = value;return this;
    }
    
    @Property(index=3)
    private String      content;
    public  String      content() {
		return content;
    }
    public  Message     content(String value) {
		content = value;return this;
    }
	
    @Property(index=4)
	private Date        sentAt;
    public  Date        sentAt() {
        return sentAt;
    }
    public  Message     sentAt(Date value) {
        sentAt = value;return this;
    }
	
    @Property(index=5)
    private Profiles    recipients;
    public  Profiles    recipients() {
    	if(recipients==null){
    		recipients=new Profiles();
    	}
		return recipients;
    }
    public  Message     recipients(Profiles value) {
		recipients = value;return this;
    }
    
    @Property(index=6)
    private Acceptances acceptances;
    public  Acceptances acceptances(){
    	if(acceptances==null){
    		acceptances=new Acceptances();
    	}
        return acceptances;
    }
    public  Message     acceptances(Acceptances value){
        acceptances = value;return this;
    }
    
}
