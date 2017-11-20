package com.bibupp.androidvantage.model;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bibupp.androidvantage.app.App;
import com.bibupp.androidvantage.constants.Constants;
import com.bibupp.androidvantage.util.CustomRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Quiz extends Application implements Constants, Parcelable {

    private long quizId, createBy;
    private String ansList, createDate, quizTitle,optionId,option_title,optionList;
int total;

    public String getOptionList() {
        return optionList;
    }

    public void setOptionList(String optionList) {
        this.optionList = optionList;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public String getOption_title() {
        return option_title;
    }

    public void setOption_title(String option_title) {
        this.option_title = option_title;
    }

    public Quiz() {

    }

    public Quiz(JSONObject jsonData) {

        try {

//            if (jsonData.getBoolean("error") == false) {

                this.setId(jsonData.getLong("id"));
                this.setQuizTitle(jsonData.getString("title"));
                this.setCreateBy(jsonData.getLong("createdBy"));
                this.setDate(jsonData.getString("createDate"));
            this.setOptionList(jsonData.getString("option_lists"));


//            JSONArray questionsArray = jsonData.getJSONArray("option_lists");
//            int arrayLength = jsonData.getString("option_lists").length();
//            this.setTotal(questionsArray.length());
//            for (int i = 0; i < questionsArray.length(); i++) {
//                JSONObject quizObj = (JSONObject) questionsArray.get(i);
//
//                this.setOption_title(quizObj.getString("option_title"));
//                this.setOptionId(quizObj.getString("id"));
//
//            }
//            }

        } catch (Throwable t) {

            Log.e("Question", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("Question", jsonData.toString());
        }
    }

    public void setId(long quizId) {

        this.quizId = quizId;
    }

    public long getId() {

        return this.quizId;
    }

    public void setCreateBy(long createBy) {

        this.createBy = createBy;
    }

    public long getCreateBy() {

        return this.createBy;
    }

    public void setQuizTitle(String quizTitle) {

        this.quizTitle = quizTitle;
    }

    public String getQuizTitle() {

        return this.quizTitle;
    }

//    public void setTo_option_lists(String optionList) {
//        this.optionList = optionList;
//    }
//
//    public String getTo_option_lists() {
//
//        return this.optionList;
//    }

    public void setAnswers(String ansList) {

        this.ansList = ansList;
    }

    public String getAnswers() {

        return this.ansList;
    }

    public void setDate(String createDate) {

        this.createDate = createDate;
    }

    public String getDate() {

        return this.createDate;
    }


    public void remove() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_QUESTIONS_REMOVE, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (response.getBoolean("error") == false) {


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

//                     Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("questionId", Long.toString(getId()));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static final Creator CREATOR = new Creator() {

        public Question createFromParcel(Parcel in) {

            return new Question();
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}
