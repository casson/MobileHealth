package com.zhuchao.mobilehealth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zhuchao.http.NetworkFunction;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity {

    private EditText phone;
    private EditText pwd;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phone = (EditText) findViewById(R.id.user_name);
        pwd = (EditText) findViewById(R.id.user_pwd);
        final String name = phone.getText().toString();
        final String password = pwd.getText().toString();

        Intent intent = getIntent();
        final String phone = intent.getStringExtra("login");

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                if (phone.equals("") || password.equals("")) {
                    Toast.makeText(getApplicationContext(), "请确保信息不为空", Toast.LENGTH_SHORT).show();
                } else{
                    new Thread(){
                        @Override
                        public void run() {
                            //传将数据发送到服务器
                            String result = NetworkFunction.ConnectServer("http://192.168.202.103/MobileHealth/user/add.action",
                                    new String[]{ "password", "phoneNum"}, new String[]{password,phone});

                            //解析返回的json数据
                            try {
                                JSONObject object=new JSONObject(result);
                                String info=object.getString("login");
                                if (info=="true"){
                                    Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
}