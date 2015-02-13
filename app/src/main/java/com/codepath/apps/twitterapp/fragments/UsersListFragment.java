package com.codepath.apps.twitterapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.apps.twitterapp.Helpers;
import com.codepath.apps.twitterapp.R;
import com.codepath.apps.twitterapp.adapters.UsersArrayAdapter;
import com.codepath.apps.twitterapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class UsersListFragment extends Fragment {

    // region Variables
    protected ArrayList<User> users;
    protected UsersArrayAdapter aUsers;
    protected ListView lvUsers;
    protected SwipeRefreshLayout swipeContainer;
    protected boolean isOnline;
    // endregion

    // inflation logic
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_users_list, parent, false);
        bindUIElements(v);
        
        lvUsers.setAdapter(aUsers);

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
        users = new ArrayList<>();
        aUsers = new UsersArrayAdapter(getActivity(), users);
        isOnline = Helpers.iAmOnline(getActivity());
    }

    private void bindUIElements(View v) {
        lvUsers = (ListView) v.findViewById(R.id.lvUsers);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
    }

    public void addAll(List<User> users) {
        aUsers.addAll(users);
    }

    public void replaceAll(List<User> users) {
        aUsers.clear();
        addAll(users);
    }
}
