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

public class TweetsArrayAdapter extends ArrayAdapter {
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
    }

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

        viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ProfileActivity.class);
                i.putExtra("screen_name", tweet.getUser().getScreenName());
                getContext().startActivity(i);
            }
        });
        
        return convertView;
    }
}

