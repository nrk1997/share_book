package com.example.asus.tushugongxiang;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.os.StrictMode;

//import org.json.JSONObject;
import  org.json.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    ///////
    Context context=null;
    EditText userNameEditView;
    EditText userPasswordEditView;
    boolean test=true;


    Handler handler;
    MyNetCollection myNetCollection;

    List<Map<String,Object>> list;
    //JSONObject j=new JSONObject(),j2=new JSONObject();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=MainActivity.this;
        /*if(android.os.Build.VERSION.SDK_INT > 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/

        //checkReadPermission(Manifest.permission.INTERNET,M)
/////////////////////////////////////////////////////////////////////////////////////////////////////文本框界面初始化
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        GridLayout gridLayout=(GridLayout)findViewById(R.id.registerGridLayout) ;
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
        setView(userNameTextView,"用户名",width*3/5,28,0);
        gridLayout.addView(userNameTextView);
        userNameEditView=new EditText(this);
        userNameEditView.setSingleLine(true);
        setView(userNameEditView,null,width*3/5,28,0);
        gridLayout.addView(userNameEditView);
        TextView userPasswordTextView=new TextView(this);
        setView(userPasswordTextView,"密码",width*3/5,28,0);
        gridLayout.addView(userPasswordTextView);
        userPasswordEditView=new EditText(this);
        userPasswordEditView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        //userPasswordEditView.
        setView(userPasswordEditView,null,width*3/5,28,0);
        gridLayout.addView(userPasswordEditView);
        /////////////////////////////////////////////////////////////////////////////////////////////选择身份
        RadioGroup identityRadioGroup=new RadioGroup(this);
        identityRadioGroup.setGravity(Gravity.CENTER_HORIZONTAL);
        identityRadioGroup.setOrientation(LinearLayout.HORIZONTAL);
        final RadioButton readerRadioButton=new RadioButton(this);
        setView(readerRadioButton,"读者",0,28,0);
        identityRadioGroup.addView(readerRadioButton);
        final RadioButton administratorRadioButton=new RadioButton(this);
        setView(administratorRadioButton,"管理员",0,28,0);
        identityRadioGroup.addView(administratorRadioButton);
        GridLayout.LayoutParams sfp=new GridLayout.LayoutParams(GridLayout.spec(7),GridLayout.spec(0));
        sfp.setGravity(Gravity.CENTER_HORIZONTAL);
        gridLayout.addView(identityRadioGroup,sfp);
        /////////////////////////////////////////////////////////////////////////////////////////////按钮设置
        MyButton registerButton=new MyButton(this);
        ((MyButton) registerButton).setButton("#664488ee","#667799ff","#ffffff",null,true,8);
        setView(registerButton,"登录",width*3/5,28,0);

        gridLayout.addView(registerButton);
        MyButton enrolButton=new MyButton(this);
        enrolButton.setButton("#66CCCCCC","#999999",null,null,true,14);
        setView(enrolButton,"注册",width*3/5,28,0);
        gridLayout.addView(enrolButton);
        /////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        enrolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,Enrol.class);
                startActivity(intent);
                finish();
            }
        });
        final SocketConnect socketConnect=(SocketConnect)MainActivity.this.getApplication();
        handler=new Handler()
        {
            @Override
            public  void handleMessage(Message msg)
            {
                if(0x123==msg.what) {

                    if(list.size()!=0){
                        socketConnect.userID=(String)list.get(0).get(SocketConnect.KEY_ADMIN_ID);
                        Intent intent=new Intent(MainActivity.this,Administrator.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(context,"请检查用户名密码后重试",Toast.LENGTH_SHORT).show();
                    }
                }
                else if(0x122==msg.what) {

                    if(list.size()!=0){
                        socketConnect.userID=(String)list.get(0).get(SocketConnect.KEY_USER_ID);
                        Intent intent=new Intent(MainActivity.this,Reader.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(context,"请检查用户名密码后重试",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    if(list.size()!=0){
                        Toast.makeText(context,list.get(0).get(socketConnect.KEY_ADMIN_NAME)+","+
                                list.get(0).get(socketConnect.KEY_ADMIN_PASSWORD),Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(context,"wu",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        if(socketConnect.dbService==null)
        {
            try {
                socketConnect.init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

       // socketConnect.setHandler(handler);
        //myNetCollection=socketConnect.myNetCollection;
        ////////////////////////////////////////////////////////////////////////////////////////////////背景图片设置
        final ImageView img=(ImageView)findViewById(R.id.registerImageView);
        img.setImageResource(R.drawable.pic006);
//        String s=Environment.getExternalStorageDirectory().toString();
//        s+="/tushugongxiang_background.jpg";
//        File file= new File(s);
//        if (file.exists()) {
           //Bitmap bmp =BitmapFactory.decodeFile(s);




        //}
        ////////////////////////////////////////////////////////////////////////////////////////////登录按钮设置事件
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName=userNameEditView.getText().toString();
                if(!test&&(userName==null||userName.length()==0))//检测用户名
                {
                    Toast.makeText(MainActivity.this, "用户名为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String  userPassword=userPasswordEditView.getText().toString();
                if(!test&&(userPassword==null||userPassword==""))//检测密码
                {
                    Toast.makeText(MainActivity.this, "密码为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!test&&(readerRadioButton.isChecked()==false&&administratorRadioButton.isChecked()==false))//检测身份选择
                {
                    Toast.makeText(MainActivity.this, "身份未选择", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(readerRadioButton.isChecked()){
                    socketConnect.identity=0;
                }
                else{
                    socketConnect.identity=1;
                }

                socketConnect.userName=userName;
                socketConnect.userPassword=userPassword;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(socketConnect.identity==1){
                            String[] user=new String[]{socketConnect.KEY_ADMIN_ID,socketConnect.KEY_ADMIN_NAME,socketConnect.KEY_ADMIN_PASSWORD};
                            list=socketConnect.dbService.getData(user,"select * from Admin where "
                                    +socketConnect.KEY_ADMIN_NAME+"='"+userName+"' and "+socketConnect.KEY_ADMIN_PASSWORD+"='"+userPassword+"'");
                            Message msg = Message.obtain();
                            msg.what = 0x123;
                            handler.sendMessage(msg);
                        }
                        else{
                            String[] user=new String[]{socketConnect.KEY_USER_ID,socketConnect.KEY_USER_NAME,socketConnect.KEY_USER_PASSWORD};
                            list=socketConnect.dbService.getData(user,"select * from User where "
                                    +socketConnect.KEY_USER_NAME+"='"+userName+"' and "+socketConnect.KEY_USER_PASSWORD+"='"+userPassword+"'");
                            Message msg = Message.obtain();
                            msg.what = 0x122;
                            handler.sendMessage(msg);
                        }
                    }
                }).start();

            }
        });
    }

    public static void setView(TextView t,String s,int w,float ts,int c)
    {
        if(t==null)return;
        if(s!=null)t.setText(s);
        if(w!=0)t.setWidth(w);
        if(ts!=0)t.setTextSize(ts);
        if(c!=0)t.setTextColor(c);
    }

    public boolean checkReadPermission(String string_permission,int request_code) {
        boolean flag = false;
        if (ContextCompat.checkSelfPermission(this, string_permission) == PackageManager.PERMISSION_GRANTED) {//已有权限
            flag = true;
        } else {//申请权限
            ActivityCompat.requestPermissions(this, new String[]{string_permission}, request_code);
        }
        return flag;
    }
}

