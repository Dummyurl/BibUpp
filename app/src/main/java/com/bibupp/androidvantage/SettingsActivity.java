package com.bibupp.androidvantage;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bibupp.androidvantage.app.App;
import com.bibupp.androidvantage.constants.Constants;
import com.bibupp.androidvantage.util.CustomRequest;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SettingsActivity extends ActionBarActivity implements Constants,GoogleApiClient.OnConnectionFailedListener {
    Toolbar toolbar;
   static GoogleApiClient mGoogleApiClient;
    final int RC_SIGN_IN = 9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        //        Инициализируем Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {

            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState == null) {

            getFragmentManager().beginTransaction()
                    .replace(R.id.settings_content, new SettingsFragment())
                    .commit();
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
//                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.\

        switch (item.getItemId()) {

            case R.id.action_settings: {

                return true;
            }

            case android.R.id.home: {

                finish();
                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class SettingsFragment extends PreferenceFragment {

        private Preference logoutPreference, aboutPreference, changePassword, itemTerms, itemThanks,changeLanguage;
        private CheckBoxPreference allowAnonymousQuestions;

        private ProgressDialog pDialog;

        LinearLayout aboutDialogContent;
        TextView aboutDialogAppName, aboutDialogAppVersion, aboutDialogAppCopyright;

        int mAnonymousQuestions;
        SharedPreferences sharedpreferences;
        public static final String MyPREFERENCES = "MyPrefs";
        String Language_name;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            Language_name = sharedpreferences.getString("Language_name", "");
            initpDialog();

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.settings);

            Preference pref = findPreference("settings_version");

            pref.setTitle(APP_NAME + " v" + APP_VERSION);

            pref = findPreference("settings_logout");

            pref.setSummary(App.getInstance().getUsername());

            pref = findPreference("settings_copyright_info");

            pref.setSummary(APP_COPYRIGHT + " © " + APP_YEAR);

            logoutPreference = findPreference("settings_logout");
            aboutPreference = findPreference("settings_version");
            changePassword = findPreference("settings_change_password");
            itemTerms = findPreference("settings_terms");
            itemThanks = findPreference("settings_thanks");
            changeLanguage=findPreference("settings_change_language");
            changeLanguage.setSummary(Language_name);
            changeLanguage.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                public boolean onPreferenceClick(Preference arg0) {

                    Intent i = new Intent(getActivity(), Change_Language.class);
                    startActivity(i);

                    return true;
                }
            });


            itemThanks.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                public boolean onPreferenceClick(Preference arg0) {

                    Intent i = new Intent(getActivity(), WebViewActivity.class);
                    i.putExtra("url", METHOD_APP_THANKS);
                    i.putExtra("title", getText(R.string.settings_thanks));
                    startActivity(i);

                    return true;
                }
            });

            itemTerms.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                public boolean onPreferenceClick(Preference arg0) {

                    Intent i = new Intent(getActivity(), WebViewActivity.class);
                    i.putExtra("url", METHOD_APP_TERMS);
                    i.putExtra("title", getText(R.string.settings_terms));
                    startActivity(i);

                    return true;
                }
            });

            allowAnonymousQuestions = (CheckBoxPreference) getPreferenceManager().findPreference("allowAnonymousQuestions");

            allowAnonymousQuestions.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    if (newValue instanceof Boolean) {

                        Boolean value = (Boolean) newValue;

                        if (value) {

                            mAnonymousQuestions = 1;

                        } else {

                            mAnonymousQuestions = 0;
                        }

                        if (App.getInstance().isConnected()) {

                            setAnonymousQuestions();

                        } else {

                            Toast.makeText(getActivity().getApplicationContext(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                        }
                    }

                    return true;
                }
            });

            aboutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                public boolean onPreferenceClick(Preference arg0) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle(getText(R.string.action_about));

                    aboutDialogContent = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.about_dialog, null);

                    alertDialog.setView(aboutDialogContent);

                    aboutDialogAppName = (TextView) aboutDialogContent.findViewById(R.id.aboutDialogAppName);
                    aboutDialogAppVersion = (TextView) aboutDialogContent.findViewById(R.id.aboutDialogAppVersion);
                    aboutDialogAppCopyright = (TextView) aboutDialogContent.findViewById(R.id.aboutDialogAppCopyright);

                    aboutDialogAppName.setText(APP_NAME);
                    aboutDialogAppVersion.setText("Version " + APP_VERSION);
                    aboutDialogAppCopyright.setText("Copyright © " + APP_YEAR + " " + APP_COPYRIGHT);

//                    alertDialog.setMessage("Version " + APP_VERSION + "/r/n" + APP_COPYRIGHT);
                    alertDialog.setCancelable(true);
                    alertDialog.setNeutralButton(getText(R.string.lang_button_ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });

                    alertDialog.show();

                    return false;
                }
            });

            logoutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                public boolean onPreferenceClick(Preference arg0) {
                    signOut();
                    App.getInstance().logout();

                    ActivityCompat.finishAffinity(getActivity());

                    Intent i = new Intent(getActivity(), AppActivity.class);
                    startActivity(i);

//                    getActivity().finish();

                    return true;
                }
            });

            changePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Intent i = new Intent(getActivity(), ChangePasswordActivity.class);
                    startActivity(i);

                    return true;
                }
            });

            checkAnonymousQuestions(App.getInstance().getAllowAnonymousQuestions());
        }

        public void checkAnonymousQuestions(int value) {

            if (value == 1) {

                allowAnonymousQuestions.setChecked(true);
                mAnonymousQuestions = 1;

            } else {

                allowAnonymousQuestions.setChecked(false);
                mAnonymousQuestions = 0;
            }
        }

        public void setAnonymousQuestions() {

            showpDialog();

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_ANONYMOUSQUESTIONS, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                if (response.getBoolean("error") == false) {

                                    App.getInstance().setAllowAnonymousQuestions(response.getInt("anonymousQuestions"));

                                    checkAnonymousQuestions(App.getInstance().getAllowAnonymousQuestions());
                                }

//                                 Toast.makeText(getActivity().getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {

                                e.printStackTrace();

                            } finally {

                                hidepDialog();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                     Toast.makeText(getActivity().getApplicationContext(), getText(R.string.error_data_loading), Toast.LENGTH_LONG).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("accountId", Long.toString(App.getInstance().getId()));
                    params.put("accessToken", App.getInstance().getAccessToken());
                    params.put("anonymousQuestions", Integer.toString(mAnonymousQuestions));

                    return params;
                }
            };

            App.getInstance().addToRequestQueue(jsonReq);
        }

        protected void initpDialog() {

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getString(R.string.msg_loading));
            pDialog.setCancelable(false);
        }

        protected void showpDialog() {

            if (!pDialog.isShowing())
                pDialog.show();
        }

        protected void hidepDialog() {

            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }
    public static void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
//                        tv_username.setText("");
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
//                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }
    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
        } else {
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
