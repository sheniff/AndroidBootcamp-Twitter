package com.codepath.apps.twitterapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@Table(name = "Users")
public class User extends Model implements Parcelable {
    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long uid;
    @Column(name = "Name")
    private String name;
    @Column(name = "ProfileImageUrl")
    private String profileImageUrl;
    @Column(name = "ScreenName")
    private String screenName;

    public User() {
        super();
    }

    public User(Parcel in) {
        this.uid = in.readLong();
        this.name = in.readString();
        this.profileImageUrl = in.readString();
        this.screenName = in.readString();
    }

    public static User fromJSON(JSONObject jsonObject) {
        User user = new User();

        try {
            user.name = jsonObject.getString("name");
            user.profileImageUrl = jsonObject.getString("profile_image_url");
            user.uid = jsonObject.getLong("id");
            user.screenName = jsonObject.getString("screen_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

    public String getName() {
        return name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getScreenName() {
        return screenName;
    }

    public long getUid() {
        return uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    // ORM methods
    public List<Tweet> tweets() {
        return getMany(Tweet.class, "User");
    }

    // Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(uid);
        dest.writeString(name);
        dest.writeString(profileImageUrl);
        dest.writeString(screenName);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[0];
        }
    };
}
