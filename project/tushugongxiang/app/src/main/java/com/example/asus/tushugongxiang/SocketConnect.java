package com.example.asus.tushugongxiang;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Vector;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.json.JSONObject;

public class SocketConnect  extends Application{

    public static final String KEY_ADMIN_ID="admin_id";
    public static final String KEY_ADMIN_NAME="admin_name";
    public static final String KEY_ADMIN_PASSWORD="admin_password";

    public static final String KEY_USER_ID="user_id";
    public static final String KEY_USER_NAME="user_name";
    public static final String KEY_USER_PASSWORD="user_password";

    public static final String KEY_BOOK_ID="book_id";
    public static final String KEY_BOOK_NAME="book_name";
    public static final String KEY_BOOK_PRESS="book_press";
    public static final String KEY_START_TIME="start_time";
    public static final String KEY_SOURCE="source";

    public static final String KEY_SITE_ID="site_id";
    public static final String KEY_SITE_LOCATION="site_loction";

    public static final String KEY_GET_TIME="get_time";
    public static final String KEY_END_TIME="end_time";
    public static final String KEY_EVALUATE="evaluate";

    public static final String KEY_TIME="time";
    public static final String KEY_OPERATE="operate";

    public Handler revHandler;
    DBService dbService=null;
    String userName=null;
    String userPassword=null;
    String userID=null;
    int identity=-1;
    String IP="47.102.114.254";
    int port=3258;
    public Bitmap bitmap=null;
    //Vector<String> allTeayonghuming=null;
    //public myNetCollection picM;



    public void init() throws IOException, Exception{

        dbService=DBService.getDbService();

    }

}

