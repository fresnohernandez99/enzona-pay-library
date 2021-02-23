package com.jesusd0897.enzonapay.webview

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.webkit.*
import androidx.annotation.RequiresApi

internal class PaymentViewClient(
    private val returnUrl: String,
    private val cancelUrl: String,
    val returnAction: (url: String?) -> Unit,
    val cancelAction: (url: String?) -> Unit,
    val onLoading: (url: String?) -> Unit,
    val onLoadFinish: (url: String?) -> Unit,
    val onError: (url: String?, errorCode: Int?, message: String?) -> Unit,
) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        when (url) {
            returnUrl -> returnAction(url)
            cancelUrl -> cancelAction(url)
        }
        return super.shouldOverrideUrlLoading(view, url)
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        onLoading(url)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        onLoadFinish(url)
    }

    override fun onReceivedError(
        view: WebView?,
        errorCode: Int,
        description: String?,
        failingUrl: String?,
    ) {
        super.onReceivedError(view, errorCode, description, failingUrl)
        onError(failingUrl, errorCode, description)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?,
    ) {
        super.onReceivedError(view, request, error)
        onError(request?.url.toString(), error?.errorCode, error?.description.toString())
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?,
    ) {
        super.onReceivedHttpError(view, request, errorResponse)
        onError(request?.url.toString(), errorResponse?.statusCode, errorResponse?.reasonPhrase)
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        super.onReceivedSslError(view, handler, error)
        onError(error?.url, error?.primaryError, "Received SSL Error.")
    }
}