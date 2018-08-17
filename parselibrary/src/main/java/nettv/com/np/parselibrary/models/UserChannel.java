package nettv.com.np.parselibrary.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("UserChannels")
public class UserChannel extends ParseObject{

    private int id;
    private String name;
    private String logo;
    private int type;
    private int number;
    private String serial;

    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_LOGO = "logo";
    public static final String KEY_TYPE = "type";
    public static final String KEY_NUMBER = "number";
    public static final String KEY_SERIAL = "serial";

    public enum Type {
        FAVORITE, RECENT
    }

    public UserChannel() {
        super();
    }

    public UserChannel(int id, String name, String logo, int number, String serial) {
        super();

        setId(id);
        setName(name);
        setLogo(logo);
        setNumber(number);
        setSerial(serial);

        this.id = id;
        this.name = name;
        this.logo = logo;
        this.number = number;
        this.serial = serial;
    }

    public String getSerial() {
        return getString(KEY_SERIAL);
    }

    public void setSerial(String serial) {
        this.serial = serial;
        put(KEY_SERIAL, serial);
    }

    public int getId() {
        return getInt(KEY_ID);
    }

    public void setId(int id) {
        this.id = id;
        put(KEY_ID, id);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        this.name = name;
        put(KEY_NAME, name);
    }

    public String getLogo() {
        return getString(KEY_LOGO);
    }

    public void setLogo(String logo) {
        this.logo = logo;
        put(KEY_LOGO, logo);
    }

    public int getType() {
        //return type;
        return getInt(KEY_TYPE);
    }

    public void setType(Type type) {
        if (type == Type.FAVORITE) {
            put(KEY_TYPE, 0);
        } else {
            put(KEY_TYPE, 1);
        }

    }

    public int getNumber() {
        return getInt(KEY_NUMBER);
    }

    public void setNumber(int number) {
        this.number = number;
        put(KEY_NUMBER, number);
    }

}