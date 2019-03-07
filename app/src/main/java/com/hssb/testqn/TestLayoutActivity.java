package com.hssb.testqn;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.hssb.testqn.utils.token.Auth;
import com.hssb.testqn.utils.token.Config;
import com.hssb.testqn.video.ReleaseActivity;

public class TestLayoutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        findViewById(R.id.test_token).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String ACCESS_KEY = Config.AK;
//                String SECRET_KEY = Config.SK;
//                //要上传的空间
//                String bucketname = Config.SPACE;
//                Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
//                String token = auth.uploadToken(bucketname);
//                Log.e("TestLayoutActivity","token = "+token);
                startActivity(new Intent(TestLayoutActivity.this,ReleaseActivity.class));
            }
        });
    }
}
