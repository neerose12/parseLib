package nettv.com.np.parselibrary.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("UserData")
public class UserData extends ParseObject {

    private String mac;
    private String serial;
    private ParseRelation<UserChannel> channels;


    private List<Integer> favoriteChannels;
    private List<Integer> recentChannels;

    private int favoriteChannelsLimit;
    private int recentChannelsLimit;

    public static final String KEY_FAVORITE_CHANNELS = "fav_channels";
    public static final String KEY_RECENT_CHANNELS = "rec_channels";
    public static final String KEY_FAVORITE_CHANNELS_LIMIT = "fav_channels_limit";
    public static final String KEY_RECENT_CHANNELS_LIMIT = "rec_channels_limit";
    public static final String KEY_MAC = "mac";

    public static final String KEY_SERIAL = "serial";
    public static final String KEY_CHANNELS = "channels";


    public UserData() {
        super();
    }

    public String getSerial() {
        return getString(KEY_SERIAL);
    }

    public void setSerial(String serial) {
        this.serial = serial;
        put(KEY_SERIAL, serial);
    }

    public ParseRelation<UserChannel> getChannels() {
        return getRelation(KEY_CHANNELS);
    }

    public void setChannels(ParseRelation<UserChannel> channels) {
        this.channels = channels;
        put(KEY_CHANNELS, channels);
    }



    public int getFavoriteChannelsLimit() {
        return getInt(KEY_FAVORITE_CHANNELS_LIMIT);
    }

    public void setFavoriteChannelsLimit(int favoriteChannelsLimit) {
        this.favoriteChannelsLimit = favoriteChannelsLimit;
        put(KEY_FAVORITE_CHANNELS_LIMIT, favoriteChannelsLimit);
    }

    public int getRecentChannelsLimit() {
        return getInt(KEY_RECENT_CHANNELS_LIMIT);
    }

    public void setRecentChannelsLimit(int recentChannelsLimit) {
        this.recentChannelsLimit = recentChannelsLimit;
        put(KEY_RECENT_CHANNELS_LIMIT, recentChannelsLimit);
    }

    public void setMac(String mac) {
        put(KEY_MAC, mac);
    }

    public void setFavoriteChannels(List<Integer> channels) {
        put(KEY_FAVORITE_CHANNELS, channels);
    }

    public void setRecentChannels(List<Integer> channels) {
        put(KEY_RECENT_CHANNELS, channels);
    }

    public List<Integer> getFavoriteChannels() {
        Object favorite = get(KEY_FAVORITE_CHANNELS);
        if (favorite == null){
            return new ArrayList<Integer>();
        } else {
            return (List<Integer>)favorite;
        }

    }

    public List<Integer> getRecentChannels() {
        Object recent = get(KEY_RECENT_CHANNELS);
        if (recent == null){
            return new ArrayList<Integer>();
        } else {
            return (List<Integer>)recent;
        }
    }

}
