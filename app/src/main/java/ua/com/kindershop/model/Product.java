package ua.com.kindershop.model;

import android.graphics.Bitmap;

public class Product {
    private String Name;
    private String id;
    private String subcat;
    private String url_image;
    private Bitmap image;
    private String price;

    public Product(String id, String name, String subcat, String image, String price){
        this.id = id;
        this.Name = name;
        this.subcat = subcat;
        this.url_image = image;
        this.price = price;
        this.subcat = "-1";
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
    public String getPrice(){
        return price;
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
