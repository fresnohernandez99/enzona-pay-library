/*
 * Copyright (c) 2021 jesusd0897.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jesusd0897.enzonapay.webview

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.webkit.*
import androidx.annotation.RequiresApi

class PaymentViewClient(
    private val returnUrl: String,
    private val cancelUrl: String,
    val returnAction: (url: String?) -> Unit,
    val cancelAction: (url: String?) -> Unit,
    val onLoading: (url: String?) -> Unit,
    val onLoadFinish: (url: String?) -> Unit,
    val onError: (url: String?, errorCode: Int?, message: String?) -> Unit,
) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, url: String) =
        when {
            url.contains(returnUrl, true) -> {
                returnAction(url)
                view?.loadUrl("about:blank")
                true
            }
            url.contains(cancelUrl, true) -> {
                cancelAction(url)
                view?.loadUrl("about:blank")
                true
            }
            else -> super.shouldOverrideUrlLoading(view, url)
        }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest) =
        when {
            request.url.toString().contains(returnUrl, true) -> {
                returnAction(request.url.toString())
                view?.loadUrl("about:blank")
                true
            }
            request.url.toString().contains(cancelUrl, true) -> {
                cancelAction(request.url.toString())
                view?.loadUrl("about:blank")
                true
            }
            else -> super.shouldOverrideUrlLoading(view, request)
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