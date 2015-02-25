package net.manhica.verbalautopsy.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import net.manhica.verbalautopsy.database.Database;
import android.content.ContentValues;

public class DeadIndividual implements Serializable, Table {

    private static final long serialVersionUID = -799404570247633407L;

    private String uuid;
    private String locationId;
    private String individualId;
    private String cluster;
    private String neighborhood;
    private String householdNo;
    private String permId;
    private String name;
    private String gender;
    private String dateOfBirth;
    private String dateOfDeath;
    private String verbalAutopsyType;
    private String verbalAutopsyUuid;
    private String verbalAutopsyProcessed;
    private String roundNumber;
    private String motherId;
    private String motherPermId;
    private String motherName;
    private String fatherId;
    private String fatherPermId;
    private String fatherName;
    private String lastContentUri;

    public DeadIndividual(){
    	/*
        this.uuid = vadeath.getUuid() + "";
        this.locationId = vadeath.getLocationId();
        this.individualId = vadeath.getIndividualId();
        this.cluster = vadeath.getCluster()+"";
        this.neighborhood = vadeath.getNeighborhood();
        this.householdNo = vadeath.getHouseholdNo();
        this.permId = vadeath.getPermId();
        this.name = vadeath.getName();
        this.gender = vadeath.getGender();
        this.dateOfBirth = new SimpleDateFormat("dd-MM-yyyy").format(vadeath.getDateOfBirth());
        this.dateOfDeath = new SimpleDateFormat("dd-MM-yyyy").format(vadeath.getDateOfDeath());
        this.verbalAutopsyType = vadeath.getVerbalAutopsyType()+"";
        this.verbalAutopsyUuid = vadeath.getVerbalAutopsyUuid();
        this.verbalAutopsyProcessed = vadeath.getVerbalAutopsyProcessed()+"";
        this.roundNumber = vadeath.getRoundNumber()+"";
        this.motherId = vadeath.getMotherId();
        this.motherPermId = vadeath.getMotherPermid();
        this.motherName = vadeath.getMotherName();
        this.fatherId = vadeath.getFatherId();
        this.fatherPermId = vadeath.getFatherPermid();
        this.fatherName = vadeath.getFatherName();
        */
    }
    
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getIndividualId() {
        return individualId;
    }

    public void setIndividualId(String individualId) {
        this.individualId = individualId;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getHouseholdNo() {
        return householdNo;
    }

    public void setHouseholdNo(String householdNo) {
        this.householdNo = householdNo;
    }

    public String getPermId() {
        return permId;
    }

    public void setPermId(String permId) {
        this.permId = permId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = convertDate(dateOfBirth);
    }

    public String getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(String dateOfDeath) {
        this.dateOfDeath = convertDate(dateOfDeath);
    }

    public String getVerbalAutopsyType() {
        return verbalAutopsyType;
    }

    public void setVerbalAutopsyType(String verbalAutopsyType) {
        this.verbalAutopsyType = verbalAutopsyType;
    }

    public String getVerbalAutopsyUuid() {
        return verbalAutopsyUuid;
    }

    public void setVerbalAutopsyUuid(String verbalAutopsyUuid) {
        this.verbalAutopsyUuid = verbalAutopsyUuid;
    }

    public String getVerbalAutopsyProcessed() {
        return verbalAutopsyProcessed;
    }

    public void setVerbalAutopsyProcessed(String verbalAutopsyProcessed) {
        this.verbalAutopsyProcessed = verbalAutopsyProcessed;
    }

    public String getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(String roundNumber) {
        this.roundNumber = roundNumber;
    }

    public String getMotherId() {
        return motherId;
    }

    public void setMotherId(String motherId) {
        this.motherId = motherId;
    }
    
    public String getMotherPermId() {
        return motherPermId;
    }

    public void setMotherPermId(String motherPermId) {
        this.motherPermId = motherPermId;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }
    
    public String getFatherPermId() {
        return fatherPermId;
    }

    public void setFatherPermId(String fatherPermId) {
        this.fatherPermId = fatherPermId;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }
 
    public String getLastContentUri() {
		return lastContentUri;
	}
    
    public void setLastContentUri(String lastContentUri) {
		this.lastContentUri = lastContentUri;
	}
    
    // dates come in from the web service in dd-MM-yyyy format but
    // they must be changed to yyyy-MM-dd for ODK Collect
    private String convertDate(String dt) {
    	/*
        try {
            DateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
            DateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd");

            
            
            String date = outFormat.format(inFormat.parse(dt));
            
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        
        return dt;
    }       
    
    public String getXml() {
        return "<individual>\n" +
               "    <uuid>"+uuid+"</uuid>\n" +
               "    <locationId>"+locationId+"</locationId>\n" +
               "    <individualId>"+individualId+"</individualId>\n" +
               "    <cluster>"+cluster+"</cluster>\n" +
               "    <neighborhood>"+neighborhood+"</neighborhood>\n" +
               "    <householdNo>"+householdNo+"</householdNo>\n" +
               "    <permId>"+permId+"</permId>\n" +
               "    <name>"+name+"</name>\n" +
               "    <gender>"+gender+"</gender>\n" +
               "    <dateOfBirth>"+dateOfBirth+"</dateOfBirth>\n" +
               "    <dateOfDeath>"+dateOfDeath+"</dateOfDeath>\n" +
               "    <verbalAutopsyType>"+verbalAutopsyType+"</verbalAutopsyType>\n" +
               "    <verbalAutopsyUuid>"+verbalAutopsyUuid+"</verbalAutopsyUuid>\n" +
               "    <verbalAutopsyProcessed>"+verbalAutopsyProcessed+"</verbalAutopsyProcessed>\n" +
               "    <roundNumber>"+roundNumber+"</roundNumber>\n" +
               "    <motherId>"+motherId+"</motherId>\n" +
               "    <motherPermId>"+motherPermId+"</motherPermId>\n" +
               "    <motherName>"+motherName+"</motherName>\n" +
               "    <fatherId>"+fatherId+"</fatherId>\n" +
               "    <fatherPermId>"+fatherPermId+"</fatherPermId>\n" +
               "    <fatherName>"+fatherName+"</fatherName>\n" +
               "</individual>";
    }

	@Override
	public String getTableName() {
		return Database.Individual.TABLE_NAME;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues cv = new ContentValues();
		
		cv.put(Database.Individual.COLUMN_UUID, uuid);
		cv.put(Database.Individual.COLUMN_LOCATIONID, locationId);
		cv.put(Database.Individual.COLUMN_INDIVIDUALID, individualId);
		cv.put(Database.Individual.COLUMN_CLUSTER, cluster);
		cv.put(Database.Individual.COLUMN_NEIGHBORHOOD, neighborhood);
		cv.put(Database.Individual.COLUMN_HOUSEHOLDNO, householdNo);
		cv.put(Database.Individual.COLUMN_PERMID, permId);
		cv.put(Database.Individual.COLUMN_NAME, name);
		cv.put(Database.Individual.COLUMN_GENDER, gender);
		cv.put(Database.Individual.COLUMN_DATEOFBIRTH, dateOfBirth);
		cv.put(Database.Individual.COLUMN_DATEOFDEATH, dateOfDeath);
		cv.put(Database.Individual.COLUMN_VERBALAUTOPSYTYPE, verbalAutopsyType);
		cv.put(Database.Individual.COLUMN_VERBALAUTOPSYUUID, verbalAutopsyUuid);
		cv.put(Database.Individual.COLUMN_VERBALAUTOPSYPROCESSED, verbalAutopsyProcessed);
		cv.put(Database.Individual.COLUMN_ROUNDNUMBER, roundNumber);
		cv.put(Database.Individual.COLUMN_MOTHERID, motherId);
		cv.put(Database.Individual.COLUMN_MOTHERPERMID, motherPermId);
		cv.put(Database.Individual.COLUMN_MOTHERNAME, motherName);
		cv.put(Database.Individual.COLUMN_FATHERID, fatherId);
		cv.put(Database.Individual.COLUMN_FATHERPERMID, fatherPermId);
		cv.put(Database.Individual.COLUMN_FATHERNAME, fatherName);
		cv.put(Database.Individual.COLUMN_CONTENT_URI, lastContentUri);
		
		return cv;
	}

	@Override
	public String[] getColumnNames() {
		return Database.Individual.ALL_COLUMNS;
	}
}
