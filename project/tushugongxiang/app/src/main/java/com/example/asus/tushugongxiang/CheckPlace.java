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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckPlace extends AppCompatActivity {
    List<Map<String,Object>> listItems=new ArrayList<Map<String,Object>>();
    ListView listView;
    Context context;
    int width;
    Handler handler;
    SocketConnect socketConnect;
    String findPlaceString;
    String placeId;
    String[] needDate=new String[]{SocketConnect.KEY_SITE_ID,SocketConnect.KEY_SITE_LOCATION,};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_place);
        context=CheckPlace.this;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width= dm.widthPixels;
        socketConnect=(SocketConnect)CheckPlace.this.getApplication();/////////获得app全局的数据
        ///////////////////////////////////////////////////////////////////////////////////////////与xml中的组件关联起来
        final EditText inputPlaceEditText=(EditText)findViewById(R.id.checkPlaceInputPlace);
        inputPlaceEditText.setWidth(width*3/5);
        final MyButton findPlaceButton=(MyButton)findViewById(R.id.checkPlaceFindPlace);
        MainActivity.setView(findPlaceButton,null,0,19,0);
        listView=(ListView)findViewById(R.id.checkPlaceList);
        MyButton returnButton=(MyButton)findViewById(R.id.checkPlaceReturn);
        MainActivity.setView(returnButton,null,0,23,0);
        final ImageView img=(ImageView)findViewById(R.id.checkPlaceImageView);
        img.setImageResource(R.drawable.pic006);
        ////////////////////////////////////////////////////////////////////////////////////////////为列表添加项
        final SimpleAdapter simpleAdapter=new SimpleAdapter(this,listItems,R.layout.listitem_place,
                new String[]{"site_id",SocketConnect.KEY_SITE_LOCATION},new int[]{R.id.placeListSiteId,R.id.placeListSiteLocation});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(context, "点击了"+i+"项", Toast.LENGTH_SHORT).show();
                placeId = listItems.get(i).get("site_id").toString();
                Intent intent = new Intent(context,CheckBook.class);
                // Bundle data=new Bundle();
                //data.putString("placeId",name);
                intent.putExtra("placeId",placeId);
                startActivity(intent);

            }
        });
        if(socketConnect.identity==1){////////////////////////////////////////////////////////////为管理员则添加菜单
            registerForContextMenu(listView);
            MyButton addPlaceButton=(MyButton)findViewById(R.id.checkPlaceAdd);//添加添加站点的按钮
            MainActivity.setView(addPlaceButton,null,0,23,0);
            addPlaceButton.setVisibility(View.VISIBLE);
            addPlaceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view0) {
                    AlertDialog.Builder updateBuilder = createAlertDialog(android.R.drawable.ic_dialog_alert, "添加地点",context);
                    View view = getLayoutInflater().inflate(R.layout.update_place, null);
                    //final EditText updatePlaceNumberEditText = (EditText) view.findViewById(R.id.updatePlaceNumberEditText);
                    final EditText updatePlaceLocationEditText = (EditText) view.findViewById(R.id.updatePlaceLocationEditText);
                    // 因为是更新，所以两个控件里应该有初始值
                    updateBuilder.setView(view);

                    updateBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String placeLocation=updatePlaceLocationEditText.getText().toString();
                            if(placeLocation.equals("")||placeLocation.length()>20){
                                Toast.makeText(context,"地点为空或超过20字节",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();;
                                    Map<String,Object> map=new HashMap<String,Object>();
                                    map.put(socketConnect.KEY_SITE_LOCATION,placeLocation);
                                    list.add(map);
                                    String[] book=new String[]{socketConnect.KEY_SITE_LOCATION};
                                    int result=socketConnect.dbService.insertUserData(list,book,"Site");
                                    Message msg = Message.obtain();
                                    msg.what = 0x123;
                                    handler.sendMessage(msg);

                                }
                            }).start();
                        }
                    });
                    updateBuilder.show();
                }
            });
        }
        handler=new Handler()
        {
            @Override
            public  void handleMessage(Message msg)
            {
                if(0x123==msg.what) {
                    if(findPlaceString!=null&&!findPlaceString.equals("")){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                listItems.clear();
                                listItems.addAll(socketConnect.dbService.getData(needDate,"select * from Site where "
                                        +SocketConnect.KEY_SITE_LOCATION+" like '%"+findPlaceString+"%'"));
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
                            int result=socketConnect.dbService.delData("Site",SocketConnect.KEY_SITE_ID,placeId);
                            if(result==0){
                                Toast.makeText(context,"失败",Toast.LENGTH_SHORT).show();
                            }
                            else{
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
        findPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                findPlaceString=inputPlaceEditText.getText().toString();
                if(findPlaceString.equals("")){
                    Toast.makeText(context,"请输入地点",Toast.LENGTH_SHORT).show();
                }
                else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            listItems.clear();
                            listItems.addAll(socketConnect.dbService.getData(needDate,"select * from Site where "
                                    +SocketConnect.KEY_SITE_LOCATION+" like '%"+findPlaceString+"%'"));
                            Message msg = Message.obtain();
                            msg.what = 0x124;
                            handler.sendMessage(msg);
                        }
                    }).start();


                }
            }
        });
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {//给列表设置菜单
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.contextmenu_listview_place, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {/////////////////////////////////////////菜单事件响应
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        placeId = listItems.get(info.position).get("site_id").toString();
        final String placeLocation=listItems.get(info.position).get(SocketConnect.KEY_SITE_LOCATION).toString();
        switch (item.getItemId()) {
            case R.id.action_delete:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int result=socketConnect.dbService.delData("Book_Site",SocketConnect.KEY_SITE_ID,placeId);


                        Message msg = Message.obtain();
                        msg.what = 0x125;
                        handler.sendMessage(msg);
                        if(result==0){
                            Toast.makeText(context,"失败",Toast.LENGTH_SHORT).show();
                        }

                    }
                }).start();
                break;

            case R.id.action_update:
                AlertDialog.Builder updateBuilder = createAlertDialog(android.R.drawable.ic_dialog_alert, "修改地点信息",context);
                View view = getLayoutInflater().inflate(R.layout.update_place, null);
                //final EditText updatePlaceNumberEditText = (EditText) view.findViewById(R.id.updatePlaceNumberEditText);
                final EditText updatePlaceLocationEditText = (EditText) view.findViewById(R.id.updatePlaceLocationEditText);
                // 因为是更新，所以两个控件里应该有初始值
                updateBuilder.setView(view);
                updatePlaceLocationEditText.setText(placeLocation);

                updateBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String placeLocation=updatePlaceLocationEditText.getText().toString();
                        if(placeLocation.equals("")||placeLocation.length()>20){
                            Toast.makeText(context,"地点为空或超过20字节",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Map<String,Object> map=new HashMap<String,Object>();
                                map.put(socketConnect.KEY_SITE_LOCATION,placeLocation);
                                String[] place=new String[]{socketConnect.KEY_SITE_LOCATION};
                                int result=socketConnect.dbService.updateData(map,place,"Site",SocketConnect.KEY_SITE_ID+"="+placeId);
                                if(result==0){
                                    Toast.makeText(context,"失败",Toast.LENGTH_SHORT).show();
                                }
                                else{
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
            case R.id.action_information:
                Intent intent = new Intent(context,CheckBook.class);
                // Bundle data=new Bundle();
                //data.putString("placeId",name);
                intent.putExtra("placeId",placeId);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
    static AlertDialog.Builder createAlertDialog(int icDialogAlert, String string,Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(icDialogAlert);
        builder.setTitle(string);
        builder.setNegativeButton("取消", null);
        return builder;
    }
}
