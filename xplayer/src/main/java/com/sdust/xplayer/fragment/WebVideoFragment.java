package com.sdust.xplayer.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.sdust.xplayer.R;
import com.sdust.xplayer.activities.VideoPlayerActivity;
import com.sdust.xplayer.utils.LogUtils;

public class WebVideoFragment extends Fragment {
    /**
     * fragment对应的view
     */
    private View mView;
    private WebView mWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_webvideo, null);
        initView();
        checkNetWork();
        return mView;
    }

    private void initView() {
        mWebView = (WebView) mView.findViewById(R.id.webview);
        mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.getSettings().setJavaScriptEnabled(true);
       //mWebView.getSettings().setPluginsEnabled(true);

        mWebView.loadUrl("http://3g.v.qq.com/");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
            }

            ;

            /**
             * 页面跳转
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//				if (FileUtils.isVideoOrAudio(url)) {
                LogUtils.e("url:" + url);
                Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
                intent.putExtra("path", url);
                startActivity(intent);
                return true;
//				}

                //return false;
            }
        });

        mWebView.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView != null && mWebView.canGoBack()) {
                    mWebView.goBack();
                    return true;
                }
                return false;
            }
        });
    }

    private void checkNetWork() {
        ConnectivityManager con=(ConnectivityManager)getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
        boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        if(wifi|internet){
            mView.findViewById(R.id.wifi).setVisibility(View.GONE);
            mView.findViewById(R.id.check_network).setVisibility(View.GONE);
        }else{
            mView.findViewById(R.id.wifi).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.check_network).setVisibility(View.VISIBLE);
        }
    }
}
