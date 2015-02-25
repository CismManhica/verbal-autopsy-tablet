package net.manhica.verbalautopsy.database;

import android.database.Cursor;
import net.manhica.verbalautopsy.model.DeadIndividual;
import net.manhica.verbalautopsy.model.Household;
import net.manhica.verbalautopsy.model.FieldWorker;
import net.manhica.verbalautopsy.model.Neighborhood;

public class Converter {
	public static FieldWorker cursorToFieldworker(Cursor cursor){
		FieldWorker fw = new FieldWorker();
		
		fw.setExtId(cursor.getString(cursor.getColumnIndex(Database.FieldWorker.COLUMN_EXTID)));
		fw.setFirstName(cursor.getString(cursor.getColumnIndex(Database.FieldWorker.COLUMN_FIRSTNAME)));
		fw.setLastName(cursor.getString(cursor.getColumnIndex(Database.FieldWorker.COLUMN_LASTNAME)));
		fw.setPasswordHash(cursor.getString(cursor.getColumnIndex(Database.FieldWorker.COLUMN_PASSWORDHASH)));
		
		return fw;
	}
	
	public static Neighborhood cursorToNeighborhood(Cursor cursor){
		Neighborhood nb = new Neighborhood();
		
		nb.setCluster(cursor.getInt(cursor.getColumnIndex(Database.Neighborhood.COLUMN_CLUSTER)));
		nb.setCode(cursor.getString(cursor.getColumnIndex(Database.Neighborhood.COLUMN_CODE)));
		nb.setName(cursor.getString(cursor.getColumnIndex(Database.Neighborhood.COLUMN_NAME)));
				
		return nb;
	}
	
	public static Household cursorToHousehold(Cursor cursor){
		Household hh = new Household();
		
		hh.setExtId(cursor.getString(cursor.getColumnIndex(Database.Household.COLUMN_EXTID)));
		hh.setNumber(cursor.getString(cursor.getColumnIndex(Database.Household.COLUMN_NUMBER)));
		hh.setHead(cursor.getString(cursor.getColumnIndex(Database.Household.COLUMN_HEAD)));
		
		return hh;
	}
	
	public static DeadIndividual cursorToIndividual(Cursor cursor){
		DeadIndividual ind = new DeadIndividual();
		
		ind.setUuid(cursor.getString(cursor.getColumnIndex(Database.Individual.COLUMN_UUID)));
		ind.setLocationId(cursor.getString(cursor.getColumnIndex(Database.Individual.COLUMN_LOCATIONID)));
		ind.setIndividualId(cursor.getString(cursor.getColumnIndex(Database.Individual.COLUMN_INDIVIDUALID)));
		ind.setCluster(cursor.getString(cursor.getColumnIndex(Database.Individual.COLUMN_CLUSTER)));
		ind.setNeighborhood(cursor.getString(cursor.getColumnIndex(Database.Individual.COLUMN_NEIGHBORHOOD)));
		ind.setHouseholdNo(cursor.getString(cursor.getColumnIndex(Database.Individual.COLUMN_HOUSEHOLDNO)));
		ind.setPermId(cursor.getString(cursor.getColumnIndex(Database.Individual.COLUMN_PERMID)));
		ind.setName(cursor.getString(cursor.getColumnIndex(Database.Individual.COLUMN_NAME)));
		ind.setGender(cursor.getString(cursor.getColumnIndex(Database.Individual.COLUMN_GENDER)));
		ind.setDateOfBirth(cursor.getString(cursor.getColumnIndex(Database.Individual.COLUMN_DATEOFBIRTH)));
		ind.setDateOfDeath(cursor.getString(cursor.getColumnIndex(Database.Individual.COLUMN_DATEOFDEATH)));
		ind.setVerbalAutopsyType(cursor.getString(cursor.getColumnIndex(Database.Individual.COLUMN_VERBALAUTOPSYTYPE)));
		ind.setVerbalAutopsyUuid(cursor.getString(cursor.getColumnIndex(Database.Individual.COLUMN_VERBALAUTOPSYUUID)));
		ind.setVerbalAutopsyProcessed(cursor.getString(cursor.getColumnIndex(Database.Individual.COLUMN_VERBALAUTOPSYPROCESSED)));
		ind.setRoundNumber(cursor.getString(cursor.getColumnIndex(Database.Individual.COLUMN_ROUNDNUMBER)));
		ind.setMotherId(cursor.getString(cursor.getColumnIndex(Database.Individual.COLUMN_MOTHERID)));
		ind.setMotherPermId(cursor.getString(cursor.getColumnIndex(Database.Individual.COLUMN_MOTHERPERMID)));
		ind.setMotherName(cursor.getString(cursor.getColumnIndex(Database.Individual.COLUMN_MOTHERNAME)));
		ind.setFatherId(cursor.getString(cursor.getColumnIndex(Database.Individual.COLUMN_FATHERID)));
		ind.setFatherPermId(cursor.getString(cursor.getColumnIndex(Database.Individual.COLUMN_FATHERPERMID)));
		ind.setFatherName(cursor.getString(cursor.getColumnIndex(Database.Individual.COLUMN_FATHERNAME)));
		ind.setLastContentUri(cursor.getString(cursor.getColumnIndex(Database.Individual.COLUMN_CONTENT_URI)));
		return ind;
	}
}
