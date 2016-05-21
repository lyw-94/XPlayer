package com.sdust.xplayer.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragmentAdapter extends FragmentPagerAdapter {

	/** fragment集合 */
	private List<Fragment> mContentList;

	public FragmentAdapter(FragmentManager fm, List<Fragment> contentList) {
		super(fm);
		mContentList = contentList;
	}

	@Override
	public Fragment getItem(int pos) {
		return mContentList.get(pos);
	}

	@Override
	public int getCount() {
		return mContentList.size();
	}
}
