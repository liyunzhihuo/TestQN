package com.hssb.testqn.video.record;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hssb.testqn.R;
import com.hssb.testqn.utils.Config;
import com.hssb.testqn.utils.RecordSettings;
import com.hssb.testqn.utils.ToastUtils;
import com.hssb.testqn.video.ReleaseActivity;
import com.hssb.testqn.widget.CustomProgressDialog;
import com.qiniu.pili.droid.shortvideo.PLAudioEncodeSetting;
import com.qiniu.pili.droid.shortvideo.PLCameraSetting;
import com.qiniu.pili.droid.shortvideo.PLCaptureFrameListener;
import com.qiniu.pili.droid.shortvideo.PLFaceBeautySetting;
import com.qiniu.pili.droid.shortvideo.PLFocusListener;
import com.qiniu.pili.droid.shortvideo.PLMicrophoneSetting;
import com.qiniu.pili.droid.shortvideo.PLRecordSetting;
import com.qiniu.pili.droid.shortvideo.PLRecordStateListener;
import com.qiniu.pili.droid.shortvideo.PLShortVideoRecorder;
import com.qiniu.pili.droid.shortvideo.PLVideoEncodeSetting;
import com.qiniu.pili.droid.shortvideo.PLVideoFrame;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Stack;

import static com.hssb.testqn.utils.RecordSettings.RECORD_SPEED_ARRAY;

public class QNRecordActivity extends Activity implements PLVideoSaveListener, View.OnClickListener, PLRecordStateListener, PLFocusListener {
    private static final String TAG = "QNRecordActivity";
    private GLSurfaceView preview;
    private TextView[] mTabViews = new TextView[2];
    private View[] mTabLines = new View[2];

    private View mRecordBtn;

    private CustomProgressDialog mProcessingDialog;

    public static final String PREVIEW_SIZE_RATIO = "PreviewSizeRatio";
    public static final String PREVIEW_SIZE_LEVEL = "PreviewSizeLevel";
    public static final String ENCODING_MODE = "EncodingMode";
    public static final String ENCODING_SIZE_LEVEL = "EncodingSizeLevel";
    public static final String ENCODING_BITRATE_LEVEL = "EncodingBitrateLevel";
    public static final String AUDIO_CHANNEL_NUM = "AudioChannelNum";

    private static final boolean USE_TUSDK = true;

    private double mRecordSpeed;
    private boolean mIsEditVideo = false;

    private PLShortVideoRecorder mShortVideoRecorder; //	负责视频的拍摄
    private PLCameraSetting mCameraSetting; //配置摄像头参数
    private PLMicrophoneSetting mMicrophoneSetting; //配置麦克风参数
    private PLRecordSetting mRecordSetting; //配置拍摄参数
    private PLVideoEncodeSetting mVideoEncodeSetting; //配置视频编码参数
    private PLAudioEncodeSetting mAudioEncodeSetting; //配置音频编码参数
    private PLFaceBeautySetting mFaceBeautySetting; //配置美颜参数

    private boolean mSectionBegan;
    private Stack<Long> mDurationRecordStack = new Stack();
    private Stack<Double> mDurationVideoStack = new Stack();

    //
    private float beautyValue = 0.0f, whiteValue = 0.5f, reddenValue = 0.5f;

    public static final String ACTION_TYPE_VIDEO = "Video";
    public static final String ACTION_TYPE_PHOTO = "Photo";

    private String ACTION_TYPE = ACTION_TYPE_VIDEO;
    private long mSectionBeginTSMs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.qn_record_act);
        initUI();
        initRecord();

    }

    private void initRecord() {

        mShortVideoRecorder = new PLShortVideoRecorder();
        mShortVideoRecorder.setRecordStateListener(this);//注册录制状态监听器
        mShortVideoRecorder.setFocusListener(this);//注册相机对焦监听器

        int previewSizeRatioPos = getIntent().getIntExtra(PREVIEW_SIZE_RATIO, 0);//预览比例
        int previewSizeLevelPos = getIntent().getIntExtra(PREVIEW_SIZE_LEVEL, 3);//预览尺寸
        int encodingModePos = getIntent().getIntExtra(ENCODING_MODE, 0);//编码类型
        int encodingSizeLevelPos = getIntent().getIntExtra(ENCODING_SIZE_LEVEL, 7);//编码尺寸
        int encodingBitrateLevelPos = getIntent().getIntExtra(ENCODING_BITRATE_LEVEL, 2);//编码码率
        int audioChannelNumPos = getIntent().getIntExtra(AUDIO_CHANNEL_NUM, 0);//声道

        Log.e("VideoRecordActivity", "previewSizeRatioPos = " + previewSizeRatioPos);
        Log.e("VideoRecordActivity", "previewSizeLevelPos = " + previewSizeLevelPos);
        Log.e("VideoRecordActivity", "encodingModePos = " + encodingModePos);
        Log.e("VideoRecordActivity", "encodingSizeLevelPos = " + encodingSizeLevelPos);
        Log.e("VideoRecordActivity", "encodingBitrateLevelPos = " + encodingBitrateLevelPos);
        Log.e("VideoRecordActivity", "audioChannelNumPos = " + audioChannelNumPos);

        //设置摄像头 预览比例及尺寸
        mCameraSetting = new PLCameraSetting();
        PLCameraSetting.CAMERA_FACING_ID facingId = chooseCameraFacingId();
        mCameraSetting.setCameraId(facingId);
        mCameraSetting.setCameraPreviewSizeRatio(RecordSettings.PREVIEW_SIZE_RATIO_ARRAY[previewSizeRatioPos]);
        mCameraSetting.setCameraPreviewSizeLevel(RecordSettings.PREVIEW_SIZE_LEVEL_ARRAY[previewSizeLevelPos]);

        //设置麦克风 声道类型
        mMicrophoneSetting = new PLMicrophoneSetting();
        mMicrophoneSetting.setChannelConfig(RecordSettings.AUDIO_CHANNEL_NUM_ARRAY[audioChannelNumPos] == 1 ?
                AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO);

        //设置视频编码 编码大小、码率、类型
        mVideoEncodeSetting = new PLVideoEncodeSetting(this);
        mVideoEncodeSetting.setEncodingSizeLevel(RecordSettings.ENCODING_SIZE_LEVEL_ARRAY[encodingSizeLevelPos]);
        mVideoEncodeSetting.setEncodingBitrate(RecordSettings.ENCODING_BITRATE_LEVEL_ARRAY[encodingBitrateLevelPos]);
        mVideoEncodeSetting.setHWCodecEnabled(encodingModePos == 0);
        mVideoEncodeSetting.setConstFrameRateEnabled(true);

        //设置音频编码 类型和声道
        mAudioEncodeSetting = new PLAudioEncodeSetting();
        mAudioEncodeSetting.setHWCodecEnabled(encodingModePos == 0);
        mAudioEncodeSetting.setChannels(RecordSettings.AUDIO_CHANNEL_NUM_ARRAY[audioChannelNumPos]);

        //设置录制
        mRecordSetting = new PLRecordSetting();
        mRecordSetting.setMaxRecordDuration(RecordSettings.DEFAULT_MAX_RECORD_DURATION);//时长

        mRecordSetting.setRecordSpeedVariable(true);
        mRecordSetting.setVideoCacheDir(Config.VIDEO_STORAGE_DIR);//缓存路径
        mRecordSetting.setVideoFilepath(Config.RECORD_FILE_PATH);//文件路径

        /**
         * 构造 PLFaceBeautySetting 对象
         * @param beauty 美颜程度，0-1 递增
         * @param whiten 美白程度，0-1 递增
         * @param redden 红润程度，0-1 递增
         */
        mFaceBeautySetting = new PLFaceBeautySetting(beautyValue, whiteValue, reddenValue);

        //配置录制参数
        mShortVideoRecorder.prepare(preview, mCameraSetting, mMicrophoneSetting, mVideoEncodeSetting,
                mAudioEncodeSetting, USE_TUSDK ? null : mFaceBeautySetting, mRecordSetting);

        mRecordSpeed = RECORD_SPEED_ARRAY[2];
        /**
         * 设置倍数拍摄，只支持 2 的倍数，或 1/2 的倍数
         * 例如：2x, 4x, 1/2x, 1/4x
         * 注意调用过一次 beginSection 后，该方法设置无效
         * @param 倍数值
         */
        mShortVideoRecorder.setRecordSpeed(mRecordSpeed);
        //set touch
        Log.e("qn_record_act", "SET");
        //设置拍摄监听
        mRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ACTION_TYPE_VIDEO.equals(ACTION_TYPE)) {
                    takeVideo();
                } else {
                    takePhoto();
                }
            }
        });

    }

    private void takePhoto() {
        mShortVideoRecorder.captureFrame(new PLCaptureFrameListener() {
            @Override
            public void onFrameCaptured(PLVideoFrame capturedFrame) {
                if (capturedFrame == null) {
                    Log.e(TAG, "capture frame failed");
                    return;
                }

                Log.i(TAG, "captured frame width: " + capturedFrame.getWidth() + " height: " + capturedFrame.getHeight() + " timestamp: " + capturedFrame.getTimestampMs());
                try {
                    FileOutputStream fos = new FileOutputStream(Config.CAPTURED_FRAME_FILE_PATH);
                    capturedFrame.toBitmap().compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.s(QNRecordActivity.this, "拍摄成功，图片已保存！！！");
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void takeVideo() {

        if (!mSectionBegan) {
            if (!mShortVideoRecorder.deleteLastSection()) {
                Log.e(TAG, "回删视频失败");
            }
            if (mShortVideoRecorder.beginSection()) {
                ToastUtils.s(QNRecordActivity.this, "开启录制");
                mSectionBegan = true;
                mSectionBeginTSMs = System.currentTimeMillis();
                updateRecordingBtns(true);//更新

            } else {
                ToastUtils.s(QNRecordActivity.this, "无法开始视频段录制");
            }
        } else {
            long sectionRecordDurationMs = System.currentTimeMillis() - mSectionBeginTSMs;
            long totalRecordDurationMs = sectionRecordDurationMs + (mDurationRecordStack.isEmpty() ? 0 : mDurationRecordStack.peek().longValue());
            double sectionVideoDurationMs = sectionRecordDurationMs / mRecordSpeed;
            double totalVideoDurationMs = sectionVideoDurationMs + (mDurationVideoStack.isEmpty() ? 0 : mDurationVideoStack.peek().doubleValue());
            mDurationRecordStack.push(new Long(totalRecordDurationMs));
            mDurationVideoStack.push(new Double(totalVideoDurationMs));
            Log.e(TAG, "sectionVideoDurationMs = " + sectionRecordDurationMs + "totalVideoDurationMs = " + totalRecordDurationMs +
                    "sectionVideoDurationMs = " + sectionVideoDurationMs + "totalVideoDurationMs = " + totalVideoDurationMs);
            if (mRecordSetting.IsRecordSpeedVariable()) {
                Log.d(TAG, "SectionRecordDuration: " + sectionRecordDurationMs + "; sectionVideoDuration: " + sectionVideoDurationMs + "; totalVideoDurationMs: " + totalVideoDurationMs + "Section count: " + mDurationVideoStack.size());

            } else {

            }
            if (totalVideoDurationMs <= 3 * 1000) {
                ToastUtils.s(QNRecordActivity.this, "视频太短了，再录制会吧");
                return;
            }

            mShortVideoRecorder.endSection();
            mSectionBegan = false;
            ToastUtils.s(QNRecordActivity.this, "关闭录制");

            //
            mProcessingDialog.show();
            showChooseDialog();
        }
    }

    private void showChooseDialog() {
        if (mShortVideoRecorder != null) {
            mShortVideoRecorder.cancelConcat();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.if_edit_video));
        builder.setPositiveButton(getString(R.string.dlg_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mIsEditVideo = true;
                /**
                 * 合并录制的音频片段，SDK 将会在缓存目录中临时创建对应文件
                 * @param   用于接收合并回调的 listener
                 */
                mShortVideoRecorder.concatSections(QNRecordActivity.this);
            }
        });
        builder.setNegativeButton(getString(R.string.dlg_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mIsEditVideo = false;
                mShortVideoRecorder.concatSections(QNRecordActivity.this);
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    public void showBeautySet(View view) {
        Log.e(TAG, "showBeautySet");

        View popView = LayoutInflater.from(QNRecordActivity.this).inflate(R.layout.face_beauty_setting_layout, null);
        SeekBar beautySet = popView.findViewById(R.id.beauty_set);
        SeekBar whiteSet = popView.findViewById(R.id.white_set);
        SeekBar reddenSet = popView.findViewById(R.id.redden_set);
        beautySet.setProgress((int) (beautyValue * 10));
        whiteSet.setProgress((int) (whiteValue * 10));
        reddenSet.setProgress((int) (reddenValue * 10));
        beautySet.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                beautyValue = progress / 10.0f;
                Log.e(TAG, "beautyValue =" + beautyValue);
                upBeautySet(beautyValue, whiteValue, reddenValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        whiteSet.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                whiteValue = progress / 10.0f;
                Log.e(TAG, "whiteValue =" + whiteValue);
                upBeautySet(beautyValue, whiteValue, reddenValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        reddenSet.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                reddenValue = progress / 10.0f;
                Log.e(TAG, "reddenValue =" + reddenValue);
                upBeautySet(beautyValue, whiteValue, reddenValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PopupWindow popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        popupWindow.setContentView(popView);
//        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(view);

    }

    /**
     * @param beautyValue
     * @param whiteValue
     * @param reddenValue
     */
    private void upBeautySet(float beautyValue, float whiteValue, float reddenValue) {
        if (mShortVideoRecorder != null) {
            mFaceBeautySetting = new PLFaceBeautySetting(beautyValue, whiteValue, reddenValue);
            mShortVideoRecorder.updateFaceBeautySetting(mFaceBeautySetting);
        }
    }

    private void initUI() {
        preview = (GLSurfaceView) findViewById(R.id.preview);
        mRecordBtn = findViewById(R.id.record);

        mTabViews[0] = findViewById(R.id.take_photo);
        mTabViews[1] = findViewById(R.id.take_video);

        mTabLines[0] = findViewById(R.id.view_photo_line);
        mTabLines[1] = findViewById(R.id.view_video_line);

        mTabViews[0].setOnClickListener(this);
        mTabViews[1].setOnClickListener(this);

        setSelectPhoto(false);

        mProcessingDialog = new CustomProgressDialog(this);
//        mProcessingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                mShortVideoRecorder.cancelConcat();
//            }
//        });
    }

    private void setSelectPhoto(boolean isSelect) {
        if (isSelect) {
            ACTION_TYPE = ACTION_TYPE_PHOTO;
            mTabLines[0].setVisibility(View.VISIBLE);
            mTabLines[1].setVisibility(View.GONE);
        } else {
            ACTION_TYPE = ACTION_TYPE_VIDEO;
            mTabLines[0].setVisibility(View.GONE);
            mTabLines[1].setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecordBtn.setEnabled(false);
        if (mShortVideoRecorder != null)
            mShortVideoRecorder.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateRecordingBtns(false);
        if (mShortVideoRecorder != null)
            mShortVideoRecorder.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mShortVideoRecorder != null)
            mShortVideoRecorder.destroy();

    }

    private PLCameraSetting.CAMERA_FACING_ID chooseCameraFacingId() {
        if (PLCameraSetting.hasCameraFacing(PLCameraSetting.CAMERA_FACING_ID.CAMERA_FACING_3RD)) {
            return PLCameraSetting.CAMERA_FACING_ID.CAMERA_FACING_3RD;
        } else if (PLCameraSetting.hasCameraFacing(PLCameraSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT)) {
            return PLCameraSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT;
        } else {
            return PLCameraSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK;
        }
    }

    /**----- PLVideoSaveListener -----**/
    /**
     * 保存成功时触发
     *
     * @param filePath 编辑后的视频文件
     */
    @Override
    public void onSaveVideoSuccess(final String filePath) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProcessingDialog.dismiss();
                ToastUtils.s(QNRecordActivity.this, "视频保存成功！！");
                if (mIsEditVideo) {
                    VideoEditActivity.start(QNRecordActivity.this, filePath);
                } else {
                    ReleaseActivity.start(QNRecordActivity.this, filePath);
                }
            }
        });
    }

    /**
     * 保存失败时触发
     *
     * @param errorCode 错误码，在 PLErrorCode 中定义
     */
    @Override
    public void onSaveVideoFailed(int errorCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProcessingDialog.dismiss();
                ToastUtils.s(QNRecordActivity.this, "拼接视频失败！！");

            }
        });
    }

    /**
     * 保存取消时触发
     */
    @Override
    public void onSaveVideoCanceled() {
        mProcessingDialog.dismiss();
    }

    /**
     * 保存进度更新时触发
     *
     * @param percentage 进度百分比
     */
    @Override
    public void onProgressUpdate(float percentage) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_photo:
                setSelectPhoto(true);
                break;
            case R.id.take_video:
                setSelectPhoto(false);
                break;
        }
    }
    /**----- PLRecordStateListener -----**/
    /**
     * 当准备完毕可以进行录制时触发
     */
    @Override
    public void onReady() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecordBtn.setEnabled(true);
                ToastUtils.s(QNRecordActivity.this, "可以开始拍摄咯");
            }
        });
    }

    @Override
    public void onError(final int code) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.toastErrorCode(QNRecordActivity.this, code);
            }
        });
    }

    /**
     * 当录制的片段过短时触发
     */
    @Override
    public void onDurationTooShort() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.s(QNRecordActivity.this, "该视频段太短了");
            }
        });
    }

    /**
     * 录制开始
     */
    @Override
    public void onRecordStarted() {
        Log.i(TAG, "record start time: " + System.currentTimeMillis());
    }

    /**
     * 录制结束
     */
    @Override
    public void onRecordStopped() {
        Log.i(TAG, "record stop time: " + System.currentTimeMillis());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateRecordingBtns(false);
            }
        });
    }

    /**
     * 实时返回录制视频时长
     */
    @Override
    public void onSectionRecording(long sectionDurationMs, long videoDurationMs, int sectionCount) {
        Log.d(TAG, "sectionDurationMs: " + sectionDurationMs + "; videoDurationMs: " + videoDurationMs + "; sectionCount: " + sectionCount);
//        updateRecordingPercentageView(videoDurationMs);
    }


    /**
     * 当有新片段录制完成时触发
     *
     * @param incDuration   增加的时长
     * @param totalDuration 总时长
     * @param sectionCount  当前的片段总数
     */
    @Override
    public void onSectionIncreased(long incDuration, long totalDuration, int sectionCount) {
//        double videoSectionDuration = mDurationVideoStack.isEmpty() ? 0 : mDurationVideoStack.peek().doubleValue();
//        if ((videoSectionDuration + incDuration / mRecordSpeed) >= mRecordSetting.getMaxRecordDuration()) {
//            videoSectionDuration = mRecordSetting.getMaxRecordDuration();
//        }
//        Log.d(TAG, "videoSectionDuration: " + videoSectionDuration + "; incDuration: " + incDuration);
//        onSectionCountChanged(sectionCount, (long) videoSectionDuration);
    }

    /**
     * 当有片段被删除时触发
     *
     * @param decDuration   减少的时长
     * @param totalDuration 总时长
     * @param sectionCount  当前的片段总数
     */
    @Override
    public void onSectionDecreased(long decDuration, long totalDuration, int sectionCount) {
        if (!mDurationVideoStack.isEmpty()) {
            mDurationVideoStack.pop();
        }
        if (!mDurationRecordStack.isEmpty()) {
            mDurationRecordStack.pop();
        }
        double currentDuration = mDurationVideoStack.isEmpty() ? 0 : mDurationVideoStack.peek().doubleValue();
    }

    /**
     * 当录制全部完成时触发
     */
    @Override
    public void onRecordCompleted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.s(QNRecordActivity.this, "已达到拍摄总时长");
            }
        });
    }

    private void updateRecordingBtns(boolean isRecording) {
        mRecordBtn.setActivated(isRecording);
    }
    /**----- PLFocusListener -----**/
    /**
     * 启动手动对焦是否成功
     *
     * @param result 启动手动对焦结果
     */
    @Override
    public void onManualFocusStart(boolean result) {

    }

    /**
     * 手动对焦是否成功
     *
     * @param result 手动对焦结果
     */
    @Override
    public void onManualFocusStop(boolean result) {

    }

    /**
     * 当前手动对焦被取消，因另一个手动对焦事件被触发了
     */
    @Override
    public void onManualFocusCancel() {

    }

    /**
     * 连续自动对焦启动事件
     * 只有在 PLCameraParamSelectListener#onFocusModeSelected(List) 中选择了
     * FOCUS_MODE_CONTINUOUS_VIDEO 或 FOCUS_MODE_CONTINUOUS_PICTURE 的对焦方式才会被触发
     */
    @Override
    public void onAutoFocusStart() {

    }

    /**
     * 连续自动对焦停止事件
     * 只有在 PLCameraParamSelectListener#onFocusModeSelected(List) 中选择了
     * FOCUS_MODE_CONTINUOUS_VIDEO 或 FOCUS_MODE_CONTINUOUS_PICTURE 的对焦方式才会被触发
     */
    @Override
    public void onAutoFocusStop() {

    }
}
