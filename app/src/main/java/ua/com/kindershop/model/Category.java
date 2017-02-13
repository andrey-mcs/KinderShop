package ua.com.kindershop.model;


import android.graphics.Bitmap;

public class Category {
    private String Name;
    private String id;
    private String subcat;
    private String url_image;
    private Bitmap image;

    public Category(String id, String name, String subcat, String image){
        this.id = id;
        this.Name = name;
        this.subcat = subcat;
        this.url_image = image;
    }

    public String getPicture(){
        return url_image;
    }
    public String getName(){
        return Name;
    }
    public String getId(){
        return id;
    }
    public String getSubcat(){
        return subcat;
    }
    public void setBitmap(Bitmap image){
        this.image = image;
    }
    public Bitmap getBitmap(){
        return image;
    }
}
