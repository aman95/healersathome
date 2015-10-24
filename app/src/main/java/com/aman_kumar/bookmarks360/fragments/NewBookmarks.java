package com.aman_kumar.bookmarks360.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aman_kumar.bookmarks360.App;
import com.aman_kumar.bookmarks360.R;
import com.aman_kumar.bookmarks360.adapters.BookmarkAdapter;
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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class NewBookmarks extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public static NewBookmarks mInstance = null;

    /**
     * Initialising Layout elements
     */

    Button addBookmarkBtn;
    TextView newBookmarkURL;
    TextView responseText;

    RequestQueue requestQueue;


    private OnFragmentInteractionListener mListener;

    public static NewBookmarks newInstance(String param1, String param2) {

        if(mInstance == null){
            mInstance = new NewBookmarks();
            //MyBookmarks fragment = new MyBookmarks();
            Bundle args = new Bundle();
            args.putString(ARG_PARAM1, param1);
            args.putString(ARG_PARAM2, param2);
            mInstance.setArguments(args);
        }

        return mInstance;
    }

    public NewBookmarks() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_new_bookmarks, container, false);

        addBookmarkBtn = (Button) rootView.findViewById(R.id.bookmark_new_add_btn);
        newBookmarkURL = (TextView) rootView.findViewById(R.id.bookmark_new_url);
        responseText = (TextView) rootView.findViewById(R.id.responseText);

        requestQueue = VolleySingleton.getmInstance().getRequestQueue();

        addBookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBookmark();
            }
        });

        return rootView;
    }

    public void createBookmark() {
        String url = newBookmarkURL.getText().toString().trim();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("url",url);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ApiEndpoints.URL_ADD_BOOKMARK, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                responseText.setText(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseText.setText(error.toString());

            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS*4,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
}
