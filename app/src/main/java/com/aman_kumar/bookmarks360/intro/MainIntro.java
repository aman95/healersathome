package com.aman_kumar.bookmarks360.intro;

import android.accounts.Account;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.aman_kumar.bookmarks360.App;
import com.aman_kumar.bookmarks360.MainActivity;
import com.aman_kumar.bookmarks360.R;
import com.aman_kumar.bookmarks360.utils.SharedPrefs;
import com.aman_kumar.bookmarks360.utils.ShowMessage;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.IOException;

public class MainIntro extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "IntroActivity";

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_intro);
//        if(SharedPrefs.getPrefs("isUserLearnedApp", false)) {
//
//            Intent i = new Intent(this, MainActivity.class);
//            startActivity(i);
//            finish();
//        }

        //SharedPrefs.delPrefs("isUserLearnedApp");

        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();

        findViewById(R.id.sign_in_button).setOnClickListener(this);
//        findViewById(R.id.sign_out_button).setOnClickListener(this);
//        findViewById(R.id.token).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        Log.d(TAG, "onConnected:" + bundle);
        mShouldResolve = false;

        // Show the signed-in UI
//        showSignedInUI();
        //ShowMessage.toast("Yeah User Signed in!!!");
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            String personName = currentPerson.getDisplayName();
            String personPhoto = currentPerson.getImage().getUrl();
            String personGooglePlusProfile = currentPerson.getUrl();
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

            SharedPrefs.setPrefs("userEmail",email);
            SharedPrefs.setPrefs("userName",personName);
            SharedPrefs.setPrefs("isUserLearnedApp", true);
            SharedPrefs.setPrefs("userDP",personPhoto);


            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();

//            TextView tv = (TextView)findViewById(R.id.textView2);
//            tv.setText(personName+"\n"+personPhoto+"\n"+personGooglePlusProfile+"\n"+email);

            new GetIdTokenTask().execute();

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button) {
            onSignInClicked();
        }
//        if (v.getId() == R.id.sign_out_button) {
//            ShowMessage.toast("Signout clicked");
//            onSignOutClicked();
//        }
//        if (v.getId() == R.id.token) {
//            new GetIdTokenTask().execute();
//        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
//                showErrorDialog(connectionResult);
                ShowMessage.toast(connectionResult.toString());
            }
        } else {
            // Show the signed-out UI
            ShowMessage.toast("Signed out");
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();

        // Show a message to the user that we are signing in.
        ShowMessage.toast("We are signing in...");
    }
    private void onSignOutClicked() {
        // Clear the default account so that GoogleApiClient will not automatically
        // connect in the future.
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }

//        showSignedOutUI();
        //ShowMessage.toast("Yoo Signed out");
    }

    private class GetIdTokenTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
            Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
            String scopes = "audience:server:client_id:" + "1074421111735-a1u9o8fttu5th52v8k0eehifl9mb8061.apps.googleusercontent.com"; // Not the app's client ID.
            //String scopes = "oauth2:" + Scopes.PLUS_LOGIN + " https://www.googleapis.com/auth/plus.profile.emails.read";
            try {

                return GoogleAuthUtil.getToken(App.getAppContext(), account, scopes);

            } catch (IOException e) {
                Log.e(TAG, "Error retrieving ID token.", e);
                return null;
            } catch (GoogleAuthException e) {
                Log.e(TAG, "Error retrieving ID token.", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "ID token: " + result);
            if (result != null) {
                // Successfully retrieved ID Token
                //ShowMessage.toast(result);
                SharedPrefs.setPrefs("accessToken",result);
                // ...
            } else {
                // There was some error getting the ID Token
                // ...
                ShowMessage.toast("Oops No token!!!");
            }
        }

    }
}
