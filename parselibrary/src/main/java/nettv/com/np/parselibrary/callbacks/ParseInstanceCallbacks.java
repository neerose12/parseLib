package nettv.com.np.parselibrary.callbacks;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.SubscriptionHandling;

public class ParseInstanceCallbacks {

    public interface OnLoginListener{
        void onLogin(boolean loginStatus, ParseUser parseUser, ParseException e);
    }

    public interface OnLiveQueryResponseListener {
        void onLiveQueryResponse(Class<?> subclass, ParseQuery<?> parseQuery, SubscriptionHandling.Event event, Object model);
    }
}
