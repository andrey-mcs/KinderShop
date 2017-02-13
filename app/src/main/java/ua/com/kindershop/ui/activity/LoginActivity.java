package ua.com.kindershop.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import ua.com.kindershop.api.Authenticate;
import ua.com.kindershop.ui.R;
import ua.com.kindershop.application.ShopInstance;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    protected void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();
        if (i.getStringExtra("exit") == "1"){
            super.onBackPressed();
        };
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Button btnLogin = (Button)findViewById(R.id.login_button);
        btnLogin.setOnClickListener(this);
        new Authentication().execute("kms", "KMS1!");

    }
    public void onClick(View v){
        if (v.getId() == R.id.login_button){
            EditText VL = (EditText)findViewById(R.id.login_user);
            String Login = VL.getText().toString();
            EditText VP = (EditText)findViewById(R.id.login_pass);
            String Pass = VP.getText().toString();
            new Authentication().execute(Login, Pass);
        }
    }



    class Authentication extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;
        String HashCode;

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Авторизация. Подождите...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            String Login = args[0];
            String Pass = args[1];
            String url = "http://kindershop.com.ua/export/kms.php";
            HashCode = Authenticate.auth(url, "GET", Login, Pass);
            if ((HashCode != null) && (!(HashCode.equals("")))) {
                ShopInstance sh = ShopInstance.getInstance();
                sh.setHashCode(HashCode);
            }
            return null;
        }


        protected void onPostExecute(String res) {
            // закрываем прогресс диалог после получение все продуктов
            pDialog.dismiss();
            if ((HashCode != null) && (!(HashCode.equals("")))) {
                // обновляем UI форму в фоновом потоке
                runOnUiThread(new Runnable() {
                    public void run() {
                        /**
                         * Обновляем распарсенные JSON данные в ListView
                         * */
                        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(i);
                    }
                });
            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Ошибка авторизации!")
                        .setMessage("Проверьте введенные данные и попробуйте снова!")
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
