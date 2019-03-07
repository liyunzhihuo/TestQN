package com.hssb.testqn;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class DefaultSelectActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_select);
        initUI();
    }

    private void initUI() {
        findViewById(R.id.btn_system_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT < 19) {
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("video/*");
                } else {
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("video/*");
                }
                startActivityForResult(Intent.createChooser(intent, "选择要导入的视频"), 0);
            }
        });
    }

}
