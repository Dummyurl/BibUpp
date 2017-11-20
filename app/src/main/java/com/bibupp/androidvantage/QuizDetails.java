package com.bibupp.androidvantage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bibupp.androidvantage.app.App;
import com.bibupp.androidvantage.constants.Constants;

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
import org.json.JSONObject;

import java.util.ArrayList;

public class QuizDetails extends AppCompatActivity implements Constants {

    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    String QuizId = "",QuizTitle="";
    ListView listView;
    ArrayList<Holder> list = new ArrayList<Holder>();
    JSONArray Quizstatusarray;
    TextView message,txt_title;
    String UserName, QuizDate, QuizStatus, AccountId;
    Toolbar toolbar;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {

            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setTitle(R.string.page_9);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Intent in = getIntent();
        QuizId = in.getStringExtra("quizId");
        QuizTitle = in.getStringExtra("quizTitle");
        listView = (ListView) findViewById(R.id.listView);
        message = (TextView) findViewById(R.id.message);
        txt_title = (TextView) findViewById(R.id.txt_title);
        message.setText(getText(R.string.label_user_havent_quizanswers).toString());
        txt_title.setText(QuizTitle);
        new Progress().execute();
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_profile_edit, menu);
//        return true;
//    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
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
        private ProgressDialog pdia;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = new ProgressDialog(QuizDetails.this);
            pdia.setMessage("Loading...");
            pdia.show();
            pdia.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            try {
                HttpPost httpPost = new HttpPost(METHOD_QUIZ_DETAILS);

                nameValuePairs.add(new BasicNameValuePair("quiz_id", QuizId));
                nameValuePairs.add(new BasicNameValuePair("accountId", Long.toString(App.getInstance().getId())));
                nameValuePairs.add(new BasicNameValuePair("accessToken", App.getInstance().getAccessToken()));
                nameValuePairs.add(new BasicNameValuePair("lang",sharedpreferences.getString("Language","en")));

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity httpEntity = response.getEntity();
                String result = EntityUtils.toString(httpEntity);
                JSONObject json = new JSONObject(result);
                Quizstatusarray = json.getJSONArray("answerss");
                for (int i = 0; i < Quizstatusarray.length(); i++) {

                    JSONObject c = Quizstatusarray.getJSONObject(i);
                    //					Bitmap myBitmap = null;
                    UserName = c.getString("name");
                    AccountId = c.getString("account_id");
                    QuizDate = c.getString("quiz_date");
                    QuizStatus = c.getString("exam_status");
                    Holder h = new Holder();


                    h.setUserName(UserName);
                    h.setQuizDate(QuizDate);
                    h.setQuizStatus(QuizStatus);

                    list.add(h);

                }
            } catch (Exception e) {
                // Log exception
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Toast.makeText(QuizDetails.this, "Successfully submited", Toast.LENGTH_LONG).show();
            pdia.dismiss();
            pdia = null;
            listView.setAdapter(new MyCustomAdapter(QuizDetails.this, list));
            if (list.size() > 0) {
                message.setVisibility(View.GONE);
            } else {
                message.setVisibility(View.VISIBLE);
            }

        }
    }

    class MyCustomAdapter extends BaseAdapter {

        LayoutInflater inflater;
        ArrayList<Holder> list;

        public MyCustomAdapter(FragmentActivity fragmentActivity, ArrayList<Holder> list) {
            inflater = LayoutInflater.from(fragmentActivity);
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int paramInt) {
            return paramInt;
        }

        class ViewHolder {
            TextView name, date, status;

        }

        @Override
        public long getItemId(int paramInt) {
            return paramInt;
        }

        @Override
        public View getView(final int paramInt, View paramView, ViewGroup paramViewGroup) {

            ViewHolder holder;
            if (paramView == null) {
                paramView = inflater.inflate(R.layout.quiz_details_listitems, paramViewGroup, false);
                holder = new ViewHolder();

                holder.name = (TextView) paramView.findViewById(R.id.txt_username);
                holder.date = (TextView) paramView.findViewById(R.id.txt_date);
                holder.status = (TextView) paramView.findViewById(R.id.txt_status);
                paramView.setTag(holder);
            } else {
                holder = (ViewHolder) paramView.getTag();
            }

            Holder h = list.get(paramInt);
            holder.name.setText(h.getUserName());
            holder.date.setText(h.getQuizDate());
            holder.status.setText(h.getQuizStatus());
            return paramView;
        }
    }
}
