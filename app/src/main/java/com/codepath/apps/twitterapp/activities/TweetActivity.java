package com.codepath.apps.twitterapp.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterapp.Helpers;
import com.codepath.apps.twitterapp.R;
import com.codepath.apps.twitterapp.TwitterApplication;
import com.codepath.apps.twitterapp.TwitterClient;
import com.codepath.apps.twitterapp.fragments.TweetDialogFragment;
import com.codepath.apps.twitterapp.models.Tweet;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;

public class TweetActivity extends ActionBarActivity {

    // region Variables
    private ImageView ivProfileImage;
    private TextView tvName;
    private TextView tvScreenName;
    private TextView tvTweet;
    private TextView tvTimestamp;
    private ImageView ivMedia;
    private Tweet tweet;
    private ImageView ivFavorite;
    private ImageView ivReply;
    private ImageView ivRetweet;
    private TwitterClient client;
    // endregion

    // region Listeners
    private View.OnClickListener onClickFavorite = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (tweet.isFavorited()) {
                ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.favorite));

                client.unfavorite(tweet.getUid(), new AsyncHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.favorite_on)); // revert image state
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        tweet.setFavorited(false);
                    }
                });
            } else {
                ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.favorite_on));

                client.favorite(tweet.getUid(), new AsyncHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.favorite)); // revert image state
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        tweet.setFavorited(true);
                    }
                });
            }
        }
    };
    private View.OnClickListener onClickReply = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            TweetDialogFragment tweetDialog = TweetDialogFragment.newInstance(tweet);
            tweetDialog.show(fm, "fragment_tweet");
        }
    };
    private View.OnClickListener onClickRetweet = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            TwitterClient client = TwitterApplication.getRestClient();

            ivRetweet.setImageDrawable(getResources().getDrawable(R.drawable.retweet_on));

            client.retweet(tweet.getUid(), new AsyncHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    ivRetweet.setImageDrawable(getResources().getDrawable(R.drawable.retweet)); // revert image state
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    tweet.setRetweeted(true);
                }
            });
        }
    };
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        bindUIElements();
        setUpListeners();

        client = TwitterApplication.getRestClient();
        tweet = getIntent().getParcelableExtra("tweet");
        populateTweet(tweet);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("@" + tweet.getUser().getScreenName() + " said...");
    }

    private void bindUIElements() {
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvName = (TextView) findViewById(R.id.tvUsername);
        tvScreenName = (TextView) findViewById(R.id.tvHandler);
        tvTweet = (TextView) findViewById(R.id.tvTweet);
        tvTimestamp = (TextView) findViewById(R.id.tvTimestamp);
        ivMedia = (ImageView) findViewById(R.id.ivMedia);
        ivFavorite = (ImageView) findViewById(R.id.ivFavorite);
        ivReply = (ImageView) findViewById(R.id.ivReply);
        ivRetweet = (ImageView) findViewById(R.id.ivRetweet);
    }

    private void setUpListeners() {
        ivFavorite.setOnClickListener(onClickFavorite);
        ivReply.setOnClickListener(onClickReply);
        ivRetweet.setOnClickListener(onClickRetweet);
    }

    private void populateTweet(Tweet tweet) {
        // profile
        tvName.setText(tweet.getUser().getName());
        tvScreenName.setText("@" + tweet.getUser().getScreenName());
        tvTimestamp.setText(Helpers.getRelativeTimeAgo(tweet.getCreatedAt()));
        Picasso.with(this).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);

        // body
        tvTweet.setText(tweet.getBody());

        // media
        if (tweet.getMedia() != null && tweet.getMedia().size() > 0) {
            // ToDo: Show all the pics that might be available?
            Picasso.with(this).load(tweet.getMedia().get(0)).into(ivMedia);
            ivMedia.setVisibility(View.VISIBLE);
        } else {
            ivMedia.setVisibility(View.GONE);
        }
        
        // actions
        if(tweet.isFavorited()) {
            ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.favorite_on));
        }

        if(tweet.isRetweeted()) {
            ivRetweet.setImageDrawable(getResources().getDrawable(R.drawable.retweet_on));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tweet, menu);
        return true;
    }

    public void reply(MenuItem item) {
        // reply
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        TweetDialogFragment tweetDialog = TweetDialogFragment.newInstance(tweet);
        tweetDialog.show(fm, "fragment_tweet");
    }
}
