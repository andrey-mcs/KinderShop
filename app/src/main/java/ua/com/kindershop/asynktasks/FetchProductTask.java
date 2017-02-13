package ua.com.kindershop.asynktasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.HashMap;

import ua.com.kindershop.model.Category;
import ua.com.kindershop.model.Product;
import ua.com.kindershop.parser.CategoryAdapter;
import ua.com.kindershop.parser.ProductsAdapter;

public class FetchProductTask extends AsyncTask<Void, Void, Product> {
    private Activity activity;
    private ProductsAdapter adapter;
    private int position;
    private String Name;
    private String Id;
    private String Subcat;
    private ImageView image;
    private String Image;
    private String id;
    private String Price;
    private TextView VName;
    private TextView VPrice;
    private TextView VId;
    private TextView VSubcat;

    /**
     * Get necessary UI objects.
     */
    public FetchProductTask(Context context, ProductsAdapter adapter, int position,
                            HashMap<String, Object> data,
                            ImageView image, TextView VName, TextView VId, TextView VSubcat, TextView VPrice) {
        this.activity = activity;
        this.adapter = adapter;
        this.position = position;
        this.Name = (String)data.get("name");
        this.Id = (String)data.get("id");
        this.Subcat = (String)data.get("subcat");
        this.Image = (String)data.get("uri");
        this.Price = (String)data.get("price");
        this.image = image;
        this.VName = VName;
        this.VSubcat = VSubcat;
        this.VId = VId;
        this.VPrice = VPrice;
    }

    /**
     * Background bitmap fetching and pasting it into FbUser.
     */
    @Override
    protected Product doInBackground(Void... params) {
        Product Product = new Product(Id, Name, Subcat, Image, Price);

        Bitmap bitmap = null;
        try {
            HttpURLConnection httpUrlConnection;
            httpUrlConnection = (HttpURLConnection) new java.net.URL(
                    Product.getPicture()).openConnection();
            httpUrlConnection.setReadTimeout(10000);
            httpUrlConnection.setConnectTimeout(10000);
            InputStream inputStream = httpUrlConnection.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(
                    inputStream);
            bitmap = BitmapFactory.decodeStream(bufferedInputStream);
            bufferedInputStream.close();
            inputStream.close();
            httpUrlConnection.disconnect();

        } catch (MalformedURLException e) {

        } catch (IOException e) {
        }

        Product.setBitmap(bitmap);

        return Product;
    }

    /**
     * Setting the UI: hide progress bar, display both images (userpic on the
     * left and tiny icon on the right) and user's full name
     */
    @Override
    protected void onPostExecute(Product result) {
        super.onPostExecute(result);
        adapter.getProducts()[position] = result; // caching current user by it's position
        VName.setText(result.getName());
        VPrice.setText(result.getPrice());
        VId.setText(result.getId());
        VSubcat.setText(result.getSubcat());
        image.setImageBitmap(result.getBitmap());
    }
}