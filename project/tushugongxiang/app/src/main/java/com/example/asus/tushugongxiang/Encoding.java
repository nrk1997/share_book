package com.example.asus.tushugongxiang;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Encoding extends AppCompatActivity {

    int width;
    SocketConnect socketConnect;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encoding);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = dm.widthPixels;
        socketConnect=(SocketConnect)Encoding.this.getApplication();/////////获得app全局的数据
        /////////////////////////////////////////////////////////////////////////////////////////////获得传过来需生成二维码的信息
        Intent intent=getIntent();
        final String className=intent.getStringExtra("class");
        final String bookId=intent.getStringExtra("bookId");
        final String siteId=intent.getStringExtra("siteId");
        String contentString=socketConnect.userName+bookId;
        ////////////////////////////////////////////////////////////////////////////////////////////与xml中的组件关联起来
        MyButton returnButton=(MyButton)findViewById(R.id.encodingReturn);
        MainActivity.setView(returnButton,null,0,23,0);
        ImageView encodingImageView=(ImageView)findViewById(R.id.encodingImageView);
        try {
            Bitmap bitmap=EncodingHandler.createQRCode(contentString, width*5/6);
            encodingImageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /////////////////////////////////////////////////////////////////////////////////////////////s设置handler
        handler=new Handler()
        {
            @Override
            public  void handleMessage(Message msg)
            {
                if(0x123==msg.what) {

                }
                else if(0x124==msg.what) {//保存至书地点关系

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();;
                            Map<String,Object> map=new HashMap<String,Object>();
                            map.put(socketConnect.KEY_SITE_ID,"1");
                            map.put(socketConnect.KEY_BOOK_ID,bookId);
                            list.add(map);
                            String[] book=new String[]{socketConnect.KEY_SITE_ID,socketConnect.KEY_BOOK_ID};
                            int result=socketConnect.dbService.insertUserData(list,book,"Book_Site");

                            Message msg = Message.obtain();
                            msg.what = 0x126;
                            handler.sendMessage(msg);
                        }
                    }).start();
                }
                else if(0x125==msg.what){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {//删除地点关系

                            int result=socketConnect.dbService.delData("Book_Site",SocketConnect.KEY_BOOK_ID,bookId);
                            Message msg = Message.obtain();
                            msg.what = 0x126;
                            handler.sendMessage(msg);

                        }
                    }).start();
                }
                else if(0x126==msg.what) {//对历史进行记录
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();;
                            Map<String,Object> map=new HashMap<String,Object>();
                            map.put(socketConnect.KEY_USER_ID,socketConnect.userID);
                            if(className.equals("CheckBook")&&socketConnect.identity==0){
                                map.put(socketConnect.KEY_SITE_ID,siteId);
                                map.put(socketConnect.KEY_OPERATE,"0");
                            }
                            else if(className.equals("Reader")&&socketConnect.identity==0){
                                map.put(socketConnect.KEY_SITE_ID,"1");
                                map.put(socketConnect.KEY_OPERATE,"1");
                            }

                            map.put(socketConnect.KEY_BOOK_ID,bookId);
                            String nowTime="";
                            Calendar c=Calendar.getInstance();
                            nowTime=String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS",c);
                            map.put(socketConnect.KEY_TIME,nowTime);
                            list.add(map);
                            String[] book=new String[]{socketConnect.KEY_USER_ID,socketConnect.KEY_SITE_ID,socketConnect.KEY_BOOK_ID,
                            socketConnect.KEY_TIME,socketConnect.KEY_OPERATE};
                            int result=socketConnect.dbService.insertUserData(list,book,"History");

                        }
                    }).start();
                }
            }
        };
        ////////////////////////////////////////////////////////////////////////////////////////////为按钮设置事件
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {/////设置返回按键功能
                finish();
            }
        });
        if(className.equals("CheckBook")&&socketConnect.identity==0){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String nowTime="";
                    Calendar c=Calendar.getInstance();
                    nowTime=String.format("%1$tY%1$tm%1$td%1$tH%1$tM",c);
                    List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();;

                    Map<String,Object> map=new HashMap<String,Object>();
                    map.put(socketConnect.KEY_USER_ID,socketConnect.userID);
                    map.put(socketConnect.KEY_BOOK_ID,bookId);
                    map.put(socketConnect.KEY_GET_TIME,nowTime);
                    map.put(socketConnect.KEY_END_TIME,"");
                    map.put(socketConnect.KEY_EVALUATE,"");
                    list.add(map);
                    String[] book=new String[]{socketConnect.KEY_USER_ID,socketConnect.KEY_BOOK_ID
                            ,socketConnect.KEY_GET_TIME,SocketConnect.KEY_END_TIME,SocketConnect.KEY_EVALUATE};
                    int result=socketConnect.dbService.insertUserData(list,book,"Book_User");
                    Message msg = Message.obtain();
                    msg.what = 0x125;
                    handler.sendMessage(msg);

                }
            }).start();
        }
        else if(className.equals("Reader")&&socketConnect.identity==0){
            final String getTime=intent.getStringExtra(SocketConnect.KEY_GET_TIME);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String nowTime="";
                    Calendar c=Calendar.getInstance();
                    nowTime=String.format("%1$tY%1$tm%1$td%1$tH%1$tM",c);
                    List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();;
                    Map<String,Object> map=new HashMap<String,Object>();
                    map.put(SocketConnect.KEY_END_TIME,nowTime);
                    String[] book=new String[]{SocketConnect.KEY_END_TIME};
                    int result=socketConnect.dbService.updateData(map,book,"Book_User",SocketConnect.KEY_BOOK_ID+"="+bookId
                            +" and "+SocketConnect.KEY_USER_ID+"="+socketConnect.userID +" and "+
                            SocketConnect.KEY_GET_TIME+"="+getTime);
                    Message msg = Message.obtain();
                    msg.what = 0x124;
                    handler.sendMessage(msg);
                }
            }).start();
        }

    }
}
