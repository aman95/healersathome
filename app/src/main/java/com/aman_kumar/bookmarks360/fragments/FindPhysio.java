package com.aman_kumar.bookmarks360.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aman_kumar.bookmarks360.App;
import com.aman_kumar.bookmarks360.R;
import com.aman_kumar.bookmarks360.activities.BookDoctor;
import com.aman_kumar.bookmarks360.adapters.BookmarkAdapter;
import com.aman_kumar.bookmarks360.adapters.DoctorListAdapter;
import com.aman_kumar.bookmarks360.config.ApiEndpoints;
import com.aman_kumar.bookmarks360.listners.RecyclerItemClickListener;
import com.aman_kumar.bookmarks360.network.VolleySingleton;
import com.aman_kumar.bookmarks360.utils.ShowMessage;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

public class FindPhysio extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    static FindPhysio fragment;

    RecyclerView doctorList;
    RecyclerView.LayoutManager layoutManager;
    DoctorListAdapter adapter;

    RequestQueue requestQueue;
    JSONArray docList;

    ProgressBar loadingSpinner;

    public static FindPhysio newInstance() {
        fragment = new FindPhysio();
        return fragment;

    }

    public FindPhysio() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.fragment_find_physio, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        ((Button)v.findViewById(R.id.button4)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(App.getAppContext(), BookDoctor.class);
                startActivity(intent);
            }
        });
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        // latitude and longitude
        double latitude = 28.605802;
        double longitude = 77.291732;

        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(latitude, longitude)).title("Me");

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));



        // adding marker
        googleMap.addMarker(marker);
        //googleMap.addMarker(marker2);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        // Perform any camera updates here

        //Recycler View==============================================================

        doctorList = (RecyclerView) v.findViewById(R.id.doctor_list);
        loadingSpinner = (ProgressBar)v.findViewById(R.id.progressBar1);

        layoutManager = new LinearLayoutManager(App.getAppContext());
        doctorList.setLayoutManager(layoutManager);

        requestQueue = VolleySingleton.getmInstance().getRequestQueue();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, ApiEndpoints.URL_FIND_NEAR_PHY, (String) null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                adapter = new DoctorListAdapter(response);

                docList = response;

                doctorList.setAdapter(adapter);

                for(int i = 0; i<response.length();i++) {
                    try {
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(response.getJSONObject(i).getJSONObject("id").getString("lat")),
                                Double.parseDouble(response.getJSONObject(i).getJSONObject("id").getString("long")))).title(response.getJSONObject(i).getJSONObject("id").getString("name")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //bookmarksInitialised = true;
                ShowMessage.log("list init");

                doctorList.setVisibility(View.VISIBLE);
                loadingSpinner.setVisibility(View.GONE);
//                tapToRetryView.setVisibility(View.GONE);
//                mSwipeRefreshLayout.setRefreshing(false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ShowMessage.toast("VolleyError: "+error.toString());
                loadingSpinner.setVisibility(View.GONE);
//                tapToRetryView.setVisibility(View.VISIBLE);
//                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS*10,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonArrayRequest);

        /**
         * Adding onClickListner for RecyclerView Elements
         */

        doctorList.addOnItemTouchListener(new RecyclerItemClickListener(App.getAppContext(), doctorList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                ShowMessage.toast("Booom!!!");
                Intent intent = new Intent(getActivity(), BookDoctor.class);
                try {
                    intent.putExtra("id",docList.getJSONObject(position).getJSONObject("id").getInt("id"));
                    intent.putExtra("name",docList.getJSONObject(position).getJSONObject("id").getString("name"));
                    intent.putExtra("clinic",docList.getJSONObject(position).getJSONObject("id").getString("clinic"));
                    intent.putExtra("rating",docList.getJSONObject(position).getJSONObject("id").getInt("rating"));
                    intent.putExtra("charges",docList.getJSONObject(position).getJSONObject("id").getInt("charges"));
                    intent.putExtra("isAvailable",docList.getJSONObject(position).getJSONObject("id").getString("is_available").equals(null)?true:false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, 1);

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}
