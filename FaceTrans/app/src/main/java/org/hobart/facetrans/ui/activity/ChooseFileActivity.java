package org.hobart.facetrans.ui.activity;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.hobart.facetrans.FTType;
import org.hobart.facetrans.R;
import org.hobart.facetrans.event.FTFilesChangedEvent;
import org.hobart.facetrans.manager.FTFileManager;
import org.hobart.facetrans.opengl.FlipViewContainer;
import org.hobart.facetrans.opengl.OpenGlUtils;
import org.hobart.facetrans.opengl.PerspectiveView;
import org.hobart.facetrans.ui.activity.base.BaseActivity;
import org.hobart.facetrans.ui.dialog.ShowSelectedFileInfoDialog;
import org.hobart.facetrans.ui.fragment.ApkListFragment;
import org.hobart.facetrans.ui.fragment.ImageListFragment;
import org.hobart.facetrans.ui.fragment.MusicListFragment;
import org.hobart.facetrans.ui.fragment.VideoListFragment;
import org.hobart.facetrans.util.AndroidUtils;
import org.hobart.facetrans.util.IntentUtils;
import org.hobart.facetrans.util.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by huzeyin on 2017/11/24.
 */

public class ChooseFileActivity extends BaseActivity {

    @Bind(R.id.tv_back)
    TextView tv_back;
    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.btn_selected)
    Button btn_selected;
    @Bind(R.id.btn_next)
    Button btn_next;

    @Bind(R.id.tab_layout)
    TabLayout tab_layout;
    @Bind(R.id.view_pager)
    ViewPager view_pager;

    @Bind(R.id.ll_fileList)
    LinearLayout ll_fileList;
    @Bind(R.id.recycleView)
    RecyclerView fileListRecycleView;

    @Bind(R.id.bottombar)
    LinearLayout ll_bottom_bar;

    @Bind(R.id.topbar)
    RelativeLayout rl_top_bar;

    public RecyclerView getFileListRecycleView() {
        return fileListRecycleView;
    }

    @Bind(R.id.mFlipViewContainer)
    FlipViewContainer mFlipViewContainer;
    private PerspectiveView mPerspectiveView;

    Fragment mCurrentFragment;
    ApkListFragment mApkListFragment;
    ImageListFragment mImageListFragment;
    MusicListFragment mMusicListFragment;
    VideoListFragment mVideoListFragment;

    private ShowSelectedFileInfoDialog mShowSelectedFileInfoDialog;

    private int mScreenWidth;
    private int mScreenHeight;

    private final Handler mHandler = new Handler();
    private boolean isFlip = false;
    private String coverUrl;
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_file);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        init();
        initData();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void init() {
        tv_title.setText("选择文件");
        tv_title.setVisibility(View.VISIBLE);

        findViewById(R.id.tv_fileList_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startReverse();
            }
        });
        TextView tv_fileList_title = (TextView) findViewById(R.id.tv_fileList_title);
        tv_fileList_title.setText("选择文件");

        mPerspectiveView = new PerspectiveView(this);
        mPerspectiveView.setAnimationCallback(new PerspectiveView.AnimationCallback() {
            @Override
            public void onAnimationEnd(boolean isReverse) {
                if (isReverse) {
                    isFlip = false;
                    mPerspectiveView.setVisibility(View.INVISIBLE);
                    ll_fileList.setVisibility(View.INVISIBLE);
                } else {
                    ll_fileList.setVisibility(View.VISIBLE);
                    //延时处理一下，防止界面跳变
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPerspectiveView.setVisibility(View.INVISIBLE);
                        }
                    }, 100);
                }
            }
        });
        mFlipViewContainer.addView(mPerspectiveView);
    }

    /**
     * 反转，回到原来的位置
     */
    private void startReverse() {
        Bitmap coverBitmap = null;
        if (TextUtils.isEmpty(coverUrl)) {
            coverBitmap = bitmap;
        } else {
            coverBitmap = BitmapFactory.decodeFile(coverUrl);
        }
        if (null == coverBitmap)
            coverBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_default);
        mPerspectiveView.setReverse(true, coverBitmap, getContentBitmap());
        final ObjectAnimator animator = ObjectAnimator.ofFloat(mFlipViewContainer, "alpha", 0.7f, 0f);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        mPerspectiveView.setVisibility(View.VISIBLE);
        ll_fileList.setVisibility(View.INVISIBLE);
        mPerspectiveView.startAnimation();
        animator.start();
    }

    public void setFlip(boolean isFlip) {
        this.isFlip = isFlip;
    }

    public boolean isFlip() {
        return isFlip;
    }

    public void delayFlipPerspectiveView(final View view, final float x, final float y, final int w, final int h, final String coverUrl, final Bitmap bitmap) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                flipPerspectiveView(view, x, y, w, h, coverUrl, bitmap);
            }
        }, 200);
    }

    private void flipPerspectiveView(final View view, float x, float y, int w, int h, String coverUrl, Bitmap bitmap) {
        this.coverUrl = coverUrl;
        this.bitmap = bitmap;
        Bitmap coverBitmap;
        if (TextUtils.isEmpty(coverUrl)) {
            coverBitmap = bitmap;
        } else {
            coverBitmap = BitmapFactory.decodeFile(coverUrl);
        }
        if (null == coverBitmap)
            coverBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_default);
        float ratio = OpenGlUtils.VIEW_W_H;
        if (w / h > ratio) {
            w = (int) (h * ratio);
        } else if (w / h < ratio) {
            h = (int) (w / ratio);
        }
        mPerspectiveView.setTextures(coverBitmap, getContentBitmap(),
                x + AndroidUtils.dip2px(15f),
                x + w + AndroidUtils.dip2px(15f),
                y + AndroidUtils.dip2px(5f) + rl_top_bar.getHeight() + tab_layout.getHeight(),
                y + h + AndroidUtils.dip2px(5f) + rl_top_bar.getHeight() + tab_layout.getHeight());
        final ObjectAnimator animator = ObjectAnimator.ofFloat(mFlipViewContainer, "alpha", 0f, 0.7f);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        mPerspectiveView.startAnimation();
        animator.start();
        //延迟显示
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPerspectiveView.setVisibility(View.VISIBLE);
            }
        }, 100);
    }

    private Bitmap getContentBitmap() {
        int height = mScreenHeight - ll_bottom_bar.getLayoutParams().height;
        return AndroidUtils.loadBitmapFromView(ll_fileList, mScreenWidth, height);
    }

    private void initData() {

        mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
        mScreenHeight = getWindowManager().getDefaultDisplay().getHeight();

        mApkListFragment = new ApkListFragment();
        mApkListFragment.setUserVisibleHint(false);
        mImageListFragment = new ImageListFragment();
        mImageListFragment.setUserVisibleHint(false);
        mMusicListFragment = new MusicListFragment();
        mMusicListFragment.setUserVisibleHint(false);
        mVideoListFragment = new VideoListFragment();
        mVideoListFragment.setUserVisibleHint(false);
        mCurrentFragment = mApkListFragment;

        String[] titles = getResources().getStringArray(R.array.array_res);
        view_pager.setAdapter(new ResPagerAdapter(getSupportFragmentManager(), titles));
        view_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mCurrentFragment = mApkListFragment;
                } else if (position == 1) {
                    mCurrentFragment = mImageListFragment;
                } else if (position == 2) {
                    mCurrentFragment = mMusicListFragment;
                } else if (position == 3) {
                    mCurrentFragment = mVideoListFragment;
                }
                mCurrentFragment.setUserVisibleHint(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        view_pager.setOffscreenPageLimit(3);

        tab_layout.setTabMode(TabLayout.MODE_FIXED);
        tab_layout.setupWithViewPager(view_pager);

        setSelectedViewStyle(false);

        mShowSelectedFileInfoDialog = new ShowSelectedFileInfoDialog(this);

        view_pager.setCurrentItem(0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFTFilesChanged(FTFilesChangedEvent event) {
        update();
    }

    private void update() {
        if (mApkListFragment != null) mApkListFragment.updateFileInfoAdapter();
        if (mImageListFragment != null) mImageListFragment.updateFileInfoAdapter();
        if (mMusicListFragment != null) mMusicListFragment.updateFileInfoAdapter();
        if (mVideoListFragment != null) mVideoListFragment.updateFileInfoAdapter();
        if (ll_fileList.getVisibility() == View.VISIBLE) {
            if (null != fileListRecycleView.getAdapter())
                fileListRecycleView.getAdapter().notifyDataSetChanged();
        }
        getSelectedView();
    }

    @OnClick({R.id.tv_back, R.id.btn_selected, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back: {
                this.finish();
                break;
            }
            case R.id.btn_selected: {
                if (mShowSelectedFileInfoDialog != null) {
                    mShowSelectedFileInfoDialog.show();
                }
                break;
            }
            case R.id.btn_next: {
                if (!FTFileManager.getInstance().isFTFilesExist()) {
                    ToastUtils.showLongToast("请选择你要传输的文件");
                    return;
                }
                IntentUtils.intentToScanReceiverActivity(this);
                break;
            }
        }
    }

    public View getSelectedView() {
        if (FTFileManager.getInstance().getFTFiles() != null && FTFileManager.getInstance().getFTFiles().size() > 0) {
            setSelectedViewStyle(true);
            int size = FTFileManager.getInstance().getFTFiles().size();
            btn_selected.setText(getResources().getString(R.string.str_has_selected_detail, size));
        } else {
            setSelectedViewStyle(false);
            btn_selected.setText(getResources().getString(R.string.str_has_selected));
        }
        return btn_selected;
    }

    private void setSelectedViewStyle(boolean isEnable) {
        if (isEnable) {
            btn_selected.setEnabled(true);
            btn_selected.setBackgroundResource(R.drawable.selector_bottom_text_common);
            btn_selected.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            btn_selected.setEnabled(false);
            btn_selected.setBackgroundResource(R.drawable.shape_bottom_text_unenable);
            btn_selected.setTextColor(getResources().getColor(R.color.darker_gray));
        }
    }

    class ResPagerAdapter extends FragmentPagerAdapter {
        String[] sTitleArray;

        public ResPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public ResPagerAdapter(FragmentManager fm, String[] sTitleArray) {
            this(fm);
            this.sTitleArray = sTitleArray;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                mCurrentFragment = mApkListFragment;
            } else if (position == 1) {
                mCurrentFragment = mImageListFragment;
            } else if (position == 2) {
                mCurrentFragment = mMusicListFragment;
            } else if (position == 3) {
                mCurrentFragment = mVideoListFragment;
            }
            return mCurrentFragment;
        }

        @Override
        public int getCount() {
            return sTitleArray.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return sTitleArray[position];
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mPerspectiveView)
            mPerspectiveView.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (null != mCurrentFragment) {
                if (((mCurrentFragment == mImageListFragment) || (mCurrentFragment == mVideoListFragment)) && isFlip) {
                    startReverse();
                    return true;
                }
            }
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }
}
