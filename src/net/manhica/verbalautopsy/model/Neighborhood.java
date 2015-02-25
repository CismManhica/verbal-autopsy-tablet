package net.manhica.verbalautopsy.model;

import net.manhica.verbalautopsy.database.Database;
import android.content.ContentValues;

public class Neighborhood implements Table {
	private String name;
	private String code;
	private int cluster;
			
	public Neighborhood() {
		// TODO Auto-generated constructor stub
	}
	
	public Neighborhood(String name, String code, int cluster) {
		super();
		this.name = name;
		this.code = code;
		this.cluster = cluster;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getCluster() {
		return cluster;
	}
	public void setCluster(int cluster) {
		this.cluster = cluster;
	}
	
	@Override
	public String getTableName() {
		return Database.Neighborhood.TABLE_NAME;
	}
	
	@Override
	public ContentValues getContentValues() {
		ContentValues cv = new ContentValues();
		
		cv.put(Database.Neighborhood.COLUMN_CODE, code);
		cv.put(Database.Neighborhood.COLUMN_NAME, name);
		cv.put(Database.Neighborhood.COLUMN_CLUSTER, cluster);		
		
		return cv;
	}
	
	@Override
	public String[] getColumnNames() {	
		return Database.Neighborhood.ALL_COLUMNS;
	}	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.code;
	}
	
	
}
