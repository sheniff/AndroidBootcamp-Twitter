package com.codepath.apps.twitterapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.codepath.apps.twitterapp.Helpers;
import com.codepath.apps.twitterapp.R;
import com.codepath.apps.twitterapp.TwitterApplication;
import com.codepath.apps.twitterapp.TwitterClient;
import com.codepath.apps.twitterapp.adapters.TweetsArrayAdapter;
import com.codepath.apps.twitterapp.models.Tweet;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

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
            public void onClickFavorite(final Tweet tweet, final View v) {
                TwitterClient client = TwitterApplication.getRestClient();
                final ImageView ivFavorite = (ImageView) v.findViewById(R.id.ivFavorite);

                if (tweet.isFavorited()) {
                    ivFavorite.setImageDrawable(v.getResources().getDrawable(R.drawable.favorite));

                    client.unfavorite(tweet.getUid(), new AsyncHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            ivFavorite.setImageDrawable(v.getResources().getDrawable(R.drawable.favorite_on)); // revert image state
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            tweet.setFavorited(false);
                        }
                    });
                } else {
                    ivFavorite.setImageDrawable(v.getResources().getDrawable(R.drawable.favorite_on));

                    client.favorite(tweet.getUid(), new AsyncHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            ivFavorite.setImageDrawable(v.getResources().getDrawable(R.drawable.favorite)); // revert image state
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            tweet.setFavorited(true);
                        }
                    });
                }
            }

            @Override
            public void onClickReply(Tweet tweet) {
                android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
                TweetDialogFragment tweetDialog = TweetDialogFragment.newInstance(tweet);
                tweetDialog.show(fm, "fragment_tweet");
            }

            @Override
            public void onClickRetweet(final Tweet tweet, final View v) {
                TwitterClient client = TwitterApplication.getRestClient();
                final ImageView ivRetweet = (ImageView) v.findViewById(R.id.ivRetweet);

                ivRetweet.setImageDrawable(v.getResources().getDrawable(R.drawable.retweet_on));

                client.retweet(tweet.getUid(), new AsyncHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        ivRetweet.setImageDrawable(v.getResources().getDrawable(R.drawable.retweet)); // revert image state
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        tweet.setRetweeted(true);
                    }
                });
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
