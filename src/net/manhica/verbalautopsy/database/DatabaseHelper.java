package net.manhica.verbalautopsy.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

		
	public DatabaseHelper(Context context) {
		super(context, Database.DATABASE_NAME, null, Database.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_FIELDWORKER);
		db.execSQL(CREATE_TABLE_NEIGHBORHOOD);
		db.execSQL(CREATE_TABLE_HOUSEHOLD);
		db.execSQL(CREATE_TABLE_INDIVIDUAL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	private static final String CREATE_TABLE_FIELDWORKER = " "
	 		+ "CREATE TABLE " + Database.FieldWorker.TABLE_NAME + "(" 
			 + Database.FieldWorker._ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			 + Database.FieldWorker.COLUMN_EXTID + " TEXT,"
			 + Database.FieldWorker.COLUMN_FIRSTNAME + " TEXT,"
			 + Database.FieldWorker.COLUMN_LASTNAME + " TEXT,"
			 + Database.FieldWorker.COLUMN_PASSWORDHASH + " TEXT);"
			 
			 + " CREATE UNIQUE INDEX IDX_FIELDWORKER_EXTID ON " + Database.FieldWorker.TABLE_NAME
             + "(" +  Database.FieldWorker.COLUMN_EXTID + ");"
	 		;
	
	private static final String CREATE_TABLE_NEIGHBORHOOD = " "
	 		+ "CREATE TABLE " + Database.Neighborhood.TABLE_NAME + "(" 
			 + Database.Neighborhood._ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			 + Database.Neighborhood.COLUMN_NAME + " TEXT,"
			 + Database.Neighborhood.COLUMN_CODE + " TEXT,"
			 + Database.Neighborhood.COLUMN_CLUSTER + " TEXT);"
			 
			 + " CREATE UNIQUE INDEX IDX_NEIGHBORHOOD_CODE ON " + Database.Neighborhood.TABLE_NAME
             + "(" +  Database.Neighborhood.COLUMN_CODE + ");"
	 		;
	
	private static final String CREATE_TABLE_HOUSEHOLD = " "
	 		+ "CREATE TABLE " + Database.Household.TABLE_NAME + "(" 
			 + Database.Household._ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			 + Database.Household.COLUMN_EXTID + " TEXT,"
			 + Database.Household.COLUMN_NUMBER + " TEXT,"
			 + Database.Household.COLUMN_HEAD + " TEXT);"
			 
			 + " CREATE UNIQUE INDEX IDX_HOUSEHOLD_EXTID ON " + Database.Household.TABLE_NAME
             + "(" +  Database.Household.COLUMN_EXTID + ");"
             
             + " CREATE UNIQUE INDEX IDX_HOUSEHOLD_NUMBER ON " + Database.Household.TABLE_NAME
             + "(" +  Database.Household.COLUMN_NUMBER + ");"
	 		;
	
	private static final String CREATE_TABLE_INDIVIDUAL = " "
	 		+ "CREATE TABLE " + Database.Individual.TABLE_NAME + "(" 
			 + Database.Individual._ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			 + Database.Individual.COLUMN_UUID + " TEXT,"
			 + Database.Individual.COLUMN_LOCATIONID + " TEXT,"
			 + Database.Individual.COLUMN_INDIVIDUALID + " TEXT,"
			 + Database.Individual.COLUMN_CLUSTER + " TEXT,"
			 + Database.Individual.COLUMN_NEIGHBORHOOD + " TEXT,"
			 + Database.Individual.COLUMN_HOUSEHOLDNO + " TEXT,"
			 + Database.Individual.COLUMN_PERMID + " TEXT,"
			 + Database.Individual.COLUMN_NAME + " TEXT,"
			 + Database.Individual.COLUMN_GENDER + " TEXT,"
			 + Database.Individual.COLUMN_DATEOFBIRTH + " TEXT,"
			 + Database.Individual.COLUMN_DATEOFDEATH + " TEXT,"
			 + Database.Individual.COLUMN_VERBALAUTOPSYTYPE + " TEXT,"
			 + Database.Individual.COLUMN_VERBALAUTOPSYUUID + " TEXT,"
			 + Database.Individual.COLUMN_VERBALAUTOPSYPROCESSED + " TEXT,"
			 + Database.Individual.COLUMN_ROUNDNUMBER + " TEXT,"
			 + Database.Individual.COLUMN_MOTHERID + " TEXT,"
			 + Database.Individual.COLUMN_MOTHERPERMID + " TEXT,"
			 + Database.Individual.COLUMN_MOTHERNAME + " TEXT,"
			 + Database.Individual.COLUMN_FATHERID + " TEXT,"
			 + Database.Individual.COLUMN_FATHERPERMID + " TEXT,"
			 + Database.Individual.COLUMN_FATHERNAME + " TEXT,"
			 + Database.Individual.COLUMN_CONTENT_URI + " TEXT);"
			 
			 + " CREATE UNIQUE INDEX IDX_INDIVIDUAL_UUID ON " + Database.Individual.TABLE_NAME
             + "(" +  Database.Individual.COLUMN_UUID + ");"
			 
			 + " CREATE UNIQUE INDEX IDX_INDIVIDUAL_ID ON " + Database.Individual.TABLE_NAME
             + "(" +  Database.Individual.COLUMN_INDIVIDUALID + ");"
			 
			 + " CREATE UNIQUE INDEX IDX_INDIVIDUAL_PERMID ON " + Database.Individual.TABLE_NAME
             + "(" +  Database.Individual.COLUMN_PERMID + ");"             
	 		;

}
