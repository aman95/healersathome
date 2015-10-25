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
 * Created by aman on 25/10/15.
 */
public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.ViewHolder> {
    private JSONArray dataSet;


    public DoctorListAdapter(JSONArray dataSet) {
        this.dataSet = dataSet;
    }

    public void setData(JSONArray dataSet) {
        this.dataSet = dataSet;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView doctorName, clinicName, doctorDistance, doctorInitials;
        public ViewHolder(View itemView) {
            super(itemView);
            doctorName = (TextView)itemView.findViewById(R.id.doctor_name);
            clinicName = (TextView) itemView.findViewById(R.id.clinic_name);
            doctorDistance = (TextView) itemView.findViewById(R.id.doctor_distance);
            doctorInitials = (TextView) itemView.findViewById(R.id.doctor_initial);
        }
    }

    @Override
    public DoctorListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_doctor,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JSONObject data = null;
        String dist = null;
        try {
            data = dataSet.getJSONObject(position).getJSONObject("id");
            dist = dataSet.getJSONObject(position).getString("distance");

            holder.doctorName.setText(data.getString("name"));
            holder.clinicName.setText(data.getString("clinic"));
            holder.doctorDistance.setText(dist.substring(2,5)+"m");
            holder.doctorInitials.setText(data.getString("name").substring(0,1).toUpperCase());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return dataSet.length();
    }

//    public String getInitial(String host) {
//
//        String initial = "X";
//        //Log.d("adapter","Hostname: "+host);
//        if (host.startsWith("www.")) {
//            initial = host.substring(4,5).toUpperCase();
//        } else {
//            initial = host.substring(0,1).toUpperCase();
//        }
//        //Log.d("adapter","initial: "+initial);
//        return initial;
//    }
}
