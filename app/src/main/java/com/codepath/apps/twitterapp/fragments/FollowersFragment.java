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

import com.codepath.apps.twitterapp.Helpers;
import com.codepath.apps.twitterapp.TwitterApplication;
import com.codepath.apps.twitterapp.TwitterClient;
import com.codepath.apps.twitterapp.listeners.EndlessScrollListener;
import com.codepath.apps.twitterapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class FollowersFragment extends UsersListFragment {

    // region Variables
    private TwitterClient client;
    private String nextUserCursor;
    // endregion

    // region Listeners
    private EndlessScrollListener endlessScrollListener = new EndlessScrollListener() {
        @Override
        public void onLoadMore(int page, int totalItemsCount) {
            if (isOnline) {   // Load more timeline from Twitter https://dev.twitter.com/rest/public/timelines
                populateTimeline();
            }
        }
    };

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isOnline = Helpers.iAmOnline(context);
            lvUsers.setOnScrollListener(isOnline ? endlessScrollListener : null);
        }
    };

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (isOnline) {
                String screenName = getArguments().getString("screen_name");
                client.getUserFollowers(screenName, null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                        List<User> users = null;
                        try {
                            users = User.fromJSONArray(json.getJSONArray("users"));
                            nextUserCursor = json.getString("next_cursor_str");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        replaceAll(users);
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
            populateTimeline();
        }
    }

    public static FollowersFragment newInstance(String screenName) {
        FollowersFragment fragmentDemo = new FollowersFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
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
        lvUsers.setOnScrollListener(endlessScrollListener);
        swipeContainer.setOnRefreshListener(onRefreshListener);
    }

    private void populateTimeline() {
        String screenName = getArguments().getString("screen_name");
        client.getUserFollowers(screenName, nextUserCursor, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                List<User> users = null;
                try {
                    users = User.fromJSONArray(json.getJSONArray("users"));
                    nextUserCursor = json.getString("next_cursor_str");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                addAll(users);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }
}
