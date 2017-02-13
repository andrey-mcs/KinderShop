package ua.com.kindershop.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import ua.com.kindershop.api.Authenticate;
import ua.com.kindershop.asynktasks.DownloadTask;
import ua.com.kindershop.parser.JSONParser;
import ua.com.kindershop.ui.R;
import ua.com.kindershop.application.ShopInstance;

/**
 * Created by andrey on 03.09.15.
 */
public class ProductActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_product);
        Intent i = getIntent();
        String id = i.getStringExtra("id");
        new Network().execute(id);
    }



    class Network extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;
        String Request;
        HashMap<String, String> ProductIfo;
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ProductActivity.this);
            pDialog.setMessage("Загрузка карточки товара. Подождите...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            String id = args[0];
            String Kmsurl = getResources().getString(R.string.url_kms);
            String Baseurl = getString(R.string.url_base);
            String FullUrl = Baseurl+Kmsurl;
            String HashCode = ShopInstance.getInstance().getHashCode();
            Request = Authenticate.getProduct(FullUrl, "GET", HashCode,id);
            if (Request.equals("failureSession error")) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(i);
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Пожалуйста залогиньтесь снова!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
            JSONObject RequestJSON = JSONParser.ParseJson(Request);
            try

            {
                int success = RequestJSON.getInt("response");
                if (success == 0) {
                    ProductIfo = new HashMap<String, String>();
                    JSONArray products = RequestJSON.getJSONArray("product");
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);
                        String idC = c.getString("product_id");
                        String name = c.getString("name");
                        String uri = c.getString("image");
                        String epac = c.getString("epac");
                        String price = c.getString("price");
                        ProductIfo.put("id", idC);
                        ProductIfo.put("name", name);
                        ProductIfo.put("uri", uri);
                        ProductIfo.put("epac", epac);
                        ProductIfo.put("price", price);
                    }
                    return "success";
                }
                else return "error";
            }
            catch(Exception e)
            {
                return "error";
            }
        }

        protected void onPostExecute(String res) {
            pDialog.dismiss();
            if (res.equals("success")) {
                // обновляем UI форму в фоновом потоке
                runOnUiThread(new Runnable() {
                    public void run() {
                        String idC = ProductIfo.get("id");
                        String name = ProductIfo.get("name");
                        String uri = ProductIfo.get("uri");
                        String epac = ProductIfo.get("epac");
                        String price = ProductIfo.get("price");
                        TextView VN = (TextView)findViewById(R.id.name_product_item_product);
                        VN.setText(name);
                        TextView VI = (TextView)findViewById(R.id.id_product_item_product);
                        VI.setText(idC);
                        TextView VC = (TextView)findViewById(R.id.code_product_item_product);
                        VC.setText(epac);
                        TextView VP = (TextView)findViewById(R.id.price_product_item_product);
                        VP.setText(price);
                        ImageView IV = (ImageView)findViewById(R.id.image_product_item_product);
                        new DownloadTask(IV).execute(uri);
                    }
                });
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductActivity.this);
                builder.setTitle("Отсуствует соединение с сервером").
                        setMessage("Проверьте подключение к интернету и попробуйте снова!")
                        .setCancelable(false)
                        .setNegativeButton("ОК",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }

    }



}
