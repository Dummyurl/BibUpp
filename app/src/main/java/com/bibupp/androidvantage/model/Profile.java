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

/**
 * Created by Administrator on 22.02.2015.
 */
public class Profile extends Application implements Constants, Parcelable {

    private long id;

    private int state, verify, answersCount, likesCount, followersCount, anonymousQuestions;

    private String username, fullname, email, status, lowPhotoUrl, bigPhotoUrl, normalPhotoUrl, normalCoverUrl, addl_Fields;

    //for search only
    private Boolean follow = false, blocked = false;

    public Profile() {


    }

    public Profile(JSONObject jsonData) {

        try {

            if (jsonData.getBoolean("error") == false) {

                if (jsonData.has("id")) {
                    this.setId(jsonData.getLong("id"));
                }
                if (jsonData.has("state")) {
                    this.setState(jsonData.getInt("state"));
                }
                if (jsonData.has("username")) {
                    this.setUsername(jsonData.getString("username"));
                }
                if (jsonData.has("fullname")) {
                    this.setFullname(jsonData.getString("fullname"));
                }
                if (jsonData.has("status")) {
                    this.setStatus(jsonData.getString("status"));
                }
                if (jsonData.has("verify")) {
                    this.setVerify(jsonData.getInt("verify"));
                }
                if (jsonData.has("lowPhotoUrl")) {
                    this.setLowPhotoUrl(jsonData.getString("lowPhotoUrl"));
                }

                if (jsonData.has("normalPhotoUrl")) {
                    this.setNormalPhotoUrl(jsonData.getString("normalPhotoUrl"));
                }
                if (jsonData.has("bigPhotoUrl")) {
                    this.setBigPhotoUrl(jsonData.getString("bigPhotoUrl"));
                }
                if (jsonData.has("normalCoverUrl")) {
                    this.setNormalCoverUrl(jsonData.getString("normalCoverUrl"));
                }
                if (jsonData.has("followersCount")) {
                    this.setFollowersCount(jsonData.getInt("followersCount"));
                }
                if (jsonData.has("answersCount")) {
                    this.setAnswersCount(jsonData.getInt("answersCount"));
                }
                if (jsonData.has("likesCount")) {
                    this.setLikesCount(jsonData.getInt("likesCount"));
                }
                if (jsonData.has("anonymousQuestions")) {
                    this.setAnonymousQuestions(jsonData.getInt("anonymousQuestions"));
                }
                if (jsonData.has("follow")) {
                    this.setFollow(jsonData.getBoolean("follow"));
                }
                if (jsonData.has("blocked")) {
                    this.setBlocked(jsonData.getBoolean("blocked"));
                }
                if (jsonData.has("addl_fields")) {
                    this.setAddl_Fields(jsonData.getString("addl_fields"));
                }
            }

        } catch (Throwable t) {

            Log.e("Profile", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("Profile", jsonData.toString());
        }
    }

    public void setAddl_Fields(String addl_Fields) {
        this.addl_Fields = addl_Fields;
    }

    public String getAddl_Fields() {
        String addl_Fields = this.addl_Fields;
        return addl_Fields;
    }

    public String getUrl() {

        String url = APP_DESKTOP_SITE + this.getUsername();

        return url;
    }

    public void setId(long profile_id) {

        this.id = profile_id;
    }

    public long getId() {

        return this.id;
    }

    public void setState(int profileState) {

        this.state = profileState;
    }

    public int getState() {

        return this.state;
    }

    public void setVerify(int profileVerify) {

        this.verify = profileVerify;
    }

    public int getVerify() {

        return this.verify;
    }

    public Boolean isVerify() {

        if (this.verify > 0) {

            return true;
        }

        return false;
    }

    public void setUsername(String profile_username) {

        this.username = profile_username;
    }

    public String getUsername() {

        return this.username;
    }

    public void setFullname(String profile_fullname) {

        this.fullname = profile_fullname;
    }

    public String getFullname() {

        return this.fullname;
    }

    public void setEmail(String profile_email) {

        this.email = profile_email;
    }

    public String getEmail() {

        return this.email;
    }

    public void setStatus(String profile_status) {

        this.status = profile_status;
    }

    public String getStatus() {

        return this.status;
    }

    public void setLowPhotoUrl(String lowPhotoUrl) {

        this.lowPhotoUrl = lowPhotoUrl;
    }

    public String getLowPhotoUrl() {

        return this.lowPhotoUrl;
    }

    public void setBigPhotoUrl(String bigPhotoUrl) {

        this.bigPhotoUrl = bigPhotoUrl;
    }

    public String getBigPhotoUrl() {

        return this.bigPhotoUrl;
    }

    public void setNormalPhotoUrl(String normalPhotoUrl) {

        this.normalPhotoUrl = normalPhotoUrl;
    }

    public String getNormalPhotoUrl() {

        return this.normalPhotoUrl;
    }

    public void setNormalCoverUrl(String normalCoverUrl) {

        this.normalCoverUrl = normalCoverUrl;
    }

    public String getNormalCoverUrl() {

        return this.normalCoverUrl;
    }

    public void setFollowersCount(int followersCount) {

        this.followersCount = followersCount;
    }

    public int getFollowersCount() {

        return this.followersCount;
    }

    public void setAnswersCount(int answersCount) {

        this.answersCount = answersCount;
    }

    public int getAnswersCount() {

        return this.answersCount;
    }

    public void setLikesCount(int likesCount) {

        this.likesCount = likesCount;
    }

    public int getLikesCount() {

        return this.likesCount;
    }

    public void setAnonymousQuestions(int anonymousQuestions) {

        this.anonymousQuestions = anonymousQuestions;
    }

    public int getAnonymousQuestions() {

        return this.anonymousQuestions;
    }

    public void setFollow(Boolean follow) {

        this.follow = follow;
    }

    public Boolean isFollow() {

        return this.follow;
    }

    public void setBlocked(Boolean blocked) {

        this.blocked = blocked;
    }

    public Boolean isBlocked() {

        return this.blocked;
    }


    public void addFollower() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_PROFILE_ADDFOLLOWER, null,
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
                params.put("profileId", Long.toString(getId()));

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

        public Profile createFromParcel(Parcel in) {

            return new Profile();
        }

        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };
}
