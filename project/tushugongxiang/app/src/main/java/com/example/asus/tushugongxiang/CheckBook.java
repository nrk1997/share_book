package com.example.asus.tushugongxiang;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckBook extends AppCompatActivity {

    List<Map<String,Object>> listItems=new ArrayList<Map<String,Object>>();
    ListView listView;
    Context context;
    int width;
    String findBookString;
    Handler handler;
    SocketConnect socketConnect;
    String bookId;
    String placeId;
    String[] needDate=new String[]{SocketConnect.KEY_BOOK_ID,SocketConnect.KEY_BOOK_NAME,
            SocketConnect.KEY_BOOK_PRESS,SocketConnect.KEY_SOURCE,SocketConnect.KEY_SITE_LOCATION,SocketConnect.KEY_SITE_ID};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_book);
        context=CheckBook.this;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = dm.widthPixels;
        Intent intent=getIntent();
        placeId=intent.getStringExtra("placeId");

        socketConnect=(SocketConnect)CheckBook.this.getApplication();/////////获得app全局的数据
        ///////////////////////////////////////////////////////////////////////////////////////////与xml中的组件关联起来
        final EditText inputBookEditText=(EditText)findViewById(R.id.checkBookInputBook);
        inputBookEditText.setWidth(width*3/5);
        final MyButton findBookButton=(MyButton)findViewById(R.id.checkBookFindBook);
        MainActivity.setView(findBookButton,null,0,19,0);
        listView=(ListView)findViewById(R.id.checkBookList);
        MyButton returnButton=(MyButton)findViewById(R.id.checkBookReturn);
        MainActivity.setView(returnButton,null,0,23,0);
        final ImageView img=(ImageView)findViewById(R.id.checkBookImageView);
        img.setImageResource(R.drawable.pic006);
        ////////////////////////////////////////////////////////////////////////////////////////////为列表添加项
//        for(int i=0;i<3;i++)
//        {
//            Map<String,Object> listItem=new HashMap<String,Object>();
//            listItem.put("book_id",""+i);
//            listItem.put("book_name","书名"+i);
//            listItem.put("book_press","出版社"+i);
//            //listItem.put("start_time","获得时间："+i);
//            listItem.put(SocketConnect.KEY_SITE_LOCATION,"地点"+i);
//            listItem.put(SocketConnect.KEY_SOURCE,"来源"+i);
//            listItems.add(listItem);
//        }
        final SimpleAdapter simpleAdapter=new SimpleAdapter(this,listItems,R.layout.listitem_book,
                new String[]{SocketConnect.KEY_BOOK_ID,"book_name","book_press",SocketConnect.KEY_SITE_LOCATION},
                new int[]{R.id.bookListBookId,R.id.bookListBookName,R.id.bookListBookPress,R.id.bookListSiteLocation});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(context, "点击了"+i+"项", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context,Encoding.class);
                intent.putExtra("class","CheckBook");
                intent.putExtra("bookId",bookId=listItems.get(i).get("book_id").toString());
                intent.putExtra("siteId",listItems.get(i).get("site_id").toString());
                startActivity(intent);
            }
        });
        handler=new Handler()
        {
            @Override
            public  void handleMessage(Message msg)
            {
                if(0x123==msg.what) {
                    if(findBookString!=null&&!findBookString.equals("")){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                listItems.clear();
                                listItems.addAll(socketConnect.dbService.getData(needDate,"select * from Book,Book_Site,Site where Book."+SocketConnect.KEY_BOOK_ID+
                                        "=Book_Site."+SocketConnect.KEY_BOOK_ID+" and Book_Site."+SocketConnect.KEY_SITE_ID+
                                        "=Site."+SocketConnect.KEY_SITE_ID+" and "+SocketConnect.KEY_BOOK_NAME+" like '%"+findBookString+"%'"));
                                Message msg = Message.obtain();
                                msg.what = 0x124;
                                handler.sendMessage(msg);
                            }
                        }).start();
                    }
                    else if(placeId!=null){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                listItems.clear();
                                listItems.addAll(socketConnect.dbService.getData(needDate,"select * from Book,Book_Site,Site where Book."+SocketConnect.KEY_BOOK_ID+
                                        "=Book_Site."+SocketConnect.KEY_BOOK_ID+" and Book_Site."+SocketConnect.KEY_SITE_ID+
                                        "=Site."+SocketConnect.KEY_SITE_ID+" and Site."+SocketConnect.KEY_SITE_ID+"="+placeId));
                                Message msg = Message.obtain();
                                msg.what = 0x124;
                                handler.sendMessage(msg);
                            }
                        }).start();
                    }
                }
                else if(0x124==msg.what) {
                    simpleAdapter.notifyDataSetChanged();
                }
                else if(0x125==msg.what){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int result=socketConnect.dbService.delData("Book",SocketConnect.KEY_BOOK_ID,bookId);
                            if(result==0){
                                Toast.makeText(context,"失败",Toast.LENGTH_SHORT).show();
                            }
                            else {

                                Message msg = Message.obtain();
                                msg.what = 0x123;
                                handler.sendMessage(msg);
                            }

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
        findBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {/////设置查找按键功能

                findBookString=inputBookEditText.getText().toString();
                if(findBookButton.equals("")){
                    Toast.makeText(context,"请输入书名",Toast.LENGTH_SHORT).show();
                }
                else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            listItems.clear();
                            listItems.addAll(socketConnect.dbService.getData(needDate,"select * from Book,Book_Site,Site where Book."+SocketConnect.KEY_BOOK_ID+
                                    "=Book_Site."+SocketConnect.KEY_BOOK_ID+" and Book_Site."+SocketConnect.KEY_SITE_ID+
                                    "=Site."+SocketConnect.KEY_SITE_ID+" and "+SocketConnect.KEY_BOOK_NAME+" like '%"+findBookString+"%'"));//
                            Message msg = Message.obtain();
                            msg.what = 0x124;
                            handler.sendMessage(msg);
                        }
                    }).start();


                }
                ////////////////////////////////////
            }
        });

        if(socketConnect.identity==1){////////////////////////////////////////////////////////////为管理员则添加菜单
            registerForContextMenu(listView);
            MyButton addBookButton=(MyButton)findViewById(R.id.checkBookAdd);//添加添加书籍的按钮
            MainActivity.setView(addBookButton,null,0,23,0);
            addBookButton.setVisibility(View.VISIBLE);
            addBookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view0) {
                    AlertDialog.Builder updateBuilder = CheckPlace.createAlertDialog(android.R.drawable.ic_dialog_alert, "增加书籍",context);
                    View view = getLayoutInflater().inflate(R.layout.update_book, null);
                    final EditText updateBookNameEditText = (EditText) view.findViewById(R.id.updateBookNameEditText);
                    final EditText updateBookPressEditText = (EditText) view.findViewById(R.id.updateBookPressEditText);
                    //final EditText updateBookLocationEditText = (EditText) view.findViewById(R.id.updateBookLocationEditText);
                    final EditText updateBookSourceEditText = (EditText) view.findViewById(R.id.updateBookSourceEditText);
                    // 因为是更新，所以两个控件里应该有初始值
                    updateBuilder.setView(view);
                    updateBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String bookName=updateBookNameEditText.getText().toString();
                            final String bookPress=updateBookPressEditText.getText().toString();
                            //String bookLocation=updateBookLocationEditText.getText().toString();
                            final String bookSource=updateBookSourceEditText.getText().toString();
                            if(bookName.equals("")||bookName.length()>20){
                                Toast.makeText(context,"书名为空或超过20字节",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(bookPress.equals("")||bookPress.length()>20){
                                Toast.makeText(context,"出版社为空或超过20字节",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(bookSource.equals("")||bookSource.length()>20){
                                Toast.makeText(context,"来源为空或超过20字节",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    String nowTime="";
                                    Calendar c=Calendar.getInstance();
                                    nowTime=String.format("%1$tY%1$tm%1$td%1$tH%1$tM",c);
                                    List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();;

                                    Map<String,Object> map=new HashMap<String,Object>();
                                    map.put(socketConnect.KEY_BOOK_NAME,bookName);
                                    map.put(socketConnect.KEY_BOOK_PRESS,bookPress);
                                    map.put(socketConnect.KEY_START_TIME,nowTime);
                                    map.put(socketConnect.KEY_SOURCE,bookSource);
                                    list.add(map);
                                    String[] book=new String[]{socketConnect.KEY_BOOK_NAME,socketConnect.KEY_BOOK_PRESS
                                    ,socketConnect.KEY_START_TIME,socketConnect.KEY_SOURCE};
                                    int result=socketConnect.dbService.insertUserData(list,book,"Book");
                                    Message msg = Message.obtain();
                                    msg.what = 0x123;
                                    handler.sendMessage(msg);

                                }
                            }).start();
/////////////////////////////////////
                        }
                    });
                    updateBuilder.show();
                }
            });
        }
        if(placeId==null||placeId.equals("")){

        }
        else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    listItems.clear();
                    listItems.addAll(socketConnect.dbService.getData(needDate,"select * from Book,Book_Site,Site where Book."+SocketConnect.KEY_BOOK_ID+
                            "=Book_Site."+SocketConnect.KEY_BOOK_ID+" and Book_Site."+SocketConnect.KEY_SITE_ID+
                            "=Site."+SocketConnect.KEY_SITE_ID+" and Site."+SocketConnect.KEY_SITE_ID+"="+placeId));
                    Message msg = Message.obtain();
                    msg.what = 0x124;
                    handler.sendMessage(msg);
                }
            }).start();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(bookId!=null){
            Message msg = Message.obtain();
            msg.what = 0x123;
            handler.sendMessage(msg);
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {//给列表设置菜单
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.contextmenu_listview_book, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {/////////////////////////////////////////菜单事件响应
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        bookId = listItems.get(info.position).get("book_id").toString();
        String bookName = listItems.get(info.position).get("book_name").toString();
        String bookPress = listItems.get(info.position).get("book_press").toString();
        String bookLocation = listItems.get(info.position).get(SocketConnect.KEY_SITE_LOCATION).toString();
        String bookSource = listItems.get(info.position).get(SocketConnect.KEY_SOURCE).toString();
        String siteId = listItems.get(info.position).get(SocketConnect.KEY_SITE_ID).toString();
        Toast.makeText(context,siteId+"aaa",Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.action_delete:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int result=socketConnect.dbService.delData("Book_Site",SocketConnect.KEY_BOOK_ID,bookId);


                        if(result==0){
                            Toast.makeText(context,"失败",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Message msg = Message.obtain();
                            msg.what = 0x125;
                            handler.sendMessage(msg);
                        }

                    }
                }).start();
                break;

            case R.id.action_update:
                AlertDialog.Builder updateBuilder = CheckPlace.createAlertDialog(android.R.drawable.ic_dialog_alert, "修改书籍信息",context);
                View view = getLayoutInflater().inflate(R.layout.update_book, null);
                //final EditText updateBookNumberEditText = (EditText) view.findViewById(R.id.updateBookNumberEditText);
                final EditText updateBookNameEditText = (EditText) view.findViewById(R.id.updateBookNameEditText);
                //final EditText updateBookLocationEditText = (EditText) view.findViewById(R.id.updateBookLocationEditText);
                final EditText updateBookPressEditText = (EditText) view.findViewById(R.id.updateBookPressEditText);
                final EditText updateBookSourceEditText = (EditText) view.findViewById(R.id.updateBookSourceEditText);
                //updateBookNumberEditText.setText(bookId);
                updateBookNameEditText.setText(bookName);
                updateBookPressEditText.setText(bookPress);
                //updateBookLocationEditText.setText(bookLocation);
                updateBookSourceEditText.setText(bookSource);
                // 因为是更新，所以两个控件里应该有初始值
                updateBuilder.setView(view);
                updateBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String bookName0=updateBookNameEditText.getText().toString();
                        final String bookPress0=updateBookPressEditText.getText().toString();
                        //String bookLocation=updateBookLocationEditText.getText().toString();
                        final String bookSource0=updateBookSourceEditText.getText().toString();
                        if(bookName0.equals("")||bookName0.length()>20){
                            Toast.makeText(context,"书名为空或超过20字节",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(bookPress0.equals("")||bookPress0.length()>20){
                            Toast.makeText(context,"出版社为空或超过20字节",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(bookSource0.equals("")||bookSource0.length()>20){
                            Toast.makeText(context,"来源为空或超过20字节",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Map<String,Object> map=new HashMap<String,Object>();
                                map.put(SocketConnect.KEY_BOOK_NAME,bookName0);
                                map.put(SocketConnect.KEY_BOOK_PRESS,bookPress0);
                                //map.put(SocketConnect.KEY_START_TIME,nowTime);
                                map.put(SocketConnect.KEY_SOURCE,bookSource0);
                                String[] book=new String[]{SocketConnect.KEY_BOOK_NAME,SocketConnect.KEY_BOOK_PRESS
                                        ,SocketConnect.KEY_SOURCE};
                                int result=socketConnect.dbService.updateData(map,book,"Book",SocketConnect.KEY_BOOK_ID+"="+bookId);
                                if(result==0){
                                    Toast.makeText(context,"失败",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Message msg = Message.obtain();
                                    msg.what = 0x123;
                                    handler.sendMessage(msg);
                                }

                            }
                        }).start();
                    }
                });
                updateBuilder.show();
                break;
            case R.id.action_encoding:
                Intent intent = new Intent(context,Encoding.class);
                intent.putExtra("bookId",bookId);
                intent.putExtra("siteId",siteId);
                startActivity(intent);
                break;
            case R.id.action_comment:
                Intent intent0 = new Intent(context,Comment.class);
                intent0.putExtra("bookId",bookId);
                startActivity(intent0);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
}
