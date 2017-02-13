package ua.com.kindershop.application;

/**
 * Created by andrey on 28.08.15.
 */
public class ShopInstance {
    private static ShopInstance SInstance;
    private String HashCode;

    public ShopInstance(){
        HashCode = null;
    }

    public static void initInstance() {
        if (SInstance == null) {
            SInstance = new ShopInstance();
        }
    }

    public static ShopInstance getInstance() {
        if (SInstance == null) {
            SInstance = new ShopInstance();
        }
        return SInstance;
    }

    public String getHashCode() {
        return HashCode;
    }

    public void setHashCode(String var) {
        HashCode = var;
    }

}
