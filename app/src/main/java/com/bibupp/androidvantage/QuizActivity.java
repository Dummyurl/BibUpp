package com.bibupp.androidvantage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bibupp.androidvantage.adapter.QuizListAdapter;
import com.bibupp.androidvantage.app.App;
import com.bibupp.androidvantage.constants.Constants;
import com.bibupp.androidvantage.model.Profile;
import com.bibupp.androidvantage.model.Quiz;
import com.bibupp.androidvantage.util.CustomRequest;
import com.bibupp.androidvantage.util.ResponderInterface;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizActivity extends AppCompatActivity implements Constants, SwipeRefreshLayout.OnRefreshListener, ResponderInterface {

    private static final String STATE_LIST = "State Adapter Data";

    private ProgressDialog pDialog;

    ListView mListView;
    TextView mMessage;

    SwipeRefreshLayout mItemsContainer;
    private ArrayList<Quiz> itemsList;
    private QuizListAdapter itemsAdapter;

    private int addedToListAt = 0;
    private int arrayLength = 0;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;
    private Boolean restore = false;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    LinearLayout layout_quiz;
    ScrollView scrollView;
    int width, height;
    LinearLayout linear_layout[] = new LinearLayout[100];
    ImageView btn_remove[] = new ImageView[100];
    EditText edttext_name[] = new EditText[100];
    CheckBox check_correct[] = new CheckBox[100];
    List<EditText> allEds_name = new ArrayList<EditText>();
    List<ImageView> allBtn_remove = new ArrayList<ImageView>();
    List<CheckBox> all_checkbox = new ArrayList<CheckBox>();
    int id = 0;
    int edt_id = -1;
    String[] strings_options, strings_ans;
    Profile profile;
    Toolbar toolbar;
    String AccessTocken,AccountId;
Long profileId;
    //public static ArrayList<String> allquiz=new ArrayList<>();
    ArrayList allquizans = new ArrayList();
    ArrayList<String> array_execut_id = new ArrayList();


      JSONObject jResult = new JSONObject();
      JSONArray jArray = new JSONArray();
      ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    public static DBHelper mydb;
    Cursor cur, cursor;
    int length, length2;
    String QuizId, QuizTitle, QuizDate, QuizAns;
    boolean check = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Intent in = getIntent();
        profileId = in.getLongExtra("profileId",0);
        AccountId=in.getStringExtra("accountId");
        AccessTocken=in.getStringExtra("accessToken");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mydb = new DBHelper(this);
        if (toolbar != null) {

            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setTitle(R.string.page_10);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        initpDialog();

        if (savedInstanceState != null) {

            itemsList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            itemsAdapter = new QuizListAdapter(QuizActivity.this, itemsList, this);

            restore = savedInstanceState.getBoolean("restore");
            addedToListAt = savedInstanceState.getInt("addedToListAt");

        } else {

            itemsList = new ArrayList<Quiz>();
            itemsAdapter = new QuizListAdapter(QuizActivity.this, itemsList, this);

            restore = false;
            addedToListAt = 0;
        }

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mItemsContainer = (SwipeRefreshLayout) findViewById(R.id.container_items);
        mItemsContainer.setOnRefreshListener(this);
        mMessage = (TextView) findViewById(R.id.message);
        layout_quiz = (LinearLayout) findViewById(R.id.layout_quiz);
        mListView = (ListView) findViewById(R.id.listView);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        mListView.setAdapter(itemsAdapter);

//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//
//                Question question = (Question) adapterView.getItemAtPosition(position);
//
//                Intent intent = new Intent(QuizActivity.this, ReplyActivity.class);
//                intent.putExtra("questionId", question.getId());
//                intent.putExtra("listPosition", position);
//                startActivityForResult(intent, QUESTION_ANSWER);
//            }
//        });


        if (itemsAdapter.getCount() == 0) {

            showMessage(getText(R.string.label_empty_list).toString());

        } else {

            hideMessage();
        }

        if (!restore) {

            showMessage(getText(R.string.msg_loading_2).toString());

            getItems();
        }

    }

    public void listViewItemChange() {

        if (mListView.getCount() == 0) {

            showMessage(getText(R.string.label_empty_list).toString());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        outState.putInt("addedToListAt", addedToListAt);
        outState.putParcelableArrayList(STATE_LIST, itemsList);
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            addedToListAt = 0;
            mItemsContainer.setRefreshing(false);
//            getItems();

        } else {

            mItemsContainer.setRefreshing(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == QUESTION_ANSWER && resultCode == RESULT_OK && null != data) {

            itemsList.remove(data.getIntExtra("listPosition", -1));
            itemsAdapter.notifyDataSetChanged();

            this.listViewItemChange();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    public void like(int position, JSONObject data) {

        //
    }

    public void getItems() {

        mItemsContainer.setRefreshing(true);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_QUIZ_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            arrayLength = 0;

                            if (!loadingMore) {

                                itemsList.clear();
                            }

                            if (!response.getBoolean("error")) {


                                JSONArray questionsArray = response.getJSONArray("quizs");

                                arrayLength = questionsArray.length();

                                if (arrayLength > 0) {
//                                    allquiz=new ArrayList<PhoneCall>(questionsArray.length());
                                    for (int i = 0; i < questionsArray.length(); i++) {

                                        JSONObject quizObj = (JSONObject) questionsArray.get(i);

                                        Quiz quiz = new Quiz(quizObj);

                                        itemsList.add(quiz);
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loadingComplete();
//                            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loadingComplete();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId",profileId.toString());
                params.put("accessToken",AccessTocken);
                params.put("lang", sharedpreferences.getString("Language", "en"));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

//    public void getRandom() {
//
//        showpDialog();
//
//        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_QUESTIONS_RANDOM, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                        try {
//
//                            if (!response.getBoolean("error")) {
//
//                                Quiz quiz = new Quiz(response);
//
//                                itemsList.add(0, quiz);
//                                itemsAdapter.notifyDataSetChanged();
//
//                                mListView.smoothScrollToPosition(0);
//                            }
//
//                        } catch (JSONException e) {
//
//                            e.printStackTrace();
//
//                        } finally {
//
//                            hidepDialog();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                hidepDialog();
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("accountId", Long.toString(App.getInstance().getId()));
//                params.put("accessToken", App.getInstance().getAccessToken());
//
//                return params;
//            }
//        };
//
//        App.getInstance().addToRequestQueue(jsonReq);
//    }

    public void loadingComplete() {

        if (arrayLength == LIST_ITEMS) {

            viewMore = true;

        } else {

            viewMore = false;
        }

        itemsAdapter.notifyDataSetChanged();

        if (itemsAdapter.getCount() == 0) {


            showMessage(getText(R.string.label_empty_list).toString());


        } else {

            hideMessage();
        }

        loadingMore = false;
        mItemsContainer.setRefreshing(false);
    }

    public void showMessage(String message) {

        mMessage.setText(message);
        mMessage.setVisibility(View.VISIBLE);
    }

    public void hideMessage() {

        mMessage.setVisibility(View.GONE);
    }


    protected void initpDialog() {

        pDialog = new ProgressDialog(QuizActivity.this);
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
                cur = mydb.getallQuiz();
                length = cur.getCount();
                if(length>0) {
                new Progress().execute();
                }
                else
                {
                    Toast.makeText(QuizActivity.this,"Please solve at least one quiz.",Toast.LENGTH_LONG).show();
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

    public class Progress extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {


            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpPost httpPost = new HttpPost(METHOD_QUIZ_ADDANSWER);
            try {
                cur = mydb.getallQuiz();
                length = cur.getCount();

                if (cur != null) {
                    cur.moveToFirst();
                }
                do {
                    check=false;
                    if (array_execut_id.size() > 0) {
                        for (int i = 0; i < array_execut_id.size(); i++) {
                            String a = array_execut_id.get(i);
                            if (a.equalsIgnoreCase(cur.getString(cur.getColumnIndex("quiz_id")))) {
                                check = true;
                                break;
                            }
                        }

                        if (check) {

                        } else {
                            check=false;
                            try {

                                QuizId = cur.getString(cur.getColumnIndex("quiz_id"));
                                QuizTitle = cur.getString(cur.getColumnIndex("quiz_title"));
                                QuizDate = cur.getString(cur.getColumnIndex("quiz_date"));
                                QuizAns = cur.getString(cur.getColumnIndex("chossed_ans"));


                                cursor = mydb.getQuizById(QuizId);
                                length2 = cursor.getCount();
                                if (length2 > 1) {
                                    JSONObject jGroup = new JSONObject();
                                    jGroup.put("user_id", Long.toString(App.getInstance().getId()));
                                    jGroup.put("quiz_id", QuizId);
                                    jGroup.put("quiz_title", QuizTitle);
                                    jGroup.put("quiz_date", QuizDate);

                                    if (cursor != null) {
                                        cursor.moveToFirst();
                                    }
                                    do {
                                        try {
                                            QuizAns = cursor.getString(cursor.getColumnIndex("chossed_ans"));
                                            allquizans.add(QuizAns);

                                        } catch (IndexOutOfBoundsException e) {

                                        } catch (OutOfMemoryError e) {

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } while (cursor.moveToNext());

                                    jGroup.put("chossed_ans", allquizans.toString().substring(1,allquizans.toString().length()-1));

                                    jArray.put(jGroup);

                                    jResult.put("question", jArray);
//                                    array_execut_id.clear();

                                } else {

                                    JSONObject jGroup = new JSONObject();
//
                                    jGroup.put("user_id",Long.toString(App.getInstance().getId()));
                                    jGroup.put("quiz_id", QuizId);
                                    jGroup.put("quiz_title", QuizTitle);
                                    jGroup.put("quiz_date", QuizDate);
//                                    jGroup.put("chossed_ans", QuizAns);
                                    try {
                                        QuizAns = cur.getString(cur.getColumnIndex("chossed_ans"));
                                        allquizans.add(QuizAns);

                                    } catch (IndexOutOfBoundsException e) {

                                    } catch (OutOfMemoryError e) {

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    jGroup.put("chossed_ans", allquizans.toString().substring(1,allquizans.toString().length()-1));
                                    jArray.put(jGroup);

                                    jResult.put("question", jArray);
                                }
                                array_execut_id.add(QuizId);


                            } catch (IndexOutOfBoundsException e) {

                            } catch (OutOfMemoryError e) {

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                    } else {
                        try {

                            QuizId = cur.getString(cur.getColumnIndex("quiz_id"));
                            QuizTitle = cur.getString(cur.getColumnIndex("quiz_title"));
                            QuizDate = cur.getString(cur.getColumnIndex("quiz_date"));
                            QuizAns = cur.getString(cur.getColumnIndex("chossed_ans"));


                            cursor = mydb.getQuizById(QuizId);
                            length2 = cursor.getCount();
                            if (length2 > 1) {
                                JSONObject jGroup = new JSONObject();
                                jGroup.put("user_id", Long.toString(App.getInstance().getId()));
                                jGroup.put("quiz_id", QuizId);
                                jGroup.put("quiz_title", QuizTitle);
                                jGroup.put("quiz_date", QuizDate);

                                if (cursor != null) {
                                    cursor.moveToFirst();
                                }
                                do {
                                    try {
                                        QuizAns = cursor.getString(cursor.getColumnIndex("chossed_ans"));
                                        allquizans.add(QuizAns);

                                    } catch (IndexOutOfBoundsException e) {

                                    } catch (OutOfMemoryError e) {

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } while (cursor.moveToNext());

                                jGroup.put("chossed_ans", allquizans.toString().substring(1,allquizans.toString().length()-1));

                                jArray.put(jGroup);

                                jResult.put("question", jArray);


                            } else {

                                JSONObject jGroup = new JSONObject();
//
                                jGroup.put("user_id",Long.toString(App.getInstance().getId()));
                                jGroup.put("quiz_id", QuizId);
                                jGroup.put("quiz_title", QuizTitle);
                                jGroup.put("quiz_date", QuizDate);
//                                jGroup.put("chossed_ans", QuizAns);


                                try {
                                    QuizAns = cur.getString(cur.getColumnIndex("chossed_ans"));
                                    allquizans.add(QuizAns);

                                } catch (IndexOutOfBoundsException e) {

                                } catch (OutOfMemoryError e) {

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                jGroup.put("chossed_ans", allquizans.toString().substring(1,allquizans.toString().length()-1));


                                jArray.put(jGroup);

                                jResult.put("question", jArray);
                            }
                            array_execut_id.add(QuizId);


                        } catch (IndexOutOfBoundsException e) {

                        } catch (OutOfMemoryError e) {

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    allquizans.clear();
                } while (cur.moveToNext());

                nameValuePairs.add(new BasicNameValuePair("addanswer", jResult.toString()));
                nameValuePairs.add(new BasicNameValuePair("accountId", Long.toString(App.getInstance().getId())));
                nameValuePairs.add(new BasicNameValuePair("accessToken", App.getInstance().getAccessToken()));
                nameValuePairs.add(new BasicNameValuePair("lang",sharedpreferences.getString("Language","en")));
                nameValuePairs.add(new BasicNameValuePair("profileId",profileId.toString()));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity httpEntity = response.getEntity();
                String result = EntityUtils.toString(httpEntity);

            } catch (Exception e) {
                // Log exception
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mydb.delete();
            jResult=null;
            Toast.makeText(QuizActivity  .this, "Successfully submited", Toast.LENGTH_LONG).show();
            QuizActivity.this.finish();

        }
    }
}