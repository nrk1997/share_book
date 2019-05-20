package com.example.asus.tushugongxiang;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Enrol extends AppCompatActivity {
    Context context=null;
    EditText userNameEditView;
    EditText userPasswordEditView,userPasswordEditView2;
    String userNameString;
    String userPasswordString;
    List<Map<String,Object>> list;
    int result;

    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrol);
        context=Enrol.this;
        ////////////////////////////////////////////////////////////////////////////////////////////文本框界面初始化
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        GridLayout gridLayout=(GridLayout)findViewById(R.id.enrolGridLayout) ;
        Intent intent=getIntent();
        TextView nullTextView=new TextView(this);
        nullTextView.setText("");
        nullTextView.setTextSize(30f);
        gridLayout.addView(nullTextView);
        ImageView titleImageView=new ImageView(this);
        titleImageView.setImageResource(R.drawable.picture1);
        gridLayout.addView(titleImageView);
        TextView nullTextView2=new TextView(this);
        nullTextView2.setText("");
        nullTextView2.setTextSize(50f);
        gridLayout.addView(nullTextView2);
        TextView userNameTextView=new TextView(this);
        MainActivity.setView(userNameTextView,"用户名",width*3/5,28,0);
        gridLayout.addView(userNameTextView);
        userNameEditView=new EditText(this);
        userNameEditView.setSingleLine(true);
        userNameEditView.setHint("长度为1至6位");
        MainActivity.setView(userNameEditView,null,width*3/5,28,0);
        gridLayout.addView(userNameEditView);
        TextView userPasswordTextView=new TextView(this);
        MainActivity.setView(userPasswordTextView,"密码",width*3/5,28,0);
        gridLayout.addView(userPasswordTextView);
        userPasswordEditView=new EditText(this);
        userPasswordEditView.setHint("6位至20位");
        userPasswordEditView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        //userPasswordEditView.
        MainActivity.setView(userPasswordEditView,null,width*3/5,28,0);
        gridLayout.addView(userPasswordEditView);
        TextView userPasswordTextView2=new TextView(this);
        MainActivity.setView(userPasswordTextView2,"密码",width*3/5,28,0);
        gridLayout.addView(userPasswordTextView2);
        userPasswordEditView2=new EditText(this);
        userPasswordEditView2.setHint("请再输入密码");
        userPasswordEditView2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        //userPasswordEditView.
        MainActivity.setView(userPasswordEditView2,null,width*3/5,28,0);
        gridLayout.addView(userPasswordEditView2);

        ////////////////////////////////////////////////////////////////////////////////////////////按钮设置
        MyButton enrolButton=new MyButton(this);
        ((MyButton) enrolButton).setButton("#664488ee","#667799ff","#ffffff",null,true,8);
        MainActivity.setView(enrolButton,"注册",width*3/5,28,0);

        gridLayout.addView(enrolButton);
        MyButton returnButton=new MyButton(this);
        returnButton.setButton("#66CCCCCC","#999999",null,null,true,14);
        MainActivity.setView(returnButton,"返回",width*3/5,28,0);
        gridLayout.addView(returnButton);


        final SocketConnect socketConnect=(SocketConnect)Enrol.this.getApplication();


        final ImageView img=(ImageView)findViewById(R.id.enrolImageView);
        img.setImageResource(R.drawable.pic006);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//返回登录界面
                Intent intent=new Intent(context,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        handler=new Handler()
        {
            @Override
            public  void handleMessage(Message msg)
            {
                if(0x122==msg.what) {

                    if(list.size()!=0){
                        Toast.makeText(context,"用户名重名",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                list.clear();
                                Map<String,Object> map=new HashMap<String,Object>();
                                map.put(socketConnect.KEY_USER_NAME,userNameString);
                                map.put(socketConnect.KEY_USER_PASSWORD,userPasswordString);
                                list.add(map);
                                String[] user=new String[]{socketConnect.KEY_USER_NAME,socketConnect.KEY_USER_PASSWORD};
                                result=socketConnect.dbService.insertUserData(list,user,"User");
                                Message msg = Message.obtain();
                                msg.what = 0x123;
                                handler.sendMessage(msg);

                            }
                        }).start();
                    }
                }
                else  if(0x123==msg.what) {

                    if(result!=1){
                        Toast.makeText(context,"失败",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Intent intent=new Intent(Enrol.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        };
        enrolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameString=userNameEditView.getText().toString();
                if(userNameString==null||userNameString.length()<=0||userNameString.length()>6)//验证用户名
                {
                    Toast.makeText(context, "用户名不符合要求", Toast.LENGTH_SHORT).show();
                    return;
                }
                userPasswordString=userPasswordEditView.getText().toString();//验证密码
                if(userPasswordString==null||userPasswordString.length()<=5||userPasswordString.length()>20)
                {
                    Toast.makeText(context, "密码不符合要求", Toast.LENGTH_SHORT).show();
                    return;
                }
                String userPasswordString2=userPasswordEditView2.getText().toString();
                if(userPasswordString2==null||userPasswordString2.length()<=5||userPasswordString2.length()>20)//验证第二次密码
                {
                    Toast.makeText(context, "第二次密码不符合要求", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!userPasswordString.equals(userPasswordString2))//查看两次密码是否相同
                {
                    Toast.makeText(context, "二次密码不同", Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                            String[] user=new String[]{socketConnect.KEY_USER_ID};
                            list=socketConnect.dbService.getData(user,"select * from User where "
                                    +socketConnect.KEY_USER_NAME+"='"+userNameString +"'");
                            Message msg = Message.obtain();
                            msg.what = 0x122;
                            handler.sendMessage(msg);

                    }
                }).start();
            }
        });
    }
}
