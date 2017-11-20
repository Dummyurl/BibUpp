package com.bibupp.androidvantage;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bibupp.androidvantage.app.App;
import com.bibupp.androidvantage.common.ActivityBase;
import com.bibupp.androidvantage.model.Profile;
import com.bibupp.androidvantage.util.CustomRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProfileEditActivity extends ActivityBase {
    Toolbar toolbar;
    EditText profileEditFullname;
    private String profileFullname;
    String[] strings;
    LinearLayout linear_layout_parent;
    LinearLayout.LayoutParams paramsedittext, paramsbutton;
    EditText edttext_name[] = new EditText[100];
    Button btn_add;
    ImageView btn_remove[] = new ImageView[100];
    LinearLayout linear_layout[] = new LinearLayout[100];
    List<EditText> allEds_name = new ArrayList<EditText>();
    List<ImageView> allBtn_remove = new ArrayList<ImageView>();
    int id = 0;
    int edt_id = -1;
    int width, height;
    Profile profile;
    boolean allfilled=false;
    TextView empty_fields;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {

            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent in = getIntent();
//        profileFullname = i.getStringExtra("profileFullname");

        profileEditFullname = (EditText) findViewById(R.id.profileEditFullname);

        profileEditFullname.setText(in.getStringExtra("fullname"));
        btn_add = (Button) findViewById(R.id.btn_add);
        empty_fields=(TextView)findViewById(R.id.empty_fields);
        empty_fields.setVisibility(View.GONE);
//        layoutedittext_name = (LinearLayout) findViewById(R.id.linearLayoutedittext);
        paramsedittext = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsedittext.gravity = Gravity.LEFT;

        paramsedittext.setMargins(0, 20, 0, 0);

        linear_layout_parent = (LinearLayout) findViewById(R.id.linearLayout);
        paramsbutton = new LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsbutton.gravity = Gravity.CENTER_VERTICAL;
        paramsbutton.setMargins(10, 0, 0, 0);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        for (int i = 0; i < ProfileActivity.strArr.length; i++) {
            edt_id = edt_id + 1;
            linear_layout[edt_id] = new LinearLayout(ProfileEditActivity.this);
            linear_layout[edt_id].setLayoutParams(paramsedittext);
            linear_layout_parent.addView(linear_layout[edt_id]);


            edttext_name[edt_id] = new EditText(ProfileEditActivity.this);
            allEds_name.add(edttext_name[edt_id]);
            edttext_name[edt_id].setId(edt_id + 0);
            edttext_name[edt_id].setWidth(width-100);
            edttext_name[edt_id].setBackgroundResource(R.drawable.rounded_edittext);
            edttext_name[edt_id].setText(ProfileActivity.strArr[i].replace("^^", " ").replace("<>", ","));
            linear_layout[edt_id].addView(edttext_name[edt_id]);

            btn_remove[edt_id] = new ImageView(ProfileEditActivity.this);
            allBtn_remove.add(btn_remove[edt_id]);
            btn_remove[edt_id].setId(edt_id + 0);
            btn_remove[edt_id].setLayoutParams(paramsbutton);
            btn_remove[edt_id].setBackgroundResource(R.drawable.remove);
//                layoutbutton.addView(btn_remove);
            linear_layout[edt_id].addView(btn_remove[edt_id]);

            btn_remove[edt_id].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    int pos1 = (Integer) v.getId();//getTag();
                    id = edttext_name[pos1].getId();
                    allEds_name.remove(edttext_name[pos1]);
//                        linear_layout_parent.removeViewAt(pos1);
                    linear_layout_parent.removeView(linear_layout[pos1]);
                }
            });

        }


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edt_id = edt_id + 1;
                linear_layout[edt_id] = new LinearLayout(ProfileEditActivity.this);
                linear_layout[edt_id].setLayoutParams(paramsedittext);
                linear_layout_parent.addView(linear_layout[edt_id]);


                edttext_name[edt_id] = new EditText(ProfileEditActivity.this);
                allEds_name.add(edttext_name[edt_id]);
                edttext_name[edt_id].setId(edt_id + 0);
//                edttext_name.setLayoutParams(paramsbutton);
                edttext_name[edt_id].setWidth(width - 100);
                edttext_name[edt_id].setBackgroundResource(R.drawable.rounded_edittext);
//                layoutedittext_name.addView(edttext_name);
                linear_layout[edt_id].addView(edttext_name[edt_id]);

                btn_remove[edt_id] = new ImageView(ProfileEditActivity.this);
                allBtn_remove.add(btn_remove[edt_id]);
                btn_remove[edt_id].setId(edt_id + 0);
                btn_remove[edt_id].setLayoutParams(paramsbutton);
                btn_remove[edt_id].setBackgroundResource(R.drawable.remove);
//                layoutbutton.addView(btn_remove);
                linear_layout[edt_id].addView(btn_remove[edt_id]);

                btn_remove[edt_id].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        int pos1 = (Integer) v.getId();//getTag();
                        id = edttext_name[pos1].getId();
                        allEds_name.remove(edttext_name[pos1]);
//                        linear_layout_parent.removeViewAt(pos1);
                        linear_layout_parent.removeView(linear_layout[pos1]);
                    }
                });


            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case R.id.action_profile_save: {

                profileFullname = profileEditFullname.getText().toString();
                profileFullname = profileFullname.trim();


                strings = new String[allEds_name.size()];

//                for (int i = 0; i < allEds_name.size(); i++) {
//                    strings[i] = allEds_name.get(i).getText().toString();
////                    mydb.insert_holidays_list(strings[i]);
//                }
                for (int i = 0; i < allEds_name.size(); i++) {
                    strings[i] = allEds_name.get(i).getText().toString().replace(",", "<>");//remove , from string to convertin string to JSONArray
                }

                if (profileEditFullname.getText().toString().equalsIgnoreCase("")) {
                    profileEditFullname.requestFocus();
                    profileEditFullname.setText("");
                    profileEditFullname.setError("Please enter profile name.");
                } else {
                    allfilled = true;
                    for (int i = 0; i < allEds_name.size(); i++) {
                        if (allEds_name.get(i).getText().toString().equalsIgnoreCase("")) {
//                            edttext_name[i].requestFocus();
//                            edttext_name[i].setText("");
//                            edttext_name[i].setError("Please enter your data");
//                            Toast.makeText(ProfileEditActivity.this,"Please fill all fields.",Toast.LENGTH_LONG).show();
                            empty_fields.setVisibility(View.VISIBLE);
                            allfilled = false;
                            break;
                        }
                    }
                    if (allfilled) {
                            save();
                    }
                }
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

    public void save() {

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_EDIT, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (response.getBoolean("error") == false) {

                                profile = new Profile(response);
                                if (profile.getState() == ACCOUNT_STATE_ENABLED) {

                                    Fill_addl_fields();

                                }

                                Intent i = new Intent();
                                i.putExtra("fullname", response.getString("fullname"));
                                setResult(RESULT_OK, i);

                                finish();
                            }

//                            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            hidepDialog();

//                            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hidepDialog();

                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("fullname", profileFullname);
//                for (int i = 0; i < allEds_name.size(); i++) {
//                    strings[i] = allEds_name.get(i).getText().toString().replace(",", "<>");
//                }
                params.put("addl_fields", Arrays.toString(strings));
                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }
    public void Fill_addl_fields()
    {
        try {
            String a = profile.getAddl_Fields().replace(" ","^^");//   remove space for convert string to JSONArray,not converting with space

            JSONArray jsonArray = new JSONArray(a);
            ProfileActivity.strArr = new String[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                ProfileActivity.strArr[i] = jsonArray.getString(i);
            }

        }
        catch (JSONException e)
        {

        }
    }


}
