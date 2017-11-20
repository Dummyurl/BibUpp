package com.bibupp.androidvantage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bibupp.androidvantage.adapter.SearchListAdapter;
import com.bibupp.androidvantage.app.App;
import com.bibupp.androidvantage.model.Profile;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class Search_Location extends Fragment implements OnMapReadyCallback, LocationListener {

    Intent intent;

    private ArrayList<Profile> searchList;

    private SearchListAdapter adapter;

    public int action;
    Boolean loadingMore = false;
    Boolean viewMore = false;
    public static GoogleMap mGoogleMap;

    int length = 0;
    public static LatLng latLng = null;
    public static MarkerOptions markerOptions = new MarkerOptions();
    public static HashMap<String, String> mMarkerPlaceLink = new HashMap<String, String>();
    String id;
    private static final long MIN_TIME_BW_UPDATES = 5;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 20;
    public static Double mLatitude = 0.0, mLongitude = 0.0;
    public static Location location = null;
public static TextView txt_mapresult;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.search__location, container, false);
        txt_mapresult=(TextView)rootView.findViewById(R.id.txt_mapresult);
        txt_mapresult.setVisibility(View.GONE);
        intent = getActivity().getIntent();
        action = intent.getIntExtra("action", 0);

        searchList = new ArrayList<Profile>();
        adapter = new SearchListAdapter(getActivity(), searchList);
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(), requestCode);
            dialog.show();
        } else { // Google Play Services are available
            // Getting reference to the SupportMapFragment
//            SupportMapFragment fragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.mapp);
//
//            mGoogleMap = fragment.getMap();
            FragmentManager fm = getChildFragmentManager();
            SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.mapp);
            if (fragment == null) {
                fragment = SupportMapFragment.newInstance();
                fm.beginTransaction().replace(R.id.mapp, fragment).commit();
            } else {
                mGoogleMap = fragment.getMap();
            }
//            mGoogleMap = mapView.getMap();
//            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
//            mGoogleMap.setMyLocationEnabled(true);

            MarkerOptions markerOptions = new MarkerOptions();

            try {
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                // getting GPS status
                boolean isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                // getting network status
                boolean isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    // no network provider is enabled
                } else {
                    //this.canGetLocation = true;
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "Network Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                mLatitude = location.getLatitude();
                                mLongitude = location.getLongitude();
                            }
                        }
                    }
                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    mLatitude = location.getLatitude();
                                    mLongitude = location.getLongitude();
                                } else {
                                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                    //				alertDialog.setTitle("Your phone does not support GPS or NETWORK access, you can still use the application but some features may not work properly.");
                                    alertDialog.setMessage("Your phone does not find current location.");//but some features may not work properly
                                    //alertDialog.setIcon(R.drawable.ic_launcher);
                                    alertDialog.setButton("QUIT", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
//                                            MapActivity.this.finish();
                                        }
                                    });
                                    alertDialog.show();
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (location != null) {
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
                latLng = new LatLng(mLatitude, mLongitude);
//                getCompleteAddressString(mLatitude,mLongitude);
                markerOptions.title("Your current Location:");
                markerOptions.snippet(getCompleteAddressString(mLatitude, mLongitude));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.redmarker));
                markerOptions.position(latLng);
                mGoogleMap.addMarker(markerOptions);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11), 1500, null);
                onLocationChanged(location);
            }
//            JSONAsyncTaskMap task = new JSONAsyncTaskMap();
//            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }


        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker arg0) {
                id = mMarkerPlaceLink.get(arg0.getId());
                if (id == null) {

                } else {
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    intent.putExtra("profileId", Long.parseLong(id));
                    intent.putExtra("accountId", Long.toString(App.getInstance().getId()));
                    startActivity(intent);
//                    go();

                }
            }
        });
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home: {

//                killActivity();
                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
//        mGoogleMap = googleMap;

        LatLng TutorialsPoint = new LatLng(30.7333, 76.7794);
        mGoogleMap.addMarker(new MarkerOptions().position(TutorialsPoint).title("Chandigarh"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(TutorialsPoint));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(TutorialsPoint)
                .zoom(11.0f)
                .bearing(90)
                .tilt(30)
                .build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    public String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
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
}

