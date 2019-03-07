package com.hssb.testqn.video.record;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hssb.testqn.R;
import com.hssb.testqn.utils.Config;
import com.hssb.testqn.utils.ToastUtils;
import com.hssb.testqn.widget.CustomProgressDialog;
import com.qiniu.pili.droid.shortvideo.PLBuiltinFilter;
import com.qiniu.pili.droid.shortvideo.PLShortVideoEditor;
import com.qiniu.pili.droid.shortvideo.PLVideoEditSetting;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;

import java.io.IOException;
import java.io.InputStream;

public class VideoEditActivity extends Activity implements PLVideoSaveListener {


    private GLSurfaceView mPreviewView;
    private RecyclerView recyclerView;
    private ImageButton mPausePalybackButton;

    private static final String TAG = "VideoEditActivity";
    private static final String MP4_PATH = "MP4_PATH";

    // 视频编辑器预览状态
    private enum PLShortVideoEditorStatus {
        Idle,
        Playing,
        Paused,
    }

    private PLShortVideoEditorStatus mShortVideoEditorStatus = PLShortVideoEditorStatus.Idle;

    private PLShortVideoEditor mShortVideoEditor;
    private String mSelectedFilter;

    private volatile boolean mCancelSave;

    private long mMixDuration = 5000; // ms
    private String mMp4path;

    private CustomProgressDialog mProcessingDialog;

    public static void start(Activity activity, String mp4Path) {
        Intent intent = new Intent(activity, VideoEditActivity.class);
        intent.putExtra(MP4_PATH, mp4Path);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        setContentView(R.layout.video_edit_act);
        initUI();
        initShortVideoEditor();
        initProcessingDialog();
    }

    private void initProcessingDialog() {
        mProcessingDialog = new CustomProgressDialog(this);
        mProcessingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mShortVideoEditor.cancelSave();
            }
        });
    }

    private void initShortVideoEditor() {
        mMp4path = getIntent().getStringExtra(MP4_PATH);
        Log.i(TAG, "editing file: " + mMp4path);

        PLVideoEditSetting setting = new PLVideoEditSetting();
        setting.setSourceFilepath(mMp4path);
        setting.setDestFilepath(Config.EDITED_FILE_PATH);

        mShortVideoEditor = new PLShortVideoEditor(mPreviewView, setting);
        mShortVideoEditor.setVideoSaveListener(this);

        mMixDuration = mShortVideoEditor.getDurationMs();

        recyclerView.setAdapter(new FilterListAdapter(mShortVideoEditor.getBuiltinFilterList()));
    }

    private void initUI() {
        mPreviewView = (GLSurfaceView) findViewById(R.id.preview);
        mPreviewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                hideViewBorder();
//                checkToAddRectView();
            }
        });

        mPausePalybackButton = findViewById(R.id.pause_playback);

        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

//        setPanelVisibility(recyclerView, true);

    }


    @Override
    protected void onResume() {
        super.onResume();
        mShortVideoEditor.setBuiltinFilter(mSelectedFilter);
        startPlayback();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPlayback();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    /**
     * 启动预览
     */
    private void startPlayback() {
        if (mShortVideoEditorStatus == PLShortVideoEditorStatus.Idle) {
            mShortVideoEditor.startPlayback();
            mShortVideoEditorStatus = PLShortVideoEditorStatus.Playing;
        } else if (mShortVideoEditorStatus == PLShortVideoEditorStatus.Paused) {
            mShortVideoEditor.resumePlayback();
            mShortVideoEditorStatus = PLShortVideoEditorStatus.Playing;
        }
        mPausePalybackButton.setImageResource(R.mipmap.btn_pause);
    }

    /**
     * 停止预览
     */
    private void stopPlayback() {
        mShortVideoEditor.stopPlayback();
        mShortVideoEditorStatus = PLShortVideoEditorStatus.Idle;
        mPausePalybackButton.setImageResource(R.mipmap.btn_play);
    }

    /**
     * 暂停预览
     */
    private void pausePlayback() {
        mShortVideoEditor.pausePlayback();
        mShortVideoEditorStatus = PLShortVideoEditorStatus.Paused;
        mPausePalybackButton.setImageResource(R.mipmap.btn_play);
    }

    public void onClickBack(View v) {
        finish();
    }

    public void onSaveEdit(View v) {
//        checkToAddRectView();
        mProcessingDialog.show();
        mShortVideoEditor.save();
//        hideViewBorder();
    }

    public void onClickTogglePlayback(View v) {
        if (mShortVideoEditorStatus == PLShortVideoEditorStatus.Playing) {
            pausePlayback();
        } else {
            startPlayback();
        }
    }

    public void onClickShowFilters(View v) {
//        setPanelVisibility(mFiltersList, true);
//
//        mFiltersList.setAdapter(new FilterListAdapter(mShortVideoEditor.getBuiltinFilterList()));
    }

    /**
     * ----- PLVideoSaveListener -----
     **/
    @Override
    public void onSaveVideoSuccess(final String filePath) {
        Log.i(TAG, "save edit success filePath: " + filePath);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProcessingDialog.dismiss();
//        PlaybackActivity.start(VideoEditActivity.this, filePath);
                ToastUtils.s(VideoEditActivity.this, "保存成功,下一步就是发表动态了");
            }
        });
    }

    @Override
    public void onSaveVideoFailed(final int errorCode) {
        Log.e(TAG, "save edit failed errorCode:" + errorCode);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProcessingDialog.dismiss();
                ToastUtils.toastErrorCode(VideoEditActivity.this, errorCode);
            }
        });
    }

    @Override
    public void onSaveVideoCanceled() {
        mProcessingDialog.dismiss();
        mCancelSave = true;
    }

    @Override
    public void onProgressUpdate(final float percentage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProcessingDialog.setProgress((int) (100 * percentage));
            }
        });
    }

    private class FilterItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView mIcon;
        public TextView mName;

        public FilterItemViewHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.icon);
            mName = (TextView) itemView.findViewById(R.id.name);
        }
    }

    private class FilterListAdapter extends RecyclerView.Adapter<FilterItemViewHolder> {
        private PLBuiltinFilter[] mFilters;

        public FilterListAdapter(PLBuiltinFilter[] filters) {
            this.mFilters = filters;
        }

        @Override
        public FilterItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View contactView = inflater.inflate(R.layout.filter_item, parent, false);
            FilterItemViewHolder viewHolder = new FilterItemViewHolder(contactView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(FilterItemViewHolder holder, int position) {
            try {
                if (position == 0) {
                    holder.mName.setText("None");
                    Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open("filters/none.png"));
                    holder.mIcon.setImageBitmap(bitmap);
                    holder.mIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSelectedFilter = null;
                            mShortVideoEditor.setBuiltinFilter(null);
                        }
                    });
                    return;
                }

                final PLBuiltinFilter filter = mFilters[position - 1];
                holder.mName.setText(filter.getName());
                InputStream is = getAssets().open(filter.getAssetFilePath());
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                holder.mIcon.setImageBitmap(bitmap);
                holder.mIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSelectedFilter = filter.getName();
                        mShortVideoEditor.setBuiltinFilter(mSelectedFilter);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mFilters != null ? mFilters.length + 1 : 0;
        }
    }
}
