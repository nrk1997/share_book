package com.example.asus.tushugongxiang;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Administrator extends AppCompatActivity {

    Context context=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator);
        context=Administrator.this;
        ////////////////////////////////////////////////////////////////////////////////////////////与xml中的组件关联起来
        MyButton returnButton=(MyButton)findViewById(R.id.administratorReturn);
        MainActivity.setView(returnButton,null,0,28,0);
        MyButton toCkeckBookButton=(MyButton)findViewById(R.id.administratorToCheckBook);
        MainActivity.setView(toCkeckBookButton,null,0,28,0);
        MyButton toCkeckPlaceButton=(MyButton)findViewById(R.id.administratorToCheckPlace);
        MainActivity.setView(toCkeckPlaceButton,null,0,28,0);
        final ImageView img=(ImageView)findViewById(R.id.administratorImageView);
        img.setImageResource(R.drawable.pic006);

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
}
