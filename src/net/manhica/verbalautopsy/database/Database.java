package net.manhica.verbalautopsy.database;

import java.util.Collection;

import net.manhica.verbalautopsy.model.Table;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class Database {
	
	public static final String DATABASE_NAME = "deads_database.db";
	public static final int DATABASE_VERSION = 1;
	
	public static final class FieldWorker implements BaseColumns  {
		public static final String TABLE_NAME = "fieldworker";
		
		public static final String COLUMN_EXTID = "extId";
		public static final String COLUMN_PASSWORDHASH = "passwordHash";
		public static final String COLUMN_FIRSTNAME = "firstName";
		public static final String COLUMN_LASTNAME = "lastName";
		
		public static final String[] ALL_COLUMNS = {COLUMN_EXTID, COLUMN_FIRSTNAME, COLUMN_LASTNAME, COLUMN_PASSWORDHASH};
	}
	
	public static final class Neighborhood implements BaseColumns {
		public static final String TABLE_NAME = "neighborhood";
		
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_CODE = "code";
		public static final String COLUMN_CLUSTER = "cluster";
		
		public static final String[] ALL_COLUMNS = {COLUMN_NAME, COLUMN_CODE, COLUMN_CLUSTER};
	}
	
	public static final class Household implements BaseColumns  {
		public static final String TABLE_NAME = "household";
		
		public static final String COLUMN_NUMBER = "number";
		public static final String COLUMN_EXTID = "extId";
		public static final String COLUMN_HEAD = "head";
		
		public static final String[] ALL_COLUMNS = {COLUMN_EXTID, COLUMN_NUMBER, COLUMN_EXTID, COLUMN_HEAD};
	}
	
	
	public static final class Individual implements BaseColumns  {
		public static final String TABLE_NAME = "individual";
		
		public static final String COLUMN_UUID = "uuid";
		public static final String COLUMN_LOCATIONID = "locationId";
		public static final String COLUMN_INDIVIDUALID = "individualId";
		public static final String COLUMN_CLUSTER = "cluster";
		public static final String COLUMN_NEIGHBORHOOD = "neighborhood";
		public static final String COLUMN_HOUSEHOLDNO = "householdNo";
		public static final String COLUMN_PERMID = "permId";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_GENDER = "gender";
		public static final String COLUMN_DATEOFBIRTH = "dateOfBirth";
		public static final String COLUMN_DATEOFDEATH = "dateOfDeath";
		public static final String COLUMN_VERBALAUTOPSYTYPE = "verbalAutopsyType";
		public static final String COLUMN_VERBALAUTOPSYUUID = "verbalAutopsyUuid";
		public static final String COLUMN_VERBALAUTOPSYPROCESSED = "verbalAutopsyProcessed";
		public static final String COLUMN_ROUNDNUMBER = "roundNumber";
		public static final String COLUMN_MOTHERID = "motherId";
		public static final String COLUMN_MOTHERPERMID = "motherPermId";
		public static final String COLUMN_MOTHERNAME = "motherName";
		public static final String COLUMN_FATHERID = "fatherId";
		public static final String COLUMN_FATHERPERMID = "fatherPermId";
		public static final String COLUMN_FATHERNAME = "fatherName";
		public static final String COLUMN_CONTENT_URI = "lastContentUri";
		
		
		public static final String[] ALL_COLUMNS = {COLUMN_UUID, COLUMN_LOCATIONID,
			COLUMN_INDIVIDUALID, COLUMN_CLUSTER,
			COLUMN_NEIGHBORHOOD, COLUMN_HOUSEHOLDNO, COLUMN_PERMID, COLUMN_NAME,
			COLUMN_GENDER, COLUMN_DATEOFBIRTH, COLUMN_DATEOFDEATH,
			COLUMN_VERBALAUTOPSYTYPE, COLUMN_VERBALAUTOPSYUUID, COLUMN_VERBALAUTOPSYPROCESSED,
			COLUMN_ROUNDNUMBER, COLUMN_MOTHERID, COLUMN_MOTHERPERMID, COLUMN_MOTHERNAME,
			COLUMN_FATHERID, COLUMN_FATHERPERMID, COLUMN_FATHERNAME, COLUMN_CONTENT_URI};
	}
	
	private DatabaseHelper dbHelper;
	private SQLiteDatabase database;
	
	public Database(Context context) {
		dbHelper = new DatabaseHelper(context);
	}
	
	public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	}

    public void close() {
	    dbHelper.close();
	}
    
    public long insert(Table entity){  	    	
    	long insertId = -1;
    	
    	insertId = database.insert(entity.getTableName(), null,  entity.getContentValues());
    	
    	return insertId;
    }
    
    public long insert(Collection<? extends Table> entities){  	    	
    	long insertId = -1;
    	
    	for (Table entity : entities){
    		insertId = database.insert(entity.getTableName(), null,  entity.getContentValues());
    	}
    	
    	return insertId;
    }
    
    public int delete(Class<? extends Table> table, String whereClause, String[] whereArgs){
    	Table entity = newInstance(table);
    	
    	int deleteRows = database.delete(entity.getTableName(), whereClause, whereArgs);
    	return deleteRows;
    }
    
    public int update(Class<? extends Table> table, ContentValues values, String whereClause, String[] whereArgs){    	
    	Table entity = newInstance(table);
    	
    	int rows = database.update(entity.getTableName(), values, whereClause, whereArgs);
    	
    	return rows;
    }
    
    /*
    public int update(Table entity){    	
    	    	
    	long rows = database.update(entity.getTableName(), entity.getContentValues(), BaseColumns._ID + " = ?", new String{entity.get});
    	
    	return 0;
    }
    */
    
    public Cursor query(Class<? extends Table> table, String selection, String[] selectionArgs, String groupBy, String having, String orderBy){    	
    	Table entity = newInstance(table);
    	
    	Cursor cursor = database.query(entity.getTableName(), entity.getColumnNames(), selection, selectionArgs, groupBy, having, orderBy);
        	
    	return cursor;
    }
    
    public Cursor query(Class<? extends Table> table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy){
    	Table entity = newInstance(table);
    	
    	Cursor cursor = database.query(entity.getTableName(), columns, selection, selectionArgs, groupBy, having, orderBy);
        	
    	return cursor;
    }
    
    private Table newInstance(Class<? extends Table> entity){
    	try {
			Table obj =  entity.newInstance();
			return obj;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return null;
    }
    
    
}
