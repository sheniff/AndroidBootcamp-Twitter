package com.codepath.apps.twitterapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterapp.Helpers;
import com.codepath.apps.twitterapp.R;
import com.codepath.apps.twitterapp.activities.ProfileActivity;
import com.codepath.apps.twitterapp.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

public abstract class TweetsArrayAdapter extends ArrayAdapter {

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, 0, tweets);
    }

    // ViewHolder pattern!!
    private static class ViewHolder {
        private ImageView ivProfileImage;
        private TextView tvScreenName;
        private TextView tvName;
        private TextView tvBody;
        private TextView tvTimestamp;
        private ImageView ivFavorite;
        private ImageView ivReply;
        private ImageView ivRetweet;
    }

    public abstract void onClickFavorite(Tweet tweet, View v);
    public abstract void onClickReply(Tweet tweet);
    public abstract void onClickRetweet(Tweet tweet, View v);

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Tweet tweet = (Tweet) getItem(position);
        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
            viewHolder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
            viewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvTimestamp = (TextView) convertView.findViewById(R.id.tvTimestamp);
            viewHolder.ivFavorite = (ImageView) convertView.findViewById(R.id.ivFavorite);
            viewHolder.ivReply = (ImageView) convertView.findViewById(R.id.ivReply);
            viewHolder.ivRetweet = (ImageView) convertView.findViewById(R.id.ivRetweet);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvScreenName.setText("@" + tweet.getUser().getScreenName());
        viewHolder.tvName.setText(tweet.getUser().getName());
        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.tvTimestamp.setText(Helpers.getRelativeTimeAgo(tweet.getCreatedAt()));
        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(viewHolder.ivProfileImage);

        if(tweet.isFavorited()) {
            viewHolder.ivFavorite.setImageDrawable(convertView.getResources().getDrawable(R.drawable.favorite_on));
        } else {
            viewHolder.ivFavorite.setImageDrawable(convertView.getResources().getDrawable(R.drawable.favorite));
        }
        
        if(tweet.isRetweeted()) {
            viewHolder.ivRetweet.setImageDrawable(convertView.getResources().getDrawable(R.drawable.retweet_on));
        } else {
            viewHolder.ivRetweet.setImageDrawable(convertView.getResources().getDrawable(R.drawable.retweet));
        }

        // listeners
        viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ProfileActivity.class);
                i.putExtra("screen_name", tweet.getUser().getScreenName());
                getContext().startActivity(i);
            }
        });
        
        viewHolder.ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFavorite(tweet, v);
            }
        });

        viewHolder.ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickReply(tweet);
            }
        });

        viewHolder.ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRetweet(tweet, v);
            }
        });

        return convertView;
    }
}

