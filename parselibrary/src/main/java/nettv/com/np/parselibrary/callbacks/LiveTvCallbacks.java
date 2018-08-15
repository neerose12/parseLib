package nettv.com.np.parselibrary.callbacks;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.livequery.SubscriptionHandling;

import java.util.List;

import nettv.com.np.parselibrary.models.UserChannel;

public class LiveTvCallbacks {


    public interface OnFavoriteGetListener {
        void onFavoriteGet(List<UserChannel> channels, ParseException e);
    }

    public interface OnRecentGetListener {
        void onRecentGet(List<UserChannel> channels, ParseException e);
    }

    public interface OnFavoriteSetListener {
        void onFavoriteSet(boolean favoriteStatus, ParseException e);
    }

    public interface OnRecentSetListener {
        void onRecentSet(boolean recentStatus, ParseException e);
    }

    public interface OnFavoriteDeleteListener {
        void onFavoriteDelete(boolean favoriteDeleteStatus, ParseException e);
    }

    public interface OnRecentDeleteListener {
        void onRecentDelete(boolean deleteStatus, ParseException e);
    }

}
