package com.codepath.apps.twitterapp.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterapp.R;
import com.codepath.apps.twitterapp.TwitterApplication;
import com.codepath.apps.twitterapp.fragments.UserTimelineFragment;
import com.codepath.apps.twitterapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

public class ProfileActivity extends ActionBarActivity {

    // region Variables
    TextView tvUsername;
    TextView tvTagLine;
    TextView tvFollowersCount;
    TextView tvFollowingCount;
    ImageView ivProfileImage;
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        bindUIElements();
        // Get the screen name
        String screenName = getIntent().getStringExtra("screen_name");
        showProfile(screenName);
        if (savedInstanceState == null) {
            // Create the user timeline fragment
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(screenName);
            // Display user timeline fragment within activity (dynamically)
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fragmentUserTimeline);
            ft.commit();
        }
    }

    private void bindUIElements() {
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvTagLine = (TextView) findViewById(R.id.tvTagLine);
        tvFollowersCount = (TextView) findViewById(R.id.tvFollowers);
        tvFollowingCount = (TextView) findViewById(R.id.tvFollowing);
    }

    private void showProfile(String screenName) {
        TwitterApplication.getRestClient().getProfile(screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                populateProfile(User.fromJSON(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("ERROR", Integer.toString(statusCode));
            }
        });
    }

    private void populateProfile(User user) {
        getSupportActionBar().setTitle("@" + user.getScreenName());
        tvUsername.setText(user.getName());
        tvTagLine.setText(user.getTagline());
        tvFollowersCount.setText(user.getFollowersCount() + " Followers");
        tvFollowingCount.setText(user.getFollowingCount() + " Following");
        Picasso.with(this).load(user.getProfileImageUrl()).into(ivProfileImage);
    }
}
