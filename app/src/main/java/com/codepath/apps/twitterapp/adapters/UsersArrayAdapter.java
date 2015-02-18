package com.codepath.apps.twitterapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterapp.R;
import com.codepath.apps.twitterapp.activities.ProfileActivity;
import com.codepath.apps.twitterapp.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersArrayAdapter extends ArrayAdapter {
    public UsersArrayAdapter(Context context, List<User> users) {
        super(context, 0, users);
    }

    // ViewHolder pattern!!
    private static class ViewHolder {
        private ImageView ivProfileImage;
        private TextView tvScreenName;
        private TextView tvName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final User user = (User) getItem(position);
        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
            viewHolder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
            viewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvScreenName.setText("@" + user.getScreenName());
        viewHolder.tvName.setText(user.getName());
        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
        Picasso.with(getContext()).load(user.getProfileImageUrl()).into(viewHolder.ivProfileImage);

        viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ProfileActivity.class);
                i.putExtra("screen_name", user.getScreenName());
                getContext().startActivity(i);
            }
        });

        return convertView;
    }
}

