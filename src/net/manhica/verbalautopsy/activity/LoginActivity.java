package net.manhica.verbalautopsy.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import net.manhica.verbalautopsy.R;
import net.manhica.verbalautopsy.R.id;
import net.manhica.verbalautopsy.R.layout;
import net.manhica.verbalautopsy.R.string;
import net.manhica.verbalautopsy.database.Converter;
import net.manhica.verbalautopsy.database.Database;
import net.manhica.verbalautopsy.model.FieldWorker;
import net.manhica.verbalautopsy.task.SyncDatabaseListener;
import net.manhica.verbalautopsy.task.SyncEntitiesTask;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor>, SyncDatabaseListener {

	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[] {
			"foo@example.com:hello", "bar@example.com:world" };
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// UI references.
	private AutoCompleteTextView mUsernameView;
	private EditText mPasswordView;
	private View mProgressView;
	private View mLoginFormView;
				
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		// Set up the login form.
		mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);
		populateAutoComplete();

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		Button mLoginButton = (Button) findViewById(R.id.login_btn);
		mLoginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mProgressView = findViewById(R.id.login_progress);
	
		Button syncBtn = (Button) findViewById(R.id.login_syncronize_btn);
		syncBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				executeSyncronization();				
			}
		});
		
		Button exitBtn = (Button) findViewById(R.id.login_exit_btn);
		exitBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				exit();				
			}
		});
		
		this.progressDialog = new ProgressDialog(this);
	}

	private void populateAutoComplete() {
		getLoaderManager().initLoader(0, null, this);
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mUsernameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		String username = mUsernameView.getText().toString();
		String password = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password, if the user entered one.
		if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}


		if (TextUtils.isEmpty(username)) {
			mUsernameView.setError(getString(R.string.error_field_required));
			focusView = mUsernameView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			showProgress(true);
			mAuthTask = new UserLoginTask(username, password);
			mAuthTask.execute((Void) null);
		}
	}	

	private boolean isPasswordValid(String password) {
		// TODO: Replace this with your own logic
		return password.length() > 1;
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});

			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mProgressView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mProgressView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return new CursorLoader(this,
				// Retrieve data rows for the device user's 'profile' contact.
				Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
						ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
				ProfileQuery.PROJECTION,

				// Select only email addresses.
				ContactsContract.Contacts.Data.MIMETYPE + " = ?",
				new String[] { ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE },

				// Show primary email addresses first. Note that there won't be
				// a primary email address if the user hasn't specified one.
				ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		List<String> emails = new ArrayList<String>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			emails.add(cursor.getString(ProfileQuery.ADDRESS));
			cursor.moveToNext();
		}

		addEmailsToAutoComplete(emails);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {

	}

	private interface ProfileQuery {
		String[] PROJECTION = { ContactsContract.CommonDataKinds.Email.ADDRESS,
				ContactsContract.CommonDataKinds.Email.IS_PRIMARY, };

		int ADDRESS = 0;
		int IS_PRIMARY = 1;
	}

	private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
		// Create adapter to tell the AutoCompleteTextView what to show in its
		// dropdown list.
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				LoginActivity.this,
				android.R.layout.simple_dropdown_item_1line,
				emailAddressCollection);

		mUsernameView.setAdapter(adapter);
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, FieldWorker> {

		private final String mUsername;
		private final String mPassword;
		
		
		UserLoginTask(String username, String password) {
			mUsername = username;
			mPassword = password;
		}

		@Override
		protected FieldWorker doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			FieldWorker fw = null;
			
			Database db = new Database(LoginActivity.this);
			db.open();

			try {
				// Simulate network access.
				Cursor cursor = db.query(FieldWorker.class, Database.FieldWorker.COLUMN_EXTID + " = ?", new String[] { this.mUsername }, null, null, null);
				
				Log.d("user-"+this.mUsername, ""+cursor);
								
				if (cursor != null) {
					boolean found = cursor.moveToFirst();
					
					Log.d("user-"+this.mUsername, "found "+found);
					
					if (found){
						fw = Converter.cursorToFieldworker(cursor);
					}
					cursor.close();
				}				
								
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			
			db.close();

			return fw;
		}

		@Override
		protected void onPostExecute(final FieldWorker fieldWorker) {
			mAuthTask = null;
			showProgress(false);
								
			
			if (fieldWorker != null) {
				
				if(BCrypt.checkpw(mPassword, fieldWorker.getPasswordHash())){
					launchVerbalAutopsyActivity(fieldWorker);
				}else{
					mPasswordView.setError(getString(R.string.error_invalid_password));
					mPasswordView.requestFocus();
				}				
				
			} else {
				mUsernameView.setError(getString(R.string.error_invalid_username));
				mUsernameView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	
	private void executeSyncronization(){
		new SyncEntitiesTask("http://sap.manhica.net:4700", "vacism","holaHola", progressDialog, this, this).execute();	
	}

	protected void exit() {
		finish();
	}
	
	private void launchVerbalAutopsyActivity(FieldWorker fieldWorker) {
		//TODO pass in a fieldworker object
		Intent intent = new Intent(this, VerbalAutopsyActivity.class);
		intent.putExtra("fieldWorker", fieldWorker);
		//usernameEditText.setText("");
		//passwordEditText.setText("");		
		startActivity(intent);
	}
	
	@Override
	public void collectionComplete(String result) {
		Log.d("listener", result);
		
		if (result.equalsIgnoreCase("success")){
			Toast.makeText(this, getString(R.string.sync_success_lbl), 4000);
		}
		
		if (result.equalsIgnoreCase("failure")){
			Toast.makeText(this, getString(R.string.sync_success_lbl), 4000);
		}
		
	}
	
	
}
