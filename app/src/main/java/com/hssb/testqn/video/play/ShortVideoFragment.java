package com.hssb.testqn.video.play;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;


import com.hssb.testqn.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pili.pldroid.player.PLOnInfoListener;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import static com.hssb.testqn.utils.Utils.createAVOptions;

public class ShortVideoFragment extends Fragment {
    private PLVideoTextureView videoView;
    private ImageView coverImage;
//    private TextView nameText;
//    private TextView detailText;
    private View topView;
    private ImageButton pausePlayImage;

//    private String videoPath = "http://demo-videos.qnsdk.com/shortvideo/桃花.mp4";
    private String videoPath = "https://img.qcms101.com/video/30676cb7f7b94dc2a3e17b210e240003.mp4";
    private String coverPath = "http://demo-videos.qnsdk.com/snapshoot/桃花.jpg";
    private DisplayImageOptions mDisplayImageOptions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.defualt_bg)            //加载图片时的图片
                .showImageForEmptyUri(R.drawable.defualt_bg)         //没有图片资源时的默认图片
                .showImageOnFail(R.drawable.defualt_bg)              //加载失败时的图片
                .cacheInMemory(true)                               //启用内存缓存
                .cacheOnDisk(true)                                 //启用外存缓存
                .considerExifParams(true)                          //启用EXIF和JPEG图像格式
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.short_video_frag, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        videoView = view.findViewById(R.id.video_texture_view);
        coverImage = view.findViewById(R.id.cover_image);
//        nameText = view.findViewById(R.id.name_text);
//        detailText = view.findViewById(R.id.detail_text);
        topView = view.findViewById(R.id.top_view);
        pausePlayImage = view.findViewById(R.id.image_pause_play);


        videoView.setAVOptions(createAVOptions());
        videoView.setDisplayAspectRatio(PLVideoTextureView.ASPECT_RATIO_PAVED_PARENT);
        View loadingView = view.findViewById(R.id.loading_view);
        videoView.setBufferingIndicator(loadingView);
        videoView.setOnInfoListener(new PLOnInfoListener() {
            @Override
            public void onInfo(int i, int i1) {
                if (i == PLOnInfoListener.MEDIA_INFO_VIDEO_RENDERING_START) {
                    coverImage.setVisibility(View.GONE);
                }
            }
        });
        topView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    pausePlayImage.setVisibility(View.VISIBLE);
                } else {
                    videoView.start();
                    pausePlayImage.setVisibility(View.GONE);
                }
            }
        });

        ImageLoader.getInstance().displayImage(coverPath, coverImage, mDisplayImageOptions);
    }

    public void startCurVideoView() {
        if (videoView != null && !videoView.isPlaying()) {
            videoView.setVideoPath(videoPath);
            videoView.start();
            pausePlayImage.setVisibility(View.GONE);
        }
    }

    public void pauseCurVideoView() {
        if (videoView != null) {
            videoView.pause();
        }
    }

    public void stopCurVideoView() {
        if (videoView != null) {
            videoView.stopPlayback();
            coverImage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startCurVideoView();
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseCurVideoView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopCurVideoView();
    }
}
