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

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterActivity extends Activity {
    private EditText uName;
    private EditText uPwd;
    private EditText uPhone;
    private Button regist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        uName=(EditText)findViewById(R.id.name);
        uPwd=(EditText)findViewById(R.id.pwd);
        uPhone=(EditText)findViewById(R.id.phone);

        final String name = uName.getText().toString();
        final String pwd = uPwd.getText().toString();
        final String phone = uPhone.getText().toString();

        regist=(Button)findViewById(R.id.registe);
        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);

                if(name=="" ||pwd==""||phone=="") {
                    Toast.makeText(getApplicationContext(), "请确保您的信息不为空", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(checkPhone(phone)) {
                        new Thread(){
                            @Override
                            public void run() {
                                //传将数据发送到服务器
                                String result = NetworkFunction.ConnectServer("http://192.168.1.108/MobileHealth/user/add.action",
                                        new String[]{ "password", "userName", "phoneNum"}, new String[]{pwd, name, phone});

                                    //解析返回的json数据
                                    try {
                                        JSONObject object=new JSONObject(result);
                                        String info=object.getString("register");
                                        if (info=="true"){
                                            Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            intent.putExtra("login", phone);
                                            startActivity(intent);
                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                            }
                        };
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //检查手机号
    public boolean checkPhone(String phone){
        Pattern pattern = Pattern.compile("^13/d{9}||15[8,9]/d{8}$");
        Matcher matcher = pattern.matcher(phone);
        if (matcher.matches()) {
            return true;
        }
        else {
            return false;
        }
    }
    /*//检查密码
    public boolean checkName(String name){
        Pattern pattern = Pattern.compile("^[0-9A-Za-z]{6,}$");
        Matcher matcher = pattern.matcher(name);
        if (matcher.matches()) {
            return true;
        }
        else {
            return false;
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
