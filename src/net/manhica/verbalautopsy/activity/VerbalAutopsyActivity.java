package net.manhica.verbalautopsy.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mz.betainteractive.odk.FormsProviderAPI;
import mz.betainteractive.odk.InstanceProviderAPI;
import mz.betainteractive.odk.listener.OdkFormLoadListener;
import mz.betainteractive.odk.model.FilledForm;
import mz.betainteractive.odk.task.OdkGeneratedFormLoadTask;
import net.manhica.verbalautopsy.R;
import net.manhica.verbalautopsy.adapter.HouseholdArrayAdapter;
import net.manhica.verbalautopsy.adapter.IndividualArrayAdapter;
import net.manhica.verbalautopsy.adapter.NeighborhoodArrayAdapter;
import net.manhica.verbalautopsy.database.Converter;
import net.manhica.verbalautopsy.database.Database;
import net.manhica.verbalautopsy.model.DeadIndividual;
import net.manhica.verbalautopsy.model.FieldWorker;
import net.manhica.verbalautopsy.model.Household;
import net.manhica.verbalautopsy.model.Neighborhood;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


public class VerbalAutopsyActivity extends Activity {

	private Database database;
	private ListView listNeighborhoods, listHouseOrIndividuals;
	
	private FieldWorker fieldWorker; 
	
   	private final String VA_NEONATE = "AVNeonate";  // Under 4 weeks - 28 days
	private final String VA_CHILD = "AVChild";      // Between 4weeks to 14 years
	private final String VA_PERSON = "AVNormal";    // 14 years to ...
	private final String VA_MATERNAL = "AVMaternal"; // Womem - 12 years Up
	
	private String jrFormId;
	private Uri contentUri;
	
	private final int OPEN_ODK_FORM = 1;
	private final int EDITING_EXISTING_ODK_FORM = 2;
	
	private AlertDialog xformUnfinishedDialog;
	private boolean formUnFinished;
	
	private DeadIndividual lastSelectedIndividual;
	private Household lastSelectedHousehold;
	private Neighborhood lastSelectedNeighborhood;
	
	private EditText searchView; 
	
	private enum ListViewState {
		NEIGHBORHOODS, HOUSEHOLDS, INDIVIDUALS
	}
	
	private ListViewState listState;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
               
        database = new Database(this);
        
        listNeighborhoods = (ListView) findViewById(R.id.listNeighborhoods);
        listHouseOrIndividuals = (ListView) findViewById(R.id.listHouseOrIndividuals);
        searchView = (EditText) findViewById(R.id.searchView);
        
        listNeighborhoods.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
				onNeighborhoodClicked(position);
			}        	
		});
        
        listHouseOrIndividuals.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
				if (listHouseOrIndividuals.getAdapter() instanceof HouseholdArrayAdapter){
					onHouseholdClicked(position);
				}else{ //Individuals
					onIndividualClicked(position);
				}
			}        	
		});
        
        searchView.addTextChangedListener(new TextWatcher() {			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {				
				if (listState == ListViewState.HOUSEHOLDS){
					loadHouseholds(s.toString());
				}else if (listState == ListViewState.NEIGHBORHOODS){
					loadNeighborhoods(s.toString());
				}
								
			}
		});
             
        
        //hello
        this.fieldWorker = (FieldWorker) getIntent().getExtras().get("fieldWorker");        
        TextView helloTxt = (TextView) findViewById(R.id.hello_txt);        
        helloTxt.setText(getString(R.string.hello_world)+", "+this.fieldWorker.getFullname());
                
        loadNeighborhoods();
    }

	private void loadNeighborhoods() {
		database.open();
		
		List<Neighborhood> neighs = new ArrayList<Neighborhood>();
		
		Cursor cursor = database.query(Neighborhood.class, null, null, null, null, null);
        
        while (cursor.moveToNext()){
        	Neighborhood nb = Converter.cursorToNeighborhood(cursor);
        	neighs.add(nb);
        	//Log.d("Neighborhood", nb.getCode()+", "+nb.getName()+", "+nb.getCluster());
        }
        
        NeighborhoodArrayAdapter nba = new NeighborhoodArrayAdapter(this, neighs);
		
        listNeighborhoods.setAdapter(nba);
        
		database.close();
		
		lastSelectedHousehold = null;
		this.listState = ListViewState.NEIGHBORHOODS;
	}
    
	private void loadNeighborhoods(String code) {
		database.open();
		
		List<Neighborhood> neighs = new ArrayList<Neighborhood>();
		
		Cursor cursor = database.query(Neighborhood.class, Database.Neighborhood.COLUMN_CODE + " like ?", 
				   new String[] { "%"+ code +"%" }, null, null, null);
        
        while (cursor.moveToNext()){
        	Neighborhood nb = Converter.cursorToNeighborhood(cursor);
        	neighs.add(nb);
        	//Log.d("Neighborhood", nb.getCode()+", "+nb.getName()+", "+nb.getCluster());
        }
        
        NeighborhoodArrayAdapter nba = new NeighborhoodArrayAdapter(this, neighs);
		
        listNeighborhoods.setAdapter(nba);
        
		database.close();
		
		lastSelectedHousehold = null;
		this.listState = ListViewState.NEIGHBORHOODS;
		
		if (neighs.size()==1){
			onNeighborhoodClicked(0);
		}
	}
	
    private void loadHouseholds(Neighborhood neighborhood){
    	database.open();
    	
    	List<Household> houses = new ArrayList<Household>();
    	
    	Cursor cursor = database.query(Household.class, Database.Household.COLUMN_NUMBER + " like ?", 
    								   new String[] { "%"+ neighborhood.getCode() +"%" }, null, null, null);
    	
    	while (cursor.moveToNext()){
    		Household hh = Converter.cursorToHousehold(cursor);
    		houses.add(hh);
    	}
    	
    	HouseholdArrayAdapter hha = new HouseholdArrayAdapter(this, houses);
    	listHouseOrIndividuals.setAdapter(hha);
    	
    	database.close();
    	
    	this.lastSelectedNeighborhood = neighborhood;
    	this.lastSelectedHousehold = null;
    	this.lastSelectedIndividual = null;
    	
    	this.listState = ListViewState.HOUSEHOLDS;
    }

    private void loadHouseholds(String houseno){
    	database.open();
    	
    	List<Household> houses = new ArrayList<Household>();
    	
    	Cursor cursor = database.query(Household.class, Database.Household.COLUMN_NUMBER + " like ?", 
    								   new String[] { "%"+ houseno +"%" }, null, null, null);
    	
    	while (cursor.moveToNext()){
    		Household hh = Converter.cursorToHousehold(cursor);
    		houses.add(hh);
    	}
    	
    	HouseholdArrayAdapter hha = new HouseholdArrayAdapter(this, houses);
    	listHouseOrIndividuals.setAdapter(hha);
    	
    	database.close();
    	
    	//this.lastSelectedNeighborhood = neighborhood;
    	this.lastSelectedHousehold = null;
    	this.lastSelectedIndividual = null;
    	
    	this.listState = ListViewState.HOUSEHOLDS;
    }
    
    private void loadIndividuals(Household household){
    	database.open();
    	
    	List<DeadIndividual> individuals = new ArrayList<DeadIndividual>();
    	
    	Cursor cursor = database.query(DeadIndividual.class, Database.Individual.COLUMN_HOUSEHOLDNO + " = ?", 
    								   new String[] { household.getNumber() }, null, null, null);
    	
    	while (cursor.moveToNext()){
    		DeadIndividual di = Converter.cursorToIndividual(cursor);
    		individuals.add(di);
    	}
    	
    	IndividualArrayAdapter iaa = new IndividualArrayAdapter(this, individuals);
    	listHouseOrIndividuals.setAdapter(iaa);
    	
    	database.close();
    	
    	this.lastSelectedHousehold = household;
    	lastSelectedIndividual = null;
    	
    	this.listState = ListViewState.INDIVIDUALS;
    }

    private void reloadIndividuals() {
		loadIndividuals(lastSelectedHousehold);		
	}
    
    private void reloadHouseholds() {
		loadHouseholds(lastSelectedNeighborhood);		
	}
    
    private void reloadNeighborhoods() {
		loadNeighborhoods();	
		listHouseOrIndividuals.setAdapter(null);
	}
    
    protected void onIndividualClicked(int position) {
    	DeadIndividual individual = (DeadIndividual) listHouseOrIndividuals.getItemAtPosition(position);
    	
    	this.lastSelectedIndividual = individual;
    	
    	
    	if (individual.getVerbalAutopsyProcessed().equalsIgnoreCase("1")){
    		loadLastCreatedForm();
    		return;
    	}
    	
    	String selectedVaForm = "";
    	int verbalAutopsyType = Integer.parseInt(individual.getVerbalAutopsyType());
    	
    	switch (verbalAutopsyType) {
			case 1: selectedVaForm = VA_NEONATE; break;
			case 2: selectedVaForm = VA_CHILD; break;
			case 3: selectedVaForm = VA_PERSON; break;
			case 4: selectedVaForm = VA_MATERNAL; break;			
		}
    	
    	FilledForm filledForm = new FilledForm(selectedVaForm);
    	filledForm.setValues(individual.getContentValues());
    	
    	filledForm.put("houseno", individual.getHouseholdNo());
    	filledForm.put("fieldWorkerId", fieldWorker.getExtId());
    	filledForm.put("dob", individual.getDateOfBirth());
    	filledForm.put("dthDate", individual.getDateOfDeath());
    	filledForm.put("inFieldDeath", "2");
    	
    	
    	loadForm(filledForm);    	
    	   	
    }	

	protected void onHouseholdClicked(int position) {
		Household household = (Household) listHouseOrIndividuals.getItemAtPosition(position);
		loadIndividuals(household);
	}


	protected void onNeighborhoodClicked(int position) {
		Neighborhood neighborhood = (Neighborhood) listNeighborhoods.getItemAtPosition(position);
		
		loadHouseholds(neighborhood);		
	}
    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }    
    
    @Override
    public void onBackPressed() {
    	
    	switch (listState){
   	    	case INDIVIDUALS: reloadHouseholds(); return;
   	    	case HOUSEHOLDS: reloadNeighborhoods(); return;
    	}    	
    	
    	super.onBackPressed();
    }
    
    /* Loading ODK Form - Methods*/    
	public void loadForm(final FilledForm filledForm) {
		
		new OdkGeneratedFormLoadTask(this, filledForm, new OdkFormLoadListener() {
            public void onOdkFormLoadSuccess(Uri contentUri) {
            	//Log.d("contenturi", contentUri+"");
            	
            	Cursor cursor = getCursorForFormsProvider(filledForm.getFormName());
                if (cursor.moveToFirst()) {
                    jrFormId = cursor.getString(0);
                }
                
                VerbalAutopsyActivity.this.contentUri = contentUri;
                
                startActivityForResult(new Intent(Intent.ACTION_EDIT, contentUri), OPEN_ODK_FORM);
            }

            public void onOdkFormLoadFailure() {
                //Toast.makeText(MainActivity.this, "Cant open ODK Form", 4000);
            	//Log.d("Cant open ODK Form", "odk");
            	createXFormNotFoundDialog();
            }
        }).execute();
    }
	
    private void createXFormNotFoundDialog() {
        //xFormNotFound = true;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
          alertDialogBuilder.setTitle(getString(R.string.warning_lbl));
          alertDialogBuilder.setMessage(getString(R.string.couldnt_open_xform_lbl));
          alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //xFormNotFound = false;
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    
    private void createUnfinishedFormDialog() {
        formUnFinished = true;
        xformUnfinishedDialog = null; 
                
        if (xformUnfinishedDialog == null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getString(R.string.warning_lbl));
            alertDialogBuilder.setMessage(getString(R.string.update_unfinish_msg1));
            alertDialogBuilder.setCancelable(true);
            
            alertDialogBuilder.setPositiveButton(getString(R.string.update_unfinish_pos_button), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    formUnFinished = false;
                    xformUnfinishedDialog.hide();
                    getContentResolver().delete(contentUri, InstanceProviderAPI.InstanceColumns.STATUS + "=?",
                            new String[] { InstanceProviderAPI.STATUS_INCOMPLETE });
                }
            });
            
            alertDialogBuilder.setNeutralButton(getString(R.string.update_unfinish_neutral_button), new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Toast.makeText(VerbalAutopsyActivity.this, getString(R.string.update_unfinish_saved), 3000);
					updateIndividualForm(lastSelectedIndividual, contentUri);
				}
			});
            
            alertDialogBuilder.setNegativeButton(getString(R.string.update_unfinish_neg_button), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    formUnFinished = false;
                    xformUnfinishedDialog.hide();
                    startActivityForResult(new Intent(Intent.ACTION_EDIT, contentUri), OPEN_ODK_FORM);
                }
            });
                        
            
            xformUnfinishedDialog = alertDialogBuilder.create();
        }

        xformUnfinishedDialog.show();
    }
	
	private Cursor getCursorForFormsProvider(String name) {
    	ContentResolver resolver = getContentResolver();
        return resolver.query(FormsProviderAPI.FormsColumns.CONTENT_URI, new String[] {
                FormsProviderAPI.FormsColumns.JR_FORM_ID, FormsProviderAPI.FormsColumns.FORM_FILE_PATH },
                FormsProviderAPI.FormsColumns.JR_FORM_ID + " like ?", new String[] { name + "%" }, null);
    }
  
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		super.onActivityResult(requestCode, resultCode, data);
		
		Log.d("activityResult", "res-"+requestCode+" "+(new Date().toString()) );
		
		switch (requestCode) {
        	case OPEN_ODK_FORM:
        		handleXformResult(requestCode, resultCode, data);
        		break;
        	case EDITING_EXISTING_ODK_FORM:
        		handleXformResult(requestCode, resultCode, data);
        		break;
        }
	}
 
	private void handleXformResult(int requestCode, int resultCode, Intent data) {		
        if (resultCode == RESULT_OK) {            
            new CheckFormStatus(getContentResolver(), contentUri).execute();
        } else {
        	
        	if (requestCode == EDITING_EXISTING_ODK_FORM){
        		updateIndividualFormRemoveProcessed(lastSelectedIndividual);
        	}
        	
            Toast.makeText(this, getString(R.string.odk_problem_lbl), Toast.LENGTH_LONG).show();    		
        }
        
    }
	
	class CheckFormStatus extends AsyncTask<Void, Void, Boolean> {

        private ContentResolver resolver;
        private Uri contentUri;

        public CheckFormStatus(ContentResolver resolver, Uri contentUri) {
            this.resolver = resolver;
            this.contentUri = contentUri;
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            Cursor cursor = resolver.query(contentUri, new String[] { InstanceProviderAPI.InstanceColumns.STATUS,
                    InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH },
                    InstanceProviderAPI.InstanceColumns.STATUS + "=?",
                    new String[] { InstanceProviderAPI.STATUS_COMPLETE }, null);
            
            Log.d("Running check form", ""+(new Date().toString()) );
            
            if (cursor.moveToNext()) {
            	Log.d("move next", ""+cursor.getString(0));
                String filepath = cursor.getString(cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH));
                try{               	               	
                	
                	/*
	                if(updatable != null){
	                	updatable.updateDatabase(getContentResolver(), filepath, jrFormId);
	                	updatable = null;
	                }
	                */
                }finally{
                	try{
                		cursor.close();
                	}catch(Exception e){
                		System.err.println("Exception while trying to close cursor !");
                		e.printStackTrace();
                	}
                }                               
                
                return true;
                
            } else {
            	Log.d("move next", "couldnt find");
            	
            	try{
            		cursor.close();
            	}catch(Exception e){
            		System.err.println("Exception while trying to close cursor !");
            		e.printStackTrace();
            	}            	
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            //hideProgressFragment();
            
            if (result) {
            	//When everything is OK - save current form location
            	updateIndividualForm(lastSelectedIndividual, contentUri);
            	
            } else {            	
                createUnfinishedFormDialog();
            }            
        }
    }

	public void updateIndividualForm(DeadIndividual individual, Uri contentUri2) {
		database.open();
		
		ContentValues cv = new ContentValues();
		
		cv.put(Database.Individual.COLUMN_CONTENT_URI, contentUri2.toString());
		cv.put(Database.Individual.COLUMN_VERBALAUTOPSYPROCESSED, "1");
				
		int result = database.update(DeadIndividual.class, cv, Database.Individual.COLUMN_INDIVIDUALID+" = ?", new String[] { individual.getIndividualId() });
			
		database.close();
		
		reloadIndividuals();
	}
	
	public void updateIndividualFormRemoveProcessed(DeadIndividual individual) {
		database.open();
		
		ContentValues cv = new ContentValues();
		
		cv.put(Database.Individual.COLUMN_CONTENT_URI, "");
		cv.put(Database.Individual.COLUMN_VERBALAUTOPSYPROCESSED, "");
				
		int result = database.update(DeadIndividual.class, cv, Database.Individual.COLUMN_INDIVIDUALID+" = ?", new String[] { individual.getIndividualId() });
				
		database.close();
		
		reloadIndividuals();
	}
	
	private void loadLastCreatedForm() {
		String strUri = lastSelectedIndividual.getLastContentUri();
		contentUri = Uri.parse(strUri);
		
		startActivityForResult(new Intent(Intent.ACTION_EDIT, contentUri), EDITING_EXISTING_ODK_FORM);
	}
}
