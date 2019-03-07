package com.hssb.testqn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.hssb.testqn.video.play.QNPlayActivity;
import com.hssb.testqn.video.record.QNRecordActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void initUI() {
        findViewById(R.id.btn_to_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, QNPlayActivity.class));
            }
        });
        findViewById(R.id.btn_to_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, QNRecordActivity.class));
//   startActivity(new Intent(MainActivity.this, TestLayoutActivity.class));
            }
        });
        findViewById(R.id.btn_test_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TestLayoutActivity.class));
            }
        });
        findViewById(R.id.btn_test_my_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SelectActivity.class));
            }
        });
//        findViewById(R.id.btn_system_select).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, DefaultSelectActivity.class));
//            }
//        });
    }
}
