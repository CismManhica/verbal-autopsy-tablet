package net.manhica.verbalautopsy.model;

import java.io.Serializable;

import net.manhica.verbalautopsy.database.Database;
import android.content.ContentValues;

public class FieldWorker implements Serializable, Table {
	
	private static final long serialVersionUID = -8973040054481039466L;
	private String extId;
	private String passwordHash;
	private String firstName;
	private String lastName;
	
	public FieldWorker() { }
	
	public FieldWorker(String extId, String firstName, String lastName) {
		this.extId = extId;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public String getExtId() {
		return extId;
	}
	
	public void setExtId(String extId) {
		this.extId = extId;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	
	public String getFullname(){
		return firstName + " " +lastName;
	}

	@Override
	public String getTableName() {
		return Database.FieldWorker.TABLE_NAME;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues cv = new ContentValues();
		
		cv.put(Database.FieldWorker.COLUMN_EXTID, extId);
		cv.put(Database.FieldWorker.COLUMN_FIRSTNAME, firstName);
		cv.put(Database.FieldWorker.COLUMN_LASTNAME, lastName);
		cv.put(Database.FieldWorker.COLUMN_PASSWORDHASH, passwordHash);
		
		return cv;
	}

	@Override
	public String[] getColumnNames() {		
		return Database.FieldWorker.ALL_COLUMNS;
	}
}
