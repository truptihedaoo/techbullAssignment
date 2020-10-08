package com.example.myapp;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import com.squareup.picasso.Picasso;

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private JSONArray jsonArray;


    public MainAdapter(Context context, JSONArray jsonArray) {
        this.mContext = context;
        this.jsonArray = jsonArray;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.item_apadter, parent, false );
        return new MyView( v );
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final MainAdapter.MyView myHolder = (MainAdapter.MyView) holder;
        setView( myHolder, position );
    }

    @Override
    public int getItemCount() {
        if (jsonArray != null && jsonArray.length() > 0) {
            return (jsonArray.length());
        }
        return (jsonArray.length());
    }


    private void setView(final MyView myHolder, final int position) {
        try {


            myHolder.txtName.setText( jsonArray.getJSONObject( position ).getString( "Title" ) );

            myHolder.txtDetails.setText( jsonArray.getJSONObject( position ).getString( "Year" ) );

            Picasso.with( mContext ).load( String.valueOf( jsonArray.getJSONObject( position ) .getString( "Poster" ))).placeholder( R.drawable.place_holder ).into( myHolder.imgBase );


            myHolder.llMainRoot.setTag( position );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MyView extends RecyclerView.ViewHolder {
        public LinearLayout llMainRoot;
        public TextView txtName, txtDetails;
        public ImageView imgBase;

        public MyView(View itemView) {
            super( itemView );
            try {
                this.llMainRoot = (LinearLayout) itemView.findViewById( R.id.llMainRoot );

                this.txtName = (TextView) itemView.findViewById( R.id.txtName );
                this.txtDetails = (TextView) itemView.findViewById( R.id.txtDetails );

                this.imgBase = (ImageView) itemView.findViewById( R.id.imgBase );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
