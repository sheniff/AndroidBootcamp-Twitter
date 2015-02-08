package com.codepath.apps.twitterapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.codepath.apps.twitterapp.Helpers;
import com.codepath.apps.twitterapp.R;
import com.codepath.apps.twitterapp.TwitterApplication;
import com.codepath.apps.twitterapp.TwitterClient;
import com.codepath.apps.twitterapp.adapters.TweetsArrayAdapter;
import com.codepath.apps.twitterapp.listeners.EndlessScrollListener;
import com.codepath.apps.twitterapp.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/*
Advanced: Improve the user interface and theme the app to feel "twitter branded"
 */

public class TimelineActivity extends ActionBarActivity {

    // region Constants
    private final int REQUEST_CODE = 200;
    // endregion

    // region Variables
    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;
    private TextView tvNoReception;
    private SwipeRefreshLayout swipeContainer;
    private Menu activityMenu;
    private boolean isOnline;
    // endregion

    // region Listeners
    private EndlessScrollListener endlessScrollListener = new EndlessScrollListener() {
        @Override
        public void onLoadMore(int page, int totalItemsCount) {
            if(isOnline) {   // Load more timeline from Twitter https://dev.twitter.com/rest/public/timelines
                populateTimeline(tweets.get(totalItemsCount - 1).getUid() - 1);
            }
        }
    };

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if(!isOnline) {
                swipeContainer.setRefreshing(false);
            } else {
                client.getHomeTimeline(-1, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                        tweets.clear();
                        tweets.addAll(Tweet.fromJSONArray(json));
                        aTweets.notifyDataSetChanged();
                        swipeContainer.setRefreshing(false);
                        updateCache(tweets, true);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("DEBUG", errorResponse.toString());
                        swipeContainer.setRefreshing(false);
                    }
                });
            }
        }
    };

    private AdapterView.OnItemClickListener onTweetClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(isOnline) {
                Intent i = new Intent(TimelineActivity.this, TweetActivity.class);
                i.putExtra("tweet", tweets.get(position));
                startActivity(i);
            }
        }
    };

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isOnline = iAmOnline();

            tvNoReception.setVisibility(isOnline ? View.GONE : View.VISIBLE);

            if(activityMenu != null && activityMenu.findItem(R.id.action_compose) != null) {
                activityMenu.findItem(R.id.action_compose).setVisible(isOnline);
            }

            // restoring onScroll listener (it doesn't work if we just control we're online inside it)
            lvTweets.setOnScrollListener(isOnline ? endlessScrollListener : null);
        }
    };
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        bindUIElements();
        setUpListeners();
        customizeActionBar();

        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(this, tweets);
        lvTweets.setAdapter(aTweets);
        isOnline = iAmOnline();
        client = TwitterApplication.getRestClient();    // singleton client

        if(isOnline) {
            populateTimeline(-1);
        } else {
            tweets.addAll(Tweet.getAll());
            aTweets.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(this.networkReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(this.networkReceiver);
    }

    private void bindUIElements() {
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        tvNoReception = (TextView) findViewById(R.id.tvNoReception);
    }

    private void setUpListeners() {
        lvTweets.setOnScrollListener(endlessScrollListener);
        lvTweets.setOnItemClickListener(onTweetClick);
        swipeContainer.setOnRefreshListener(onRefreshListener);
    }

    private void customizeActionBar() {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);

        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        activityMenu = menu;
        menu.findItem(R.id.action_compose).setVisible(isOnline);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Tweet newTweet = new Tweet(
                    data.getExtras().getLong("tweetID"),
                    data.getExtras().getString("tweet"),
                    Helpers.fetchProfileFromSharedPrefs(this),
                    data.getExtras().getString("tweetDate")
            );
            // Add to Tweets ArrayList (at the beginning)
            tweets.add(0, newTweet);
            // Update adapter
            aTweets.notifyDataSetChanged();
        }
    }

    private void populateTimeline(final long lastTweetId) {
        client.getHomeTimeline(lastTweetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                // clean up DB when fetching new tweets
                tweets.addAll(Tweet.fromJSONArray(json));
                aTweets.notifyDataSetChanged();
                updateCache(tweets, lastTweetId == -1);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    private void updateCache(ArrayList<Tweet> newTweets, boolean cleanCache) {
        if(cleanCache && Tweet.countAll() > 0) {
            new Delete().from(Tweet.class).execute();
        }
        // re-populate with new tweets (bulk insert)
        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < newTweets.size(); i++) {
                newTweets.get(i).getUser().save();
                newTweets.get(i).save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public void compose(MenuItem item) {
        if(isOnline) {
            Intent i = new Intent(this, NewTweetActivity.class);
            startActivityForResult(i, REQUEST_CODE);
        }
    }

    private boolean iAmOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
    }
}
