package com.hssb.testqn.video;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.util.Log;

import com.hssb.testqn.R;
import com.hssb.testqn.utils.ToastUtils;
import com.hssb.testqn.utils.token.Auth;
import com.hssb.testqn.utils.token.Config;
import com.qiniu.pili.droid.shortvideo.PLShortVideoUploader;
import com.qiniu.pili.droid.shortvideo.PLUploadProgressListener;
import com.qiniu.pili.droid.shortvideo.PLUploadResultListener;
import com.qiniu.pili.droid.shortvideo.PLUploadSetting;

import org.json.JSONException;
import org.json.JSONObject;


public class ReleaseActivity extends Activity implements PLUploadProgressListener, PLUploadResultListener {

    private static final String MP4_PATH = "MP4_PATH";
    private PLShortVideoUploader mVideoUploadManager;
    private boolean mIsUpload = false;
    private String mVideoPath;

    public static void start(Activity activity, String mp4Path) {
        Intent intent = new Intent(activity, ReleaseActivity.class);
        intent.putExtra(MP4_PATH, mp4Path);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.release_act);
        initUI();
        initUpLoad();


    }

    private void initUpLoad() {
        PLUploadSetting uploadSetting = new PLUploadSetting();

        mVideoUploadManager = new PLShortVideoUploader(getApplicationContext(), uploadSetting);
        mVideoUploadManager.setUploadProgressListener(this);
        mVideoUploadManager.setUploadResultListener(this);

        mVideoPath = getIntent().getStringExtra(MP4_PATH);
    }

    private void initUI() {
        // TODO: 2018/11/5
    }

    private void uploadFile() {
        String ACCESS_KEY = Config.AK;
        String SECRET_KEY = Config.SK;
        //要上传的空间
        String bucketname = Config.SPACE;
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        String token = auth.uploadToken(bucketname);


        if (!mIsUpload) {
            mVideoUploadManager.startUpload(mVideoPath, token);
            mIsUpload = true;
        } else {
            mVideoUploadManager.cancelUpload();
            mIsUpload = false;
        }
    }

    @Override
    public void onUploadProgress(String s, double v) {


    }

    @Override
    public void onUploadVideoSuccess(JSONObject response) {
        try {
            final String filePath = "http://" + "phpilmlrm.bkt.clouddn.com" + "/" + response.getString("key");
            Log.e("ReleaseActivity", "filePath =" + filePath);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.l(ReleaseActivity.this, "文件上传成功，" + filePath + "已复制到粘贴板");
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUploadVideoFailed(final int statusCode, final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.l(ReleaseActivity.this, "Upload failed, statusCode = " + statusCode + " error = " + error);
            }
        });
    }
}
