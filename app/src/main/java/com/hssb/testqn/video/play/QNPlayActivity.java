package com.hssb.testqn.video.play;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.hssb.testqn.R;
import com.hssb.testqn.utils.PermissionChecker;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

import static com.hssb.testqn.utils.Config.DEFAULT_CACHE_DIR_NAME;

public class QNPlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qn_play_act);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        isStoragePermissionOK();
        initImageLoader();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_fragment, new ShortVideoFragment())
                .commit();


    }

    public boolean isStoragePermissionOK() {
        PermissionChecker checker = new PermissionChecker(this);
        boolean isPermissionOK = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checker.isStoragePermissionOK();
        if (!isPermissionOK) {
            Toast.makeText(this, "Storage permissions is necessary !!!", Toast.LENGTH_SHORT).show();
        }
        return isPermissionOK;
    }

    private void initImageLoader() {
        File cacheDir = StorageUtils.getOwnCacheDirectory(this, DEFAULT_CACHE_DIR_NAME);//sdcard目录
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .diskCache(new UnlimitedDiskCache(cacheDir)) // default 可以自定义缓存路径
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13)
                .build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
