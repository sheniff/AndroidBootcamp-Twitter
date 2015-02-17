package com.codepath.apps.twitterapp.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.twitterapp.R;
import com.codepath.apps.twitterapp.TwitterApplication;
import com.codepath.apps.twitterapp.fragments.FollowersFragment;
import com.codepath.apps.twitterapp.fragments.FollowingFragment;
import com.codepath.apps.twitterapp.fragments.UserTimelineFragment;
import com.codepath.apps.twitterapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

import java.text.NumberFormat;

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
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_profile);
        bindUIElements();
        // Get the screen name
        String screenName = getIntent().getStringExtra("screen_name");
        showProfile(screenName);

        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
        vpPager.setAdapter(new ProfilePagerAdapter(getSupportFragmentManager()));
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(vpPager);
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
        tvFollowersCount.setText(NumberFormat.getInstance().format(user.getFollowersCount()) + " Followers");
        tvFollowingCount.setText(NumberFormat.getInstance().format(user.getFollowingCount()) + " Following");
        Picasso.with(this).load(user.getProfileImageUrl()).into(ivProfileImage);
    }

    // Returns the order of the fragments in the view pager
    private class ProfilePagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = {"Tweets", "Followers", "Following"};

        public ProfilePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return UserTimelineFragment.newInstance(getIntent().getStringExtra("screen_name"));
            } else if (position == 1) {
                return FollowersFragment.newInstance(getIntent().getStringExtra("screen_name"));
            } else if (position == 2) {
                return FollowingFragment.newInstance(getIntent().getStringExtra("screen_name"));
            } else {
                return null;
            }
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
