package com.codepath.apps.twitterapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.twitterapp.Helpers;
import com.codepath.apps.twitterapp.R;
import com.codepath.apps.twitterapp.TwitterApplication;
import com.codepath.apps.twitterapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class NewTweetActivity extends ActionBarActivity {

    // region Constants
    private final int TWEET_LENGTH = 140;
    // endregion

    // region Variables
    private ImageView ivProfileImage;
    private TextView tvUsername;
    private TextView tvHandler;
    private EditText etTweet;
    private String tweet;
    private User me;
    private TextView tvCharsLeft;
    // endregion

    // region Listeners
    private TextWatcher onTextChange = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            tvCharsLeft.setText(Integer.toString(TWEET_LENGTH - etTweet.length()) + "  |");
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    };
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tweet);
        setUpCustomActionBar();
        bindUIElements();
        setUpListeners();
        showProfile();

        tvCharsLeft.setText(Integer.toString(TWEET_LENGTH) + "  |");
    }

    private void bindUIElements() {
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvHandler = (TextView) findViewById(R.id.tvHandler);
        etTweet = (EditText) findViewById(R.id.etTweet);
        tvCharsLeft = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.tvCharsLeft);
    }

    private void setUpListeners() {
        etTweet.addTextChangedListener(onTextChange);
    }

    private void setUpCustomActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        getSupportActionBar().setCustomView(R.layout.actionbar_new_tweet);
    }

    private void showProfile() {
        // ToDo: We should remove the profile info from shared prefs when log out
        User myUser = Helpers.fetchProfileFromSharedPrefs(this);

        if(myUser.getUid() == 0) {
            TwitterApplication.getRestClient().getProfile(null, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    me = User.fromJSON(response);
                    Helpers.storeProfileInSharedPrefs(NewTweetActivity.this, me);
                    populateProfile(me);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("ERROR", Integer.toString(statusCode));
                }
            });
        } else {
            populateProfile(myUser);
        }
    }

    private void populateProfile(User me) {
        tvUsername.setText(me.getName());
        tvHandler.setText("@" + me.getScreenName());
        Picasso.with(this).load(me.getProfileImageUrl()).into(ivProfileImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_tweet, menu);
        return true;
    }

    // Send new tweet
    public void saveTweet(MenuItem item) {
        tweet = etTweet.getText().toString();

        if(tweet.length() <= 0) {
            Toast.makeText(NewTweetActivity.this, "Try to type something smart first...", Toast.LENGTH_SHORT).show();
            return;
        }

        TwitterApplication.getRestClient().postTweet(tweet, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Intent res = new Intent();
                try {
                    res.putExtra("tweetID", response.getLong("id"));
                    res.putExtra("tweetDate", response.getString("created_at"));
                    res.putExtra("tweet", response.getString("text"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult(RESULT_OK, res);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("ERROR_TWEETING", errorResponse.toString());
            }
        });
    }
}
