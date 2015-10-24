package com.aman_kumar.bookmarks360.fragments;

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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aman_kumar.bookmarks360.App;
import com.aman_kumar.bookmarks360.R;
import com.aman_kumar.bookmarks360.adapters.BookmarkAdapter;
import com.aman_kumar.bookmarks360.listners.RecyclerItemClickListener;
import com.aman_kumar.bookmarks360.network.VolleySingleton;
import com.aman_kumar.bookmarks360.utils.ShowMessage;
import com.aman_kumar.bookmarks360.config.ApiEndpoints;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

public class MyBookmarks extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public static MyBookmarks mInstance = null;

    RecyclerView bookmarkList;
    RecyclerView.LayoutManager layoutManager;
    BookmarkAdapter adapter;
    ProgressBar loadingSpinner;
    LinearLayout tapToRetryView;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public static JSONArray allBookmarks = null;
    RequestQueue requestQueue;
    boolean bookmarksInitialised = false;

    private OnFragmentInteractionListener mListener;

    public static MyBookmarks newInstance(String param1, String param2) {

        if(mInstance == null){
            mInstance = new MyBookmarks();
            //MyBookmarks fragment = new MyBookmarks();
            Bundle args = new Bundle();
            args.putString(ARG_PARAM1, param1);
            args.putString(ARG_PARAM2, param2);
            mInstance.setArguments(args);
        }

        return mInstance;
    }

    public MyBookmarks() {
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
        final View rootView = inflater.inflate(R.layout.fragment_my_bookmarks, container, false);

        bookmarkList = (RecyclerView) rootView.findViewById(R.id.my_bookmark_list);
        loadingSpinner = (ProgressBar)rootView.findViewById(R.id.loadingSpinner);
        tapToRetryView = (LinearLayout)rootView.findViewById(R.id.tap_retry);
        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_refresh_bookmark_list);


        initBookmarkList();
        initSwipeRefreshBookmarkList();

        return rootView;
    }

    public void initBookmarkList() {
        layoutManager = new LinearLayoutManager(App.getAppContext());
        bookmarkList.setLayoutManager(layoutManager);

        /**
         * Fetching public bookmarks
         */
        requestQueue = VolleySingleton.getmInstance().getRequestQueue();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, ApiEndpoints.URL_PUBLIC_BOOKMARKS, (String) null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                allBookmarks = response;

                adapter = new BookmarkAdapter(response);

                bookmarkList.setAdapter(adapter);

                bookmarksInitialised = true;
                ShowMessage.log("bookmark init");

                bookmarkList.setVisibility(View.VISIBLE);
                loadingSpinner.setVisibility(View.GONE);
                tapToRetryView.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ShowMessage.toast("VolleyError: "+error.toString());
                loadingSpinner.setVisibility(View.GONE);
                tapToRetryView.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS*3,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonArrayRequest);

        /**
         * Adding onClickListner for RecyclerView Elements
         */

        bookmarkList.addOnItemTouchListener(new RecyclerItemClickListener(App.getAppContext(), bookmarkList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView url = (TextView) view.findViewById(R.id.bookmark_item_url);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.getText().toString()));
                startActivity(browserIntent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        /**
         * Adding Swipe to Dismiss to RecyclerView
         */
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //ShowMessage.toast("Direction: " + direction + " Adap Pos: " + viewHolder.getAdapterPosition()+ "Layout Pos: " + viewHolder.getLayoutPosition());
                TextView tv = (TextView)viewHolder.itemView.findViewById(R.id.bookmark_item_url);
                ShowMessage.toast(tv.getText().toString());
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        itemTouchHelper.attachToRecyclerView(bookmarkList);


    }

    public void refreshBookmarks() {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, ApiEndpoints.URL_PUBLIC_BOOKMARKS, (String) null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                allBookmarks = response;

                if(bookmarksInitialised == false) {
                    adapter = new BookmarkAdapter(response);
                    bookmarkList.setAdapter(adapter);
                    bookmarksInitialised = true;
                } else {
                    adapter.setData(response);
                    adapter.notifyDataSetChanged();
                }

                bookmarkList.setVisibility(View.VISIBLE);
                loadingSpinner.setVisibility(View.GONE);
                tapToRetryView.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ShowMessage.toast("VolleyError: "+error.toString());
                loadingSpinner.setVisibility(View.GONE);
                tapToRetryView.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS*4,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonArrayRequest);

    }

    public void initSwipeRefreshBookmarkList() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshBookmarks();
            }
        });

        tapToRetryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tapToRetryView.setVisibility(View.GONE);
                loadingSpinner.setVisibility(View.VISIBLE);
                refreshBookmarks();
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
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
