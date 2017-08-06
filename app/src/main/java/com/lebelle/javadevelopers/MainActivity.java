package com.lebelle.javadevelopers;

import android.app.Dialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lebelle.javadevelopers.Miscellaneous.Url;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    //navigation drawer
    private DrawerLayout mDrawerLayout;

    //night mode theme for the app
    //private static final String PREFS_NAME = "prefs";
    //private static final String PREF_DARK_THEME = "dark_theme";

    ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    TextView Disconnected;
    TextView EmptyState;
    //ProgressBar loading;

    public static int lastPosition = 0;

    Boolean loading = true;
    Boolean isLastPage = true;
    int pageCount = 1;

    int visibleItemCount;
    int totalItemCount;
    int pastVisiblesItems;
    int pastTotal = 0;

    private static final String LOG_TAG = MainActivity.class.getName();

    /**
     * URL for java developers data from the github api
     */
    private static final String GITHUB_SEARCH_API_DEFAULT_URL = Url.BASE_URL + Url.SEARCH_URL + "lagos" + Url.PAGE;

    //url required
    private String USGS_REQUEST_URL;
    //private Boolean isLoadingProfiles;

    /**
     * Constant value for the javaDevelopers loader ID. We can choose any integer.
     */
    private static final int JAVA_LOADER_ID = 0;

    /**
     * Adapter for the list of javaDevelopers
     */
    private JavaDevelopersAdapter mAdapter;


    private List<JavaDevelopers> myJavaDevelopers;
    private RecyclerView recyclerView;

    LinearLayoutManager llm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //use the chosen theme
        //SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        //boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //the required url is instantiated and assigned the first page
        USGS_REQUEST_URL = GITHUB_SEARCH_API_DEFAULT_URL + String.valueOf(pageCount);

        //create a recyclerView
        recyclerView = (RecyclerView) findViewById(R.id.list_view);
        myJavaDevelopers = new ArrayList<>();

        llm = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.smoothScrollToPosition(lastPosition);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setHasFixedSize(true);

        //create a progress dialog to let the user know whats happening
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Java Developers...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        Disconnected = (TextView) findViewById(R.id.disconnected);
        EmptyState = (TextView) findViewById(R.id.empty_state);
        //loading = (ProgressBar) findViewById(R.id.progress_bar);
        //loading.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FF0084"), PorterDuff.Mode.SRC_IN);


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);

        }

        initializeAdapter();

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        //get details on the current active network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        //if there is network fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            //LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            MyLoaderManager myLoaderManager = new MyLoaderManager();
            getLoaderManager().initLoader(JAVA_LOADER_ID, null, myLoaderManager);

        } else {            //otherwise display error
            Disconnected.setVisibility(View.VISIBLE);
            progressDialog.hide();
            EmptyState.setVisibility(View.GONE);
        }

    }

    private void initializeAdapter() {
        // Create a new adapter that takes an empty list of javaDevelopers as input
        mAdapter = new JavaDevelopersAdapter(getApplicationContext(), myJavaDevelopers);
        recyclerView.setAdapter(mAdapter);
    }


    private class MyLoaderManager implements LoaderManager.LoaderCallbacks<List<JavaDevelopers>>{

        @Override
        public Loader<List<JavaDevelopers>> onCreateLoader(int i, Bundle bundle) {
            Uri baseUri = Uri.parse(USGS_REQUEST_URL);
            Uri.Builder uriBuilder = baseUri.buildUpon();
            return new JavaLoader(getApplicationContext(), uriBuilder.toString());
        } 

        @Override
        public void onLoadFinished(Loader<List<JavaDevelopers>> loader, List<JavaDevelopers> javaDevelopers) {

            recyclerView.setAdapter(mAdapter);
            progressDialog.hide();
            //mAdapter.clear();
            EmptyState.setVisibility(View.VISIBLE);
            Disconnected.setVisibility(View.GONE);


            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            // Hide loading indicator because the data has been loaded
            if (javaDevelopers != null && !javaDevelopers.isEmpty()) {
                myJavaDevelopers.addAll(javaDevelopers);
                mAdapter.notifyDataSetChanged();
                EmptyState.setVisibility(View.GONE);
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        if (dy > 0) //check for scroll down
                        {
                            visibleItemCount = llm.getChildCount();
                            totalItemCount = llm.getItemCount();
                            pastVisiblesItems = llm.findFirstVisibleItemPosition();

                            if (loading) {
                                if (totalItemCount > pastTotal) {
                                    loading = false;
                                    pastTotal = totalItemCount;
                                }
                            }

                            if (!loading && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                pageCount = pageCount + 1;
                                if(pageCount < 8){
                                    //reload_progress_bar.setVisibility(View.VISIBLE);
                                    USGS_REQUEST_URL = GITHUB_SEARCH_API_DEFAULT_URL + String.valueOf(pageCount);
                                    getLoaderManager().initLoader(pageCount, null, new MyLoaderManager());
                                     loading = true;
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "You have gotten to the end of the list", Toast.LENGTH_LONG).show();
                                    loading = true;
                                }
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void onLoaderReset(Loader<List<JavaDevelopers>> loader) {
            // Loader reset, so we can clear out our existing data.
            mAdapter.clear();

        }



    }

    public void swipeRefresh() {
        MyLoaderManager myLoaderManager = new MyLoaderManager();
        getLoaderManager().restartLoader(JAVA_LOADER_ID, null, myLoaderManager);
        swipeRefreshLayout.setRefreshing(true);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.exit:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        //return true;
                        switch (menuItem.getItemId()) {
                            case R.id.share:
                                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                sharingIntent.setType("text/plain");
                                String stringToShare = "Hello! please check out my app";
                                sharingIntent.putExtra(Intent.EXTRA_TEXT, stringToShare);
                                startActivity(Intent.createChooser(sharingIntent, "Share Quad via"));
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.feedback:
                                Intent feedbackEmail = new Intent(Intent.ACTION_SENDTO);
                                feedbackEmail.setData(Uri.parse("mailto:omawumieyekpimi@gmail.com")); // only email apps should handle this
                                feedbackEmail.putExtra(Intent.EXTRA_EMAIL, "");
                                feedbackEmail.putExtra(Intent.EXTRA_SUBJECT, "Quad Feedback");
                                if (feedbackEmail.resolveActivity(getPackageManager()) != null) {
                                    startActivity(Intent.createChooser(feedbackEmail, "Send Quad Feedback via"));
                                }
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.about:
                                final Dialog d = new Dialog(MainActivity.this);
                                d.setContentView(R.layout.about_quad);
                                d.setTitle("About");
                                Button connect = (Button) d.findViewById(R.id.dialogButtonOK);
                                connect.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        d.dismiss();                                     }
                                });
                                d.show();
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.night_switch:
                                SwitchCompat toggle = (SwitchCompat) findViewById(R.id.drawer_switch);
                                //toggle.setChecked(useDarkTheme);
                                toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                                        //toggleTheme(isChecked);
                                        if (isChecked){
                                            //change theme
                                            setNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                        }else {
                                            setNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                        }
                                    }
                                });
                                mDrawerLayout.closeDrawers();
                                break;
                            case android.R.id.home:
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                break;
                        }
                        return true;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // Associate searchable configuration with the SearchView
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        return true;

    }

    private void search(final SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void setNightMode(@AppCompatDelegate.NightMode int nightMode) {
        AppCompatDelegate.setDefaultNightMode(nightMode);

        if (Build.VERSION.SDK_INT >= 11) {
            recreate();
        }
    }

    //private void toggleTheme (boolean darkTheme){
      //  SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        //editor.putBoolean(PREF_DARK_THEME, darkTheme);
        //editor.apply();

        //Intent intent = getIntent();
        //finish();
        //startActivity(intent);
    //}
}