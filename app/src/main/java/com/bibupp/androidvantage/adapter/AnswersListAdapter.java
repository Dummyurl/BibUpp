package com.bibupp.androidvantage.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.bibupp.androidvantage.HashtagsActivity;
import com.bibupp.androidvantage.PhotoViewActivity;
import com.bibupp.androidvantage.ProfileActivity;
import com.bibupp.androidvantage.R;
import com.bibupp.androidvantage.app.App;
import com.bibupp.androidvantage.constants.Constants;
import com.bibupp.androidvantage.model.Answer;
import com.bibupp.androidvantage.model.Profile;
import com.bibupp.androidvantage.util.CustomRequest;
import com.bibupp.androidvantage.util.TagClick;
import com.bibupp.androidvantage.util.TagSelectingTextview;
import com.bibupp.androidvantage.view.ResizableImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswersListAdapter extends BaseAdapter implements Constants, TagClick {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Answer> answersList;
    boolean check_like = false;
    Profile profile;
    TagSelectingTextview mTagSelectingTextview;

    public static int hashTagHyperLinkEnabled = 1;
    public static int hashTagHyperLinkDisabled = 0;

    ImageLoader imageLoader = App.getInstance().getImageLoader();

    public AnswersListAdapter(Activity activity, List<Answer> answersList) {

        this.activity = activity;
        this.answersList = answersList;

        mTagSelectingTextview = new TagSelectingTextview();
    }

    @Override
    public int getCount() {

        return answersList.size();
    }

    @Override
    public Object getItem(int location) {

        return answersList.get(location);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    static class ViewHolder {

        public TextView questionText;
        public TextView answerText;
        public TextView questionAuthor;
        public TextView answerReplyAt;
        public TextView answerLikesCount;
        public ImageView answerAction;
        public ImageView answerLike;
        public ResizableImageView answerImg;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (inflater == null) {

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.answer_list_row, null);

            viewHolder = new ViewHolder();

            viewHolder.answerAction = (ImageView) convertView.findViewById(R.id.answerAction);
            viewHolder.answerLike = (ImageView) convertView.findViewById(R.id.answerLike);
            viewHolder.answerImg = (ResizableImageView) convertView.findViewById(R.id.answerImg);
            viewHolder.answerLikesCount = (TextView) convertView.findViewById(R.id.answerLikesCount);
            viewHolder.questionText = (TextView) convertView.findViewById(R.id.questionText);
            viewHolder.answerText = (TextView) convertView.findViewById(R.id.answerText);
            viewHolder.questionAuthor = (TextView) convertView.findViewById(R.id.questionAuthor);
            viewHolder.answerReplyAt = (TextView) convertView.findViewById(R.id.answerReplyAt);

//            viewHolder.questionRemove.setTag(position);
            convertView.setTag(viewHolder);

            viewHolder.answerAction.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int getPosition = (Integer) v.getTag();
                    ViewHolder viewHolder = (ViewHolder) v.getTag(R.id.answerAction);
                    // TODO Auto-generated method stub

//                    Answer a = answersList.get(getPosition);
//                    a.remove();
//
//
//                    answersList.remove(getPosition);
//                    notifyDataSetChanged();
                }
            });

            viewHolder.questionAuthor.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int getPosition = (Integer) v.getTag();

                    Answer answer = answersList.get(getPosition);

                    Intent intent = new Intent(activity, ProfileActivity.class);
                    intent.putExtra("profileId", answer.getFromUserId());
                    activity.startActivity(intent);
                }
            });

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (imageLoader == null) {

            imageLoader = App.getInstance().getImageLoader();
        }

//        viewHolder.questionRemove.setTag(position);
        viewHolder.questionText.setTag(position);
        viewHolder.questionAuthor.setTag(position);
        viewHolder.answerReplyAt.setTag(position);
        viewHolder.answerAction.setTag(position);
        viewHolder.answerText.setTag(position);
        viewHolder.answerImg.setTag(position);
        viewHolder.answerLike.setTag(position);
        viewHolder.answerLikesCount.setTag(position);
        viewHolder.answerAction.setTag(R.id.answerAction, viewHolder);

        final Answer answer = answersList.get(position);

        viewHolder.questionText.setText(answer.getQuestionText());

        if (answer.getToUserId() == App.getInstance().getId()) {

            viewHolder.answerAction.setVisibility(View.VISIBLE);
            viewHolder.answerAction.setImageResource(R.drawable.ic_action_remove);

            viewHolder.answerAction.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int getPosition = (Integer) v.getTag();
                    ViewHolder viewHolder = (ViewHolder) v.getTag(R.id.answerAction);
                    // TODO Auto-generated method stub

                    Answer a = answersList.get(getPosition);
                    a.remove();


                    answersList.remove(getPosition);
                    notifyDataSetChanged();
                }
            });

        } else {

            viewHolder.answerAction.setVisibility(View.GONE);
            viewHolder.answerAction.setImageResource(R.drawable.answer_report);
        }

        if (answer.getFromUserId() != 0) {

            viewHolder.questionAuthor.setVisibility(View.VISIBLE);
            viewHolder.questionAuthor.setText(answer.getFromUserFullname());

        } else {

            viewHolder.questionAuthor.setVisibility(View.GONE);
        }

        if (answer.getAnswerText().length() > 0) {

            viewHolder.answerText.setMovementMethod(LinkMovementMethod.getInstance());

            String textHtml = answer.getAnswerText().replaceAll("<br>", "\n");

            viewHolder.answerText.setText(mTagSelectingTextview.addClickablePart(Html.fromHtml(textHtml).toString(), this, hashTagHyperLinkDisabled, HASHTAGS_COLOR), TextView.BufferType.SPANNABLE);

//            viewHolder.answerText.setText( answer.getAnswerText().replaceAll("<br>", "\n"));
            viewHolder.answerText.setVisibility(View.VISIBLE);

        } else {

            viewHolder.answerText.setVisibility(View.GONE);
        }

        if (answer.getImgUrl().length() > 0) {

            imageLoader.get(answer.getImgUrl(), ImageLoader.getImageListener(viewHolder.answerImg, R.drawable.img_loading, R.drawable.img_loading));
            viewHolder.answerImg.setVisibility(View.VISIBLE);

            viewHolder.answerImg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent i = new Intent(activity, PhotoViewActivity.class);
                    i.putExtra("imgUrl", answer.getImgUrl());
                    activity.startActivity(i);
                }
            });

        } else {

            viewHolder.answerImg.setVisibility(View.GONE);
        }

        //		 Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                answer.getReplyAt() * 1000l,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

        viewHolder.answerReplyAt.setText(timeAgo);

        viewHolder.answerLikesCount.setVisibility(View.GONE);
        viewHolder.answerLikesCount.setText(Integer.toString(answer.getLikesCount()));
        viewHolder.answerLike.setImageResource(R.drawable.like);
//        if(check_like) {
//            ProfileActivity.total_like = ProfileActivity.total_like + answer.getLikesCount();
//            ProfileActivity.mLikesCount.setText(Integer.toString(ProfileActivity.total_like));
//        }
        if (answer.getLikesCount() != 0) {

            viewHolder.answerLikesCount.setVisibility(View.VISIBLE);

            if (answer.isMyLike()) {

                viewHolder.answerLike.setImageResource(R.drawable.like_active);
            }
        }

        viewHolder.answerLike.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final int getPosition = (Integer) v.getTag();

                if (App.getInstance().isConnected()) {

                    CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ANSWERS_LIKE, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    try {

                                        if (response.getBoolean("error") == false) {

                                            answer.setLikesCount(response.getInt("likesCount"));
                                            answer.setMyLike(response.getBoolean("myLike"));
                                        }

                                    } catch (JSONException e) {

                                        e.printStackTrace();

                                    } finally {
//                                        ProfileActivity pt=new ProfileActivity();
//                                        pt.getData();
                                        ProfileActivity.total_like = 0;
                                        check_like = true;
                                        notifyDataSetChanged();
                                        getData();
//                                        getSupportActionBar().setTitle(R.string.page_1);
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

//                        Toast.makeText(activity.getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("accountId", Long.toString(App.getInstance().getId()));
                            params.put("accessToken", App.getInstance().getAccessToken());
                            params.put("answerId", Long.toString(answer.getId()));

                            return params;
                        }
                    };

                    App.getInstance().addToRequestQueue(jsonReq);

                } else {

                    Toast.makeText(activity.getApplicationContext(), activity.getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }

    public void getData() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_PROFILE_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (response.getBoolean("error") == false) {
                                profile = new Profile(response);
                                updateLikesCount();
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

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("profileId", Long.toString(ProfileActivity.profile_id));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }
    private void updateLikesCount() {

        ProfileActivity.mLikesCount.setText(Integer.toString(profile.getLikesCount()));
    }
    @Override
    public void clickedTag(CharSequence tag) {
        // TODO Auto-generated method stub

        Intent i = new Intent(activity, HashtagsActivity.class);
        i.putExtra("hashtag", tag);
        activity.startActivity(i);
    }
}