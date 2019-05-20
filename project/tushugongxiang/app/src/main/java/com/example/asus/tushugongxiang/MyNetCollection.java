package com.example.asus.tushugongxiang;
import java.io.*;
import java.net.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
public class MyNetCollection implements Runnable{
    private Socket s;
    private Handler handler;
    public Handler revHandler=null;
    public String userName=null;
    public String userPassword=null;
    //public Vector<Class> classVector=new Vector<Class>();
    String IP="47.102.114.254";
    int port=3258;
    int i=1;
    BufferedReader br=null;
    OutputStream os=null;
    PrintStream ps=null;
    FileInputStream fis=null;
    public  MyNetCollection(Handler handler)
    {
        this.handler=handler;

    }
    public void run()
    {
        try
        {
            s=new Socket(IP,port);
            br=new BufferedReader(new InputStreamReader(s.getInputStream()));
            os=s.getOutputStream();
            ps=new PrintStream(s.getOutputStream());

            new Thread()
            {
                @Override
                public void run()
                {
                    String content=null;

                    try
                    {

                        while((content=br.readLine())!=null)
                        {
                            JSONObject j=new JSONObject(content);

                            Message msg=new Message();
                            msg.what=0x123;
                            msg.obj=j;
                            //Thread.sleep(1000);
                            handler.sendMessage(msg);
                            //i--;
                        }
                    }
                    catch (Exception e)//IO
                    {
                        e.printStackTrace();
                    }
                }
            }.start();
            Looper.prepare();
            revHandler=new Handler()
            {
                @Override
                public void handleMessage(Message msg)
                {
                    if(msg.what==0x345)
                    {
                        try
                        {
                            os.write((msg.obj.toString()+"\r\n").getBytes("utf-8"));
                            //ps.println((String)msg.obj);
                        }
                        catch (Exception e)
                        {
                            restart();
                            e.printStackTrace();
                        }
                    }
                    else if(msg.what==0x346)
                    {
                        //FileInputStream fis=(FileInputStream) msg.obj;
                        byte[] buf = new byte[1024];

                        int len = 0;

                        try {
                            while ((len = fis.read(buf)) != -1)
                            {
                                os.write(buf, 0, len);
                            }
                            os.flush();
                        }catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        JSONObject js=new JSONObject();
                        try {
                            js.put("type",-1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Message messageSend=new Message();
                        messageSend.what=0x123;
                        messageSend.obj=js;
                        handler.sendMessage(messageSend);

                    }
                }
            };
            Looper.loop();

        }
        catch (SocketTimeoutException el)
        {
            System.out.println("网络超时！");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public  void setRevHandler()
    {
        Looper.loop();
        /*Looper.prepare();
        revHandler=new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what==0x345)
                {
                    try
                    {
                        os.write((msg.obj.toString()+"\r\n").getBytes("utf-8"));
                        //ps.println((String)msg.obj);
                    }
                    catch (Exception e)
                    {
                        restart();
                        e.printStackTrace();
                    }
                }
                else if(msg.what==0x346)
                {
                    //FileInputStream fis=(FileInputStream) msg.obj;
                    byte[] buf = new byte[1024];

                    int len = 0;

                    try {
                        while ((len = fis.read(buf)) != -1)
                        {
                            os.write(buf, 0, len);
                        }
                        os.flush();
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    JSONObject js=new JSONObject();
                    try {
                        js.put("type",-1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Message messageSend=new Message();
                    messageSend.what=0x123;
                    messageSend.obj=js;
                    handler.sendMessage(messageSend);

                }
            }
        };
        Looper.loop();*/
    }
    public void setUser(String n,String s)
    {
        userName=n;
        userPassword=s;
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("type", 1);
            jsonObject.put("account", userName);
            jsonObject.put("password", userPassword);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        Message messageSend=new Message();
        messageSend.what=0x345;
        messageSend.obj=jsonObject;
        revHandler.sendMessage(messageSend);
    }
    public void restart()
    {

        if(userName==null||userPassword==null)
        {

        }
        else
        {

            try {
                s=new Socket(IP,port);
                br=new BufferedReader(new InputStreamReader(s.getInputStream()));
                os=s.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("type", 1);
                jsonObject.put("account", userName);
                jsonObject.put("password", userPassword);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            Message messageSend=new Message();
            messageSend.what=0x345;
            messageSend.obj=jsonObject;
            revHandler.sendMessage(messageSend);
        }
    }
    void reSetHandler(Handler h)
    {
        handler=h;
    }
    public void sendPicture(FileInputStream fis)
    {
        this.fis=fis;

    }

}