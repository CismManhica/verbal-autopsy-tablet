package net.manhica.verbalautopsy.model;

import net.manhica.verbalautopsy.database.Database;
import android.content.ContentValues;

public class Household implements Table{
	private String number;
	private String extId;
	private String head;
	
	public Household() {
		// TODO Auto-generated constructor stub
	}
	
	public Household(String number, String extId, String head) {
		super();
		this.number = number;
		this.extId = extId;
		this.head = head;
	}

	public String getNumber() {
		return number;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getExtId() {
		return extId;
	}
	
	public void setExtId(String extId) {
		this.extId = extId;
	}
	
	public String getHead() {
		return head;
	}
	
	public void setHead(String head) {
		this.head = head;
	}

	@Override
	public String getTableName() {
		return Database.Household.TABLE_NAME;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues cv = new ContentValues();
		
		cv.put(Database.Household.COLUMN_EXTID, extId);
		cv.put(Database.Household.COLUMN_NUMBER, number);
		cv.put(Database.Household.COLUMN_HEAD, head);		
		
		return cv;
	}

	@Override
	public String[] getColumnNames() {
		return Database.Household.ALL_COLUMNS;
	}
	
	
	
}
