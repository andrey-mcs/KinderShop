package ua.com.kindershop.asynktasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.HashMap;

import ua.com.kindershop.model.Category;
import ua.com.kindershop.parser.CategoryAdapter;
import ua.com.kindershop.ui.R;

public class FetchCategoryTask extends AsyncTask<Void, Void, Category> {
    private Activity activity;
    private CategoryAdapter adapter;
    private int position;
    private String Name;
    private String Id;
    private String Subcat;
    private ImageView image;
    private String Image;
    private String id;
    private TextView VName;
    private TextView VId;
    private TextView VSubcat;

    /**
     * Get necessary UI objects.
     */
    public FetchCategoryTask(Context context, CategoryAdapter adapter, int position,
                             HashMap<String, Object> data, ImageView image, TextView VName, TextView VId, TextView VSubcat) {
        this.activity = activity;
        this.adapter = adapter;
        this.position = position;
        this.Name = (String)data.get("name");
        this.Id = (String)data.get("id");
        this.Subcat = (String)data.get("subcat");
        this.Image = (String)data.get("uri");
        this.image = image;
        this.VName = VName;
        this.VSubcat = VSubcat;
        this.VId = VId;
    }

    /**
     * Background bitmap fetching and pasting it into FbUser.
     */
    @Override
    protected Category doInBackground(Void... params) {
        Category Category = new Category(Id, Name, Subcat, Image);

        Bitmap bitmap = null;
        try {
            HttpURLConnection httpUrlConnection;
            httpUrlConnection = (HttpURLConnection) new java.net.URL(
                    Category.getPicture()).openConnection();
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

        Category.setBitmap(bitmap);

        return Category;
    }

    /**
     * Setting the UI: hide progress bar, display both images (userpic on the
     * left and tiny icon on the right) and user's full name
     */
    @Override
    protected void onPostExecute(Category result) {
        super.onPostExecute(result);
        adapter.getCategories()[position] = result; // caching current user by it's position
        VName.setText(result.getName());
        VId.setText(result.getId());
        VSubcat.setText(result.getSubcat());
        image.setImageBitmap(result.getBitmap());
    }
}