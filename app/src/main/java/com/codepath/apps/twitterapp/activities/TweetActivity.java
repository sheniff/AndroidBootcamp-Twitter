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
import com.codepath.apps.twitterapp.fragments.TweetDialogFragment;
import com.codepath.apps.twitterapp.models.Tweet;
import com.squareup.picasso.Picasso;

public class TweetActivity extends ActionBarActivity {

    // region Variables
    private ImageView ivProfileImage;
    private TextView tvName;
    private TextView tvScreenName;
    private TextView tvTweet;
    private TextView tvTimestamp;
    private ImageView ivMedia;
    private Tweet tweet;
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        bindUIElements();
        setUpListeners();

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
    }

    private void setUpListeners() {}

    private void populateTweet(Tweet tweet) {
        // profile
        tvName.setText(tweet.getUser().getName());
        tvScreenName.setText("@" + tweet.getUser().getScreenName());
        tvTimestamp.setText(Helpers.getRelativeTimeAgo(tweet.getCreatedAt()));
        Picasso.with(this).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);

        // body
        tvTweet.setText(tweet.getBody());

        // media
        if(tweet.getMedia() != null && tweet.getMedia().size() > 0) {
            // ToDo: Show all the pics that might be available?
            Picasso.with(this).load(tweet.getMedia().get(0)).into(ivMedia);
            ivMedia.setVisibility(View.VISIBLE);
        } else {
            ivMedia.setVisibility(View.GONE);
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
