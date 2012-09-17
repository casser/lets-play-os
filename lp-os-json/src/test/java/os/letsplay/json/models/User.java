package os.letsplay.json.models;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import os.letsplay.utils.reflection.annotations.Property;



public class User {
	
	
	public static class Mappings extends EnumMap<Mappings.Key,Mappings.Value> {
		private static final long serialVersionUID = -805184175429688860L;
		
		public Mappings() {
			super(Key.class);
		}
		
		public static enum Key{
			FB,DC,GP,VK,OD;
		}
		
		public static class Value{
			
			private String id;
			private String token;
			
			public String id() {
				return id;
			}
			public Value id(String id) {
				this.id = id;
				return this;
			}
			public String token() {
				return token;
			}
			public Value token(String token) {
				this.token = token;
				return this;
			}
		}
	}
	
	public static class Neighbors extends LinkedHashMap<String,Neighbors.Value> {
		private static final long serialVersionUID = -6528783000089047183L;
		public static enum Value {NONE,PENDING,ACCEPTED;}
	}
	
	public static class Outbox extends HashSet<String> {
		private static final long serialVersionUID = 4269769049791151040L;
	}
	
	public static class Inbox extends LinkedHashMap<String,Inbox.Value> {
		private static final long serialVersionUID = -5610753277425335005L;
		public static class Value{
			
			@Property(index=1)
			private String sender;
			@Property(index=2)
			private Long createdAt;
			
			public String getSender() {
				return sender;
			}
			public void setSender(String sender) {
				this.sender = sender;
			}
			
			public Long getCreatedAt() {
				return createdAt;
			}
			public void setCreatedAt(Long createdAt) {
				this.createdAt = createdAt;
			}
			
			public Value(){
				createdAt = System.currentTimeMillis();
			}
			public Value(String sender){
				this();
				setSender(sender);
			}
		}
	}
	
	private String 		id;
	private String 		email;
	private Mappings 	mappings;
	private Neighbors 	neighbors;
	private Outbox 		outbox;
	private Inbox  		inbox;
	private Object 		any;
	
	public String getId() {
		return id;
	}
	
	public void id(String id) {
		this.id = id;
	}
	
	public String getEmail() {
		return email;
	}
	public void email(String email) {
		this.email = email;
	}
	
	public Mappings mappings() {
		if(mappings==null){
			mappings = new Mappings();
		}
		return mappings;
	}
	public void setMappings(Mappings mappings) {
		this.mappings = mappings;
	}
	
	public Neighbors neighbors() {
		if(neighbors==null){
			neighbors = new Neighbors();
		}
		return neighbors;
	}
	
	public void neighbors(Neighbors neighbors) {
		this.neighbors = neighbors;
	}
	
	public Inbox inbox() {
		if(inbox==null){
			inbox = new Inbox();
		}
		return inbox;
	}
	public void setInbox(Inbox inbox) {
		this.inbox = inbox;
	}
	
	public Outbox getOutbox() {
		return outbox;
	}
	public void setOutbox(Outbox outbox) {
		this.outbox = outbox;
	}
	
	public Object getAny() {
		return any;
	}
	public void setAny(Object any) {
		this.any = any;
	}
}
