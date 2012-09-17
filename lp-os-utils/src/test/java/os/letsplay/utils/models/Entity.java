package os.letsplay.utils.models;

import java.util.Date;


public class Entity {
	
	private Long id;
	private Date createdAt;
	private Date updatedAt;
	
	public Long id(){
		return id;
	}
	public Entity id(Long value){
		this.id = value;
		return this;
	}
	
	public Date createdAt(){
		return createdAt;
	}
	public Entity createdAt(Date value){
		this.createdAt = value;
		return this;
	}
	
	public Date updatedAt(){
		return updatedAt;
	}
	public Entity updatedAt(Date value){
		this.updatedAt = value;
		return this;
	}
	
	
	public void setHambal(String value){
		//
	}
}
