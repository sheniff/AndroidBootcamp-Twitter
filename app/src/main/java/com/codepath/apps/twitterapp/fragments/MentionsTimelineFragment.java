package com.codepath.apps.twitterapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.codepath.apps.twitterapp.Helpers;
import com.codepath.apps.twitterapp.TwitterApplication;
import com.codepath.apps.twitterapp.TwitterClient;
import com.codepath.apps.twitterapp.activities.TweetActivity;
import com.codepath.apps.twitterapp.listeners.EndlessScrollListener;
import com.codepath.apps.twitterapp.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class MentionsTimelineFragment extends TweetsListFragment {

    // region Variables
    private TwitterClient client;
    // endregion

    // region Listeners
    private EndlessScrollListener endlessScrollListener = new EndlessScrollListener() {
        @Override
        public void onLoadMore(int page, int totalItemsCount) {
            if (isOnline) {   // Load more timeline from Twitter https://dev.twitter.com/rest/public/timelines
                populateTimeline(tweets.get(totalItemsCount - 1).getUid() - 1);
            }
        }
    };

    private AdapterView.OnItemClickListener onTweetClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (isOnline) {
                Intent i = new Intent(getActivity(), TweetActivity.class);
                i.putExtra("tweet", tweets.get(position));
                startActivity(i);
            }
        }
    };

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isOnline = Helpers.iAmOnline(context);
            lvTweets.setOnScrollListener(isOnline ? endlessScrollListener : null);
        }
    };

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (isOnline) {
                client.getMentionsTimeline(-1, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                        replaceAll(Tweet.fromJSONArray(json));
                    }

                    @Override
                    public void onFinish() {
                        swipeContainer.setRefreshing(false);
                    }
                });
            } else {
                swipeContainer.setRefreshing(false);
            }
        }
    };
    // endregion

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);
        setUpListeners();
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = TwitterApplication.getRestClient();    // singleton client

        if (isOnline) {
            populateTimeline(-1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(this.networkReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(this.networkReceiver);
    }

    private void setUpListeners() {
        lvTweets.setOnScrollListener(endlessScrollListener);
        lvTweets.setOnItemClickListener(onTweetClick);
        swipeContainer.setOnRefreshListener(onRefreshListener);
    }

    private void populateTimeline(long lastTweetId) {
        client.getMentionsTimeline(lastTweetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                // clean up DB when fetching new tweets
                List<Tweet> tweets = Tweet.fromJSONArray(json);
                addAll(tweets);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }
}
