package com.faicalculer;

import android.webkit.WebResourceResponse;
import java.io.ByteArrayInputStream;

/**
 * Bloqueador de anuncios avanzado que simula las reglas de filtrado de red de bloqueadores como uBlock.
 */
public class AdBlocker {

    private static final String[] AD_KEYWORDS = {
        // Redes de anuncios comunes
        "doubleclick",
        "googleads",
        "googlesyndication",
        "googleadservices",
        "adservice",
        "adnxs",
        "outbrain",
        "taboola",
        "amazon-adsystem",
        "criteo",
        "rubiconproject",
        "pubmatic",
        "openx",
        "adcolony",
        "applovin",
        "admob",
        "flurry",
        "adroll",
        "scorecardresearch",
        "perimeterx",
        "px-cdn",
        "px-cloud",
        "perimeterx.net",
        "jwplayer",
        "jwplatform",
        "videojs",
        "connatix",
        "aniview",
        "anyclip",
        "casalemedia",
        "indexexchange",
        "triplelift",
        "smartadserver",
        "ketch",
        "onesignal",
        "quantserve",
        "moatads",
        "yieldmo",
        "teads",
        "bluekai",
        "krxd.net",
        "exelator",
        "media.net",
        // Servicios de Paywall / Bloqueos de suscripción (ej. Piano en Bloomberg)
        "piano.io",
        "tinypass",
        "po.st",
        // Analíticas y rastreadores
        "chartbeat",
        "google-analytics",
        "googletagservices",
        "segment.io",
        "hotjar",
        "mixpanel",
        "sentry.io",
        "amplitude",
        // Palabras clave de rutas
        "/ads/",
        "/ad/",
        "/advert/",
        "advertisement",
        "sponsored",
        "adserver",
        "adnetwork",
        "marketing",
        "tracker",
        "telemetry"
    };

    /**
     * Determina si una URL debe ser bloqueada por ser de anuncios, rastreadores o muros de pago.
     */
    public static boolean isAd(String url) {
        if (url == null) {
            return false;
        }
        String lower = url.toLowerCase();
        for (String keyword : AD_KEYWORDS) {
            if (lower.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retorna una respuesta vacía de red para anular la carga del script/recurso publicitario.
     */
    public static WebResourceResponse createEmptyResource() {
        return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream("".getBytes()));
    }
}
