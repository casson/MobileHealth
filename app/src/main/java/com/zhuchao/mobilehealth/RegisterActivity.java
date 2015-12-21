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

        final String id="001";
        final String name = uName.getText().toString();
        final String pwd = uPwd.getText().toString();
        final String phone = uPhone.getText().toString();

        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.equals("") ||pwd.equals("")||phone.equals("")) {
                    Toast.makeText(getApplicationContext(), "������������Ϣ", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(checkPhone(phone)&&checkName(name)&&(name.length()<=20)) {
                        //��ע��ʱ��д�����ݷ��͵���������
                        String result = NetworkFunction.ConnectServer("http://123.56.85.58:8080/MobileHealth/user/add.action",
                                new String[]{"userId", "password", "userName", "phoneNumber"}, new String[]{id, pwd, name, phone});
                        if (result.equals("true")) {
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.putExtra("login", phone);
                            startActivity(intent);
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "�������������", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //�жϵ绰�����Ƿ���ȷ
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
    //�ж��û����Ƿ�Ϊ������λ�������ֺ���ĸ���
    public boolean checkName(String name){
        Pattern pattern = Pattern.compile("^[0-9A-Za-z]{6,}$");
        Matcher matcher = pattern.matcher(name);
        if (matcher.matches()) {
            return true;
        }
        else {
            return false;
        }
    }

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
