package com.sdust.xplayer.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.sdust.xplayer.R;
import com.sdust.xplayer.adapter.FragmentAdapter;
import com.sdust.xplayer.fragment.FileExploerFragment;
import com.sdust.xplayer.fragment.LocalVideoFragment;
import com.sdust.xplayer.fragment.ShortVideoFragment;
import com.sdust.xplayer.fragment.WebVideoFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 播放器主界面
 * 
 * @author Liu Yongwei
 * @version 1.1 2016-4-9
 *
 */
public class HomeActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

	/** 本地 */
	private TextView mLocal;
	/** 网络 */
	private TextView mWeb;
	/** 短视频 */
	private TextView mShortVideo;
	/** 文件 */
	private TextView mFile;

	private ViewPager mViewPager;
	/** fragment集合 */
	private List<Fragment> mFragmentList;
	/** fragment适配器 */
	private FragmentAdapter mAdapter;
	/** 菜单栏view的集合 */
	private List<TextView> mMenusList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}

	private void initView() {
		setContentView(R.layout.activity_home);

		mLocal = (TextView) findViewById(R.id.local);
		mWeb = (TextView) findViewById(R.id.web);
		mShortVideo = (TextView) findViewById(R.id.short_video);
		mFile = (TextView) findViewById(R.id.file);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);

		mLocal.setOnClickListener(this);
		mWeb.setOnClickListener(this);
		mShortVideo.setOnClickListener(this);
		mFile.setOnClickListener(this);
		mViewPager.addOnPageChangeListener(this);
	}

	private void initData() {
		// 初始化数据
		
		// 设置viewpager最大缓存的页数
		mViewPager.setOffscreenPageLimit(3);
		
		// 初始化fragment
		mFragmentList = new ArrayList<Fragment>();
		Fragment localVideo = new LocalVideoFragment();
		Fragment webVideo = new WebVideoFragment();
		Fragment shortVideo = new ShortVideoFragment();
		Fragment fileExploer = new FileExploerFragment();
		mFragmentList.add(localVideo);
		mFragmentList.add(webVideo);
		mFragmentList.add(shortVideo);
		mFragmentList.add(fileExploer);

		mAdapter = new FragmentAdapter(getSupportFragmentManager(),
				mFragmentList);
		mViewPager.setAdapter(mAdapter);

		mMenusList = new ArrayList<>();
		mMenusList.add(mLocal);
		mMenusList.add(mWeb);
		mMenusList.add(mShortVideo);
		mMenusList.add(mFile);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

	@Override
	public void onPageSelected(int position) {
		changeTextColor(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.local:
				changeTextColor(0);
				mViewPager.setCurrentItem(0, true);
				break;
			case R.id.web:
				changeTextColor(1);
				mViewPager.setCurrentItem(1, true);
				break;
			case R.id.short_video:
				changeTextColor(2);
				mViewPager.setCurrentItem(2, true);
				break;
			case R.id.file:
				changeTextColor(3);
				mViewPager.setCurrentItem(3, true);
				break;

		}
	}

	/**
	 * 改变菜单栏文本颜色
	 * @param position  要变为白色的菜单的位置
     */
	private void changeTextColor(int position) {
		for (TextView v : mMenusList) {
			v.setTextColor(getResources().getColor(R.color.black));
		}
		mMenusList.get(position).setTextColor(getResources().getColor(R.color.white));
	}
}
