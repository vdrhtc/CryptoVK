package model;

import http.ConnectionOperator;

import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

public class VKPerson {

	public VKPerson(JSONObject content) {
		this.firstName = content.getString("first_name");
		this.lastName = content.getString("last_name");
		this.id = content.getInt("id");
		this.photoURL = content.getString("photo_50");
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

	public String getFullName() {
		return firstName+" "+lastName;
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((photoURL == null) ? 0 : photoURL.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof VKPerson)) {
			return false;
		}
		VKPerson other = (VKPerson) obj;
		if (firstName == null) {
			if (other.firstName != null) {
				return false;
			}
		} else if (!firstName.equals(other.firstName)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (lastName == null) {
			if (other.lastName != null) {
				return false;
			}
		} else if (!lastName.equals(other.lastName)) {
			return false;
		}
		if (photoURL == null) {
			if (other.photoURL != null) {
				return false;
			}
		} else if (!photoURL.equals(other.photoURL)) {
			return false;
		}
		return true;
	}
}
