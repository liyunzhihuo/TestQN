package com.hssb.testqn.utils;

import android.os.Environment;

public class Config {
    //Play
    public static final String SDCARD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String DEFAULT_CACHE_DIR_NAME = "PLDroidPlayer";
    public static final String DEFAULT_CACHE_DIR_PATH = SDCARD_DIR + "/PLDroidPlayer";
    public static final String VIDEO_PATH_PREFIX = "http://demo-videos.qnsdk.com/";
    public static final String COVER_PATH_PREFIX = "http://demo-videos.qnsdk.com/snapshoot/";
    public static final String COVER_PATH_SUFFIX = ".jpg";
    public static final String MOVIE_PATH_PREFIX = "https://api-demo.qnsdk.com/v1/kodo/bucket/demo-videos?prefix=movies";
    public static final String SHORT_VIDEO_PATH_PREFIX = "https://api-demo.qnsdk.com/v1/kodo/bucket/demo-videos?prefix=shortvideo";
    public static final String LIVE_TEST_URL = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
    public static final String UPGRADE_URL_PREFIX = "https://api-demo.qnsdk.com/v1/upgrade/app?appId=";

    //Record
    public static final String TOKEN = "QxZugR8TAhI38AiJ_cptTl3RbzLyca3t-AAiH-Hh:3hK7jJJQKwmemseSwQ1duO5AXOw=:eyJzY29wZSI6InNhdmUtc2hvcnQtdmlkZW8tZnJvbS1kZW1vIiwiZGVhZGxpbmUiOjM1NTk2OTU4NzYsInVwaG9zdHMiOlsiaHR0cDovL3VwLXoyLnFpbml1LmNvbSIsImh0dHA6Ly91cGxvYWQtejIucWluaXUuY29tIiwiLUggdXAtejIucWluaXUuY29tIGh0dHA6Ly8xNC4xNTIuMzcuNCJdfQ==";
    public static final String ak = "MqF35-H32j1PH8igh-am7aEkduP511g-5-F7j47Z";
    public static final String DOMAIN = "panm32w98.bkt.clouddn.com";

    public static final String VIDEO_STORAGE_DIR = Environment.getExternalStorageDirectory() + "/ShortVideo/";
    public static final String RECORD_FILE_PATH = VIDEO_STORAGE_DIR + "record.mp4";
    public static final String DUB_FILE_PATH = VIDEO_STORAGE_DIR + "dub.mp4";
    public static final String AUDIO_RECORD_FILE_PATH = VIDEO_STORAGE_DIR + "audio_record.m4a";
    public static final String EDITED_FILE_PATH = VIDEO_STORAGE_DIR + "edited.mp4";
    public static final String TRIM_FILE_PATH = VIDEO_STORAGE_DIR + "trimmed.mp4";
    public static final String TRANSCODE_FILE_PATH = VIDEO_STORAGE_DIR + "transcoded.mp4";
    public static final String CAPTURED_FRAME_FILE_PATH = VIDEO_STORAGE_DIR + "captured_frame.jpg";
    public static final String GIF_SAVE_PATH = VIDEO_STORAGE_DIR + "generated.gif";
    public static final String SCREEN_RECORD_FILE_PATH = VIDEO_STORAGE_DIR + "screen_record.mp4";
    public static final String COMPOSE_FILE_PATH = VIDEO_STORAGE_DIR + "composed.mp4";
    public static final String IMAGE_COMPOSE_FILE_PATH = VIDEO_STORAGE_DIR + "image_composed.mp4";
    public static final String VIDEO_DIVIDE_FILE_PATH = VIDEO_STORAGE_DIR + "divide_composed.mp4";
}

