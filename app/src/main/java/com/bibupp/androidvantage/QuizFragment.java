package com.bibupp.androidvantage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.bibupp.androidvantage.app.App;
import com.bibupp.androidvantage.constants.Constants;
import com.bibupp.androidvantage.model.Profile;
import com.bibupp.androidvantage.model.Question;
import com.bibupp.androidvantage.model.Quiz;
import com.bibupp.androidvantage.util.CustomRequest;
import com.bibupp.androidvantage.util.ResponderInterface;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener, ResponderInterface, View.OnClickListener {

    private static final String STATE_LIST = "State Adapter Data";

    private ProgressDialog pDialog;

    ListView mListView;
    TextView mMessage;

    SwipeRefreshLayout mItemsContainer;

    FloatingActionButton mFabButton;

    private ArrayList<Quiz> itemsList;
    private QuizListAdapter2 itemsAdapter;

    private int addedToListAt = 0;
    private int arrayLength = 0;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;
    private Boolean restore = false;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    LinearLayout layout_quiz;
    ScrollView scrollView;
    EditText edt_quiz;
    LinearLayout linear_layout_parent;
    LinearLayout.LayoutParams paramsedittext, paramsbutton;
    int width, height;
    ToggleButton create_quiz;
    Button btn_add, btn_quiz_submit;
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
    JSONObject jResult = new JSONObject();
    JSONArray jArray = new JSONArray();
    Boolean allfilled = false;
    Boolean checkbox_check = false;
TextView empty_fields;
    public QuizFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        initpDialog();

        if (savedInstanceState != null) {

            itemsList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            itemsAdapter = new QuizListAdapter2(getActivity(), itemsList, this);

            restore = savedInstanceState.getBoolean("restore");
            addedToListAt = savedInstanceState.getInt("addedToListAt");

        } else {

            itemsList = new ArrayList<Quiz>();
            itemsAdapter = new QuizListAdapter2(getActivity(), itemsList, this);

            restore = false;
            addedToListAt = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.quiz_fragment, container, false);

        mItemsContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.container_items);
        mItemsContainer.setOnRefreshListener(this);

        mFabButton = (FloatingActionButton) rootView.findViewById(R.id.fabButton);
        mFabButton.setImageResource(R.drawable.ic_action_random);

        mMessage = (TextView) rootView.findViewById(R.id.message);
        empty_fields=(TextView)rootView.findViewById(R.id.empty_fields);
        empty_fields.setVisibility(View.GONE);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        layout_quiz = (LinearLayout) rootView.findViewById(R.id.layout_quiz);
        mListView = (ListView) rootView.findViewById(R.id.listView);
        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        edt_quiz = (EditText) rootView.findViewById(R.id.edt_quiz);

        edt_quiz.setFilters(new InputFilter[]{ignoreFirstWhiteSpace()});

        create_quiz = (ToggleButton) rootView.findViewById(R.id.tbtn_quiz);
        btn_add = (Button) rootView.findViewById(R.id.btn_add);
        btn_quiz_submit = (Button) rootView.findViewById(R.id.btn_quiz_submit);
        btn_add.setOnClickListener(this);
        btn_quiz_submit.setOnClickListener(this);
        paramsedittext = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsedittext.gravity = Gravity.LEFT;

        paramsedittext.setMargins(0, 20, 0, 0);

        linear_layout_parent = (LinearLayout) rootView.findViewById(R.id.linearLayout);
        paramsbutton = new LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsbutton.gravity = Gravity.CENTER_VERTICAL;
        paramsbutton.setMargins(10, 0, 0, 0);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        for (int i = 0; i < 2; i++) {
            edt_id = edt_id + 1;
            linear_layout[edt_id] = new LinearLayout(getActivity());
            linear_layout[edt_id].setLayoutParams(paramsedittext);
            linear_layout_parent.addView(linear_layout[edt_id]);

            check_correct[edt_id] = new CheckBox(getActivity());
            all_checkbox.add(check_correct[edt_id]);
            check_correct[edt_id].setId(edt_id + 0);
//                edttext_name.setLayoutParams(paramsbutton);
            check_correct[edt_id].setWidth(50);
            linear_layout[edt_id].addView(check_correct[edt_id]);

            edttext_name[edt_id] = new EditText(getActivity());
            allEds_name.add(edttext_name[edt_id]);
            edttext_name[edt_id].setId(edt_id + 0);
//                edttext_name.setLayoutParams(paramsbutton);
            edttext_name[edt_id].setWidth(width - 100);
            edttext_name[edt_id].setBackgroundResource(R.drawable.rounded_edittext);
            edttext_name[edt_id].setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            edttext_name[edt_id].setFilters(new InputFilter[]{ignoreFirstWhiteSpace()});
//                layoutedittext_name.addView(edttext_name);
            linear_layout[edt_id].addView(edttext_name[edt_id]);
        }

        create_quiz.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.FILL_PARENT, 290));
                    layout_quiz.setVisibility(View.VISIBLE);
                    btn_add.setVisibility(View.VISIBLE);
                } else {
                    scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.FILL_PARENT, 0));
                    layout_quiz.setVisibility(View.GONE);
                    edt_quiz.setText("");
                    btn_add.setVisibility(View.GONE);
//                    edt_option1.setText("");
//                    edt_option2.setText("");
//                    edt_option3.setText("");
//                    edt_option4.setText("");
                }
            }
        });
        mFabButton.attachToListView(mListView, new ScrollDirectionListener() {

            @Override
            public void onScrollDown() {

            }

            @Override
            public void onScrollUp() {

            }

        }, new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;

                if ((lastInScreen == totalItemCount) && !(loadingMore) && (viewMore) && !(mItemsContainer.isRefreshing())) {

                    if (App.getInstance().isConnected()) {

                        loadingMore = true;

                        getItems();
                    }
                }
            }
        });

        mListView.setAdapter(itemsAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Question question = (Question) adapterView.getItemAtPosition(position);

                Intent intent = new Intent(getActivity(), QuizDetails.class);
                intent.putExtra("questionId", question.getId());
                intent.putExtra("listPosition", position);
                startActivityForResult(intent, QUESTION_ANSWER);
            }
        });

        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getRandom();
            }
        });

        if (itemsAdapter.getCount() == 0) {

            showMessage(getText(R.string.label_empty_list).toString());

        } else {

            hideMessage();
        }

        if (!restore) {

            showMessage(getText(R.string.msg_loading_2).toString());

            getItems();
        }
        edt_quiz.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // put the code of save Database here
                edt_quiz.setError(null);

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        edttext_name[0].addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // put the code of save Database here
                edttext_name[0].setError(null);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        edttext_name[1].addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // put the code of save Database here
                edttext_name[1].setError(null);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        return rootView;
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
            getItems();

        } else {

            mItemsContainer.setRefreshing(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == QUESTION_ANSWER && resultCode == getActivity().RESULT_OK && null != data) {

            itemsList.remove(data.getIntExtra("listPosition", -1));
            itemsAdapter.notifyDataSetChanged();

            this.listViewItemChange();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
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
                sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("lang", sharedpreferences.getString("Language", "en"));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void getRandom() {

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_QUESTIONS_RANDOM, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                Quiz quiz = new Quiz(response);

                                itemsList.add(0, quiz);
                                itemsAdapter.notifyDataSetChanged();

                                mListView.smoothScrollToPosition(0);
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            hidepDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void loadingComplete() {

        if (arrayLength == LIST_ITEMS) {

            viewMore = true;

        } else {

            viewMore = false;
        }

        itemsAdapter.notifyDataSetChanged();

        if (itemsAdapter.getCount() == 0) {

            if (QuizFragment.this.isVisible()) {

                showMessage(getText(R.string.label_empty_list).toString());
            }

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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tbtn_quiz:
                break;
            case R.id.btn_add:
                edt_id = edt_id + 1;
                linear_layout[edt_id] = new LinearLayout(getActivity());
                linear_layout[edt_id].setLayoutParams(paramsedittext);
                linear_layout_parent.addView(linear_layout[edt_id]);

                check_correct[edt_id] = new CheckBox(getActivity());
                all_checkbox.add(check_correct[edt_id]);
                check_correct[edt_id].setId(edt_id + 0);
//                edttext_name.setLayoutParams(paramsbutton);
                check_correct[edt_id].setWidth(50);
                linear_layout[edt_id].addView(check_correct[edt_id]);


                edttext_name[edt_id] = new EditText(getActivity());
                allEds_name.add(edttext_name[edt_id]);
                edttext_name[edt_id].setId(edt_id + 0);
//                edttext_name.setLayoutParams(paramsbutton);
                edttext_name[edt_id].setWidth(width - 150);
                edttext_name[edt_id].setBackgroundResource(R.drawable.rounded_edittext);
                edttext_name[edt_id].setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                edttext_name[edt_id].setFilters(new InputFilter[]{ignoreFirstWhiteSpace()});
//                layoutedittext_name.addView(edttext_name);
                linear_layout[edt_id].addView(edttext_name[edt_id]);

                btn_remove[edt_id] = new ImageView(getActivity());
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
//                edttext_name[edt_id].addTextChangedListener(new TextWatcher() {
//                    public void afterTextChanged(Editable s) {
//                        // put the code of save Database here
//                        edttext_name[edt_id].setError(null);
//                    }
//
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                    }
//
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    }
//                });
                break;
            case R.id.btn_quiz_submit:
                if (edt_quiz.getText().toString().equalsIgnoreCase("")) {
                    edt_quiz.requestFocus();
                    edt_quiz.setText("");
                    edt_quiz.setError("Please enter quiz.");
                } else {
                    allfilled = true;
                    for (int i = 0; i < allEds_name.size(); i++) {
                        if (allEds_name.get(i).getText().toString().equalsIgnoreCase("")) {
//                            edttext_name[i].requestFocus();
//                            edttext_name[i].setText("");
//                            edttext_name[i].setError("Please enter your option.");
                            empty_fields.setVisibility(View.VISIBLE);
                            allfilled = false;
                            break;
                        }
                    }
                    if (allfilled) {
                        checkbox_check = false;
                        for (int i = 0; i < allEds_name.size(); i++) {

                            if (all_checkbox.get(i).isChecked()) {
                                checkbox_check = true;
//                                save();
                                break;
                            }
                        }
                        if (checkbox_check) {
                            save();
                        } else {
                            Toast.makeText(getActivity(), "Please select correct option.", Toast.LENGTH_LONG).show();
                        }
                    }

                }
                break;
        }
    }

    public void save() {

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_QUIZ_ADD, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (response.getBoolean("error") == false) {

                                profile = new Profile(response);
                                if (profile.getState() == ACCOUNT_STATE_ENABLED) {

//                                    Fill_addl_fields();
                                    scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.FILL_PARENT, 0));
                                    layout_quiz.setVisibility(View.GONE);
                                    edt_quiz.setText("");
                                    btn_add.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "Your info successfully submited", Toast.LENGTH_LONG).show();
                                    getItems();

                                    Fragment fragment = new QuizFragment();

                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        fragmentManager.beginTransaction()
                                                .replace(R.id.container_body, fragment)
                                                .commit();
                                }
//
//                                Intent i = new Intent();
//                                i.putExtra("fullname", response.getString("fullname"));
////                                setResult(RESULT_OK, i);
//
////                                finish();
                            }

//                            Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_LONG).show();

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

//                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("quiz_title", edt_quiz.getText().toString());
                strings_options = new String[allEds_name.size()];
                ArrayList<String> list = new ArrayList<String>();
//                strings_ans = new String[all_checkbox.size()];

                try {
                    for (int i = 0; i < allEds_name.size(); i++) {
//                    strings_options[i] = allEds_name.get(i).getText().toString().replace(",", "<>");
                        JSONObject jGroup = new JSONObject();

                        jGroup.put("option_title", allEds_name.get(i).getText().toString().replace(",", "<>"));
                        if (all_checkbox.get(i).isChecked()) {
                            jGroup.put("option_status", true);
                        } else {
                            jGroup.put("option_status", false);
                        }
                        jArray.put(jGroup);
                        jResult.put("question", jArray);

                    }

                } catch (IndexOutOfBoundsException e) {

                } catch (OutOfMemoryError e) {

                } catch (Exception e) {
                    e.printStackTrace();
                }
                params.put("quiz_options", jArray.toString());
//                for (int j = 0; j < all_checkbox.size(); j++) {
//
//                    if (all_checkbox.get(j).isChecked()) {
//                        list.add(allEds_name.get(j).getText().toString().replace(",", "<>"));
//                    }
//                }
//                strings_ans = new String[list.size()];
//                for (int k = 0; k < list.size(); k++) {
//                    strings_ans[k] = list.get(k).replace(",", "<>");
//                }
//                params.put("quiz_ans", Arrays.toString(strings_ans));
                params.put("lang", sharedpreferences.getString("Language", "en"));
                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }
    public class QuizListAdapter2 extends BaseAdapter implements Parcelable,Constants {

        private Activity activity;
        private LayoutInflater inflater;
        private List<Quiz> quizList;
        private ResponderInterface responder;

        ImageLoader imageLoader = App.getInstance().getImageLoader();

        public QuizListAdapter2(Activity activity, List<Quiz> quizList, ResponderInterface responder) {

            this.activity = activity;
            this.quizList = quizList;
            this.responder = responder;
        }

        protected QuizListAdapter2(Parcel in) {
            quizList = in.createTypedArrayList(Quiz.CREATOR);
        }

          final Creator<QuizListAdapter2> CREATOR = new Creator<QuizListAdapter2>() {
            @Override
            public QuizListAdapter2 createFromParcel(Parcel in) {
                return new QuizListAdapter2(in);
            }

            @Override
            public QuizListAdapter2[] newArray(int size) {
                return new QuizListAdapter2[size];
            }
        };

        @Override
        public int getCount() {

            return quizList.size();
        }

        @Override
        public Object getItem(int location) {

            return quizList.get(location);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

         class ViewHolder {

            public TextView quizText,quizid;
            RelativeLayout layout;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

           QuizListAdapter2.ViewHolder viewHolder = null;

            if (inflater == null) {

                inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            if (convertView == null) {

                convertView = inflater.inflate(R.layout.quizlist_item, null);

                final Quiz q = quizList.get(position);

                viewHolder = new QuizListAdapter2.ViewHolder();
                viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.layout);
                viewHolder.quizText = (TextView) convertView.findViewById(R.id.txt_quiz);
                viewHolder.quizid= (TextView) convertView.findViewById(R.id.txt_quizid);

                convertView.setTag(viewHolder);

//			viewHolder.questionRemove.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//
//					int getPosition = (Integer) v.getTag();
//					ViewHolder viewHolder = (ViewHolder) v.getTag(R.id.questionRemove);
//					// TODO Auto-generated method stub
//
//                    Question q = questionsList.get(getPosition);
//                    q.remove();
//
//                    questionsList.remove(getPosition);
//                    notifyDataSetChanged();
//
//                    responder.listViewItemChange();
//				}
//			});

//            viewHolder.questionAuthor.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//
//                    int getPosition = (Integer) v.getTag();
//
//                    Question q = questionsList.get(getPosition);
//
//                    Intent intent = new Intent(activity, ProfileActivity.class);
//                    intent.putExtra("profileId", q.getFromUserId());
//                    activity.startActivity(intent);
//                }
//            });

            } else {

                viewHolder = (QuizListAdapter2.ViewHolder) convertView.getTag();
            }

//        viewHolder.questionRemove.setTag(position);
//        viewHolder.quizText.setTag(position);
//        viewHolder.questionAuthor.setTag(position);
//        viewHolder.questionCreateAt.setTag(position);
//        viewHolder.questionRemove.setTag(position);
//        viewHolder.questionRemove.setTag(R.id.questionRemove, viewHolder);

            final Quiz quiz = quizList.get(position);

            if (quiz.getId() != 0) {

//            viewHolder.questionAuthor.setVisibility(View.VISIBLE);
//            viewHolder.questionAuthor.setText(q.getFromUserFullname());

            } else {

//            viewHolder.questionAuthor.setVisibility(View.GONE);
            }
            viewHolder.quizText.setTag(position);
            viewHolder.quizText.setText(quiz.getQuizTitle());
            viewHolder.quizid.setTag(position);
            viewHolder.quizid.setText(""+quiz.getId());

            viewHolder.quizText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos1 = (Integer) v.getTag();
                    Quiz h1 = (Quiz) quizList.get(pos1);
                    Intent in=new Intent(getActivity(), QuizDetails.class);
                    in.putExtra("quizId",Long.toString(h1.getId()));
                    in.putExtra("quizTitle",h1.getQuizTitle());
                    activity.startActivity(in);
                }
            });
            viewHolder.layout.setTag(position);
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos1 = (Integer) v.getTag();
                    Quiz h1 = (Quiz) quizList.get(pos1);
                    Intent in=new Intent(getActivity(), QuizDetails.class);
                    in.putExtra("quizId",h1.getId());
                    in.putExtra("quizTitle",h1.getQuizTitle());
                    activity.startActivity(in);
//                activity.startActivityForResult(in, QUESTION_ANSWER);
                }
            });
            //		 Converting timestamp into x ago format
//        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
//                quiz.getCreateAt() * 1000l,
//                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
//if()
//        viewHolder.questionCreateAt.setText(timeAgo);

            return convertView;
        }

        @Override
        public int describeContents() {

            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeTypedList(quizList);
        }
    }
    public InputFilter ignoreFirstWhiteSpace() {
        return new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {

                for (int i = start; i < end; i++) {
                    if (Character.isWhitespace(source.charAt(i))) {
                        if (dstart == 0)
                            return "";
                    }
                }
                return null;
            }
        };
    }
}