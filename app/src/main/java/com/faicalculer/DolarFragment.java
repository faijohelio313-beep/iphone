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
 * Fragment con protección multinivel Anti-Congelamiento de CPU que carga EXCLUSIVAMENTE
 * la cotización oficial de Bloomberg Línea (USDPEN:CUR) sin ralentizar el celular.
 */
public class DolarFragment extends Fragment implements OnBackPressedListener {

    private WebView webView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;

    private static final String BLOOMBERG_URL = "https://www.bloomberglinea.com/quote/USDPEN:CUR/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);

        webView = view.findViewById(R.id.webview);
        progressBar = view.findViewById(R.id.progress_bar);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);

        // Limpiar completamente el caché de la WebView para eliminar datos o anuncios guardados previamente en el celular
        webView.clearCache(true);
        webView.clearHistory();
        webView.clearFormData();

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setMediaPlaybackRequiresUserGesture(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // User Agent de Chrome Desktop para que Bloomberg entregue una versión limpia sin bucles anti-bot móviles
        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36");

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
                injectAntiFreezeEngine(view);
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
                injectAntiFreezeEngine(view);
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

        // Sincronizar Pull-to-Refresh con la parte superior del WebView
        webView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (swipeRefresh != null) {
                    swipeRefresh.setEnabled(scrollY == 0);
                }
            }
        });

        // Cargar EXCLUSIVAMENTE la cotización de Bloomberg Línea
        webView.loadUrl(BLOOMBERG_URL);

        return view;
    }

    /**
     * Motor Anti-Congelamiento Inyectado: Desactiva videos en autoplay, ralentiza temporizadores
     * de anuncios y destruye elementos flotantes de muros de pago antes de que saturen la memoria CPU.
     */
    private void injectAntiFreezeEngine(WebView view) {
        if (view == null) return;
        String js = "(function() { " +
                "  try { " +
                "    window.HTMLMediaElement.prototype.play = function() { return Promise.resolve(); }; " +
                "    var css = 'iframe, video, amp-ad, .ad-box, .advertisement, .sponsored-content, #piano-lightbox-container, .piano-lightbox, .tp-modal, .tp-backdrop, div[class*=\"paywall\"], div[id*=\"paywall\"], .outbrain, .taboola { display: none !important; opacity: 0 !important; pointer-events: none !important; height: 0 !important; }'; " +
                "    var style = document.createElement('style'); " +
                "    style.type = 'text/css'; " +
                "    style.innerHTML = css; " +
                "    if (document.head) document.head.appendChild(style); " +
                "    var bads = document.querySelectorAll('iframe, video, amp-ad, .piano-lightbox, .tp-modal, .tp-backdrop'); " +
                "    for(var i=0; i<bads.length; i++) { bads[i].remove(); } " +
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
