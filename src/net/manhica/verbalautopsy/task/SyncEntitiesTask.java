package net.manhica.verbalautopsy.task;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import net.manhica.verbalautopsy.R;
import net.manhica.verbalautopsy.database.Database;
import net.manhica.verbalautopsy.model.DeadIndividual;
import net.manhica.verbalautopsy.model.FieldWorker;
import net.manhica.verbalautopsy.model.Household;
import net.manhica.verbalautopsy.model.Neighborhood;
import net.manhica.verbalautopsy.model.Table;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * AsyncTask responsible for downloading the OpenHDS "database", that is a
 * subset of the OpenHDS database records. It does the downloading
 * incrementally, by downloading parts of the data one at a time. For example,
 * it gets all locations and then retrieves all individuals. Ordering is
 * somewhat important here, because the database has a few foreign key
 * references that must be satisfied (e.g. individual references a location
 * location)
 */
public class SyncEntitiesTask extends
		AsyncTask<Void, Integer, String> {

	private static final String API_PATH = "/files/verbal_autopsies";

	private SyncDatabaseListener listener;
	private ContentResolver resolver;

	private UsernamePasswordCredentials creds;
	private ProgressDialog dialog;
	private HttpGet httpGet;
	private HttpClient client;

	private String baseurl;
	private String username;
	private String password;

	String lastExtId;

	private final List<Table> values = new ArrayList<Table>();
	private final ContentValues[] emptyArray = new ContentValues[] {};

	private State state;
	private Entity entity;

	private Database database;
	
	private enum State {
		DOWNLOADING, SAVING
	}

	private enum Entity {
		FIELDWORKERS, INDIVIDUALS, NEIGHBORHOODS, HOUSEHOLDS
	}

	private Context mContext;
	
	public SyncEntitiesTask(String url, String username, String password, ProgressDialog dialog, Context context,
			SyncDatabaseListener listener) {
		this.baseurl = url;
		this.username = username;
		this.password = password;
		this.dialog = dialog;
		this.listener = listener;
		this.resolver = context.getContentResolver();
		this.mContext = context;
		
		database = new Database(context);
		
		dialog.show();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		StringBuilder builder = new StringBuilder();
		
		switch (state) {
		case DOWNLOADING:
			builder.append(mContext.getString(R.string.downloading_lbl));
			break;
		case SAVING:
			builder.append(mContext.getString(R.string.saving_lbl));
			break;
		}

		switch (entity) {
		case FIELDWORKERS:
			builder.append(" " + mContext.getString(R.string.fieldworkers_lbl));
			break;
		case INDIVIDUALS:
			builder.append(" " + mContext.getString(R.string.dead_individuals_lbl));
			break;		
		case NEIGHBORHOODS:
			builder.append(" " + mContext.getString(R.string.neighborhoods_lbl));
			break;		
		case HOUSEHOLDS:
			builder.append(" " + mContext.getString(R.string.households_lbl));
			break;		
		}

		if (values.length > 0) {
			//builder.append(" " + mContext.getString(R.string.sync_task_saved) + " " + values[0] + " " + mContext.getString(R.string.sync_task_items));
			builder.append(". " + mContext.getString(R.string.saved_lbl) + " "  + values[0] + " " + mContext.getString(R.string.records_lbl) );
		}	

		dialog.setMessage(builder.toString());
	}

	@Override
	protected String doInBackground(Void... params) {
		creds = new UsernamePasswordCredentials(username, password);

		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 60000);
		HttpConnectionParams.setSoTimeout(httpParameters, 90000);
		HttpConnectionParams.setSocketBufferSize(httpParameters, 8192);
		client = new DefaultHttpClient(httpParameters);

		// at this point, we don't care to be smart about which data to
		// download, we simply download it all
		deleteAllTables();

		try {
			entity = Entity.FIELDWORKERS;
			processUrl(baseurl + API_PATH + "/fieldworkers.xml");

			entity = Entity.INDIVIDUALS;
			processUrl(baseurl + API_PATH + "/verbal-autopsies.xml");
			
		} catch (Exception e) {
			e.printStackTrace();
			return "Failure";//HttpTask.EndResult.FAILURE;
		}

		return "Success"; //HttpTask.EndResult.SUCCESS;
	}

	private void deleteAllTables() {
		// ordering is somewhat important during delete. a few tables have
		// foreign keys
		
		database.open();
		
		database.delete(FieldWorker.class, null, null);
		database.delete(Neighborhood.class, null, null);
		database.delete(Household.class, null, null);
		database.delete(DeadIndividual.class, null, null);
		
		database.close();
		
	}

	private void processUrl(String url) throws Exception {
		state = State.DOWNLOADING;
		publishProgress();

		Log.d("processing", ""+url);
		
		httpGet = new HttpGet(url);
		processResponse();
	}

	private void processResponse() throws Exception {
		InputStream inputStream = getResponse();
		if (inputStream != null)
			processXMLDocument(inputStream);
	}

	private InputStream getResponse() throws AuthenticationException, ClientProtocolException, IOException {
		HttpResponse response = null;

		httpGet.addHeader(new BasicScheme().authenticate(creds, httpGet));
		httpGet.addHeader("content-type", "application/xml");
		response = client.execute(httpGet);

		HttpEntity entity = response.getEntity();
		return entity.getContent();
	}

	private void processXMLDocument(InputStream content) throws Exception {
		state = State.DOWNLOADING;

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);

		XmlPullParser parser = factory.newPullParser();
		parser.setInput(new InputStreamReader(content));

		Log.d("parser", ""+parser);
		
		int eventType = parser.getEventType();
		
		
		while (eventType != XmlPullParser.END_DOCUMENT && !isCancelled()) {
			String name = null;

			//Log.d("parser", "eType: "+eventType +", name: " + parser.getName());
			
			
			switch (eventType) {
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("count")) {
					parser.next();
					int cnt = Integer.parseInt(parser.getText());
					publishProgress(cnt);
					parser.nextTag();
				} else if (name.equalsIgnoreCase("fieldworkers")) {
					processFieldworkersParams(parser);
				} else if (name.equalsIgnoreCase("deaths")) {
					processDeathsParams(parser);
				} 
				break;
			}
						
			eventType = parser.next();
		}
	}

	private void processFieldworkersParams(XmlPullParser parser) throws XmlPullParserException, IOException {
				
		database.open();
		
		parser.nextTag();

		int count = 0;
		values.clear();
		
		while (notEndOfXmlDoc("fieldworkers", parser)) {
			count++;
						
			FieldWorker fw = new FieldWorker();
			
			parser.nextTag(); //extId
			fw.setExtId(parser.nextText());
			
			//Log.d("extId", parser.nextText());
			
			parser.nextTag(); //firstName						
			fw.setFirstName(parser.nextText());
			
			//Log.d("firstName", parser.nextText());
			
			parser.nextTag(); //lastName
			fw.setLastName(parser.nextText());
			
			//Log.d("lastName", parser.nextText());
			
			parser.nextTag(); //passwordHash
			fw.setPasswordHash(parser.nextText());
			
			//Log.d("passwordHash", parser.nextText());
						
			values.add(fw);
			
			publishProgress(count);

			parser.nextTag(); // </fieldworker>
			parser.nextTag(); // <fieldworker>			
			
		}
		
		state = State.SAVING;
		entity = Entity.FIELDWORKERS;
		
		if (!values.isEmpty()) {
			count = 0;
			for (Table t : values){
				count++;
				database.insert(t);
				publishProgress(count);
			}
		}
		
		database.close();
	}

	private void processDeathsParams(XmlPullParser parser) throws XmlPullParserException, IOException {
		
		Set<String> listOfNeighborhoods = new HashSet<String>();
		Set<String> listOfHouseholds = new HashSet<String>();
		List<Neighborhood> neighs = new ArrayList<Neighborhood>();
		List<Household> houses = new ArrayList<Household>();
		
		
		database.open();
		
		parser.nextTag();

		int count = 0;
		values.clear();
		
		while (notEndOfXmlDoc("deaths", parser)) {
			count++;
						
			DeadIndividual indv = new DeadIndividual();
			
			parser.nextTag(); //roundNumber
			indv.setRoundNumber(parser.nextText());

			//Log.d("roundNumber", indv.getRoundNumber());

			parser.nextTag(); //locationId
			indv.setLocationId(parser.nextText());

			//Log.d("locationId", indv.getLocationId());

			parser.nextTag(); //individualId
			indv.setIndividualId(parser.nextText());

			//Log.d("individualId", indv.getIndividualId());

			parser.nextTag(); //cluster
			indv.setCluster(parser.nextText());

			//Log.d("cluster", indv.getCluster());

			parser.nextTag(); //neighborhood
			indv.setNeighborhood(parser.nextText());

			//Log.d("neighborhood", indv.getNeighborhood());

			parser.nextTag(); //householdNo
			indv.setHouseholdNo(parser.nextText());

			//Log.d("householdNo", indv.getHouseholdNo());

			parser.nextTag(); //permId
			indv.setPermId(parser.nextText());

			//Log.d("permId", indv.getPermId());

			parser.nextTag(); //name
			indv.setName(parser.nextText());

			//Log.d("name", indv.getName());

			parser.nextTag(); //gender
			indv.setGender(parser.nextText());

			//Log.d("gender", indv.getGender());

			parser.nextTag(); //dateOfBirth
			indv.setDateOfBirth(parser.nextText());

			//Log.d("dateOfBirth", indv.getDateOfBirth());

			parser.nextTag(); //dateOfDeath
			indv.setDateOfDeath(parser.nextText());

			//Log.d("dateOfDeath", indv.getDateOfDeath());

			parser.nextTag(); //motherId
			indv.setMotherId(parser.nextText());

			//Log.d("motherId", indv.getMotherId());

			parser.nextTag(); //motherPermId
			indv.setMotherPermId(parser.nextText());

			//Log.d("motherPermId", indv.getMotherPermId());

			parser.nextTag(); //motherName
			indv.setMotherName(parser.nextText());

			//Log.d("motherName", indv.getMotherName());

			parser.nextTag(); //fatherId
			indv.setFatherId(parser.nextText());

			//Log.d("fatherId", indv.getFatherId());

			parser.nextTag(); //fatherPermId
			indv.setFatherPermId(parser.nextText());

			//Log.d("fatherPermId", indv.getFatherPermId());

			parser.nextTag(); //fatherName
			indv.setFatherName(parser.nextText());

			//Log.d("fatherName", indv.getFatherName());

			parser.nextTag(); //verbalAutopsyType
			indv.setVerbalAutopsyType(parser.nextText());

			//Log.d("verbalAutopsyType", indv.getVerbalAutopsyType());

			parser.nextTag(); //verbalAutopsyUuid
			indv.setVerbalAutopsyUuid(parser.nextText());

			//Log.d("verbalAutopsyUuid", indv.getVerbalAutopsyUuid());

			parser.nextTag(); //verbalAutopsyProcessed
			indv.setVerbalAutopsyProcessed(parser.nextText());

			//Log.d("verbalAutopsyProcessed", indv.getVerbalAutopsyProcessed());

			values.add(indv);
			
			publishProgress(count);
			
			parser.nextTag(); // </individual>
			parser.nextTag(); // <individual>
			
//			if (count==2) break;
			
			if (!listOfNeighborhoods.contains(indv.getNeighborhood())){
				listOfNeighborhoods.add(indv.getNeighborhood());
				
				Neighborhood nb = new Neighborhood("", indv.getNeighborhood(), Integer.parseInt(indv.getCluster()));
				neighs.add(nb);				
			}
						
			if (!listOfHouseholds.contains(indv.getHouseholdNo())){
				listOfHouseholds.add(indv.getHouseholdNo());
				
				Household hh = new Household(indv.getHouseholdNo(), indv.getLocationId(), "");
				houses.add(hh);				
			}
			
		}
		
		state = State.SAVING;
						
		entity = Entity.NEIGHBORHOODS;
		if (!neighs.isEmpty()){
			count = 0;
			for (Table t : neighs){
				count++;
				database.insert(t);
				publishProgress(count);
			}
			
		}
		
		entity = Entity.HOUSEHOLDS;
		if (!houses.isEmpty()){
			count = 0;
			for (Table t : houses){
				count++;
				database.insert(t);
				publishProgress(count);
			}
		}
		
		entity = Entity.INDIVIDUALS;
		if (!values.isEmpty()) {
			count = 0;
			for (Table t : values){
				count++;
				database.insert(t);
				publishProgress(count);
			}
		}
		
		database.close();
	}
	
	private boolean notEndOfXmlDoc(String element, XmlPullParser parser) throws XmlPullParserException {
		return !element.equals(parser.getName())
				&& parser.getEventType() != XmlPullParser.END_TAG
				&& !isCancelled();
	}

	
	protected void onPostExecute(String result) {
		listener.collectionComplete(result);
		dialog.hide();
	}
}
