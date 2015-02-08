package com.codepath.apps.twitterapp.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.twitterapp.Helpers;
import com.codepath.apps.twitterapp.R;
import com.codepath.apps.twitterapp.TwitterApplication;
import com.codepath.apps.twitterapp.models.Tweet;
import com.codepath.apps.twitterapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

public class TweetDialogFragment extends DialogFragment {

    // region Constants
    private final int TWEET_LENGTH = 140;
    // endregion

    // region Variables
    private ImageView ivProfileImage;
    private TextView tvUsername;
    private TextView tvHandler;
    private EditText etTweet;
    private Tweet tweetToReply;
    private Button btnSend;
    // endregion

    // region Listeners
    private View.OnClickListener onSendTweetListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String tweetText = etTweet.getText().toString();

            if(tweetText.length() <= 0) {
                Toast.makeText(getActivity(), "Try typing something first...", Toast.LENGTH_SHORT).show();
                return;
            }

            // Send tweet
            TwitterApplication.getRestClient().reply(tweetToReply.getUid(), tweetText, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    getDialog().dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e("ERROR_TWEETING", errorResponse.toString());
                }
            });
        }
    };

    private TextWatcher onTextChange = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            btnSend.setText(Integer.toString(TWEET_LENGTH - etTweet.length()) + " | Tweet");
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    };
    // endregion

    public TweetDialogFragment() {}

    public static TweetDialogFragment newInstance(Tweet tweetToReply) {
        TweetDialogFragment frag = new TweetDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("tweet", tweetToReply);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet, container);
        tweetToReply = getArguments().getParcelable("tweet");
        bindUIElements(view);
        setUpListeners();

        showProfile();

        // print handler before message
        etTweet.setText("@" + tweetToReply.getUser().getScreenName() + " ");
        etTweet.setSelection(etTweet.getText().length());

        getDialog().setTitle("Reply to @" + tweetToReply.getUser().getScreenName());

        return view;
    }

    private void bindUIElements(View view) {
        ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
        tvUsername = (TextView) view.findViewById(R.id.tvUsername);
        tvHandler = (TextView) view.findViewById(R.id.tvHandler);
        etTweet = (EditText) view.findViewById(R.id.etTweet);
        btnSend = (Button) view.findViewById(R.id.btnSend);
    }

    private void setUpListeners() {
        btnSend.setOnClickListener(onSendTweetListener);
        etTweet.addTextChangedListener(onTextChange);
    }

    private void showProfile() {
        User myUser = Helpers.fetchProfileFromSharedPrefs(getActivity());

        if(myUser.getUid() == 0) {
            TwitterApplication.getRestClient().getProfile(new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    User me = User.fromJSON(response);
                    Helpers.storeProfileInSharedPrefs(getActivity(), me);
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
        Picasso.with(getActivity()).load(me.getProfileImageUrl()).into(ivProfileImage);
    }
}
