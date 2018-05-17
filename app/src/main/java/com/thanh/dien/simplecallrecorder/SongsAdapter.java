package com.thanh.dien.simplecallrecorder;

import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dien.simplecallrecorder.R;

import java.util.ArrayList;
import java.util.HashMap;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.MyViewHolder> {

    ArrayList<HashMap<String, String>> songList;
    private MediaPlayer mp = new MediaPlayer();

    public SongsAdapter(ArrayList<HashMap<String, String>> songList) {
        this.songList = songList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        final MyViewHolder vHolder = new MyViewHolder(view);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.textView.setText(songList.get(position).get("file_name"));
        holder.textView2.setText(songList.get(position).get("file_date"));
        holder.textView3.setText(songList.get(position).get("file_size"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView textView, textView2, textView3;
        //HashMap<String, String> map = songList.get(position);


        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tvTitle);
            textView2 = (TextView) itemView.findViewById(R.id.tvDate);
            textView3 = (TextView) itemView.findViewById(R.id.tvSize);
        }

            /*AlertDialog.Builder mBuilder = new AlertDialog.Builder(v.getContext());
            mBuilder.setCancelable(false);
            LayoutInflater inflater = LayoutInflater.from(v.getContext());
            View mView = inflater.inflate(R.layout.play_dialog, null);
            mBuilder.setView(mView);*/

    }
}
