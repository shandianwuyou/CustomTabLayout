
package com.my.customtablayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CustomTablayout tablayout = findViewById(R.id.tablayout);
        List<String> strList = new ArrayList<>();
        strList.add("推荐");
        strList.add("相亲");
        strList.add("情感");
        strList.add("小红");
        strList.add("小明");
        strList.add("小鹏");
        strList.add("小强");
        strList.add("小东");
        strList.add("情感");
        tablayout.setTabTextList(strList);
        tablayout.setSelectedPos(0);
    }
}
