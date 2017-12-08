package org.hobart.facetrans.util;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;

import org.hobart.facetrans.R;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 从Thumbnail里面获取图片
 * Created by huzeyin on 2017/12/8.
 */

public class SimpleImageThumbnailLoader {

    private static SimpleImageThumbnailLoader sInstance = null;

    private static ReentrantLock LOCK = new ReentrantLock();

    public static SimpleImageThumbnailLoader getInstance() {
        try {
            LOCK.lock();
            if (null == sInstance)
                sInstance = new SimpleImageThumbnailLoader();
        } finally {
            LOCK.unlock();
        }
        return sInstance;
    }

    private LruCache<String, Bitmap> mMemoryCache;

    private SimpleImageThumbnailLoader() {

        long maxMemory = (Runtime.getRuntime().maxMemory());

        int cacheSize = (int) (maxMemory / 8);

        if (cacheSize <= 0)
            cacheSize = 1024 * 50;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };
    }

    private static final int TAG_KEY_URL = R.id.image_tag;

    public void displayImageView(final String path, final ImageView imageView, int defaultResId) {

        imageView.setTag(TAG_KEY_URL, path);

        Bitmap bitmap = getBitmapToMemoryCache(path);
        if (null != bitmap) {
            imageView.setImageBitmap(bitmap);
            return;
        }
        THREAD_POOL_EXECUTOR.execute(new LoadBitmapTask(imageView, path, defaultResId));
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (mMemoryCache.get(key) == null)
            mMemoryCache.put(key, bitmap);
    }

    public Bitmap getBitmapToMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    private Bitmap loadBitmapFromPath(String path) {
        Bitmap bitmap = null;
        try {
            bitmap = ScreenshotUtils.createVideoThumbnail(path);
        } catch (Exception e) {
        }
        return bitmap;
    }

    class LoadBitmapTask implements Runnable {
        private WeakReference<ImageView> imageViewWeakReference;
        private String path;
        private int defaultResId;

        public LoadBitmapTask(ImageView imageView, String path, int defaultResId) {
            imageViewWeakReference = new WeakReference<>(imageView);
            this.path = path;
            this.defaultResId = defaultResId;
        }

        @Override
        public void run() {
            Bitmap bitmap = loadBitmapFromPath(path);
            ImageView imageView = imageViewWeakReference.get();
            if (null != imageView) {
                LoaderResult result = new LoaderResult(imageView, path,
                        bitmap, defaultResId);
                mMainHandler.obtainMessage(MESSAGE_POST_RESULT, result)
                        .sendToTarget();
            }
            if (null != bitmap) addBitmapToMemoryCache(path, bitmap);
        }
    }


    public void clear() {
    }

    private static final int CPU_COUNT = Runtime.getRuntime()
            .availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE = 10L;


    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "SimpleImageThumbnailLoader#" + mCount.getAndIncrement());
        }
    };
    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(), sThreadFactory);

    private static final int MESSAGE_POST_RESULT = 0x101;

    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_POST_RESULT) {
                LoaderResult result = (LoaderResult) msg.obj;
                ImageView imageView = result.imageView;
                String path = (String) imageView.getTag(TAG_KEY_URL);
                if (path.equals(result.path)) {
                    if (null != result.bitmap) {
                        imageView.setImageBitmap(result.bitmap);
                    } else {
                        imageView.setImageResource(result.defaultResId);
                    }
                }
            }
        }
    };

    class LoaderResult {
        ImageView imageView;
        String path;
        Bitmap bitmap;
        int defaultResId;

        public LoaderResult(ImageView imageView, String path, Bitmap bitmap, int defaultResId) {
            this.imageView = imageView;
            this.path = path;
            this.bitmap = bitmap;
            this.defaultResId = defaultResId;
        }
    }
}
