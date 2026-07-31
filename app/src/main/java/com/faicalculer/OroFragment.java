package com.faicalculer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Fragment optimizado que carga Kitco Gold con soporte para Pull-to-Refresh (deslizar hacia abajo para actualizar).
 */
public class OroFragment extends Fragment implements OnBackPressedListener {

    private WebView webView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;

    private static final String KITCO_URL = "https://www.kitco.com/charts/gold";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);

        webView = view.findViewById(R.id.webview);
        progressBar = view.findViewById(R.id.progress_bar);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);

        // Optimización de capa de hardware
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.clearCache(false);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setMediaPlaybackRequiresUserGesture(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        if (swipeRefresh != null) {
            swipeRefresh.setColorSchemeResources(R.color.fajio_navy);
            swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (webView != null) {
                        webView.reload();
                    }
                }
            });
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                injectAntiFreezeScript(view);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                if (swipeRefresh != null) {
                    swipeRefresh.setRefreshing(false);
                }
                injectAntiFreezeScript(view);
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String reqUrl = request.getUrl().toString();
                if (AdBlocker.isAd(reqUrl)) {
                    return AdBlocker.createEmptyResource();
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        // Sincronizar el gesto de deslizar hacia abajo con la parte superior del WebView
        webView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (swipeRefresh != null) {
                    swipeRefresh.setEnabled(scrollY == 0);
                }
            }
        });

        webView.loadUrl(KITCO_URL);

        return view;
    }

    private void injectAntiFreezeScript(WebView view) {
        if (view == null) return;
        String js = "(function() { " +
                "  try { " +
                "    var style = document.createElement('style'); " +
                "    style.type = 'text/css'; " +
                "    style.innerHTML = 'iframe, video, amp-ad, .ad-box, .advertisement, .sponsored-content, #piano-lightbox-container, .piano-lightbox, .tp-modal, .tp-backdrop { display: none !important; }'; " +
                "    if (document.head) document.head.appendChild(style); " +
                "    var videos = document.getElementsByTagName('video'); " +
                "    for(var i=0; i<videos.length; i++) { videos[i].pause(); } " +
                "  } catch(e) {} " +
                "})()";
        view.evaluateJavascript(js, null);
    }

    @Override
    public boolean onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }
}
