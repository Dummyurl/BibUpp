package com.bibupp.androidvantage;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.bibupp.androidvantage.app.App;
import com.bibupp.androidvantage.constants.Constants;
import com.bibupp.androidvantage.model.Profile;
import com.bibupp.androidvantage.util.CustomRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.pkmmte.view.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile_Details extends AppCompatActivity implements Constants{
    public long profile_id, accountId, accessToken;
    Toolbar toolbar;
    EditText profileEditFullname;
    long profileId;
    String[] strings;
    LinearLayout linear_layout_parent;
    LinearLayout.LayoutParams paramsedittext;
    EditText edttext_name[] = new EditText[100];
    LinearLayout linear_layout[] = new LinearLayout[100];
    List<EditText> allEds_name = new ArrayList<EditText>();
    int id = 0;
    int edt_id = -1;
    int width, height;
    Profile profile;
    TextView profileFullname, profileUsername;
    CircularImageView profilePhoto;
    ImageView profileCover;
    LinearLayout layouttop;
    RelativeLayout mProfileLoadingScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_me);
        Intent in = getIntent();
        profileId=in.getLongExtra("profileId",0);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {

            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setTitle(R.string.page_9);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        profileFullname = (TextView)findViewById(R.id.profileFullname);
        profileUsername = (TextView)findViewById(R.id.profileUsername);
        profilePhoto = (CircularImageView)findViewById(R.id.profilePhoto);
        profileCover = (ImageView)findViewById(R.id.profileCover);
        layouttop=(LinearLayout)findViewById(R.id.layouttop);
        mProfileLoadingScreen = (RelativeLayout) findViewById(R.id.profileLoadingScreen);

        layouttop.setVisibility(View.GONE);
        paramsedittext = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsedittext.gravity = Gravity.LEFT;

        paramsedittext.setMargins(0, 20, 0, 0);

        linear_layout_parent = (LinearLayout) findViewById(R.id.linearLayout);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        if (App.getInstance().isConnected()) {

            showLoadingScreen();
            getData();

        } else {

            showErrorScreen();
        }

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
    public void showLoadingScreen() {

        getSupportActionBar().setTitle(getText(R.string.title_activity_profile));
        mProfileLoadingScreen.setVisibility(View.VISIBLE);
//        loadingComplete = false;

        supportInvalidateOptionsMenu();
    }

    public void showErrorScreen() {

        getSupportActionBar().setTitle(getText(R.string.title_activity_profile));

        mProfileLoadingScreen.setVisibility(View.GONE);
//        loadingComplete = false;
        supportInvalidateOptionsMenu();
    }
    public void showDisabledScreen() {

        if (profile.getState() == ACCOUNT_STATE_BLOCKED) {

//            mProfileDisabledScreenMsg.setText(getText(R.string.label_profile_blocked));

        } else {

//            mProfileDisabledScreenMsg.setText(getText(R.string.label_profile_disabled));
        }

        getSupportActionBar().setTitle(getText(R.string.account_disabled));

        mProfileLoadingScreen.setVisibility(View.GONE);
//        loadingComplete = false;

        supportInvalidateOptionsMenu();
    }

    public void getData() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_PROFILE_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (response.getBoolean("error") == false) {

                                profile = new Profile(response);
                                if (profile.getState() == ACCOUNT_STATE_ENABLED) {

                                    showContentScreen();
                                    updateProfile();

                                }
                                else {

                                    showDisabledScreen();
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

//                            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                showErrorScreen();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("profileId", Long.toString(profileId));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }
    public void showContentScreen() {

        getSupportActionBar().setTitle(profile.getFullname());
        mProfileLoadingScreen.setVisibility(View.GONE);
//        loadingComplete = true;

        supportInvalidateOptionsMenu();

        if (ADMOB) {

//            mProfileAdMobCont.setVisibility(View.VISIBLE);

            AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
    }
    public void updateProfile() {

        profileUsername.setText(profile.getUsername());

        updateFullname();

        showPhoto(profile.getLowPhotoUrl());
        showCover(profile.getNormalCoverUrl());
Fill_addl_fields();
        supportInvalidateOptionsMenu();
    }
    private void updateFullname() {

        if (profile.getFullname().length() == 0) {

            profileFullname.setText(profile.getUsername());
            getSupportActionBar().setTitle(profile.getUsername());

        } else {

            profileFullname.setText(profile.getFullname());
            getSupportActionBar().setTitle(profile.getFullname());
        }

        if (!profile.isVerify()) {

            profileFullname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }
    public void showPhoto(String photoUrl) {

        if (photoUrl.length() > 0) {

            ImageLoader imageLoader = App.getInstance().getImageLoader();

            imageLoader.get(photoUrl, ImageLoader.getImageListener(profilePhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));
        }
    }

    public void showCover(String coverUrl) {

        if (coverUrl.length() > 0) {

            ImageLoader imageLoader = App.getInstance().getImageLoader();

            imageLoader.get(coverUrl, ImageLoader.getImageListener(profileCover, R.drawable.profile_default_cover, R.drawable.profile_default_cover));
        }
    }
   public void Fill_addl_fields()
    {
        try {
            String a = profile.getAddl_Fields().replace(" ","^^");

            JSONArray jsonArray = new JSONArray(a);
            String[] strArr = new String[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                strArr[i] = jsonArray.getString(i);

                edt_id = edt_id + 1;
                linear_layout[edt_id] = new LinearLayout(Profile_Details.this);
                linear_layout[edt_id].setLayoutParams(paramsedittext);
                linear_layout_parent.addView(linear_layout[edt_id]);


                edttext_name[edt_id] = new EditText(Profile_Details.this);
                allEds_name.add(edttext_name[edt_id]);
                edttext_name[edt_id].setId(edt_id + 0);
                edttext_name[edt_id].setWidth(width);
                edttext_name[edt_id].setBackgroundResource(R.drawable.rounded_edittext);
                edttext_name[edt_id].setText(strArr[i].replace("^^"," ").replace("<>",","));
                linear_layout[edt_id].addView(edttext_name[edt_id]);

            }
//            for (int i = 0; i < 25; i++) {
//                edt_id = edt_id + 1;
//                linear_layout[edt_id] = new LinearLayout(Profile_Details.this);
//                linear_layout[edt_id].setLayoutParams(paramsedittext);
//                linear_layout_parent.addView(linear_layout[edt_id]);
//
//
//                edttext_name[edt_id] = new EditText(Profile_Details.this);
//                allEds_name.add(edttext_name[edt_id]);
//                edttext_name[edt_id].setId(edt_id + 0);
//                edttext_name[edt_id].setWidth(width);
//                edttext_name[edt_id].setBackgroundResource(R.drawable.rounded_edittext);
//                linear_layout[edt_id].addView(edttext_name[edt_id]);
//            }

        }
        catch (JSONException e)
        {

        }
        layouttop.setVisibility(View.VISIBLE);
    }
}
