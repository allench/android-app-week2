package com.example.allench.googleimagesearcher.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.allench.googleimagesearcher.R;
import com.example.allench.googleimagesearcher.adapters.ImageResultsAdapter;
import com.example.allench.googleimagesearcher.fragments.SettingsDialogFragment;
import com.example.allench.googleimagesearcher.models.ImageResult;
import com.example.allench.googleimagesearcher.models.QueryParam;
import com.example.allench.googleimagesearcher.utils.EndlessScrollListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainSearchActivity extends AppCompatActivity {
    private SearchView svSearchBar;
    private GridView gvResults;
    private ArrayList<ImageResult> mImages;
    private ImageResultsAdapter mImagesAdapter;
    private QueryParam mQueryParam = new QueryParam();
    private int mVisibleThreshold = 2;
    private int mNextPageStartIndex = 0;
    private String mLastQueryKeyword = "animal";

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isNetOK = (activeNetworkInfo != null) && activeNetworkInfo.isConnectedOrConnecting();
        // if network is unavailable, show a Toast to user
        if (!isNetOK) {
            Toast.makeText(MainSearchActivity.this, "Internet Connection is NOT available!", Toast.LENGTH_LONG).show();
        }
        return isNetOK;
    }

    private void setupGridView() {
        gvResults = (GridView) findViewById(R.id.gvResults);
        // init data source
        mImages = new ArrayList<>();
        // create adapter
        mImagesAdapter = new ImageResultsAdapter(this, mImages);
        // link adapter to GridView
        gvResults.setAdapter(mImagesAdapter);
        // bind image click
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // check if network available
                if (!isNetworkAvailable()) {
                    return;
                }
                // prepare Intent
                Intent i = new Intent(MainSearchActivity.this, ImageDisplayActivity.class);
                // get image info object
                ImageResult img = mImages.get(position);
                // put img info object into Intent
                i.putExtra("img", img);
                // launch ImageDisplay layer
                startActivity(i);
            }
        });
        // bind scroll event
        gvResults.setOnScrollListener(new EndlessScrollListener(mVisibleThreshold) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                fetchImageResults(mLastQueryKeyword, mNextPageStartIndex, mQueryParam);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_search);
        // action bar icon
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        // setup GridView
        setupGridView();
        // fetch landing images
        fetchImageResults(mLastQueryKeyword, mNextPageStartIndex, mQueryParam);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        svSearchBar = (SearchView) MenuItemCompat.getActionView(searchItem);
        svSearchBar.setQueryHint("Enter to Search...");
        svSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mLastQueryKeyword = query;
                mNextPageStartIndex = 0;
                fetchImageResults(mLastQueryKeyword, mNextPageStartIndex, mQueryParam);
                svSearchBar.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void fetchImageResults(String query, int start, QueryParam param) {
        // check if network available
        if (!isNetworkAvailable()) {
            return;
        }
        // if end, do nothing, just exit
        if (start == -1) {
            return;
        }
        // if first, clear list
        if (start == 0) {
            mImages.clear();
        }

        // prepare api url
        String url = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz=8&q=" + query + "&start=" + start;
        if (param.as_sitesearch != "") {
            url += "&as_sitesearch=" + param.as_sitesearch.toLowerCase();
        }
        if (param.imgcolor != "") {
            url += "&imgcolor=" + param.imgcolor.toLowerCase();
        }
        if (param.imgtype != "") {
            url += "&imgtype=" + param.imgtype.toLowerCase();
        }
        if (param.imgsz != "") {
            url += "&imgsz=" + param.imgsz.toLowerCase();
        }

        // do async http request
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    response = response.getJSONObject("responseData");
                    // load images from JSONArray
                    JSONArray results = response.getJSONArray("results");
                    mImages.addAll(ImageResult.fromJSONArray(results));
                    mImagesAdapter.notifyDataSetChanged();
                    // get pagination data
                    JSONObject cursor = response.getJSONObject("cursor");
                    JSONObject nextPage = cursor.getJSONArray("pages").optJSONObject(cursor.getInt("currentPageIndex") + 1);
                    mNextPageStartIndex = (nextPage == null) ? -1 : nextPage.getInt("start");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        if (id == R.id.action_settings) {
            // popup settings dialog
            SettingsDialogFragment dialog = new SettingsDialogFragment();
            // init dialog fields
            dialog.initQueryParam(mQueryParam);
            // bind dialog button click
            dialog.setOnButtonClickListener(new SettingsDialogFragment.OnButtonClickListener() {
                @Override
                public void onButtonApplyClick(QueryParam param) {
                    mQueryParam = param;
                    mNextPageStartIndex = 0;
                    fetchImageResults(mLastQueryKeyword, mNextPageStartIndex, mQueryParam);
                }
            });
            dialog.show(getSupportFragmentManager(), "fragment_settings_dialog");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
