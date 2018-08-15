package nettv.com.np.parselibrary.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("UserMessages")
public class UserMessage extends ParseObject {

    private String serial;
    private String title;
    private String message;
    private String type;
    private String action;
    private String link;
    private String options;

    public static final String KEY_SERIAL = "serial";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TYPE = "type";
    public static final String KEY_TYPE_REMOTE = "remote";

    public static final String KEY_ACTION = "action";
    public static final String KEY_LINK = "link";
    public static final String KEY_OPTIONS = "options";




    public UserMessage() {
        super();
    }

    public UserMessage( String serial, String title, String message, String type, String action, String link, String options) {
        setSerial(serial);
        setTitle(title);
        setMessage(message);
        setAction(action);
        setLink(link);
        setOptions(options);
        this.serial = serial;
        this.title = title;
        this.message = message;
        this.action = action;
        this.link = link;
        this.options = options;
    }

    public String getType() {
        return getString(KEY_TYPE);
    }

    public void setType(String type) {
        this.type = type;
        put(KEY_TYPE, type);
    }

    public String getSerial() {
        return getString(KEY_SERIAL);
    }

    public void setSerial(String serial) {
        this.serial = serial;
        put(KEY_SERIAL, serial);
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        this.title = title;
        put(KEY_TITLE, title);
    }

    public String getMessage() {
        return getString(KEY_MESSAGE);
    }

    public void setMessage(String message) {
        this.message = message;
        put(KEY_MESSAGE, message);
    }

    public String getAction() {
        return getString(KEY_ACTION);
    }

    public void setAction(String action) {
        this.action = action;
        put(KEY_ACTION, action);
    }

    public String getLink() {
        return getString(KEY_LINK);
    }

    public void setLink(String link) {
        this.link = link;
        put(KEY_LINK, link);
    }

    public String getOptions() {
        return getString(KEY_OPTIONS);
    }

    public void setOptions(String options) {
        this.options = options;
        put(KEY_OPTIONS, options);
    }
}