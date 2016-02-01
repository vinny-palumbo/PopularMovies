package com.vinnypalumbo.popularmovies;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.vinnypalumbo.popularmovies.sync.PopularMoviesSyncAdapter;

public class MainActivity extends AppCompatActivity implements MovieFragment.Callback {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;
    private String mSort;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("vinny-debug", "MainActivity - onCreate");
        super.onCreate(savedInstanceState);

        // force landscape orientation on tablets
        if(getResources().getBoolean(R.bool.landscape_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        // show watchlist movies if watchlist sort option selected
        mSort = Utility.getPreferredSorting(this);
        if(mSort.equals(getResources().getString(R.string.pref_sort_watchlist))){
            MovieFragment.isWatchlistSelected = true;
        }else{
            MovieFragment.isWatchlistSelected = false;
        }

        setContentView(R.layout.activity_main);
        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        Log.d("vinny-debug", "MainActivity - onCreate: isWatchlistSelected:" + MovieFragment.isWatchlistSelected);
        if(!MovieFragment.isWatchlistSelected){
            PopularMoviesSyncAdapter.initializeSyncAdapter(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("vinny-debug", "MainActivity - onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("vinny-debug", "MainActivity - onOptionsItemSelected");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        Log.d("vinny-debug", "MainActivity - onResume");
        super.onResume();

        // show watchlist movies if watchlist sort option selected
        String sort = Utility.getPreferredSorting(this);
        if(sort.equals(getResources().getString(R.string.pref_sort_watchlist))){
            MovieFragment.isWatchlistSelected = true;
        }else{
            MovieFragment.isWatchlistSelected = false;
        }

        // update the sorting in our second pane using the fragment manager
        if (sort != null && !sort.equals(mSort)) {
            MovieFragment mf = (MovieFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movie);
            if (null != mf) {
                mf.onSortChanged();
            }
            mSort = sort;
        }
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        Log.d("vinny-debug", "MainActivity - onItemSelected");
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.

            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }
}
