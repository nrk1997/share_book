package com.example.asus.tushugongxiang;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reader extends AppCompatActivity {
    List<Map<String,Object>> listItems=new ArrayList<Map<String,Object>>();
    ListView listView;
    Context context=null;
    Handler handler;
    SocketConnect socketConnect;
    String[] needDate=new String[]{SocketConnect.KEY_BOOK_ID,SocketConnect.KEY_BOOK_NAME,
            SocketConnect.KEY_BOOK_PRESS,SocketConnect.KEY_GET_TIME,SocketConnect.KEY_EVALUATE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        context=Reader.this;
        socketConnect=(SocketConnect)Reader.this.getApplication();
        ////////////////////////////////////////////////////////////////////////////////////////////与xml中的组件关联起来
        MyButton returnButton=(MyButton)findViewById(R.id.readerReturn);
        MainActivity.setView(returnButton,null,0,28,0);
        MyButton toCkeckBookButton=(MyButton)findViewById(R.id.readerToCheckBook);
        MainActivity.setView(toCkeckBookButton,null,0,28,0);
        MyButton toCkeckPlaceButton=(MyButton)findViewById(R.id.readerToCheckPlace);
        MainActivity.setView(toCkeckPlaceButton,null,0,28,0);
        listView=(ListView)findViewById(R.id.readerListView);
        final ImageView img=(ImageView)findViewById(R.id.readerImageView);
        img.setImageResource(R.drawable.pic006);
////////////////////////////////////////////////////////////////////////////////////////////为列表添加项

        final SimpleAdapter simpleAdapter=new SimpleAdapter(this,listItems,R.layout.listitem_book,
                new String[]{"book_id","book_name","book_press",SocketConnect.KEY_GET_TIME},
                new int[]{R.id.bookListBookId,R.id.bookListBookName,R.id.bookListBookPress,R.id.bookListSiteLocation});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(context, "点击了"+i+"项", Toast.LENGTH_SHORT).show();

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(context, "点击了"+i+"项", Toast.LENGTH_SHORT).show();

                return false;
            }
        });
        registerForContextMenu(listView);//与列表菜单绑定
        handler=new Handler()
        {
            @Override
            public  void handleMessage(Message msg)
            {
                if(0x123==msg.what) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            listItems.clear();
                            listItems.addAll(socketConnect.dbService.getData(needDate, "select * from Book,Book_User where Book." + SocketConnect.KEY_BOOK_ID +
                                    "=Book_User." + SocketConnect.KEY_BOOK_ID + " and Book_User." + SocketConnect.KEY_END_TIME + "=''"));
                            Message msg0 = Message.obtain();
                            msg0.what = 0x124;
                            handler.sendMessage(msg0);
                        }
                    }).start();
                }
                else if(0x124==msg.what) {
                    simpleAdapter.notifyDataSetChanged();
                }
                else if(0x125==msg.what){
                }
            }
        };
        ////////////////////////////////////////////////////////////////////////////////////////////为按钮设置事件
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//返回登录界面
                Intent intent=new Intent(context,MainActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        toCkeckBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//跳转到书籍查询界面
                Intent intent=new Intent(context,CheckBook.class);
                startActivity(intent);
                //finish();
            }
        });
        toCkeckPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//跳转到站点查询界面
                Intent intent=new Intent(context,CheckPlace.class);
                startActivity(intent);
                //finish();
            }
        });
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {//给列表设置菜单
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.contextmenu_listview_reader, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {/////////////////////////////////////////菜单事件响应
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final String bookId = listItems.get(info.position).get("book_id").toString();
        String evaluate=listItems.get(info.position).get(SocketConnect.KEY_EVALUATE).toString();
        final String getTime=listItems.get(info.position).get(SocketConnect.KEY_GET_TIME).toString();
        switch (item.getItemId()) {
            case R.id.action_comment:
                AlertDialog.Builder commentBuilder = CheckPlace.createAlertDialog(android.R.drawable.ic_dialog_alert, "评论",context);
                View view0 = getLayoutInflater().inflate(R.layout.comment, null);
                final TextView commentTextView = (TextView) view0.findViewById(R.id.commentTextView);
                final EditText commentEditText = (EditText) view0.findViewById(R.id.commentEditText);
                // 因为是更新，所以两个控件里应该有初始值
                commentTextView.setText("请您写下珍贵的评论");
                commentEditText.setText(evaluate);
                commentBuilder.setView(view0);
                commentBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //评论后操作
                        final String evaluate=commentEditText.getText().toString();
                        if(evaluate.equals("")){
                            Toast.makeText(context,"评论为空",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Map<String,Object> map=new HashMap<String,Object>();
                                    map.put(SocketConnect.KEY_EVALUATE,evaluate);
                                    String[] book=new String[]{SocketConnect.KEY_EVALUATE};
                                    int result=socketConnect.dbService.updateData(map,book,"Book_User",SocketConnect.KEY_BOOK_ID+"="+bookId
                                            +" and "+SocketConnect.KEY_USER_ID+"="+socketConnect.userID +" and "+
                                            SocketConnect.KEY_GET_TIME+"="+getTime);
                                    Message msg = Message.obtain();
                                    msg.what = 0x123;
                                    handler.sendMessage(msg);

                                }
                            }).start();
                        }
                    }
                });
                commentBuilder.show();
                break;
            case R.id.action_encoding:
                Intent intent = new Intent(context,Encoding.class);
                intent.putExtra("class","Reader");
                intent.putExtra("bookId",bookId);
                intent.putExtra(SocketConnect.KEY_GET_TIME,getTime);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                listItems.clear();
                listItems.addAll(socketConnect.dbService.getData(needDate,"select * from Book,Book_User where Book."+SocketConnect.KEY_BOOK_ID+
                        "=Book_User."+SocketConnect.KEY_BOOK_ID+" and Book_User."+SocketConnect.KEY_END_TIME+"=''"));
                Message msg = Message.obtain();
                msg.what = 0x124;
                handler.sendMessage(msg);
            }
        }).start();
    }
}
