package com.bibupp.androidvantage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bibupp.androidvantage.app.App;
import com.bibupp.androidvantage.captcha.MathCaptcha;
import com.bibupp.androidvantage.captcha.TextCaptcha;
import com.bibupp.androidvantage.common.ActivityBase;
import com.bibupp.androidvantage.constants.Constants;
import com.bibupp.androidvantage.util.CustomRequest;
import com.bibupp.androidvantage.util.Helper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class SignupActivity extends ActivityBase implements Constants, View.OnFocusChangeListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "sirnup_activity";

    Toolbar toolbar;

    CallbackManager callbackManager;

    LoginButton loginButton;

    EditText signupUsername, signupFullname, signupPassword, signupEmail, signupAddress, signupZipcode;
    AutoCompleteTextView signupCountry, signupState, signupCity;
    //    EditText signupAddress,signupCity,signupState,signupCountry,signupZipcode;
    Button Btnsignup;
    TextView mLabelTerms, mRegularSignup, mLabelAuthorizationViaFacebook;

    private String username, fullname, password, email, address = "", city, state, country, zipcode, latitude = "", longitude = "", language;
    String facebookId = "", facebookName = "", facebookEmail = "", googleplusId = "", googleplushName = "", googleplushEmail = "", Login_From = "";
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    //    String Gcm_RegistrationId="",DeviceId;
    Geocoder geocoder;
    GoogleApiClient mGoogleApiClient;
    final int RC_SIGN_IN = 9001;
    ArrayList<String> countryListName = new ArrayList<String>();
    String country_name, country_id;
    ArrayList<String> country_sate_ListName = new ArrayList<String>();
    ArrayList<String> country_sate__city_ListName = new ArrayList<String>();
    String country_state_name, Iso_code;
    private SQLiteDatabase db;
    String selected_country;
    ArrayAdapter<String> country_adapter;
    ArrayAdapter<String> state_adapter;
    ArrayAdapter<String> city_adapter;
    PlacesTask placesTask;
    ParserTask parserTask;
    JSONArray predictions = null;
    JSONArray termsArray = null;

    ImageView imageView;
    TextCaptcha textCaptcha;
    MathCaptcha mathCaptcha;
    AppCompatEditText edtTextCaptcha;
    int width;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//        Gcm_RegistrationId=sharedpreferences.getString("Gcm_registrationId","");
//        DeviceId=sharedpreferences.getString("DeviceId","");
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("gcm_regId", FirebaseInstanceId.getInstance().getToken());
        editor.commit();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        App.getInstance().setGcmToken(refreshedToken);
        if (AccessToken.getCurrentAccessToken() != null) LoginManager.getInstance().logOut();

        callbackManager = CallbackManager.Factory.create();

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {

            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //************ for captcha*************
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        width=width-20;
        textCaptcha = new TextCaptcha(width, 100, 4, TextCaptcha.TextOptions.NUMBERS_AND_LETTERS);
        mathCaptcha = new MathCaptcha(width, 100, MathCaptcha.MathOptions.PLUS_MINUS);

        imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageBitmap(textCaptcha.getImage());
        edtTextCaptcha = (AppCompatEditText) findViewById(R.id.edt_text_image);
//*****************end captcha***********************
        db = openOrCreateDatabase("PersonDB", Context.MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("select name from country", null);
        if (cursor != null) {
            cursor.moveToFirst();
            countryListName.clear();
        }
        do {
            try {
                country_name = cursor.getString(cursor.getColumnIndex("name"));
                countryListName.add(country_name);
            } catch (IndexOutOfBoundsException e) {

            } catch (OutOfMemoryError e) {

            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (cursor.moveToNext());

        country_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, countryListName);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
//                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends, email");

        signupUsername = (EditText) findViewById(R.id.signupUsername);
        signupFullname = (EditText) findViewById(R.id.signupFullname);
        signupPassword = (EditText) findViewById(R.id.signupPassword);
        signupEmail = (EditText) findViewById(R.id.signupEmail);
        signupAddress = (EditText) findViewById(R.id.signupAddress);
        signupCity = (AutoCompleteTextView) findViewById(R.id.signupCity);
        signupState = (AutoCompleteTextView) findViewById(R.id.signupState);
        signupCountry = (AutoCompleteTextView) findViewById(R.id.signupCountry);
        signupZipcode = (EditText) findViewById(R.id.signupZipcode);

        signupCountry.setThreshold(1);
        //Set the adapter
//        countryAdapter = new CountryAdapter(this, R.layout.activity_main, R.id.textView1, country_list);
        signupCountry.setAdapter(country_adapter);
        signupState.setEnabled(false);
        signupCity.setEnabled(false);
        signupCity.setThreshold(1);
//        signupState.setThreshold(1);
//        //Set the adapter
//        signupState.setAdapter(adapter);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Login_From = sharedpreferences.getString("Login_From", "app");

        if (Login_From.equalsIgnoreCase("facebook")) {
            Intent i = getIntent();
            facebookId = i.getStringExtra("facebookId");
            facebookName = i.getStringExtra("facebookName");
            facebookEmail = i.getStringExtra("facebookEmail");
            signupFullname.setText(facebookName);
            signupEmail.setText(facebookEmail);
        } else if (Login_From.equalsIgnoreCase("google_plush")) {
            Intent i = getIntent();
            googleplusId = i.getStringExtra("googleplusId");
            googleplushName = i.getStringExtra("googleplushName");
            googleplushEmail = i.getStringExtra("googleplushEmail");
            signupFullname.setText(googleplushName);
            signupEmail.setText(googleplushEmail);
        }
//        Intent i = getIntent();
//        facebookId = i.getStringExtra("facebookId");
//        facebookName = i.getStringExtra("facebookName");
//        facebookEmail = i.getStringExtra("facebookEmail");
//        googleplusId=i.getStringExtra("googleplusId");


        mLabelTerms = (TextView) findViewById(R.id.SignupLabelTerms);

        mLabelTerms.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(SignupActivity.this, WebViewActivity.class);
                i.putExtra("url", METHOD_APP_TERMS);
                i.putExtra("title", getText(R.string.signup_label_terms_and_policies));
                startActivity(i);
            }
        });

        mLabelAuthorizationViaFacebook = (TextView) findViewById(R.id.labelAuthorizationViaFacebook);
        mLabelAuthorizationViaFacebook.setVisibility(View.GONE);

        mRegularSignup = (TextView) findViewById(R.id.regularSignup);
        mRegularSignup.setVisibility(View.GONE);

        mRegularSignup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                facebookId = "";
                facebookName = "";
                facebookEmail = "";
                googleplusId = "";
                googleplushName = "";
                googleplushEmail = "";
                loginButton.setVisibility(View.VISIBLE);
                mRegularSignup.setVisibility(View.GONE);
                mLabelAuthorizationViaFacebook.setVisibility(View.GONE);
            }
        });

        if (facebookId != null && !facebookId.equals("")) {

            loginButton.setVisibility(View.GONE);
            mRegularSignup.setVisibility(View.VISIBLE);
            mLabelAuthorizationViaFacebook.setVisibility(View.VISIBLE);
        }

        if (facebookId == null) {

            facebookId = "";
        }
        if (googleplusId == null) {

            googleplusId = "";
        }

        signupUsername.setOnFocusChangeListener(this);
        signupFullname.setOnFocusChangeListener(this);
        signupPassword.setOnFocusChangeListener(this);
        signupEmail.setOnFocusChangeListener(this);
        signupAddress.setOnFocusChangeListener(this);
        signupCity.setOnFocusChangeListener(this);
        signupState.setOnFocusChangeListener(this);
        signupCountry.setOnFocusChangeListener(this);
        signupZipcode.setOnFocusChangeListener(this);

        signupUsername.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                if (App.getInstance().isConnected() && checkUsername()) {

//                        showpDialog();

                    CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_APP_CHECKUSERNAME, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    try {

                                        if (response.getBoolean("error") == true) {

                                            signupUsername.setError(getString(R.string.error_login_taken));
                                        }

//                                            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();

                                    } catch (JSONException e) {

                                        e.printStackTrace();

                                    } finally {

//                                            hidepDialog();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

//                                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

//                                hidepDialog();
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("username", username);

                            return params;
                        }
                    };

                    App.getInstance().addToRequestQueue(jsonReq);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        signupFullname.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                checkFullname();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        signupPassword.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                checkPassword();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        signupEmail.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                checkEmail();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        signupAddress.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                checkAddress();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        signupCity.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

//                checkCity();
                signupCity.setError(null);
                placesTask = new PlacesTask();
                placesTask.execute(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        signupState.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    signupCity.setEnabled(true);
                } else {
                    signupCity.setEnabled(false);
                }
                signupCity.setText("");
                signupState.setError(null);
//                checkState();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        signupCountry.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                checkCountry();
                selected_country = s.toString();
//
                Cursor cursor = db.rawQuery("select country_id from country where name='" + selected_country + "'", null);
                if (cursor != null) {
                    cursor.moveToFirst();
                }
                if (cursor.getCount() > 0) {
                    do {
                        signupState.setEnabled(true);
                        signupCity.setEnabled(false);
                        try {
                            country_id = cursor.getString(cursor.getColumnIndex("country_id"));
                            Cursor cursor_state = db.rawQuery("select * from country_states where country_id='" + country_id + "'", null);
                            if (cursor_state != null) {
                                cursor_state.moveToFirst();
                                country_sate_ListName.clear();
                            }
                            if (cursor_state.getCount() > 0) {
                                do {
                                    try {
                                        country_state_name = cursor_state.getString(cursor_state.getColumnIndex("name"));
                                        country_sate_ListName.add(country_state_name);
                                    } catch (IndexOutOfBoundsException e) {

                                    } catch (OutOfMemoryError e) {

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } while (cursor_state.moveToNext());
                            }
                        } catch (IndexOutOfBoundsException e) {

                        } catch (OutOfMemoryError e) {

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } while (cursor.moveToNext());

                    state_adapter = new ArrayAdapter<String>(SignupActivity.this, android.R.layout.simple_dropdown_item_1line, country_sate_ListName);

                    //Set the adapter
                    signupState.setThreshold(1);
                    signupState.setAdapter(state_adapter);
                } else {
                    signupState.setText("");
                    signupState.setEnabled(false);
                    signupCity.setEnabled(false);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        signupZipcode.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                checkZipcode();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        Btnsignup = (Button) findViewById(R.id.Btnsignup);

        Btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = signupUsername.getText().toString();
                fullname = signupFullname.getText().toString();
                password = signupPassword.getText().toString();
                email = signupEmail.getText().toString();
                address = signupAddress.getText().toString();
                city = signupCity.getText().toString();
                state = signupState.getText().toString();
                country = signupCountry.getText().toString();
                zipcode = signupZipcode.getText().toString();
                language = Locale.getDefault().getLanguage();

                if (verifyRegForm()) {

                    if (App.getInstance().isConnected()) {

                        showpDialog();

                        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, Constants.METHOD_AUTH_SIGNUP, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        Log.e("Profile", "Malformed JSON: \"" + response.toString() + "\"");

                                        if (App.getInstance().authorize(response)) {

                                            Log.e("Profile", "Malformed JSON: \"" + response.toString() + "\"");

                                            ActivityCompat.finishAffinity(SignupActivity.this);

                                            Intent i = new Intent(getApplicationContext(), WelcomeActivity.class);
                                            startActivity(i);

                                        } else {

                                            switch (App.getInstance().getErrorCode()) {

                                                case ERROR_LOGIN_TAKEN: {

                                                    signupUsername.setError(getString(R.string.error_login_taken));
                                                    break;
                                                }

                                                case ERROR_EMAIL_TAKEN: {

                                                    signupEmail.setError(getString(R.string.error_email_taken));
                                                    break;
                                                }

                                                default: {

                                                    Log.e("Profile", "Could not parse malformed JSON: \"" + response.toString() + "\"");
                                                    break;
                                                }
                                            }
                                        }

                                        hidepDialog();
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(getApplicationContext(), "Please fill correct addresses", Toast.LENGTH_SHORT).show();
//

                                hidepDialog();
                            }
                        }) {

                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();

                                Geocoder geoCoder1 = new Geocoder(SignupActivity.this);
                                List<Address> pickAddress;
                                try {
                                    String p = address + " " + city + " " + state + " " + country + " " + zipcode;
                                    p = p.replace("\n", " ");
                                    if (p.length() > 0) {
                                        pickAddress = geoCoder1.getFromLocationName(p, 1);
                                        if (pickAddress.size() > 0) {
                                            latitude = String.valueOf(pickAddress.get(0).getLatitude());
                                            longitude = String.valueOf(pickAddress.get(0).getLongitude());
                                        } else {
//                                            Toast.makeText(getApplicationContext(), "Please fill correct addresses", Toast.LENGTH_SHORT).show();
                                            p = city + " " + state + " " + country + " " + zipcode;
                                            p = p.replace("\n", " ");
                                            pickAddress = geoCoder1.getFromLocationName(p, 1);
                                            if (pickAddress.size() > 0) {
                                                latitude = String.valueOf(pickAddress.get(0).getLatitude());
                                                longitude = String.valueOf(pickAddress.get(0).getLongitude());
                                            } else {
                                                p = state + " " + country + " " + zipcode;
                                                p = p.replace("\n", " ");
                                                pickAddress = geoCoder1.getFromLocationName(p, 1);
                                                if (pickAddress.size() > 0) {
                                                    latitude = String.valueOf(pickAddress.get(0).getLatitude());
                                                    longitude = String.valueOf(pickAddress.get(0).getLongitude());
                                                } else {
                                                    p = state + " " + country;
                                                    p = p.replace("\n", " ");
                                                    pickAddress = geoCoder1.getFromLocationName(p, 1);
                                                    if (pickAddress.size() > 0) {
                                                        latitude = String.valueOf(pickAddress.get(0).getLatitude());
                                                        longitude = String.valueOf(pickAddress.get(0).getLongitude());
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Please fill correct addresses", Toast.LENGTH_SHORT).show();
                                                        textCaptcha = new TextCaptcha(width, 150, 4, TextCaptcha.TextOptions.NUMBERS_AND_LETTERS);
                                                        mathCaptcha = new MathCaptcha(width, 150, MathCaptcha.MathOptions.PLUS_MINUS);
                                                        imageView.setImageBitmap(textCaptcha.getImage());
                                                        edtTextCaptcha.setText("");
                                                    }
                                                }


                                            }
                                        }
                                    }
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
//                                    Toast.makeText(getApplicationContext(), "Please fill correct addresses", Toast.LENGTH_SHORT).show();
                                    try {
                                        String p = city + " " + state + " " + country + " " + zipcode;
                                        p = p.replace("\n", " ");
                                        if (p.length() > 0) {
                                            pickAddress = geoCoder1.getFromLocationName(p, 1);
                                            if (pickAddress.size() > 0) {
                                                latitude = String.valueOf(pickAddress.get(0).getLatitude());
                                                longitude = String.valueOf(pickAddress.get(0).getLongitude());
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Please fill correct addresses", Toast.LENGTH_SHORT).show();
                                                textCaptcha = new TextCaptcha(width, 150, 4, TextCaptcha.TextOptions.NUMBERS_AND_LETTERS);
                                                mathCaptcha = new MathCaptcha(width, 150, MathCaptcha.MathOptions.PLUS_MINUS);
                                                imageView.setImageBitmap(textCaptcha.getImage());
                                                edtTextCaptcha.setText("");
                                            }
                                        }
                                    } catch (IOException ex) {
                                        // TODO Auto-generated catch block
//                                        Toast.makeText(getApplicationContext(), "Please fill correct addresses", Toast.LENGTH_SHORT).show();
                                        try {
                                            String p = state + " " + country + " " + zipcode;
                                            p = p.replace("\n", " ");
                                            if (p.length() > 0) {
                                                pickAddress = geoCoder1.getFromLocationName(p, 1);
                                                if (pickAddress.size() > 0) {
                                                    latitude = String.valueOf(pickAddress.get(0).getLatitude());
                                                    longitude = String.valueOf(pickAddress.get(0).getLongitude());
                                                } else {
                                                    textCaptcha = new TextCaptcha(width, 150, 4, TextCaptcha.TextOptions.NUMBERS_AND_LETTERS);
                                                    mathCaptcha = new MathCaptcha(width, 150, MathCaptcha.MathOptions.PLUS_MINUS);
                                                    imageView.setImageBitmap(textCaptcha.getImage());
                                                    edtTextCaptcha.setText("");
                                                    Toast.makeText(getApplicationContext(), "Please fill correct addresses", Toast.LENGTH_SHORT).show();
//                                            signupAddress.requestFocus();
//                                            signupAddress.setText("");
//                                            signupAddress.setError("Fill correct address");
                                                }
                                            }
                                        } catch (IOException exx) {
                                            // TODO Auto-generated catch block
                                            textCaptcha = new TextCaptcha(width, 150, 4, TextCaptcha.TextOptions.NUMBERS_AND_LETTERS);
                                            mathCaptcha = new MathCaptcha(width, 150, MathCaptcha.MathOptions.PLUS_MINUS);
                                            imageView.setImageBitmap(textCaptcha.getImage());
                                            edtTextCaptcha.setText("");
                                            Toast.makeText(getApplicationContext(), "Please fill correct addresses", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                }


                                if (Login_From.equalsIgnoreCase("facebook")) {

                                    params.put("username", username);
                                    params.put("fullname", fullname);
                                    params.put("password", password);
                                    params.put("email", email);
                                    params.put("street_address", address);
                                    params.put("city", city);
                                    params.put("states", state);
                                    params.put("country", country);
                                    params.put("zipcode", zipcode);
                                    params.put("language", language);
                                    params.put("facebookId", facebookId);
                                    params.put("clientId", CLIENT_ID);
                                    params.put("latitude", latitude);
                                    params.put("longitude", longitude);
                                    params.put("gcm_regId", sharedpreferences.getString("gcm_regId", ""));
//                                    params.put("deviceId", DeviceId);
                                } else if (Login_From.equalsIgnoreCase("google_plush")) {

                                    params.put("username", username);
                                    params.put("fullname", fullname);
                                    params.put("password", password);
                                    params.put("email", email);
                                    params.put("street_address", address);
                                    params.put("city", city);
                                    params.put("states", state);
                                    params.put("country", country);
                                    params.put("zipcode", zipcode);
                                    params.put("language", language);
                                    params.put("googleplusId", googleplusId);
                                    params.put("clientId", CLIENT_ID);
//                                    params.put("gcm_regId", App.getInstance().getGcmToken());
                                    params.put("latitude", latitude);
                                    params.put("longitude", longitude);
                                    params.put("gcm_regId", sharedpreferences.getString("gcm_regId", ""));
//                                    params.put("deviceId", DeviceId);
                                } else {

                                    params.put("username", username);
                                    params.put("fullname", fullname);
                                    params.put("password", password);
                                    params.put("email", email);
                                    params.put("street_address", address);
                                    params.put("city", city);
                                    params.put("states", state);
                                    params.put("country", country);
                                    params.put("zipcode", zipcode);
                                    params.put("language", language);
                                    params.put("clientId", CLIENT_ID);
//                                    params.put("gcm_regId", App.getInstance().getGcmToken());
                                    params.put("latitude", latitude);
                                    params.put("longitude", longitude);
                                    params.put("gcm_regId", sharedpreferences.getString("gcm_regId", ""));
//                                    params.put("deviceId", DeviceId);
                                }

//                                params.put("username", username);
//                                params.put("fullname", fullname);
//                                params.put("password", password);
//                                params.put("email", email);
//                                params.put("language", language);
//                                params.put("facebookId", facebookId);
//                                params.put("googleplusId", googleplusId);
//                                params.put("clientId", CLIENT_ID);
//                                params.put("gcm_regId", App.getInstance().getGcmToken());

                                return params;
                            }
                        };

                        App.getInstance().addToRequestQueue(jsonReq);

                    } else {

                        Toast.makeText(getApplicationContext(), R.string.msg_network_error, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        // App code

                        if (App.getInstance().isConnected()) {

                            showpDialog();

                            GraphRequest request = GraphRequest.newMeRequest(
                                    AccessToken.getCurrentAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject object, GraphResponse response) {

                                            // Application code

                                            try {

                                                facebookId = object.getString("id");
                                                facebookName = object.getString("name");

                                                if (object.has("email")) {

                                                    facebookEmail = object.getString("email");
                                                }

                                            } catch (Throwable t) {

                                                Log.e("Profile", "Could not parse malformed JSON: \"" + object.toString() + "\"");

                                            } finally {

                                                if (AccessToken.getCurrentAccessToken() != null)
                                                    LoginManager.getInstance().logOut();

                                                Log.d("Profile", object.toString());

                                                if (App.getInstance().isConnected()) {

                                                    if (!facebookId.equals("")) {

                                                        signinByFacebookId();

                                                    } else {

                                                        hidepDialog();
                                                    }

                                                } else {

                                                    hidepDialog();
                                                }
                                            }
                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,link,email");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }
                    }

                    @Override
                    public void onCancel() {

                        // App code
                        // Cancel
                    }

                    @Override
                    public void onError(FacebookException exception) {

                        // App code
                        // Error
                    }
                });
    }

    public void signinByFacebookId() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_AUTH_FACEBOOK, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (App.getInstance().authorize(response)) {

                            if (App.getInstance().getState() == ACCOUNT_STATE_ENABLED) {

                                ActivityCompat.finishAffinity(SignupActivity.this);

                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);

                            } else {

                                if (App.getInstance().getState() == ACCOUNT_STATE_BLOCKED) {

                                    App.getInstance().logout();
                                    Toast.makeText(SignupActivity.this, getText(R.string.account_blocked), Toast.LENGTH_SHORT).show();

                                } else {

                                    ActivityCompat.finishAffinity(SignupActivity.this);

                                    Intent i = new Intent(getApplicationContext(), WelcomeActivity.class);
                                    startActivity(i);
                                }
                            }

                        } else {

                            if (facebookId != "") {

                                loginButton.setVisibility(View.GONE);
                                mRegularSignup.setVisibility(View.VISIBLE);
                                mLabelAuthorizationViaFacebook.setVisibility(View.VISIBLE);

                                signupFullname.setText(facebookName);

                                if (facebookEmail != null && !facebookEmail.equals("")) {

                                    signupEmail.setText(facebookEmail);
                                }
                            }
                        }

                        hidepDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), getText(R.string.error_data_loading), Toast.LENGTH_LONG).show();

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("facebookId", facebookId);
                params.put("clientId", CLIENT_ID);
                params.put("gcm_regId", App.getInstance().getGcmToken());

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        switch (v.getId()) {

            case R.id.signupUsername: {

                break;
            }

            case R.id.signupFullname: {

                if (!hasFocus) {


                }

                break;
            }

            case R.id.signupPassword: {

                if (!hasFocus) {


                }

                break;
            }

            case R.id.signupEmail: {

                if (!hasFocus) {


                }

                break;
            }
            case R.id.signupAddress: {

                if (!hasFocus) {


                }

                break;
            }
            case R.id.signupCity: {

                if (!hasFocus) {


                }

                break;
            }
            case R.id.signupState: {

                if (!hasFocus) {


                }

                break;
            }
            case R.id.signupCountry: {

                if (!hasFocus) {


                }

                break;
            }
            case R.id.signupZipcode: {

                if (!hasFocus) {


                }

                break;
            }


            default: {

                break;
            }
        }
    }

    public Boolean checkUsername() {

        username = signupUsername.getText().toString();

        Helper helper = new Helper();

        if (username.length() == 0) {

            signupUsername.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (username.length() < 5) {

            signupUsername.setError(getString(R.string.error_small_username));

            return false;
        }

        if (!helper.isValidLogin(username)) {

            signupUsername.setError(getString(R.string.error_wrong_format));

            return false;
        }

        signupUsername.setError(null);

        return true;
    }

    public Boolean checkFullname() {

        fullname = signupFullname.getText().toString();

        fullname = fullname.trim();

        if (fullname.length() == 0) {

            signupFullname.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (fullname.length() < 2) {

            signupFullname.setError(getString(R.string.error_small_fullname));

            return false;
        }

        signupFullname.setError(null);

        return true;
    }

    public Boolean checkPassword() {

        password = signupPassword.getText().toString();

        Helper helper = new Helper();

        if (password.length() == 0) {

            signupPassword.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (password.length() < 6) {

            signupPassword.setError(getString(R.string.error_small_password));

            return false;
        }

        if (!helper.isValidPassword(password)) {

            signupPassword.setError(getString(R.string.error_wrong_format));

            return false;
        }

        signupPassword.setError(null);

        return true;
    }

    public Boolean checkEmail() {

        email = signupEmail.getText().toString();

        Helper helper = new Helper();

        if (email.length() == 0) {

            signupEmail.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (!helper.isValidEmail(email)) {

            signupEmail.setError(getString(R.string.error_wrong_format));

            return false;
        }

        signupEmail.setError(null);

        return true;
    }

    public Boolean checkAddress() {

        address = signupAddress.getText().toString();

        address = address.trim();

        if (address.length() == 0) {

            signupAddress.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (address.length() < 2) {

            signupAddress.setError(getString(R.string.error_small_fullname));

            return false;
        }

        signupAddress.setError(null);

        return true;
    }

    public Boolean checkCity() {

        city = signupCity.getText().toString();

        city = city.trim();

        if (city.length() == 0) {

            signupCity.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (city.length() < 5) {

            signupCity.setError(getString(R.string.error_small_username));

            return false;
        }

        signupCity.setError(null);

        return true;
    }

    public Boolean checkState() {

        state = signupState.getText().toString();

        state = state.trim();

        if (state.length() == 0) {

            signupState.setError(getString(R.string.error_field_empty));
            signupCity.setEnabled(false);
            return false;
        }

        if (state.length() < 5) {

            signupState.setError(getString(R.string.error_small_username));
            signupCity.setEnabled(false);
            return false;
        }
        signupCity.setEnabled(true);
        signupState.setError(null);

        return true;
    }

    public Boolean checkCountry() {

        country = signupCountry.getText().toString();

        country = country.trim();

        if (country.length() == 0) {

            signupCountry.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (country.length() < 2) {

            signupCountry.setError(getString(R.string.error_small_fullname));

            return false;
        }

        signupCountry.setError(null);

        return true;
    }

    public Boolean checkZipcode() {

        zipcode = signupZipcode.getText().toString();

        zipcode = zipcode.trim();

        if (zipcode.length() == 0) {

            signupZipcode.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (zipcode.length() < 5) {

            signupZipcode.setError(getString(R.string.error_small_username));

            return false;
        }

        signupZipcode.setError(null);

        return true;
    }

    public Boolean verifyRegForm() {

        signupUsername.setError(null);
        signupFullname.setError(null);
        signupPassword.setError(null);
        signupEmail.setError(null);
        signupAddress.setError(null);
        signupCity.setError(null);
        signupState.setError(null);
        signupCountry.setError(null);
        signupZipcode.setError(null);

        edtTextCaptcha.setError(null);
        int numberOfCaptchaFalse = 0;
        Helper helper = new Helper();

        if (username.length() == 0) {

            signupUsername.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (username.length() < 5) {

            signupUsername.setError(getString(R.string.error_small_username));

            return false;
        }

        if (!helper.isValidLogin(username)) {

            signupUsername.setError(getString(R.string.error_wrong_format));

            return false;
        }

        if (fullname.length() == 0) {

            signupFullname.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (fullname.length() < 2) {

            signupFullname.setError(getString(R.string.error_small_fullname));

            return false;
        }

        if (password.length() == 0) {

            signupPassword.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (password.length() < 6) {

            signupPassword.setError(getString(R.string.error_small_password));

            return false;
        }

        if (!helper.isValidPassword(password)) {

            signupPassword.setError(getString(R.string.error_wrong_format));

            return false;
        }

        if (email.length() == 0) {

            signupEmail.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (!helper.isValidEmail(email)) {

            signupEmail.setError(getString(R.string.error_wrong_format));

            return false;
        }
        if (address.length() < 2) {

            signupAddress.setError(getString(R.string.error_small_fullname));

            return false;
        }
        if (city.length() < 2) {

            signupCity.setError(getString(R.string.error_small_fullname));

            return false;
        }
        if (state.length() < 2) {

            signupState.setError(getString(R.string.error_small_fullname));

            return false;
        }
        if (country.length() < 2) {

            signupCountry.setError(getString(R.string.error_small_fullname));

            return false;
        }
        if (zipcode.length() < 5) {

            signupZipcode.setError(getString(R.string.error_small_username));

            return false;
        }
        if (!textCaptcha.checkAnswer(edtTextCaptcha.getText().toString().trim())) {
            edtTextCaptcha.setError("Captcha is not match");
            numberOfCaptchaFalse++;
            edtTextCaptcha.setText("");
            textCaptcha = new TextCaptcha(width, 150, 4, TextCaptcha.TextOptions.NUMBERS_AND_LETTERS);
            mathCaptcha = new MathCaptcha(width, 150, MathCaptcha.MathOptions.PLUS_MINUS);
            imageView.setImageBitmap(textCaptcha.getImage());
            return false;
        }

        //checking math captcha
//        if (!mathCaptcha.checkAnswer(edtTextCaptcha.getText().toString().trim())) {
//            edtTextCaptcha.setError("Captcha not match");
//            numberOfCaptchaFalse++;
//        }

        if (numberOfCaptchaFalse == 0) {
            edtTextCaptcha.setText("");
//            Toast.makeText(SignupActivity.this, "Captcha texts match!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        signOut();

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home: {
                signOut();
                finish();
                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    public void signOut() {
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
        callbackManager.onActivityResult(requestCode, resultCode, data);
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

    private class PlacesTask extends AsyncTask<String, Void, String> {
        String STATE_VALUE, COUNTRY_VALUE;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            STATE_VALUE = signupState.getText().toString();
            COUNTRY_VALUE = signupCountry.getText().toString();
        }

        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console
            String key = "key=AIzaSyBVcKaeFrAXk9W9OoFCAZXXZYspNdiHJwo";

            String input = "";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }


            // place type to be searched
            String types = "types=geocode";

            // Sensor enabled
            String sensor = "sensor=false";

            Cursor cursor_code = db.rawQuery("select * from country where name='" + COUNTRY_VALUE + "'", null);
            if (cursor_code != null) {
                cursor_code.moveToFirst();
            }
            if (cursor_code.getCount() > 0) {
                do {
                    try {
                        Iso_code = cursor_code.getString(cursor_code.getColumnIndex("iso_code_2"));

                    } catch (Exception e) {

                    }
                } while (cursor_code.moveToNext());
            }
            String country = "components=country:" + Iso_code;
            // Building the parameters to the web service
            String parameters = input + "&" + country + "&" + types + "&" + sensor + "&" + key;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;
            JSONParser jParser = new JSONParser();
            JSONObject json = new JSONObject();
            json = jParser.getJSONFromUrl(url);

            try {
                // Fetching the data from web service in background
//                data = downloadUrl(url);
                predictions = json.getJSONArray("predictions");
                int length = predictions.length();
                country_sate__city_ListName.clear();
                for (int i = 0; i < length; i++) {
                    JSONObject ob = predictions.getJSONObject(i);
                    String terms = ob.getString("terms");
//                    terms=terms.substring(1,terms.length()-1);
//                    JSONObject jObj=null;
//                    try {
//                         jObj = new JSONObject(terms);
//                    } catch (JSONException e) {
//                        Log.e("JSON Parser", "Error parsing data " + e.toString());
//                    }
                    termsArray = new JSONArray(terms);//jObj.getJSONArray("terms");
                    int lengths = termsArray.length();
                    for (int j = lengths - 1; j >= 0; j--) {
                        JSONObject obs = termsArray.getJSONObject(j);
                        String value = obs.getString("value");

                        if (value.equalsIgnoreCase(STATE_VALUE)) {
                            JSONObject objs = termsArray.getJSONObject(0);
                            String values = objs.getString("value");

                            country_sate__city_ListName.add(values);
                        }

                    }


                }
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Creating ParserTask
            parserTask = new ParserTask();

            // Starting Parsing the JSON string returned by Web Service
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            String[] from = new String[]{"description"};
            int[] to = new int[]{android.R.id.text1};

            // Creating a SimpleAdapter for the AutoCompleteTextView
//            SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);
            city_adapter = new ArrayAdapter<String>(SignupActivity.this, android.R.layout.simple_dropdown_item_1line, country_sate__city_ListName);
            // Setting the adapter
            signupCity.setAdapter(city_adapter);

        }
    }

}
