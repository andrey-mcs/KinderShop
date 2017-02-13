package ua.com.kindershop.ui.activity;

import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;

import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ua.com.kindershop.api.Authenticate;
import ua.com.kindershop.parser.CategoryAdapter;
import ua.com.kindershop.parser.JSONParser;
import ua.com.kindershop.ui.R;
import ua.com.kindershop.application.ShopInstance;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{
    LinearLayout hlayout;
    public ArrayList<HashMap<String, String>> productsList;
    ListView myList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.home_screen);
        productsList = new ArrayList<HashMap<String, String>>();
        myList = (ListView)findViewById(R.id.listhome);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                 LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        hlayout = (LinearLayout) findViewById(R.id.hlayout);
        final TextView txt_new = new TextView(getBaseContext());
        txt_new.setId(R.id.edit_home);
        txt_new.setTextColor(getResources().getColor(R.color.txt_bg));
        hlayout.addView(txt_new, lparams);

        Button btn_new = new Button(getBaseContext());
        btn_new.setText(R.string.logout);
        btn_new.setId(R.id.btn_logout);
        btn_new.setOnClickListener(this);
        hlayout.addView(btn_new, lparams);
        //JSONParser jsonparser = new JSONParser();
        new Network().execute();
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
                        // Запускаем новый intent который покажет нам Activity
                        Intent in = new Intent(getApplicationContext(), CategoryActivity.class);
                        // отправляем pid в следующий activity
                        in.putExtra("id", pid);
                        in.putExtra("subcat", subcat);
                        startActivity(in);
                        // запуская новый Activity ожидаем ответ обратно
                        //startActivityForResult(in, 100);
                    }
                }
        );

    }


    public void onBackPressed() {
    /*    new AlertDialog.Builder(this)
                .setTitle("Выйти из приложения?")
                .setMessage("Вы действительно хотите выйти?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //SomeActivity - имя класса Activity для которой переопределяем onBackPressed();

                    }

                });
    */
    }

    public void onClick(View v){
        if (v.getId() == R.id.btn_logout){
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            ShopInstance.getInstance().setHashCode(null);
            finish();
            startActivity(i);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    class Network extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;
        String Request;

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HomeActivity.this);
            pDialog.setMessage("Получение данных с сервера. Подождите...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            String Kmsurl = getResources().getString(R.string.url_kms);
            String Baseurl = getString(R.string.url_base);
            String FullUrl = Baseurl+Kmsurl;
            String HashCode = ShopInstance.getInstance().getHashCode();
            Request = Authenticate.getCategories(FullUrl, "GET", HashCode);
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
                    JSONArray categories = RequestJSON.getJSONArray("category");
                    for (int i = 0; i < categories.length(); i++) {
                        JSONObject c = categories.getJSONObject(i);
                                // Сохраняем каждый json елемент в переменную
                        String id = c.getString("category_id");
                        String name = c.getString("name");
                        String uri = c.getString("image");
                        String subcatC = c.getString("sub_count");
                        HashMap<String, String> map = new HashMap<String, String>();
                        //String uri_full  = Baseurl + uri;
                        // добавляем каждый елемент в HashMap ключ => значение
                        map.put("id", id);
                        map.put("uri",uri);
                        map.put("name", name);
                        map.put("subcat", subcatC);
                        // добавляем HashList в ArrayList
                        productsList.add(map);
                    }
                    return null;
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
            if (res == null) {
                // обновляем UI форму в фоновом потоке
                runOnUiThread(new Runnable() {
                    public void run() {
                        /**
                         * Обновляем распарсенные JSON данные в ListView
                         * */
                        CategoryAdapter adapter = new CategoryAdapter(
                                HomeActivity.this, productsList,
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
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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
