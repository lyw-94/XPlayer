package com.sdust.xplayer.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdust.xplayer.R;

public class WebVideoFragment extends Fragment{
	/** fragment对应的view */
	private View mView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_webvideo, null);
		initView();
		
		return mView;
	}
	
	private void initView() {

	}
}
