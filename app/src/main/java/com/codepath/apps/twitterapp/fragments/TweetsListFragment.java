package com.codepath.apps.twitterapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.apps.twitterapp.Helpers;
import com.codepath.apps.twitterapp.R;
import com.codepath.apps.twitterapp.adapters.TweetsArrayAdapter;
import com.codepath.apps.twitterapp.models.Tweet;

import java.util.ArrayList;
import java.util.List;

public class TweetsListFragment extends Fragment {

    // region Variables
    protected ArrayList<Tweet> tweets;
    protected TweetsArrayAdapter aTweets;
    protected ListView lvTweets;
    protected SwipeRefreshLayout swipeContainer;
    protected boolean isOnline;
    // endregion

    // inflation logic
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);
        View footerView = inflater.inflate(R.layout.item_loader, null, false);
        bindUIElements(v);
        
        lvTweets.setAdapter(aTweets);
        lvTweets.addFooterView(footerView, null, false);

        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return v;
    }

    // creation lifecycle event
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(getActivity(), tweets) {
            @Override
            public void onClickFavorite(Tweet tweet) {

            }

            @Override
            public void onClickReply(Tweet tweet) {
                android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
                TweetDialogFragment tweetDialog = TweetDialogFragment.newInstance(tweet);
                tweetDialog.show(fm, "fragment_tweet");
            }

            @Override
            public void onClickRetweet(Tweet tweet) {

            }
        };
        isOnline = Helpers.iAmOnline(getActivity());
    }

    private void bindUIElements(View v) {
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
    }

    public void addAll(List<Tweet> tweets) {
        aTweets.addAll(tweets);
    }

    public void replaceAll(List<Tweet> tweets) {
        aTweets.clear();
        addAll(tweets);
    }
    
    public void add(int pos, Tweet newTweet) {
        // Add to Tweets ArrayList (at the beginning)
        tweets.add(pos, newTweet);
        // Update adapter
        aTweets.notifyDataSetChanged();
    }    
}
