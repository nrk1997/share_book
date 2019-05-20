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

public class Comment extends AppCompatActivity {
    List<Map<String,Object>> listItems=new ArrayList<Map<String,Object>>();
    ListView listView;
    Context context;
    int width;
    Handler handler;
    SocketConnect socketConnect;
    String bookId;
    String[] needDate=new String[]{SocketConnect.KEY_USER_ID,SocketConnect.KEY_USER_NAME,SocketConnect.KEY_GET_TIME,SocketConnect.KEY_END_TIME,SocketConnect.KEY_EVALUATE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        context=Comment.this;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width= dm.widthPixels;
        final SocketConnect socketConnect=(SocketConnect)Comment.this.getApplication();/////////获得app全局的数据
        Intent intent=getIntent();
        bookId=intent.getStringExtra("bookId");//获得书籍


        ///////////////////////////////////////////////////////////////////////////////////////////与xml中的组件关联起来
        listView=(ListView)findViewById(R.id.commentList);
        MyButton returnButton=(MyButton)findViewById(R.id.commentReturn);
        MainActivity.setView(returnButton,null,0,23,0);
        final ImageView img=(ImageView)findViewById(R.id.commentImageView);
        img.setImageResource(R.drawable.pic006);
        ////////////////////////////////////////////////////////////////////////////////////////////为列表添加项
        final SimpleAdapter simpleAdapter=new SimpleAdapter(this,listItems,R.layout.listitem,
                new String[]{SocketConnect.KEY_USER_NAME,SocketConnect.KEY_GET_TIME,SocketConnect.KEY_END_TIME,SocketConnect.KEY_EVALUATE},
                new int[]{R.id.listItemTitle,R.id.listItemGetTime,R.id.listItemEndTime,R.id.listItemInformation});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder commentBuilder = CheckPlace.createAlertDialog(android.R.drawable.ic_dialog_alert, "评论",context);
                View view0 = getLayoutInflater().inflate(R.layout.comment, null);
                final TextView commentTextView = (TextView) view0.findViewById(R.id.commentTextView);
                final EditText commentEditText = (EditText) view0.findViewById(R.id.commentEditText);
                final String getTime=listItems.get(i).get(SocketConnect.KEY_GET_TIME).toString();
                // 因为是更新，所以两个控件里应该有初始值
                commentTextView.setText("请管理员仔细审核评论");
                commentEditText.setText(listItems.get(i).get(SocketConnect.KEY_EVALUATE).toString());
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
                                    msg.what = 0x124;
                                    handler.sendMessage(msg);
                                }
                            }).start();
                        }
                    }
                });
                commentBuilder.show();

            }
        });
        handler=new Handler()
        {
            @Override
            public  void handleMessage(Message msg)
            {
                if(0x123==msg.what) {

                    simpleAdapter.notifyDataSetChanged();
                }
                else if(0x124==msg.what) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            listItems.clear();
                            listItems.addAll(socketConnect.dbService.getData(needDate,"select * from User,Book_User  where User."
                                    +SocketConnect.KEY_USER_ID+"=Book_User."+SocketConnect.KEY_USER_ID+" and Book_User."
                                    +SocketConnect.KEY_BOOK_ID+"="+Integer.parseInt(bookId)+" and "+SocketConnect.KEY_END_TIME+"!= '' and "+SocketConnect.KEY_EVALUATE+"!= ''"));
                            Message msg = Message.obtain();
                            msg.what = 0x123;
                            handler.sendMessage(msg);

                        }
                    }).start();
                }
                else if(0x125==msg.what){

                }
            }
        };
//        if(socketConnect.identity==1){////////////////////////////////////////////////////////////为管理员则添加菜单
//            registerForContextMenu(listView);
//        }
        ////////////////////////////////////////////////////////////////////////////////////////////为按钮设置事件
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {/////设置返回按键功能
                finish();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();

                Message msg = Message.obtain();
                msg.what = 0x124;
                handler.sendMessage(msg);


    }
}
