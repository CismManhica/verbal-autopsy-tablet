package net.manhica.verbalautopsy.activity;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mz.betainteractive.odk.FormsProviderAPI;
import mz.betainteractive.odk.InstanceProviderAPI;
import mz.betainteractive.odk.listener.OdkFormLoadListener;
import mz.betainteractive.odk.model.FilledForm;
import mz.betainteractive.odk.task.OdkGeneratedFormLoadTask;
import net.manhica.verbalautopsy.R;
import net.manhica.verbalautopsy.R.id;
import net.manhica.verbalautopsy.R.layout;
import net.manhica.verbalautopsy.R.menu;
import net.manhica.verbalautopsy.R.string;
import net.manhica.verbalautopsy.model.FieldWorker;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class InFieldVerbalAutopsyActivity extends Activity {

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
	
	private InputFilter[] filtersHouseNr;
	private InputFilter[] filtersPermId;
	private InputFilter[] filtersChildId;
	private EditText roundTxt;
	private EditText houseNrTxt;
	private EditText permIdTxt;
	private RadioButton avMaternalRbt;
	private RadioButton avNeonateRbt;
	private Button executeAvBtn;
	
	private final String HOUSENO_REGEXP = "\\d\\d-\\d\\d\\d\\d-\\d\\d\\d";
    //12-9898-234-01
	private final String PERM_ID_REGEXP = "\\d\\d-\\d\\d\\d\\d-\\d\\d\\d\\-\\d\\d";
    //12-9898-234-01-01
	private final String CHILD_ID_REGEXP = "\\d\\d-\\d\\d\\d\\d-\\d\\d\\d\\-\\d\\d-\\d\\d";
	
	private final String X_HOUSENO_REGEXP = "[0-9]*-[0-9]*-[0-9]*";
	private final String X_PERM_ID_REGEXP = "\\d\\d-\\d\\d\\d\\d-\\d\\d\\d\\-\\d\\d";
	private final String X_CHILD_ID_REGEXP = "\\d\\d-\\d\\d\\d\\d-\\d\\d\\d\\-\\d\\d-\\d\\d";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.infield_vb);
		init();
	}

	private void init() {
		this.fieldWorker = (FieldWorker) getIntent().getExtras().get("fieldWorker");
		
		createFilters();
		
		roundTxt = (EditText) findViewById(R.id.roundTxt);
		houseNrTxt = (EditText) findViewById(R.id.houseNumberTxt);
		permIdTxt = (EditText) findViewById(R.id.permIdTxt);
		avMaternalRbt = (RadioButton) findViewById(R.id.radioAVMaterna);
		avNeonateRbt = (RadioButton) findViewById(R.id.radioAVNeonato);
		executeAvBtn = (Button) findViewById(R.id.executeAV_btn);
		
		
		houseNrTxt.setFilters(filtersHouseNr);
		permIdTxt.setFilters(filtersPermId);
				
		executeAvBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				openAutopsy();				
			}
		});
		
		avMaternalRbt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if (isChecked){ //maternal
					permIdTxt.setFilters(filtersPermId);
				}else{ //neonate
					permIdTxt.setFilters(filtersChildId);				
				}
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.in_field_verbal_autopsy, menu);
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
	
	private void createFilters(){
		filtersHouseNr = new InputFilter[1];
		filtersPermId = new InputFilter[1];
		filtersChildId = new InputFilter[1];
		
		//12-9898-999
        filtersHouseNr[0] = new CustomInputFilter(HOUSENO_REGEXP, new int[]{12,7000,999});
        //12-9898-234-01
        filtersPermId[0] = new CustomInputFilter(PERM_ID_REGEXP, new int[]{12,7000,999,99});
        //12-9898-234-01-01
        filtersChildId[0] = new CustomInputFilter(CHILD_ID_REGEXP, new int[]{12,7000,999,99,99});
        		
	}

	//01-9898-234-01
	private class CustomInputFilter implements InputFilter {
		private String regularExpression;
		private int[] lengths;			
		private Pattern pattern;
		private Matcher matcher;
		
		public CustomInputFilter(String regularExpression, int[] lengths) {
			super();
			this.regularExpression = regularExpression;
			this.lengths = lengths;
			this.pattern = Pattern.compile(regularExpression);
			this.matcher = pattern.matcher("");
		}

		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {

			if (end > start) {
				String destTxt = dest.toString();
				String resultingTxt = destTxt.substring(0, dstart)	+ source.subSequence(start, end) + destTxt.substring(dend);
				String newText = source.subSequence(start, end) + "";
				
				matcher.reset(resultingTxt);
												
				
				if (!matcher.matches() && !matcher.hitEnd()) {
					Log.d("doesnt match", ""+resultingTxt);
					
					String[] splits = resultingTxt.split("-");
					for (int i = 0; i < splits.length; i++) {
						if (Integer.valueOf(splits[i]) > lengths[i] || splits[i].length() > (lengths[i]+"").length()) {
							Log.d("filter", "ending "+splits[i]);
							return "";
						}
					}
					
					//return "";
				} else {
					String[] splits = resultingTxt.split("-");
					for (int i = 0; i < splits.length; i++) {
						if (Integer.valueOf(splits[i]) > lengths[i] || splits[i].length() > (lengths[i]+"").length()) {
							Log.d("filter", "ending "+splits[i]);
							return "";
						}
					}
					//auto complete
					/*
					for (int i = 0; i < splits.length; i++) {
						if (!(Integer.valueOf(splits[i]) > lengths[i])) {
							switch (i){
								case 0: if (splits[i].length()==2 && splits.length==1) return newText+"-";
								case 1: if (splits[i].length()==4 && splits.length==2) return newText+"-";
								case 2: if (splits[i].length()==3 && splits.length==3 && lengths.length >= 4) return newText+"-";
								case 3: if (splits[i].length()==2 && splits.length==4 && lengths.length == 5) return newText+"-";
								//case 4: if (splits[i].length()==2 && splits.length==1) return newText+"-";
							}
						}
					}*/
				}
			}
			return null;
		}
		
		
	}

	private void openAutopsy(){
		String round = roundTxt.getText().toString();
		String houseno = houseNrTxt.getText().toString();
		String permid = permIdTxt.getText().toString();			
		String selectedVaForm = avMaternalRbt.isChecked() ? VA_MATERNAL : VA_NEONATE;
		
		//DO CHECKS
		if (round.isEmpty()){
			roundTxt.setError("Número da ronda incorrecto");
			//Toast.makeText(this, "Número da ronda incorrecto", 2000).show();
			roundTxt.requestFocus();
			return;
		}
		
		if (houseno.isEmpty() || !houseno.matches(HOUSENO_REGEXP)){
			houseNrTxt.setError("Número da casa incorrecto");
			//Toast.makeText(this, "Número da casa incorrecto", 2000).show();
			houseNrTxt.requestFocus();
			return;
		}
		
		if (permid.isEmpty() || (avMaternalRbt.isChecked() && !permid.matches(PERM_ID_REGEXP)) || (avNeonateRbt.isChecked() && !permid.matches(CHILD_ID_REGEXP))  ){
			permIdTxt.setError("Perm ID incorrecto");
			//Toast.makeText(this, "Perm ID incorrecto", 2000).show();
			permIdTxt.requestFocus();
			return;
		}
		
    	FilledForm filledForm = new FilledForm(selectedVaForm);
    	
    	
    	filledForm.put("roundNumber", round);
    	filledForm.put("houseno", houseno);
    	filledForm.put("permId", permid);
    	filledForm.put("fieldWorkerId", fieldWorker.getExtId());    	
    	filledForm.put("inFieldDeath", "1");
    	
    	
    	loadForm(filledForm);    	
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
                
                InFieldVerbalAutopsyActivity.this.contentUri = contentUri;
                
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
					Toast.makeText(InFieldVerbalAutopsyActivity.this, getString(R.string.update_unfinish_saved), 3000);
					//updateIndividualForm(lastSelectedIndividual, contentUri);
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
        		//updateIndividualFormRemoveProcessed(lastSelectedIndividual);
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
            	//updateIndividualForm(lastSelectedIndividual, contentUri);
            	
            } else {            	
                createUnfinishedFormDialog();
            }            
        }
    }

}
