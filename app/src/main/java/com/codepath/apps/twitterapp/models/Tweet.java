package com.codepath.apps.twitterapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Table(name = "Tweets")
public class Tweet extends Model implements Parcelable {
    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long uid;
    @Column(name = "Body")
    private String body;
    @Column(name = "User", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private User user;
    @Column(name = "CreatedAt")
    private String createdAt;
    @Column(name = "Favorited")
    private boolean favorited;
    @Column(name = "Retweeted")
    private boolean retweeted;

    private ArrayList<String> media;

    public Tweet() {
        super();
    }

    public Tweet(long uid, String body, User user, String createdAt) {
        super();
        this.uid = uid;
        this.body = body;
        this.user = user;
        this.createdAt = createdAt;
    }

    public Tweet(Parcel in) {
        this.uid = in.readLong();
        this.body = in.readString();
        this.createdAt = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.media = in.readArrayList(String.class.getClassLoader());
        this.favorited = in.readInt() == 1;
        this.retweeted = in.readInt() == 1;
    }

    // Deserialize JSON
    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();

        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            tweet.createdAt = jsonObject.getString("created_at");

            // in case there is media for this tweet
            tweet.media = new ArrayList<>();

            if(jsonObject.has("entities") && jsonObject.getJSONObject("entities").has("media")) {
                JSONArray media = jsonObject.getJSONObject("entities").getJSONArray("media");
                for(int i = 0; i < media.length(); i++) {
                    tweet.media.add(media.getJSONObject(i).getString("media_url"));
                }
            }
            
            tweet.favorited = jsonObject.getBoolean("favorited");
            tweet.retweeted = jsonObject.getBoolean("retweeted");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); i++) {
            try {
                Tweet tweet = Tweet.fromJSON(jsonArray.getJSONObject(i));
                if(tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        return tweets;
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public User getUser() {
        return user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public ArrayList<String> getMedia() {
        return media;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    // ORM methods

    public static List<Tweet> getAll() {
        return new Select()
                .from(Tweet.class)
                .orderBy("CreatedAt DESC")
                .execute();
    }

    public static int countAll() {
        return new Select()
                .from(Tweet.class)
                .count();
    }

    // Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(uid);
        dest.writeString(body);
        dest.writeString(createdAt);
        dest.writeParcelable(user, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeList(media);
        dest.writeInt(favorited ? 1 : 0);
        dest.writeInt(retweeted ? 1 : 0);
    }

    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[0];
        }
    };
}
