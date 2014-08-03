package model;

import http.ConnectionOperator;

import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

public class VKPerson {

	public VKPerson(JSONObject interlocutorContent) {
		this.firstName = interlocutorContent.getString("first_name");
		this.lastName = interlocutorContent.getString("last_name");
		this.id = interlocutorContent.getInt("id");
		this.photoURL = interlocutorContent.getString("photo_50");
		VKPerson.knownPersons.put(this.id, this);
	}
	
	public String getPhotoURL() {
		return photoURL;
	}

	@Override
	public String toString() {
		return "VKPerson [firstName=" + firstName + ", lastName=" + lastName
				+ ", id=" + id + ", photoURL=" + photoURL + "]";
	}

	public String getFirstName() {
		return firstName;
	}
	public VKPerson setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}
	public String getLastName() {
		return lastName;
	}
	public VKPerson setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}
	public Integer getId() {
		return id;
	}
	public VKPerson setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public static VKPerson getKnownPerson(int id) {
		VKPerson p = knownPersons.get(id);
		return p != null ? p : loadById(id);
	}
	
	private static VKPerson loadById(int id) {
		return new VKPerson(ConnectionOperator.getUser(id));
	}
	
	public static VKPerson getOwner() {
		return ownerID==0 ? loadOwner() : knownPersons.get(ownerID);
	}
	
	private static VKPerson loadOwner() {
		VKPerson me = new VKPerson(ConnectionOperator.getOwner());
		ownerID = me.getId();
		return me;
	}

	private String firstName;
	private String lastName;
	private Integer id;
	private String photoURL;
	
	private static ConcurrentHashMap<Integer, VKPerson> knownPersons = new ConcurrentHashMap<>();
	private static int ownerID;
}
