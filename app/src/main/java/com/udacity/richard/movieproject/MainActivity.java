package com.udacity.richard.movieproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.udacity.richard.movieproject.data.MoviesContract;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback{

    private static final String DETAIL_FRAGMENT_TAG = "DFTAG";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_fragment_container, MainFragment.newInstance())
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Long movieId) {
        if(getResources().getBoolean(R.bool.twopane)){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, DetailFragment.newInstance(
                                    MoviesContract.MoviesListContract.buildMovieSelectionUri(movieId)
                            ),
                            DETAIL_FRAGMENT_TAG)
                    .commit();
        }
        else{
            startActivity(DetailActivity.newIntent(this,
                    MoviesContract.MoviesListContract.buildMovieSelectionUri(movieId)));
        }
    }
}
