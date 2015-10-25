package com.aman_kumar.bookmarks360.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aman_kumar.bookmarks360.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aman on 2/10/15.
 */
public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {
    private JSONArray dataSet;


    public BookmarkAdapter(JSONArray dataSet) {
        this.dataSet = dataSet;
    }

    public void setData(JSONArray dataSet) {
        this.dataSet = dataSet;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView bookmarkTitle, bookmarkUrl, bookmarkInitial;
        public ViewHolder(View itemView) {
            super(itemView);
            bookmarkTitle = (TextView)itemView.findViewById(R.id.bookmark_item_title);
            bookmarkUrl = (TextView) itemView.findViewById(R.id.bookmark_item_url);
            bookmarkInitial = (TextView) itemView.findViewById(R.id.doctor_initial);
        }
    }

    @Override
    public BookmarkAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_bookmark,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JSONObject data = null;
        try {
            data = dataSet.getJSONObject(position);
            holder.bookmarkTitle.setText((data.getString("title").trim().equals(""))?"<No Title>":data.getString("title").trim());
            holder.bookmarkUrl.setText(data.getString("url").trim());
            holder.bookmarkInitial.setText(getInitial(data.getString("hostname")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return dataSet.length();
    }

    public String getInitial(String host) {

        String initial = "X";
        //Log.d("adapter","Hostname: "+host);
        if (host.startsWith("www.")) {
            initial = host.substring(4,5).toUpperCase();
        } else {
            initial = host.substring(0,1).toUpperCase();
        }
        //Log.d("adapter","initial: "+initial);
        return initial;
    }
}
