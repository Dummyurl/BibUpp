package com.bibupp.androidvantage;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bibupp.androidvantage.app.App;
import com.bibupp.androidvantage.common.ActivityBase;
import com.bibupp.androidvantage.constants.Constants;
import com.bibupp.androidvantage.util.CustomRequest;
import com.bibupp.androidvantage.util.Helper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends ActivityBase implements FragmentDrawer.FragmentDrawerListener, SwipeRefreshLayout.OnRefreshListener {

    Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    // used to store app title
    private CharSequence mTitle;
    LinearLayout mContainerAdmob;
    Fragment fragment;
    Boolean action = false;
    int page = 0,prev_page=0;

    private Boolean restore = false;
    MenuItem searchItem;
    SearchManager searchManager;
    SearchView searchView = null;
    static String[] users_Id;
    static String[] users_latitude;
    static String[] users_longitude;
    static String[] users_Street_Address;
    static String[] users_City;
    static String[] users_State;
    static String[] users_Name;
    static String[] users_Email;
    static String[] users_FullName;
    int length = 0;
    Boolean loadingMore = false;
    public String queryText, currentQuery, oldQuery;
    private int createAt = 0;
    public int itemCount;
    private int arrayLength = 0;
    double lat, lng;
    int Prev_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {

            //Restore the fragment's instance
            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");

            restore = savedInstanceState.getBoolean("restore");
            mTitle = savedInstanceState.getString("mTitle");

        } else {

            fragment = new Search_Location();
//            fragment = new QuestionsFragment();

            restore = false;
            mTitle = getString(R.string.page_8);
        }

        if (fragment != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container_body, fragment).commit();
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(mTitle);

        drawerFragment = (FragmentDrawer) getFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        mContainerAdmob = (LinearLayout) findViewById(R.id.container_admob);

        if (ADMOB) {

            mContainerAdmob.setVisibility(View.VISIBLE);

            AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        if (!restore) {

            displayView(8);
            Prev_title = R.string.page_8;
            prev_page=8;
        }
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//
//        super.onSaveInstanceState(outState);
//
//        outState.putBoolean("restore", true);
//        outState.putString("mTitle", getSupportActionBar().getTitle().toString());
//        getSupportFragmentManager().putFragment(outState, "currentFragment", fragment);
//    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

        displayView(position);
    }

    private void displayView(int position) {

        action = false;

        switch (position) {

            case 0: {

                break;
            }
            case 1: {
                page = 1;
                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                i.putExtra("profileId", App.getInstance().getId());
                i.putExtra("accountId", Long.toString(App.getInstance().getId()));

                startActivity(i);
                getSupportActionBar().setTitle(R.string.page_1);
                action = true;
                break;
            }

            case 2: {
                page = 2;
                prev_page=2;
                fragment = new QuestionsFragment();
                searchItem.setEnabled(false);
                searchView.setVisibility(View.GONE);
                getSupportActionBar().setTitle(R.string.page_2);
                Prev_title = R.string.page_2;
                action = true;
                break;
            }
            case 3: {
                page = 3;
                prev_page=3;
                fragment = new FriendsFragment();
                getSupportActionBar().setTitle(R.string.page_3);
                Prev_title = R.string.page_3;
                action = true;
                break;
            }
            case 4: {
                page = 4;
                prev_page=4;
                fragment = new NotifyLikesFragment();
                getSupportActionBar().setTitle(R.string.page_4);
                Prev_title = R.string.page_4;
                action = true;
                break;
            }

            case 5: {
                page = 5;
                prev_page=5;
                fragment = new NotifyAnswersFragment();
                getSupportActionBar().setTitle(R.string.page_5);
                Prev_title = R.string.page_5;
                action = true;
                break;
            }

            case 6: {
                page = 6;
                Intent i = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(i);
                action = true;
                break;
            }
            case 7: {
                page = 7;
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                action = true;
                break;
            }
            case 8: {
                page = 8;
                prev_page=8;
                fragment = new Search_Location();
                getSupportActionBar().setTitle(R.string.page_8);
                Prev_title = R.string.page_8;
                action = true;

                break;

            }
            case 9: {
                page = 10;
                prev_page=10;
                fragment = new QuizFragment();
                getSupportActionBar().setTitle(R.string.page_10);
                Prev_title = R.string.page_10;
                action = true;
                break;
            }
            default: {

                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                action = true;
            }
        }

        if (action) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container_body, fragment)
                    .commit();
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home: {

                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (drawerFragment.isDrawerOpen()) {

            drawerFragment.closeDrawer();

        } else {

            super.onBackPressed();
        }
    }

    @Override
    public void setTitle(CharSequence title) {

        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);

        searchItem = menu.findItem(R.id.options_menu_main_search);

        searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {

            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        }
//
        if ((page != 8)&(prev_page!=8)) {
            menu.clear();
        }

        if (searchView != null) {

            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
            searchView.setIconifiedByDefault(false);
            searchView.setIconified(false);

            SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchAutoComplete.setHint(getText(R.string.placeholder_search));
            searchAutoComplete.setHintTextColor(getResources().getColor(R.color.light_gray));
//            searchAutoComplete.setTextSize(14);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {

                    if (newText.length() == 0) {

                        showStartScreen();
                    }

                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (Search_Location.mGoogleMap != null) {
                        Search_Location.mGoogleMap.clear();
                    }
                    if (Search_Location.location != null) {
                        Search_Location.mLatitude = Search_Location.location.getLatitude();
                        Search_Location.mLongitude = Search_Location.location.getLongitude();
                        LatLng latLng = new LatLng(Search_Location.mLatitude, Search_Location.mLongitude);
                        Search_Location.markerOptions.title("Your current position:");
                        Search_Location.markerOptions.snippet(getCompleteAddressString(Search_Location.mLatitude, Search_Location.mLongitude));
                        Search_Location.markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.redmarker));
                        Search_Location.markerOptions.position(latLng);
                        Search_Location.mGoogleMap.addMarker(Search_Location.markerOptions);
                        Search_Location.mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                        Search_Location.mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11), 1500, null);
//                        onLocationChanged(Search_Location.location);
                    }

                    queryText = query;
                    searchStart();

                    return false;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);
    }

    public String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder
                    .getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                android.location.Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress
                            .append(returnedAddress.getAddressLine(i)).append(
                            "\n");
                }
                strAdd = strReturnedAddress.toString();
//                Log.w("My Current loction address","" + strReturnedAddress.toString());
            } else {
//                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
//            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    @Override
    public void onRefresh() {

        currentQuery = getCurrentQuery();

        if (currentQuery.equals(oldQuery)) {

            createAt = 0;

            search();

        } else {

            searchCompleted();
        }
    }

    public String getCurrentQuery() {

        String searchText = searchView.getQuery().toString();
        searchText = searchText.trim();

        return searchText;
    }

    public Boolean isValidCurrentQuery() {

        Helper helper = new Helper();

        return helper.isValidSearchQuery(getCurrentQuery());
    }

    public void showListViewFooter() {

//        searchListView.addFooterView(searchListViewFooter, null, false);
    }

    public void hideListViewFooter() {

//        searchListView.removeFooterView(searchListViewFooter);
    }

    public void searchStart() {

        currentQuery = getCurrentQuery();

        currentQuery = currentQuery.trim();

//        if (isValidCurrentQuery()) {

        if (App.getInstance().isConnected()) {

            showLoadScreen();

            search();

        } else {

            Toast.makeText(MainActivity.this, getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
        }

//        } else {
//
//
//        }
    }

    public void searchCompleted() {

        loadingMore = false;

//        mSwipeRefreshLayout.setRefreshing(false);

//        searchResults.setText(getText(R.string.label_search_results) + " " + Integer.toString(itemCount));

        if (itemCount > 0) {

//            searchStartScreen.setVisibility(View.GONE);
//            searchLoadScreen.setVisibility(View.GONE);
//            mSwipeRefreshLayout.setVisibility(View.VISIBLE);

        } else {

            showNoResultsScreen();
        }
    }

    public void showLoadScreen() {

//        mSwipeRefreshLayout.setVisibility(View.GONE);
//        searchStartScreen.setVisibility(View.GONE);
//        searchLoadScreen.setVisibility(View.VISIBLE);
    }

    public void showNoResultsScreen() {

//        searchStartScreen.setVisibility(View.VISIBLE);
//        mSwipeRefreshLayout.setVisibility(View.GONE);
//        searchLoadScreen.setVisibility(View.GONE);

//        searchStartScreenMsg.setText(getText(R.string.label_search_no_results));
    }

    public void showStartScreen() {

//        searchStartScreen.setVisibility(View.VISIBLE);
//        mSwipeRefreshLayout.setVisibility(View.GONE);
//        searchLoadScreen.setVisibility(View.GONE);

//        searchStartScreenMsg.setText(getText(R.string.label_search_start_screen_msg));
    }

    public void search() {

        length = 0;
        if (loadingMore) {

            showListViewFooter();
        }

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, Constants.METHOD_APP_LOCATIONSEARCH, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray sub_responce = response.getJSONArray("users");
                            //LatLng latLng = null;
                            length = sub_responce.length();
                            users_Id = new String[length];
                            users_latitude = new String[length];
                            users_longitude = new String[length];
                            users_Street_Address = new String[length];
                            users_City = new String[length];
                            users_State = new String[length];
                            users_Name = new String[length];
                            users_FullName = new String[length];
                            users_Email = new String[length];
                            if (!loadingMore) {

//                                searchList.clear();
                            }

                            arrayLength = 0;
                            for (int i = 0; i < length; i++) {
                                JSONObject c = sub_responce.getJSONObject(i);
                                users_Id[i] = c.getString("id");
                                users_Name[i] = c.getString("name");
                                users_FullName[i] = c.getString("fullname");
                                users_Email[i] = c.getString("email");
                                users_Street_Address[i] = c.getString("street_address");
                                users_City[i] = c.getString("city");
                                users_State[i] = c.getString("states");
                                users_latitude[i] = c.getString("latitude");
                                users_longitude[i] = c.getString("longitude");
                            }
//                            if (response.getBoolean("error") == false) {
//
//                                itemCount = response.getInt("id");
//                                oldQuery = response.getString("name");
//                                createAt = response.getInt("fullname");
//                                oldQuery = response.getString("email");
//                                itemCount = response.getInt("street_address");
//                                createAt = response.getInt("latitude");
//                                oldQuery = response.getString("longitude");
//
//
//                                if (response.has("users")) {
//
//                                    JSONArray usersArray = response.getJSONArray("users");
//
//                                    arrayLength = usersArray.length();
//
//                                    if (arrayLength > 0) {
//
//                                        for (int i = 0; i < usersArray.length(); i++) {
//
//                                            JSONObject profileObj = (JSONObject) usersArray.get(i);
//
//                                            Profile profile = new Profile(profileObj);
//
//                                            searchList.add(profile);
//                                        }
//                                    }
//                                }
//                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loadingComplete();

//                            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loadingComplete();
                Toast.makeText(MainActivity.this, getString(R.string.error_data_loading), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("query", currentQuery);
                params.put("createAt", Integer.toString(createAt));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void loadingComplete() {
        if (length > 0) {
            Search_Location.txt_mapresult.setVisibility(View.GONE);
            for (int i = 0; i < length; i++) {
                if((!users_latitude[i].equalsIgnoreCase(""))&&(!users_longitude[i].equalsIgnoreCase("")))
                {
                    lat = Double.parseDouble(users_latitude[i]);
                    lng = Double.parseDouble(users_longitude[i]);
                    LatLng latLng = new LatLng(lat, lng);
                    Search_Location.markerOptions.title(users_FullName[i].substring(0, 1).toUpperCase() + users_FullName[i].substring(1));
                    Search_Location.markerOptions.snippet(users_Street_Address[i] + ", " + users_City[i] + ", " + users_State[i]);
                    Search_Location.markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bluemarker));
                    Search_Location.markerOptions.position(latLng);
                    Marker m = Search_Location.mGoogleMap.addMarker(Search_Location.markerOptions);
                    Search_Location.mMarkerPlaceLink.put(m.getId(), users_Id[i]);
                    if (i == 0) {
                        Search_Location.mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                        Search_Location.mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11), 1500, null);
                    }
                }
            }
//            if (length>0) {
//                LatLng latLng = new LatLng(Double.parseDouble(users_latitude[0]), Double.parseDouble(users_longitude[0]));
////                getCompleteAddressString(mLatitude,mLongitude);
//                Search_Location.markerOptions.title(users_FullName[0].substring(0, 1).toUpperCase() + users_FullName[0].substring(1));
//                Search_Location.markerOptions.snippet(users_Street_Address[0] + ", " + users_City[0] + ", " + users_State[0]);
//                Search_Location.markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.redmarker));
//                Search_Location.markerOptions.position(latLng);
//                Search_Location.mGoogleMap.addMarker(Search_Location.markerOptions);
//                Search_Location.mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
//                Search_Location.mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11), 1500, null);
//            }

        } else {
            Search_Location.txt_mapresult.setVisibility(View.VISIBLE);
        }
        if (arrayLength == Constants.LIST_ITEMS) {

//            viewMore = true;

        } else {

//            viewMore = false;
        }

//        adapter.notifyDataSetChanged();

//        searchResults.setText(getText(R.string.label_search_results) + " " + Integer.toString(itemCount));

        loadingMore = false;

        hideListViewFooter();

//        mSwipeRefreshLayout.setRefreshing(false);

//        if (adapter.getCount() > 0) {
//
////            searchStartScreen.setVisibility(View.GONE);
////            searchLoadScreen.setVisibility(View.GONE);
////            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
//
//        } else {
//
//            showNoResultsScreen();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setTitle(Prev_title);
    }
}
