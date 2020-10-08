package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    private MainAdapter adapter;
    private JSONArray jsonArray = new JSONArray();

    private Context mContext;
    private RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;


    private SearchView searchView;
    private SwipeRefreshLayout swipeRefresh;
    public String searchField = "batman"; // for sample
    APIAsyncTask asyncTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        mContext = MainActivity.this;
        recyclerView = (RecyclerView) findViewById( R.id.recyclerView );
        searchView = (SearchView) findViewById( R.id.searchView );
        swipeRefresh = (SwipeRefreshLayout) findViewById( R.id.swipeRefresh );

        asyncTask = new APIAsyncTask();
        asyncTask.execute( "http://www.omdbapi.com/?s=" + searchField + "&apikey=88328ad7" );

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById( R.id.swipeRefresh );
        swipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                jsonArray = new JSONArray();
                APIAsyncTask asyncTask = new APIAsyncTask();
                asyncTask.execute( "http://www.omdbapi.com/?s=" + "batman" + "&apikey=88328ad7" );
            }
        } );
        swipeRefreshLayout.setRefreshing( false );
        initSearchView();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                jsonArray = new JSONArray();
                asyncTask = new APIAsyncTask();
                asyncTask.execute( "http://www.omdbapi.com/?s=" + searchField + "&apikey=88328ad7" );
            }
        });
        swipeRefresh.setColorSchemeResources(android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeRefresh.setRefreshing(true);

    }


    private void initSearchView() {
        searchView.setQueryHint("Search here");
        searchView.setIconified(true);
        searchView.onActionViewExpanded();
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().length() > 1 || newText.trim().length() == 0) {

                    searchField = newText.trim();
                    jsonArray = new JSONArray();
                    swipeRefresh.setRefreshing(true);

                    asyncTask = new APIAsyncTask();
                    asyncTask.execute( "http://www.omdbapi.com/?s=" + searchField + "&apikey=88328ad7" );
                } else {
                    searchField = "batman";
                    swipeRefresh.setRefreshing(true);

                    asyncTask = new APIAsyncTask();
                    asyncTask.execute( "http://www.omdbapi.com/?s=" + searchField + "&apikey=88328ad7" );

                }

                ImageView searchViewIcon = (ImageView) searchView.findViewById(R.id.search_close_btn);

                searchViewIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchView.setQuery("", false);
                        searchField = "batman";
                        swipeRefresh.setRefreshing(true);
                        asyncTask = new APIAsyncTask();
                        asyncTask.execute( "http://www.omdbapi.com/?s=" + searchField + "&apikey=88328ad7" );


                    }
                });
                return true;

            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.setQuery("", false);
                searchField = "batman";

                jsonArray = new JSONArray();
                asyncTask = new APIAsyncTask();
                asyncTask.execute( "http://www.omdbapi.com/?s=" + searchField + "&apikey=88328ad7" );

                return true;
            }
        });
    }


    private class APIAsyncTask extends AsyncTask<String, Void, String> {

        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... params) {
            String stringUrl = params[0];
            String result = null;
            String inputLine;

            try {
                URL myUrl = new URL( stringUrl );
                HttpURLConnection connection = (HttpURLConnection)
                        myUrl.openConnection();
                connection.setRequestMethod( REQUEST_METHOD );
                connection.setReadTimeout( READ_TIMEOUT );
                connection.setConnectTimeout( CONNECTION_TIMEOUT );
                connection.connect();
                InputStreamReader streamReader = new
                        InputStreamReader( connection.getInputStream() );
                BufferedReader reader = new BufferedReader( streamReader );
                StringBuilder stringBuilder = new StringBuilder();
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append( inputLine );
                }
                reader.close();
                streamReader.close();
                result = stringBuilder.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute( result );
            try {

                JSONObject rootObject = new JSONObject( result.toString() );
                jsonArray = new JSONArray( rootObject.getString( "Search" ) );
                swipeRefresh.setRefreshing( false );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            linearLayoutManager = new LinearLayoutManager(mContext);
            adapter = new MainAdapter( mContext, jsonArray );
            recyclerView.setAdapter( adapter );
            recyclerView.setLayoutManager(linearLayoutManager);
        }
    }


}
