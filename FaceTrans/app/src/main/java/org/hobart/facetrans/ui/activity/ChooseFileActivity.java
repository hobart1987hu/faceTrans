package org.hobart.facetrans.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.hobart.facetrans.FTType;
import org.hobart.facetrans.R;
import org.hobart.facetrans.event.FTFilesChangedEvent;
import org.hobart.facetrans.manager.FTFileManager;
import org.hobart.facetrans.ui.activity.base.BaseActivity;
import org.hobart.facetrans.ui.dialog.ShowSelectedFileInfoDialog;
import org.hobart.facetrans.ui.fragment.ApkListFragment;
import org.hobart.facetrans.ui.fragment.ImageListFragment;
import org.hobart.facetrans.ui.fragment.MusicListFragment;
import org.hobart.facetrans.ui.fragment.VideoListFragment;
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
    @Bind(R.id.iv_search)
    ImageView iv_search;
    @Bind(R.id.search_view)
    SearchView search_view;
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

    Fragment mCurrentFragment;
    ApkListFragment mApkListFragment;
    ImageListFragment mImageListFragment;
    MusicListFragment mMusicListFragment;
    VideoListFragment mVideoListFragment;

    ShowSelectedFileInfoDialog mShowSelectedFileInfoDialog;

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
        iv_search.setVisibility(View.INVISIBLE);
        search_view.setVisibility(View.GONE);
    }

    private void initData() {
        mApkListFragment = ApkListFragment.newInstance(FTType.APK);
        mImageListFragment = ImageListFragment.newInstance(FTType.IMAGE);
        mMusicListFragment = MusicListFragment.newInstance(FTType.MUSIC);
        mVideoListFragment = VideoListFragment.newInstance(FTType.VIDEO);
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

                } else if (position == 1) {

                } else if (position == 2) {

                } else if (position == 3) {

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        view_pager.setOffscreenPageLimit(4);

        tab_layout.setTabMode(TabLayout.MODE_FIXED);
        tab_layout.setupWithViewPager(view_pager);

        setSelectedViewStyle(false);

        mShowSelectedFileInfoDialog = new ShowSelectedFileInfoDialog(this);
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
        getSelectedView();
    }

    @OnClick({R.id.tv_back, R.id.btn_selected, R.id.btn_next, R.id.iv_search})
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

            case R.id.iv_search: {
                btn_selected.setEnabled(true);
                btn_selected.setBackgroundResource(R.drawable.selector_bottom_text_common);
                btn_selected.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            }
        }
    }

    /**
     * 获取选中文件的View
     *
     * @return
     */
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
}
