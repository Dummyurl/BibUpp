package com.bibupp.androidvantage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Locale;

public class Change_Language extends Activity implements View.OnClickListener {
    Button btn_eng, btn_french, btn_dutch,btn_german;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    String LANGUAGE, Language_name;
    LinearLayout layout_english, layout_french, layout_dutch,layout_german;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        btn_eng = (Button) findViewById(R.id.btn_eng);
        btn_french = (Button) findViewById(R.id.btn_french);
        btn_dutch = (Button) findViewById(R.id.btn_dutch);
        btn_german = (Button) findViewById(R.id.btn_german);
        btn_eng.setOnClickListener(this);
        btn_french.setOnClickListener(this);
        btn_dutch.setOnClickListener(this);
        btn_german.setOnClickListener(this);

        layout_english = (LinearLayout) findViewById(R.id.layout_english);
        layout_french = (LinearLayout) findViewById(R.id.layout_french);
        layout_dutch = (LinearLayout) findViewById(R.id.layout_dutch);
        layout_german = (LinearLayout) findViewById(R.id.layout_german);
        layout_english.setOnClickListener(this);
        layout_french.setOnClickListener(this);
        layout_dutch.setOnClickListener(this);
        layout_german.setOnClickListener(this);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        Intent in = new Intent(Change_Language.this, MainActivity.class);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String languageToLoad;
        Locale locale;
        Configuration config;
        String check_lang;

        switch (v.getId()) {
            case R.id.layout_english:
            case R.id.btn_eng:
                LANGUAGE = "en";
                Language_name = "English";
                check_lang = Locale.getDefault().getDisplayLanguage();
                languageToLoad = "en"; // your language
                locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                check_lang = Locale.getDefault().getDisplayLanguage();

                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                editor.putString("Language", LANGUAGE);
                editor.putString("Language_name", Language_name);

                editor.commit();
                startActivity(in);
                break;
            case R.id.layout_french:
            case R.id.btn_french:
                LANGUAGE = "fr";
                Language_name = "Français";
                check_lang = Locale.getDefault().getDisplayLanguage();
                languageToLoad = "fr"; // your language
                locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

                check_lang = Locale.getDefault().getDisplayLanguage();
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                editor.putString("Language", LANGUAGE);
                editor.putString("Language_name", Language_name);
                editor.commit();
                startActivity(in);
                break;
            case R.id.layout_dutch:
            case R.id.btn_dutch:
                LANGUAGE = "nl";
                Language_name = "Nederlands";
                check_lang = Locale.getDefault().getDisplayLanguage();
                languageToLoad = "nl"; // your language
                locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

                check_lang = Locale.getDefault().getDisplayLanguage();
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                editor.putString("Language", LANGUAGE);
                editor.putString("Language_name", Language_name);
                editor.commit();
                startActivity(in);
                break;
            case R.id.layout_german:
            case R.id.btn_german:
                LANGUAGE = "de";
                Language_name = "Deutsche";
                check_lang = Locale.getDefault().getDisplayLanguage();
                languageToLoad = "de"; // your language
                locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

                check_lang = Locale.getDefault().getDisplayLanguage();

                editor.putString("Language", LANGUAGE);
                editor.putString("Language_name", Language_name);
                editor.commit();
                startActivity(in);
                break;
        }
    }
}
