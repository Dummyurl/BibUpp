package com.bibupp.androidvantage.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArrayMap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.bibupp.androidvantage.QuizActivity;
import com.bibupp.androidvantage.R;
import com.bibupp.androidvantage.app.App;
import com.bibupp.androidvantage.model.Question;
import com.bibupp.androidvantage.model.Quiz;
import com.bibupp.androidvantage.util.ResponderInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QuizListAdapter extends BaseAdapter implements Parcelable {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Quiz> quizList;
    private ResponderInterface responder;
    public static List<CheckBox> all_chechbox = new ArrayList<CheckBox>();
    int check_id = -1;
    ArrayList<String> title = new ArrayList<String>();
    ImageLoader imageLoader = App.getInstance().getImageLoader();

    public QuizListAdapter(Activity activity, List<Quiz> questionsList, ResponderInterface responder) {

        this.activity = activity;
        this.quizList = questionsList;
        this.responder = responder;
    }

    protected QuizListAdapter(Parcel in) {
        quizList = in.createTypedArrayList(Question.CREATOR);
    }

    public static final Creator<QuizListAdapter> CREATOR = new Creator<QuizListAdapter>() {
        @Override
        public QuizListAdapter createFromParcel(Parcel in) {
            return new QuizListAdapter(in);
        }

        @Override
        public QuizListAdapter[] newArray(int size) {
            return new QuizListAdapter[size];
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

    public static class ViewHolder {

        public TextView quizTitle;
        String quizOption, quizAns;
        String[] quizOptionArray, quizAnsArray;
        CheckBox chk_opt1, chk_opt2, chk_opt3, chk_opt4, chk_opt5, chk_opt6, chk_opt7, chk_opt8, chk_opt9, chk_opt10;


        LinearLayout linear_layout_parent;
        LinearLayout.LayoutParams paramsedittext;
        RadioGroup radio_group[] = new RadioGroup[100];
        CheckBox checkbox_name[] = new CheckBox[100];
        //        public List<CheckBox> all_chechbox = new ArrayList<CheckBox>();
        int edt_id = -1;
        public ArrayMap<Integer, String> alloptions = new ArrayMap<Integer, String>();
        public ArrayMap<Integer, String> alloptionsvalue = new ArrayMap<Integer, String>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (inflater == null) {

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.quiz_list_item_checkbox, null);

            final Quiz q = quizList.get(position);

            viewHolder = new ViewHolder();

//			viewHolder.questionRemove = (ImageView) convertView.findViewById(R.id.questionRemove);
            viewHolder.quizTitle = (TextView) convertView.findViewById(R.id.txt_quiz);
            viewHolder.chk_opt1 = (CheckBox) convertView.findViewById(R.id.option1);
            viewHolder.chk_opt2 = (CheckBox) convertView.findViewById(R.id.option2);
            viewHolder.chk_opt3 = (CheckBox) convertView.findViewById(R.id.option3);
            viewHolder.chk_opt4 = (CheckBox) convertView.findViewById(R.id.option4);
            viewHolder.chk_opt5 = (CheckBox) convertView.findViewById(R.id.option5);
            viewHolder.chk_opt6 = (CheckBox) convertView.findViewById(R.id.option6);
            viewHolder.chk_opt7 = (CheckBox) convertView.findViewById(R.id.option7);
            viewHolder.chk_opt8 = (CheckBox) convertView.findViewById(R.id.option8);
            viewHolder.chk_opt9 = (CheckBox) convertView.findViewById(R.id.option9);
            viewHolder.chk_opt10 = (CheckBox) convertView.findViewById(R.id.option10);
//            viewHolder.questionAuthor = (TextView) convertView.findViewById(R.id.questionAuthor);
//            viewHolder.questionCreateAt = (TextView) convertView.findViewById(R.id.questionCreateAt);

            viewHolder.paramsedittext = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            viewHolder.paramsedittext.gravity = Gravity.LEFT;

            viewHolder.paramsedittext.setMargins(0, 15, 0, 0);

            viewHolder.linear_layout_parent = (LinearLayout) convertView.findViewById(R.id.linearLayout);
//            viewHolder.questionRemove.setTag(position);
            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.quizTitle.setTag(position);
        final Quiz quiz = quizList.get(position);

        if (quiz.getId() != 0) {

//            viewHolder.questionAuthor.setVisibility(View.VISIBLE);
//            viewHolder.questionAuthor.setText(q.getFromUserFullname());

        } else {

//            viewHolder.questionAuthor.setVisibility(View.GONE);
        }

        viewHolder.quizTitle.setText(quiz.getQuizTitle());

        String OptionList = quiz.getOptionList();
        viewHolder.alloptions.clear();
        viewHolder.alloptionsvalue.clear();
        try {
//        JSONObject obj=new JSONObject(OptionList.replace("[","").replace("]",""));

            JSONArray jsonArray = new JSONArray(OptionList);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject quizObj = (JSONObject) jsonArray.get(i);
                viewHolder.alloptions.put(Integer.parseInt(quizObj.getString("id")), quizObj.getString("option_title"));
                viewHolder.alloptionsvalue.put(Integer.parseInt(quizObj.getString("id")), quizObj.getString("id"));

            }
        } catch (JSONException je) {
        }


//        viewHolder.quizOption = quiz.getTo_option_lists();
//        viewHolder.quizAns = quiz.getAnswers();
//        viewHolder.quizOptionArray = viewHolder.quizOption.replace("[", "").replace("]", "").split(",");
        if (viewHolder.alloptions.size() == 2) {
            viewHolder.chk_opt1.setVisibility(View.VISIBLE);
            viewHolder.chk_opt2.setVisibility(View.VISIBLE);
            viewHolder.chk_opt3.setVisibility(View.GONE);
            viewHolder.chk_opt4.setVisibility(View.GONE);
            viewHolder.chk_opt5.setVisibility(View.GONE);
            viewHolder.chk_opt6.setVisibility(View.GONE);
            viewHolder.chk_opt7.setVisibility(View.GONE);
            viewHolder.chk_opt8.setVisibility(View.GONE);
            viewHolder.chk_opt9.setVisibility(View.GONE);
            viewHolder.chk_opt10.setVisibility(View.GONE);

            viewHolder.chk_opt1.setText(viewHolder.alloptions.valueAt(0));
            viewHolder.chk_opt2.setText(viewHolder.alloptions.valueAt(1));
            viewHolder.chk_opt1.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(0)));
            viewHolder.chk_opt2.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(1)));
//            for(int j=0;j<viewHolder.alloptions.size();j++)
//            {
//                if(j==0)
//                viewHolder.chk_opt1.setText(viewHolder.alloptions.valueAt(j));
//                else if(j==1)
//                    viewHolder.chk_opt2.setText(viewHolder.alloptions.valueAt(j));
//                else if(j==2)
//                    viewHolder.chk_opt3.setText(viewHolder.alloptions.valueAt(j));
//                else if(j==3)
//                    viewHolder.chk_opt4.setText(viewHolder.alloptions.valueAt(j));
//            }
//            viewHolder.chk_opt1.setText(viewHolder.alloptions.get("option_lists"));
//            viewHolder.chk_opt2.setText(quiz.getOption_title());

            viewHolder.chk_opt1.setTag(position);
            viewHolder.chk_opt2.setTag(position);

        } else if (viewHolder.alloptions.size() == 3) {
            viewHolder.chk_opt1.setVisibility(View.VISIBLE);
            viewHolder.chk_opt2.setVisibility(View.VISIBLE);
            viewHolder.chk_opt3.setVisibility(View.VISIBLE);
            viewHolder.chk_opt4.setVisibility(View.GONE);
            viewHolder.chk_opt5.setVisibility(View.GONE);
            viewHolder.chk_opt6.setVisibility(View.GONE);
            viewHolder.chk_opt7.setVisibility(View.GONE);
            viewHolder.chk_opt8.setVisibility(View.GONE);
            viewHolder.chk_opt9.setVisibility(View.GONE);
            viewHolder.chk_opt10.setVisibility(View.GONE);
            viewHolder.chk_opt1.setText(viewHolder.alloptions.valueAt(0));
            viewHolder.chk_opt2.setText(viewHolder.alloptions.valueAt(1));
            viewHolder.chk_opt3.setText(viewHolder.alloptions.valueAt(2));
            viewHolder.chk_opt1.setTag(position);
            viewHolder.chk_opt2.setTag(position);
            viewHolder.chk_opt3.setTag(position);
            viewHolder.chk_opt1.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(0)));
            viewHolder.chk_opt2.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(1)));
            viewHolder.chk_opt3.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(2)));
        } else if (viewHolder.alloptions.size() == 4) {
            viewHolder.chk_opt1.setVisibility(View.VISIBLE);
            viewHolder.chk_opt2.setVisibility(View.VISIBLE);
            viewHolder.chk_opt3.setVisibility(View.VISIBLE);
            viewHolder.chk_opt4.setVisibility(View.VISIBLE);
            viewHolder.chk_opt5.setVisibility(View.GONE);
            viewHolder.chk_opt6.setVisibility(View.GONE);
            viewHolder.chk_opt7.setVisibility(View.GONE);
            viewHolder.chk_opt8.setVisibility(View.GONE);
            viewHolder.chk_opt9.setVisibility(View.GONE);
            viewHolder.chk_opt10.setVisibility(View.GONE);
            viewHolder.chk_opt1.setText(viewHolder.alloptions.valueAt(0));
            viewHolder.chk_opt2.setText(viewHolder.alloptions.valueAt(1));
            viewHolder.chk_opt3.setText(viewHolder.alloptions.valueAt(2));
            viewHolder.chk_opt4.setText(viewHolder.alloptions.valueAt(3));
            viewHolder.chk_opt1.setTag(position);
            viewHolder.chk_opt2.setTag(position);
            viewHolder.chk_opt3.setTag(position);
            viewHolder.chk_opt4.setTag(position);
            viewHolder.chk_opt1.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(0)));
            viewHolder.chk_opt2.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(1)));
            viewHolder.chk_opt3.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(2)));
            viewHolder.chk_opt4.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(3)));

        } else if (viewHolder.alloptions.size() == 5) {
            viewHolder.chk_opt1.setVisibility(View.VISIBLE);
            viewHolder.chk_opt2.setVisibility(View.VISIBLE);
            viewHolder.chk_opt3.setVisibility(View.VISIBLE);
            viewHolder.chk_opt4.setVisibility(View.VISIBLE);
            viewHolder.chk_opt5.setVisibility(View.VISIBLE);
            viewHolder.chk_opt6.setVisibility(View.GONE);
            viewHolder.chk_opt7.setVisibility(View.GONE);
            viewHolder.chk_opt8.setVisibility(View.GONE);
            viewHolder.chk_opt9.setVisibility(View.GONE);
            viewHolder.chk_opt10.setVisibility(View.GONE);

            viewHolder.chk_opt1.setText(viewHolder.alloptions.valueAt(0));
            viewHolder.chk_opt2.setText(viewHolder.alloptions.valueAt(1));
            viewHolder.chk_opt3.setText(viewHolder.alloptions.valueAt(2));
            viewHolder.chk_opt4.setText(viewHolder.alloptions.valueAt(3));
            viewHolder.chk_opt5.setText(viewHolder.alloptions.valueAt(4));
            viewHolder.chk_opt1.setTag(position);
            viewHolder.chk_opt2.setTag(position);
            viewHolder.chk_opt3.setTag(position);
            viewHolder.chk_opt4.setTag(position);
            viewHolder.chk_opt5.setTag(position);
            viewHolder.chk_opt1.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(0)));
            viewHolder.chk_opt2.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(1)));
            viewHolder.chk_opt3.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(2)));
            viewHolder.chk_opt4.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(3)));
            viewHolder.chk_opt5.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(4)));
        } else if (viewHolder.alloptions.size() == 6) {
            viewHolder.chk_opt1.setVisibility(View.VISIBLE);
            viewHolder.chk_opt2.setVisibility(View.VISIBLE);
            viewHolder.chk_opt3.setVisibility(View.VISIBLE);
            viewHolder.chk_opt4.setVisibility(View.VISIBLE);
            viewHolder.chk_opt5.setVisibility(View.VISIBLE);
            viewHolder.chk_opt6.setVisibility(View.VISIBLE);
            viewHolder.chk_opt7.setVisibility(View.GONE);
            viewHolder.chk_opt8.setVisibility(View.GONE);
            viewHolder.chk_opt9.setVisibility(View.GONE);
            viewHolder.chk_opt10.setVisibility(View.GONE);
            viewHolder.chk_opt1.setText(viewHolder.alloptions.valueAt(0));
            viewHolder.chk_opt2.setText(viewHolder.alloptions.valueAt(1));
            viewHolder.chk_opt3.setText(viewHolder.alloptions.valueAt(2));
            viewHolder.chk_opt4.setText(viewHolder.alloptions.valueAt(3));
            viewHolder.chk_opt5.setText(viewHolder.alloptions.valueAt(4));
            viewHolder.chk_opt6.setText(viewHolder.alloptions.valueAt(5));
            viewHolder.chk_opt1.setTag(position);
            viewHolder.chk_opt2.setTag(position);
            viewHolder.chk_opt3.setTag(position);
            viewHolder.chk_opt4.setTag(position);
            viewHolder.chk_opt5.setTag(position);
            viewHolder.chk_opt6.setTag(position);
            viewHolder.chk_opt1.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(0)));
            viewHolder.chk_opt2.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(1)));
            viewHolder.chk_opt3.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(2)));
            viewHolder.chk_opt4.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(3)));
            viewHolder.chk_opt5.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(4)));
            viewHolder.chk_opt6.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(5)));

        } else if (viewHolder.alloptions.size() == 7) {
            viewHolder.chk_opt1.setVisibility(View.VISIBLE);
            viewHolder.chk_opt2.setVisibility(View.VISIBLE);
            viewHolder.chk_opt3.setVisibility(View.VISIBLE);
            viewHolder.chk_opt4.setVisibility(View.VISIBLE);
            viewHolder.chk_opt5.setVisibility(View.VISIBLE);
            viewHolder.chk_opt6.setVisibility(View.VISIBLE);
            viewHolder.chk_opt7.setVisibility(View.VISIBLE);
            viewHolder.chk_opt8.setVisibility(View.GONE);
            viewHolder.chk_opt9.setVisibility(View.GONE);
            viewHolder.chk_opt10.setVisibility(View.GONE);
            viewHolder.chk_opt1.setText(viewHolder.alloptions.valueAt(0));
            viewHolder.chk_opt2.setText(viewHolder.alloptions.valueAt(1));
            viewHolder.chk_opt3.setText(viewHolder.alloptions.valueAt(2));
            viewHolder.chk_opt4.setText(viewHolder.alloptions.valueAt(3));
            viewHolder.chk_opt5.setText(viewHolder.alloptions.valueAt(4));
            viewHolder.chk_opt6.setText(viewHolder.alloptions.valueAt(5));
            viewHolder.chk_opt7.setText(viewHolder.alloptions.valueAt(6));
            viewHolder.chk_opt1.setTag(position);
            viewHolder.chk_opt2.setTag(position);
            viewHolder.chk_opt3.setTag(position);
            viewHolder.chk_opt4.setTag(position);
            viewHolder.chk_opt5.setTag(position);
            viewHolder.chk_opt6.setTag(position);
            viewHolder.chk_opt7.setTag(position);
            viewHolder.chk_opt1.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(0)));
            viewHolder.chk_opt2.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(1)));
            viewHolder.chk_opt3.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(2)));
            viewHolder.chk_opt4.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(3)));
            viewHolder.chk_opt5.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(4)));
            viewHolder.chk_opt6.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(5)));
            viewHolder.chk_opt7.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(6)));
        } else if (viewHolder.alloptions.size() == 8) {
            viewHolder.chk_opt1.setVisibility(View.VISIBLE);
            viewHolder.chk_opt2.setVisibility(View.VISIBLE);
            viewHolder.chk_opt3.setVisibility(View.VISIBLE);
            viewHolder.chk_opt4.setVisibility(View.VISIBLE);
            viewHolder.chk_opt5.setVisibility(View.VISIBLE);
            viewHolder.chk_opt6.setVisibility(View.VISIBLE);
            viewHolder.chk_opt7.setVisibility(View.VISIBLE);
            viewHolder.chk_opt8.setVisibility(View.VISIBLE);
            viewHolder.chk_opt9.setVisibility(View.GONE);
            viewHolder.chk_opt10.setVisibility(View.GONE);
            viewHolder.chk_opt1.setText(viewHolder.alloptions.valueAt(0));
            viewHolder.chk_opt2.setText(viewHolder.alloptions.valueAt(1));
            viewHolder.chk_opt3.setText(viewHolder.alloptions.valueAt(2));
            viewHolder.chk_opt4.setText(viewHolder.alloptions.valueAt(3));
            viewHolder.chk_opt5.setText(viewHolder.alloptions.valueAt(4));
            viewHolder.chk_opt6.setText(viewHolder.alloptions.valueAt(5));
            viewHolder.chk_opt7.setText(viewHolder.alloptions.valueAt(6));
            viewHolder.chk_opt8.setText(viewHolder.alloptions.valueAt(7));
            viewHolder.chk_opt1.setTag(position);
            viewHolder.chk_opt2.setTag(position);
            viewHolder.chk_opt3.setTag(position);
            viewHolder.chk_opt4.setTag(position);
            viewHolder.chk_opt5.setTag(position);
            viewHolder.chk_opt6.setTag(position);
            viewHolder.chk_opt7.setTag(position);
            viewHolder.chk_opt8.setTag(position);
            viewHolder.chk_opt1.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(0)));
            viewHolder.chk_opt2.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(1)));
            viewHolder.chk_opt3.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(2)));
            viewHolder.chk_opt4.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(3)));
            viewHolder.chk_opt5.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(4)));
            viewHolder.chk_opt6.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(5)));
            viewHolder.chk_opt7.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(6)));
            viewHolder.chk_opt8.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(7)));

        } else if (viewHolder.alloptions.size() == 9) {
            viewHolder.chk_opt1.setVisibility(View.VISIBLE);
            viewHolder.chk_opt2.setVisibility(View.VISIBLE);
            viewHolder.chk_opt3.setVisibility(View.VISIBLE);
            viewHolder.chk_opt4.setVisibility(View.VISIBLE);
            viewHolder.chk_opt5.setVisibility(View.VISIBLE);
            viewHolder.chk_opt6.setVisibility(View.VISIBLE);
            viewHolder.chk_opt7.setVisibility(View.VISIBLE);
            viewHolder.chk_opt8.setVisibility(View.VISIBLE);
            viewHolder.chk_opt9.setVisibility(View.VISIBLE);
            viewHolder.chk_opt10.setVisibility(View.GONE);

            viewHolder.chk_opt1.setText(viewHolder.alloptions.valueAt(0));
            viewHolder.chk_opt2.setText(viewHolder.alloptions.valueAt(1));
            viewHolder.chk_opt3.setText(viewHolder.alloptions.valueAt(2));
            viewHolder.chk_opt4.setText(viewHolder.alloptions.valueAt(3));
            viewHolder.chk_opt5.setText(viewHolder.alloptions.valueAt(4));
            viewHolder.chk_opt6.setText(viewHolder.alloptions.valueAt(5));
            viewHolder.chk_opt7.setText(viewHolder.alloptions.valueAt(6));
            viewHolder.chk_opt8.setText(viewHolder.alloptions.valueAt(7));
            viewHolder.chk_opt9.setText(viewHolder.alloptions.valueAt(8));
            viewHolder.chk_opt1.setTag(position);
            viewHolder.chk_opt2.setTag(position);
            viewHolder.chk_opt3.setTag(position);
            viewHolder.chk_opt4.setTag(position);
            viewHolder.chk_opt5.setTag(position);
            viewHolder.chk_opt6.setTag(position);
            viewHolder.chk_opt7.setTag(position);
            viewHolder.chk_opt8.setTag(position);
            viewHolder.chk_opt9.setTag(position);
            viewHolder.chk_opt1.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(0)));
            viewHolder.chk_opt2.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(1)));
            viewHolder.chk_opt3.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(2)));
            viewHolder.chk_opt4.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(3)));
            viewHolder.chk_opt5.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(4)));
            viewHolder.chk_opt6.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(5)));
            viewHolder.chk_opt7.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(6)));
            viewHolder.chk_opt8.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(7)));
            viewHolder.chk_opt9.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(8)));
        } else if (viewHolder.alloptions.size() == 10) {
            viewHolder.chk_opt1.setVisibility(View.VISIBLE);
            viewHolder.chk_opt2.setVisibility(View.VISIBLE);
            viewHolder.chk_opt3.setVisibility(View.VISIBLE);
            viewHolder.chk_opt4.setVisibility(View.VISIBLE);
            viewHolder.chk_opt5.setVisibility(View.VISIBLE);
            viewHolder.chk_opt6.setVisibility(View.VISIBLE);
            viewHolder.chk_opt7.setVisibility(View.VISIBLE);
            viewHolder.chk_opt8.setVisibility(View.VISIBLE);
            viewHolder.chk_opt9.setVisibility(View.VISIBLE);
            viewHolder.chk_opt10.setVisibility(View.VISIBLE);

            viewHolder.chk_opt1.setText(viewHolder.alloptions.valueAt(0));
            viewHolder.chk_opt2.setText(viewHolder.alloptions.valueAt(1));
            viewHolder.chk_opt3.setText(viewHolder.alloptions.valueAt(2));
            viewHolder.chk_opt4.setText(viewHolder.alloptions.valueAt(3));
            viewHolder.chk_opt5.setText(viewHolder.alloptions.valueAt(4));
            viewHolder.chk_opt6.setText(viewHolder.alloptions.valueAt(5));
            viewHolder.chk_opt7.setText(viewHolder.alloptions.valueAt(6));
            viewHolder.chk_opt8.setText(viewHolder.alloptions.valueAt(7));
            viewHolder.chk_opt9.setText(viewHolder.alloptions.valueAt(8));
            viewHolder.chk_opt10.setText(viewHolder.alloptions.valueAt(9));
            viewHolder.chk_opt1.setTag(position);
            viewHolder.chk_opt2.setTag(position);
            viewHolder.chk_opt3.setTag(position);
            viewHolder.chk_opt4.setTag(position);
            viewHolder.chk_opt5.setTag(position);
            viewHolder.chk_opt6.setTag(position);
            viewHolder.chk_opt7.setTag(position);
            viewHolder.chk_opt8.setTag(position);
            viewHolder.chk_opt9.setTag(position);
            viewHolder.chk_opt10.setTag(position);
            viewHolder.chk_opt1.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(0)));
            viewHolder.chk_opt2.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(1)));
            viewHolder.chk_opt3.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(2)));
            viewHolder.chk_opt4.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(3)));
            viewHolder.chk_opt5.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(4)));
            viewHolder.chk_opt6.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(5)));
            viewHolder.chk_opt7.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(6)));
            viewHolder.chk_opt8.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(7)));
            viewHolder.chk_opt9.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(8)));
            viewHolder.chk_opt10.setId(Integer.parseInt(viewHolder.alloptionsvalue.valueAt(9)));
        }
//        else
//        {
//            viewHolder.chk_opt1.setVisibility(View.VISIBLE);
//            viewHolder.chk_opt2.setVisibility(View.VISIBLE);
//            viewHolder.chk_opt3.setVisibility(View.GONE);
//            viewHolder.chk_opt4.setVisibility(View.GONE);
//            viewHolder.chk_opt5.setVisibility(View.GONE);
//            viewHolder.chk_opt6.setVisibility(View.GONE);
//            viewHolder.chk_opt7.setVisibility(View.GONE);
//            viewHolder.chk_opt8.setVisibility(View.GONE);
//            viewHolder.chk_opt9.setVisibility(View.GONE);
//            viewHolder.chk_opt10.setVisibility(View.GONE);
//
//            viewHolder.chk_opt1.setText(quiz.getOption_title());
//            viewHolder.chk_opt2.setText(quiz.getOption_title());
//
//            viewHolder.chk_opt1.setTag(position);
//            viewHolder.chk_opt2.setTag(position);
//
//        }
        viewHolder.chk_opt1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int id = buttonView.getId();
                QuizActivity.mydb.insert(quiz.getId(), quiz.getQuizTitle(), quiz.getDate(), ""+buttonView.getId());

            }
        });
        viewHolder.chk_opt2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int id = buttonView.getId();
                QuizActivity.mydb.insert(quiz.getId(), quiz.getQuizTitle(), quiz.getDate(),""+buttonView.getId());

            }
        });
        viewHolder.chk_opt3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int id = buttonView.getId();
                QuizActivity.mydb.insert(quiz.getId(), quiz.getQuizTitle(), quiz.getDate(),""+buttonView.getId());

            }
        });
        viewHolder.chk_opt4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int id = buttonView.getId();
                QuizActivity.mydb.insert(quiz.getId(), quiz.getQuizTitle(), quiz.getDate(), ""+buttonView.getId());

            }
        });
        viewHolder.chk_opt5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                QuizActivity.mydb.insert(quiz.getId(), quiz.getQuizTitle(), quiz.getDate(), ""+buttonView.getId());

            }
        });
        viewHolder.chk_opt6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                QuizActivity.mydb.insert(quiz.getId(), quiz.getQuizTitle(), quiz.getDate(), ""+buttonView.getId());

            }
        });
        viewHolder.chk_opt7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                QuizActivity.mydb.insert(quiz.getId(), quiz.getQuizTitle(), quiz.getDate(), ""+buttonView.getId());

            }
        });
        viewHolder.chk_opt8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                QuizActivity.mydb.insert(quiz.getId(), quiz.getQuizTitle(), quiz.getDate(), ""+buttonView.getId());

            }
        });
        viewHolder.chk_opt9.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                QuizActivity.mydb.insert(quiz.getId(), quiz.getQuizTitle(), quiz.getDate(), ""+buttonView.getId());

            }
        });
        viewHolder.chk_opt10.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                QuizActivity.mydb.insert(quiz.getId(), quiz.getQuizTitle(), quiz.getDate(), ""+buttonView.getId());

            }
        });

//        viewHolder.edt_id = viewHolder.edt_id + 1;
//        viewHolder.radio_group[viewHolder.edt_id] = new RadioGroup(this.activity);
//        viewHolder.radio_group[viewHolder.edt_id].setLayoutParams(viewHolder.paramsedittext);
//        viewHolder.linear_layout_parent.addView(viewHolder.radio_group[viewHolder.edt_id]);

//        for (int i = 0; i < viewHolder.quizOptionArray.length; i++) {
//            check_id = check_id + 1;
//            viewHolder.checkbox_name[check_id] = new CheckBox(this.activity);
//            all_chechbox.add(viewHolder.checkbox_name[check_id]);
//            viewHolder.checkbox_name[check_id].setId(check_id + 0);
//            viewHolder.checkbox_name[check_id].setTag(i);
//            viewHolder.checkbox_name[check_id].setText(viewHolder.quizOptionArray[i]);
//            viewHolder.radio_group[viewHolder.edt_id].addView(viewHolder.checkbox_name[check_id]);
//            viewHolder.checkbox_name[check_id].setTag(check_id);
//            viewHolder.checkbox_name[check_id].setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = (Integer) v.getTag();
////                    title.add(Long.toString(quiz.getId()));
//                    Boolean check = false;
//                    try {
////                        if (title.size() > 0) {
////                            for (int i = 0; i < title.size(); i++) {
////                                String a = title.get(i);
////                                if (a.equalsIgnoreCase(Long.toString(quiz.getId()))) {
////
//////                                    JSONObject jGroup = new JSONObject();
//////
//////                                    jGroup.put("user_id", "2");
//////                                    jGroup.put("quiz_id", quiz.getId());
//////                                    jGroup.put("quiz_title", quiz.getQuizTitle());
//////                                    jGroup.put("quiz_date", quiz.getDate());
//////                                    jGroup.put("chossed_ans", all_chechbox.get(position).getText());
//////
//////                                    QuizActivity.jArray.put(jGroup);
//////
//////                                    QuizActivity.jResult.put("question", QuizActivity.jArray);
////                                    check = true;
////                                    break;
////                                }
////                            }
////
////                            if (!check) {
////
////                                JSONObject jGroup = new JSONObject();
////
////                                jGroup.put("user_id", "2");
////                                jGroup.put("quiz_id", quiz.getId());
////                                jGroup.put("quiz_title", quiz.getQuizTitle());
////                                jGroup.put("quiz_date", quiz.getDate());
////                                jGroup.put("chossed_ans", all_chechbox.get(position).getText());
////
////                                QuizActivity.jArray.put(jGroup);
////
////                                QuizActivity.jResult.put("question", QuizActivity.jArray);
////                                title.add(Long.toString(quiz.getId()));
////                            }
////                        } else {
////                            JSONObject jGroup = new JSONObject();
////
////                            jGroup.put("user_id", "2");
////                            jGroup.put("quiz_id", quiz.getId());
////                            jGroup.put("quiz_title", quiz.getQuizTitle());
////                            jGroup.put("quiz_date", quiz.getDate());
////                            jGroup.put("chossed_ans", all_chechbox.get(position).getText());
////
////                            QuizActivity.jArray.put(jGroup);
////
////                            QuizActivity.jResult.put("question", QuizActivity.jArray);
////                            title.add(Long.toString(quiz.getId()));
////                        }
//                        QuizActivity.mydb.insert(quiz.getId(), quiz.getQuizTitle(), quiz.getDate(), all_chechbox.get(position).getText().toString());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            });
//
////            viewHolder.checkbox_name[check_id].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
////                @Override
////                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
////                    try {
////                        for (int i = 0; i < 1; i++) {
////                            JSONObject jGroup = new JSONObject();
////
////                            jGroup.put("user_id", "2");
////
////                            jGroup.put("quiz_id", quiz.getId());
////                            jGroup.put("quiz_title", quiz.getQuizTitle());
////                            jGroup.put("quiz_date", quiz.getDate());
////                            jGroup.put("chossed_ans",all_chechbox.get(check_id));
////
////                            QuizActivity.jArray.put(jGroup);
////                        }
////                        QuizActivity.jResult.put("question", QuizActivity.jArray);
//////                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
////                        QuizActivity.nameValuePairs.add(new BasicNameValuePair("addanswer", QuizActivity.jResult.toString()));
////                        QuizActivity.nameValuePairs.add(new BasicNameValuePair("accountId", Long.toString(App.getInstance().getId())));
////                        QuizActivity.nameValuePairs.add(new BasicNameValuePair("accessToken", App.getInstance().getAccessToken()));
////                        QuizActivity.nameValuePairs.add(new BasicNameValuePair("lang", QuizActivity.Language));
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                    }
////
////                }
////            });
//        }

//        viewHolder.checkbox_name[viewHolder.check_id].setTag(position);

//        viewHolder.quizTitle.setTag(position);
//        viewHolder.quizTitle.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                int getPosition = (Integer) v.getTag();
//                try {
//                    for (int i = 0; i < 1; i++) {
//                        JSONObject jGroup = new JSONObject();
//
//                        jGroup.put("user_id", "2");
//
//                        jGroup.put("quiz_id", quiz.getId());
//                        jGroup.put("quiz_title", quiz.getQuizTitle());
//                        jGroup.put("quiz_date", quiz.getDate());
//                        jGroup.put("chossed_ans", "tyujytdjuy");
//
//                        QuizActivity.jArray.put(jGroup);
//                    }
//                    QuizActivity.jResult.put("question", QuizActivity.jArray);
////                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//                    QuizActivity.nameValuePairs.add(new BasicNameValuePair("addanswer", QuizActivity.jResult.toString()));
//                    QuizActivity.nameValuePairs.add(new BasicNameValuePair("accountId", Long.toString(App.getInstance().getId())));
//                    QuizActivity.nameValuePairs.add(new BasicNameValuePair("accessToken", App.getInstance().getAccessToken()));
//                    QuizActivity.nameValuePairs.add(new BasicNameValuePair("lang", QuizActivity.Language));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
////                    Question q = questionsList.get(getPosition);
//
////                    Intent intent = new Intent(activity, QuizDetails.class);
////                    intent.putExtra("profileId", q.getFromUserId());
////                    activity.startActivity(intent);
//            }
//        });


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