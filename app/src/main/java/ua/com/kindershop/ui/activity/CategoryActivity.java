package ua.com.kindershop.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ua.com.kindershop.api.Authenticate;
import ua.com.kindershop.parser.CategoryAdapter;
import ua.com.kindershop.parser.JSONParser;
import ua.com.kindershop.parser.ProductsAdapter;
import ua.com.kindershop.ui.R;
import ua.com.kindershop.application.ShopInstance;

/**
 * Created by andrey on 30.08.15.
 */
public class CategoryActivity extends AppCompatActivity {
    LinearLayout hlayout;
    public ArrayList<HashMap<String, String>> productsList;
    ListView myList;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        productsList = new ArrayList<HashMap<String, String>>();
        myList = (ListView)findViewById(R.id.listhome);
        Intent i = getIntent();
        String id = i.getStringExtra("id");
        String subcat = i.getStringExtra("subcat");
        if (!(subcat.equals("-1"))) new Network().execute(id, subcat);

            myList.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            // getting values from selected ListItem
                            String pid = ((TextView) view.findViewById(R.id.pid)).getText()
                                    .toString();
                            String subcat = ((TextView) view.findViewById(R.id.subcat)).getText()
                                    .toString();

                            if (subcat.equals("-1")) {
                                Intent in = new Intent(getApplicationContext(), ProductActivity.class);
                                // отправляем pid в следующий activity
                                in.putExtra("id", pid);
                                startActivity(in);
                            } else {
                                // Запускаем новый intent который покажет нам Activity
                                Intent in = new Intent(getApplicationContext(), CategoryActivity.class);
                                // отправляем pid в следующий activity
                                in.putExtra("id", pid);

                                in.putExtra("subcat", subcat);
                                startActivity(in);
                            }
                            // запуская новый Activity ожидаем ответ обратно
                            //startActivityForResult(in, 100);
                        }

                    }
            );

    }



    class Network extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;
        String Request;

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CategoryActivity.this);
            pDialog.setMessage("Получение данных с сервера. Подождите...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            String id = args[0];
            String subcat = args[1];
            String Kmsurl = getResources().getString(R.string.url_kms);
            String Baseurl = getString(R.string.url_base);
            String FullUrl = Baseurl+Kmsurl;
            String HashCode = ShopInstance.getInstance().getHashCode();
            if (!(subcat.equals("0"))) {
                Request = Authenticate.getCategories(FullUrl, "GET", HashCode, id);
                JSONObject RequestJSON = JSONParser.ParseJson(Request);
                try {
                    int success = RequestJSON.getInt("response");
                    if (success == 0) {
                        JSONArray categories = RequestJSON.getJSONArray("category");
                        for (int i = 0; i < categories.length(); i++) {
                            JSONObject c = categories.getJSONObject(i);

                            // Сохраняем каждый json елемент в переменную
                            String idC = c.getString("category_id");
                            String name = c.getString("name");
                            String uri = c.getString("image");
                            String subcatC = c.getString("sub_count");
                            HashMap<String, String> map = new HashMap<String, String>();
                            //String uri_full = Baseurl + uri;
                            // добавляем каждый елемент в HashMap ключ => значение
                            map.put("id", idC);
                            map.put("uri", uri);
                            map.put("name", name);
                            map.put("subcat", subcatC);

                            // добавляем HashList в ArrayList
                            productsList.add(map);
                        }
                        return "categories";
                    } else return "error";
                }
                catch (Exception e){
                    return "error";
                }
            }
            else
            {
                Request = Authenticate.getProducts(FullUrl, "GET", HashCode, id);
                JSONObject RequestJSON = JSONParser.ParseJson(Request);
                try {
                    // PEREDELAT' ZAVTRA!!!
                    int success = RequestJSON.getInt("response");
                    if (success == 0) {
                        JSONArray products = RequestJSON.getJSONArray("product");
                        for (int i = 0; i < products.length(); i++) {
                            JSONObject c = products.getJSONObject(i);
                            // Сохраняем каждый json елемент в переменную
                            String idC = c.getString("product_id");
                            String name = c.getString("name");
                            String uri = c.getString("image");
                            String price = c.getString("price");
                            //String subcatC = c.getString("sub_count");
                            HashMap<String, String> map = new HashMap<String, String>();
                            //String uri_full = Baseurl + uri;
                            // добавляем каждый елемент в HashMap ключ => значение
                            map.put("id", idC);
                            map.put("uri", uri);
                            map.put("name", name);
                            map.put("subcat", "-1");
                            map.put("price", price);

                            // добавляем HashList в ArrayList
                            productsList.add(map);
                        }
                        return "products";
                    } else return "error";
                }
                catch (Exception e){
                    return "error";
                }
            }

        }

        protected void onPostExecute(String res) {
	pDialog.dismiss();            
	if (res.equals("products")) {

                // обновляем UI форму в фоновом потоке
                runOnUiThread(new Runnable() {
                    public void run() {
                        /**
                         * Обновляем распарсенные JSON данные в ListView
                         * */
                        ProductsAdapter adapter = new ProductsAdapter(
                                CategoryActivity.this, productsList,
                                R.layout.list_item_product, new String[] { "id","uri",
                                "name", "subcat", "price"},
                                new int[] { R.id.pid,R.id.cat_image, R.id.name , R.id.subcat, R.id.list_price});
                        // обновляем listview

                        myList.setAdapter(adapter);

                        //Intent i = getIntent();
                        //    finish();
                        //   startActivity(i);
                    }
                });
            }
            else if (res.equals("categories")) {

                // обновляем UI форму в фоновом потоке
                runOnUiThread(new Runnable() {
                    public void run() {
                        /**
                         * Обновляем распарсенные JSON данные в ListView
                         * */

                        CategoryAdapter adapter = new CategoryAdapter(
                                CategoryActivity.this, productsList,
                                R.layout.list_item_caterory, new String[] { "id","uri",
                                "name", "subcat"},
                                new int[] { R.id.pid,R.id.cat_image, R.id.name , R.id.subcat});
                        // обновляем listview

                        myList.setAdapter(adapter);

                        //Intent i = getIntent();
                        //    finish();
                        //   startActivity(i);
                    }
                });
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(CategoryActivity.this);
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
