package os.letsplay.bson.models;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class Modern {
	
	
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
			
			public static Value create(){
				return new Value();
			}
		}
		
		public static Mappings create() {
			return new Mappings();
		}
		public Mappings insert(Mappings.Key key,Mappings.Value value) {
			put(key,value);return this;
		}
	}
	
	public static class Neighbors extends LinkedHashMap<String,Neighbors.Value> {
		private static final long serialVersionUID = -6528783000089047183L;
		public static enum Value {NONE,PENDING,ACCEPTED;}
		public static Neighbors create() {
			return new Neighbors();
		}
		public Neighbors insert(String key,Neighbors.Value value) {
			put(key,value);return this;
		}
	}
	
	public static class Outbox extends HashSet<String> {
		private static final long serialVersionUID = 4269769049791151040L;
		public static Outbox create() {
			return new Outbox();
		}
		public Outbox insert(String value) {
			add(value);return this;
		}
	}
	
	public static class Inbox extends LinkedHashMap<String,Inbox.Value> {
		private static final long serialVersionUID = -5610753277425335005L;
		public static class Value{
			
			private String sender;
			private Long createdAt;
			
			public String sender() {
				return sender;
			}
			public Value sender(String sender) {
				this.sender = sender; return this;
			}
			
			public Long createdAt() {
				return createdAt;
			}
			public Value createdAt(Long createdAt) {
				this.createdAt = createdAt; return this;
			}
			
			public Value(){
				createdAt = System.currentTimeMillis();
			}
			
			public static Value create(){
				return new Value();
			}
		}
		public static Inbox create() {
			return new Inbox();
		}
		public Inbox insert(String key,Inbox.Value value){
			put(key, value); return this;
		}
	}
	
	private String 		id;
	private String 		email;
	private Mappings 	mappings;
	private Neighbors 	neighbors;
	private Outbox 		outbox;
	private Inbox  		inbox;
	private Object 		any;
	
	public String id() {
		return id;
	}
	public Modern id(String id) {
		this.id = id; return this;
	}
	
	public String email() {
		return email;
	}
	public Modern email(String email) {
		this.email = email; return this;
	}
	
	public Mappings mappings() {
		if(mappings==null){
			mappings = Mappings.create();
		}
		return mappings;
	}
	public Modern mappings(Mappings mappings) {
		this.mappings = mappings; return this;
	}
	
	public Neighbors neighbors() {
		if(neighbors==null){
			neighbors = Neighbors.create();
		}
		return neighbors;
	}
	public Modern neighbors(Neighbors neighbors) {
		this.neighbors = neighbors; return this;
	}
	
	public Inbox inbox() {
		if(inbox==null){
			inbox = Inbox.create();
		}
		return inbox;
	}
	public Modern inbox(Inbox inbox) {
		this.inbox = inbox; return this;
	}
	
	public Outbox outbox() {
		if(outbox==null){
			outbox = Outbox.create();
		}
		return outbox;
	}
	public Modern outbox(Outbox outbox) {
		this.outbox = outbox; return this;
	}
	
	public Object any() {
		return any;
	}
	public Modern any(Object any) {
		this.any = any; return this;
	}
	
	public static Modern create() {
		return new Modern();
	}
}

