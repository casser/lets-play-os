package os.letsplay.mongo.ops;

import java.util.ArrayList;
import java.util.List;

import os.letsplay.bson.BSON;
import os.letsplay.bson.BsonByteArray;
import os.letsplay.bson.BsonParseError;
import os.letsplay.mongo.Messages;


public class OpReply extends BaseMsg {
	
	private static final int CursorNotFound=1;
	private static final int QueryFailure=2;
	private static final int ShardConfigStale=4;
	private static final int AwaitCapable=7;
	
	public Boolean isFailed(){
		return (
			((CursorNotFound	&getResponseFlags())	==CursorNotFound	)||
			((QueryFailure		&getResponseFlags())	==QueryFailure		)||
			((ShardConfigStale	&getResponseFlags())	==ShardConfigStale	)||
			((AwaitCapable		&getResponseFlags())	==AwaitCapable		)
		);
	}
	
	private int  responseFlags;
	private long cursorID;
	private int  skip;
	
	private Class<?>  documentType;
	
	
	private List<Object> documents;
	private List<byte[]> frames;
	
	public Class<?> getDocumentType() {
		return documentType;
	}
	public void setDocumentType(Class<?> documentType) {
		this.documentType = documentType;
	}
	
	public int getResponseFlags() {
		return responseFlags;
	}
	public void setResponseFlags(int responseFlags) {
		this.responseFlags = responseFlags;
	}
	
	public long getCursorID() {
		return cursorID;
	}
	public void setCursorID(long cursorID) {
		this.cursorID = cursorID;
	}
	
	public int getSkip() {
		return skip;
	}
	public void setSkip(int skip) {
		this.skip = skip;
	}
	
	public List<Object> getDocuments() throws BsonParseError {
		if(documents==null){
			documents = new ArrayList<Object>();
			for(byte[] bytes:frames){
				documents.add(BSON.decode( bytes, getDocumentType()));
			}
		}
		return documents;
	}
	public void setDocuments(List<Object> documents) {
		this.documents = documents;
	}
	
	public OpReply(int responseFlags, long cursorID, int skip, List<Object> documents) {
		super();
		setSkip(skip);
		setResponseFlags(responseFlags);
		setDocuments(documents);
		setCursorID(cursorID);
	}
	
	public OpReply(byte[] bytes) throws Exception{
		super(bytes);
	}
	
	@Override
	public int getCode() {
		return Messages.OP_REPLY.getCode();
	}
	
	@Override
	public Boolean isSafe() {
		return false;
	}
	
	@Override
	protected void writeBody(BsonByteArray bin) {
		bin.writeInt( getResponseFlags() ); 
		bin.writeLong( getCursorID() );
		bin.writeInt( getSkip() );
		bin.writeInt( documents.size() );
		for( Object document : documents ) {
			bin.writeBytes(BSON.encode( document ) );
		}
	}
	
	@Override
	protected void readBody(BsonByteArray bin) {
		setResponseFlags(bin.readInt()); 
		setCursorID(bin.readLong()); 
		setSkip(bin.readInt()); 
		int length = bin.readInt();
		frames = new ArrayList<byte[]>(length);
		for(int i=0;i<length;i++){
			frames.add(bin.readBytes());
		}
	}
	
	public <T> T getResult(Class<T> clazz) throws BsonParseError {
		return (T) getResults(clazz).get(0);
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getResults(Class<T> clazz) throws BsonParseError {
		setDocumentType(clazz);
		return (List<T>) getDocuments();
	}
	
}
