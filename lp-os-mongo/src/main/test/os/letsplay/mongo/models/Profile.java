package os.letsplay.mongo.models;

import os.letsplay.utils.reflection.Simple;

/**
 *
 * @author Sergey Mamyan <sergey.mamyan@gmail.com>
 */
public class Profile implements Simple, Comparable<Profile> {

    private String value;

    public Profile(Network network, String id) {
		this(id+"@"+network.name());
    }
    
    public  String id() {
		return value.split("@")[0];
    }
    
    public Network network() {
    	try{
    		return Network.valueOf(value.split("@")[1]);
    	}catch(Exception ex){
    		return Network.EMAIL;
    	}
    }

    public Profile(String value) {
		value = value.toLowerCase();
		if(value.matches("^[-.\\w]{1,64}@[-.\\w]{1,64}$")){
			this.value = value;
		}else{
			throw new IllegalArgumentException("Invalid profile string <"+value+">");
		}
    }
    
    @Override
    public String toString() {
       	return value;
    }
    public static Profile valueOf(String str){
		return new Profile(str);
    }

    @Override
    public int hashCode() {
		return value.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
		if(obj==null){
			return false;
		}    
		return value.equals(obj.toString());
    }
    
    public int compareTo(Profile o) {
		return this.value.compareTo(o.value);
    }
    
}
