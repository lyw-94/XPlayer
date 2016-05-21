package com.sdust.xplayer.fragment;


import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sdust.xplayer.R;

import java.security.spec.RSAKeyGenParameterSpec;

public class FileExploerFragment extends Fragment{
	
	/** fragment对应的view */
	private View mView;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_file_exploer, null);
		initView();

		return mView;
	}
	
	private void initView() {
		
	}
}
