package os.letsplay.mongo.models;

import java.util.TreeSet;

public class Profiles extends TreeSet<Profile> {
    public Profiles add(String profile) {
		add(Profile.valueOf(profile));return this;
    }	
}
