package com.codemobi.android.tvthailand.player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewPlayer extends WebView {
	MyWebViewClient mWebViewClient;
	
	public WebViewPlayer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public WebViewPlayer(Context context) {
		super(context);
		init();
	}

	@SuppressWarnings("deprecation")
	private void init() {
		mWebViewClient = new MyWebViewClient();
        this.setWebViewClient(mWebViewClient);
        
		this.getSettings().setBuiltInZoomControls(false);
		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setLoadsImagesAutomatically(true);
		this.getSettings().setPluginState(PluginState.ON);
		this.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		this.getSettings().setAllowFileAccess(true);
		this.setBackgroundColor(Color.BLACK);
		this.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		this.getSettings().setAppCacheEnabled(true);
		this.getSettings().setSaveFormData(true);
		
//		this.setInitialScale((int) (728/600 * 100));
//		this.setInitialScale(300);
		
		this.setWebChromeClient(new WebChromeClient());
	}
	
	
	public void loadDataWithIFrame(String iframeData) {
		String html = "<html><head><meta name='viewport' content='width=device-width'/></head>" +
				"<body style='background-color:#000;'>" + iframeData +"</body></html>";
		this.loadData(html,  "text/html", "utf-8");
	}
	
    class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);    //To change body of overridden methods use File | Settings | File Templates.
        }
    }

    public void actionSkip() {
        this.loadUrl("javascript:document.getElementById('iframe1').contentWindow.jw_append_skip_ad();");
    }

}
