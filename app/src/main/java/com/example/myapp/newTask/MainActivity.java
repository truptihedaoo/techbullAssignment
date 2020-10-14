package com.example.myapp.newTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.example.myapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.myapp.newTask.PaginationListener.PAGE_START;

public class MainActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MainActivity";

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.searchView)
    SearchView searchView;
    private PostRecyclerAdapter adapter;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    int itemCount = 0;

    JSONArray jsonArray;
    public String searchField = "batman"; // for sample
    APIAsyncTask asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        ButterKnife.bind( this );

        swipeRefresh.setOnRefreshListener( this );
        mRecyclerView.setHasFixedSize( true );
        LinearLayoutManager layoutManager = new LinearLayoutManager( this );
        mRecyclerView.setLayoutManager( layoutManager );

        swipeRefresh.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                jsonArray = new JSONArray();
                APIAsyncTask asyncTask = new APIAsyncTask();
                asyncTask.execute( "http://www.omdbapi.com/?s=" + "batman" + "&apikey=88328ad7" );
            }
        } );
        swipeRefresh.setRefreshing( false );
        adapter = new PostRecyclerAdapter( this, new ArrayList<PostItem>() );
        mRecyclerView.setAdapter( adapter );
        asyncTask = new APIAsyncTask();
        asyncTask.execute( "http://www.omdbapi.com/?s=" + searchField + "&apikey=88328ad7" );
        initSearchView();


        mRecyclerView.addOnScrollListener( new PaginationListener( layoutManager ) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                asyncTask = new APIAsyncTask();
                asyncTask.execute( "http://www.omdbapi.com/?s=" + searchField + "&apikey=88328ad7" );

            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        } );
    }

    private void initSearchView() {
        searchView.setQueryHint( "Search here" );
        searchView.setIconified( true );
        searchView.onActionViewExpanded();
        searchView.clearFocus();
        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().length() > 1 || newText.trim().length() == 0) {
                    searchField = newText.trim();
                    jsonArray = new JSONArray();
                    swipeRefresh.setRefreshing( true );

                    itemCount = 0;
                    currentPage = PAGE_START;
                    isLastPage = false;
                    adapter.clear();
//        doApiCall();
                    asyncTask = new APIAsyncTask();
                    asyncTask.execute( "http://www.omdbapi.com/?s=" + searchField + "&apikey=88328ad7" );
                } else {
                    itemCount = 0;
                    currentPage = PAGE_START;
                    isLastPage = false;
                    adapter.clear();
                    searchField = "batman";
                    swipeRefresh.setRefreshing( true );

                    asyncTask = new APIAsyncTask();
                    asyncTask.execute( "http://www.omdbapi.com/?s=" + searchField + "&apikey=88328ad7" );

                }

                ImageView searchViewIcon = (ImageView) searchView.findViewById( R.id.search_close_btn );

                searchViewIcon.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchView.setQuery( "", false );
                        searchField = "batman";
                        itemCount = 0;
                        currentPage = PAGE_START;
                        isLastPage = false;
                        adapter.clear();
                        swipeRefresh.setRefreshing( true );
                        asyncTask = new APIAsyncTask();
                        asyncTask.execute( "http://www.omdbapi.com/?s=" + searchField + "&apikey=88328ad7" );


                    }
                } );
                return true;

            }
        } );
        searchView.setOnCloseListener( new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.setQuery( "", false );
                searchField = "batman";
                itemCount = 0;
                currentPage = PAGE_START;
                isLastPage = false;
                adapter.clear();
                jsonArray = new JSONArray();
                asyncTask = new APIAsyncTask();
                asyncTask.execute( "http://www.omdbapi.com/?s=" + searchField + "&apikey=88328ad7" );

                return true;
            }
        } );
    }

    /**
     * do api call here to fetch data from server
     * In example i'm adding data manually
     */
    private void doApiCall() {
        final ArrayList<PostItem> items = new ArrayList<>();
        new Handler().postDelayed( new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    itemCount++;
                    PostItem postItem = new PostItem();
                    postItem.setTitle( getString( R.string.text_title ) + itemCount );
//                    postItem.setDescription(getString(R.string.text_description));
                    items.add( postItem );
                }
                /**
                 * manage progress view
                 */
                if (currentPage != PAGE_START) adapter.removeLoading();
                adapter.addItems( items );
                swipeRefresh.setRefreshing( false );

                // check weather is last page or not
                if (currentPage < totalPage) {
                    adapter.addLoading();
                } else {
                    isLastPage = true;
                }
                isLoading = false;
            }
        }, 1500 );
    }


    private class APIAsyncTask extends AsyncTask<String, Void, String> {

        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;
        final ArrayList<PostItem> items = new ArrayList<>();


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

                for (int i = 0; i < 10; i++) {
                    itemCount++;
                    PostItem postItem = new PostItem();
                    postItem.setTitle( jsonArray.getJSONObject( i ).getString( "Title" ) );
                    postItem.setYear( jsonArray.getJSONObject( i ).getString( "Year" ) );
                    postItem.setImageUrl( jsonArray.getJSONObject( i ).getString( "Poster" ) );

                    items.add( postItem );
                }
                /**
                 * manage progress view
                 */
                if (currentPage != PAGE_START) adapter.removeLoading();
                adapter.addItems( items );
                swipeRefresh.setRefreshing( false );

                // check weather is last page or not
                if (currentPage < totalPage) {
                    adapter.addLoading();
                } else {
                    isLastPage = true;
                }
                isLoading = false;

            } catch (JSONException e) {
                e.printStackTrace();
            }

//            adapter = new MainAdapter( MainActivity.this, jsonArray );
//            mRecyclerView.setAdapter( adapter );
        }
    }


    @Override
    public void onRefresh() {
        itemCount = 0;
        currentPage = PAGE_START;
        isLastPage = false;
        adapter.clear();
//        doApiCall();
        asyncTask = new APIAsyncTask();
        asyncTask.execute( "http://www.omdbapi.com/?s=" + searchField + "&apikey=88328ad7" );

    }
}
