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

package com.jesusd0897.enzonapay.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jesusd0897.enzonapay.PayCreateResponse
import com.jesusd0897.enzonapay.R
import com.jesusd0897.enzonapay.webview.ObservableWebView
import com.jesusd0897.enzonapay.webview.PaymentViewClient

@SuppressLint("ViewConstructor")
class PayBottomSheetDialog(
    context: Context,
    private val createResponse: PayCreateResponse,
    val title: String? = null,
    val subtitle: String? = null,
    val shouldUseHeader: Boolean = true,
) : FrameLayout(context) {


    private val sheetDialog: BottomSheetDialog = BottomSheetDialog(context)
    private var currentWebViewScrollY = 0
    private var webView: ObservableWebView
    private var loadingLayout: View
    private var headerLayout: View
    private var titleView: TextView
    private var subtitleView: TextView
    var webViewLoadListener: WebViewLoadListener? = null
    private var isConfirmed: Boolean = false

    init {
        inflateLayout(context)
        headerLayout = findViewById(R.id.header_layout)
        titleView = findViewById(R.id.title_view)
        subtitleView = findViewById(R.id.subtitle_view)
        loadingLayout = findViewById(R.id.progress_view)
        webView = findViewById(R.id.web_view)
        setupBottomSheet()
        setupWebView()
    }

    private fun inflateLayout(context: Context) {
        inflate(context, R.layout.bottom_sheet_webview, this)
        sheetDialog.setContentView(this)
        sheetDialog.window?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)
    }

    private fun setupBottomSheet() {
        (parent as? View)?.let { view ->
            BottomSheetBehavior.from(view).let { behaviour ->
                behaviour.addBottomSheetCallback(object :
                    BottomSheetBehavior.BottomSheetCallback() {
                    override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_DRAGGING && currentWebViewScrollY > 0) {
                            // this is where we check if webview can scroll up or not and based on that we let BottomSheet close on scroll down
                            behaviour.setState(BottomSheetBehavior.STATE_EXPANDED)
                        } else if (newState == BottomSheetBehavior.STATE_HIDDEN) dismiss()
                    }
                })
            }
        }
        titleView.text = title
        subtitleView.text = subtitle
        sheetDialog.setOnDismissListener {
            if (!isConfirmed) webViewLoadListener?.onPaymentCanceled(createResponse)
        }
    }

    private fun setupWebView() {
        if (shouldUseHeader) {
            headerLayout.visibility = VISIBLE
        } else {
            headerLayout.visibility = GONE
            val p = webView.layoutParams as MarginLayoutParams
            p.topMargin = 0
        }
        loadingLayout = findViewById(R.id.progress_view)
        webView.onScrollChangedCallback = object : ObservableWebView.OnScrollChangeListener {
            override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
                currentWebViewScrollY = t
            }
        }
        webView.webViewClient = PaymentViewClient(
            returnUrl = createResponse.returnUrl,
            cancelUrl = createResponse.cancelUrl,
            returnAction = {
                webView.visibility = GONE
                webViewLoadListener?.onPaymentConfirmed(createResponse = createResponse)
                isConfirmed = true
            },
            cancelAction = {
                webView.visibility = GONE
                webViewLoadListener?.onPaymentCanceled(createResponse = createResponse)
            },
            onLoading = {
                loadingLayout.visibility = VISIBLE
                webView.visibility = GONE
                webViewLoadListener?.onLoading(createResponse = createResponse)
            },
            onLoadFinish = {
                loadingLayout.visibility = GONE
                webView.visibility = VISIBLE
                webViewLoadListener?.onLoadFinish(createResponse = createResponse)
            },
            onError = { _, errorCode, message ->
                webView.visibility = VISIBLE
                webViewLoadListener?.onError(
                    createResponse = createResponse,
                    code = errorCode,
                    message = message
                )
            },
        )
        val links = createResponse.links.filter { it.method == "REDIRECT" }
        if (!links.isNullOrEmpty()) {
            webView.loadUrl(links[0].href)
        } else {
            webViewLoadListener?.onError(
                createResponse = createResponse,
                code = -1,
                message = "REDIRECT link is not available."
            )
        }
    }

    fun loadInitialUrl() {
        val links = createResponse.links.filter { it.method == "REDIRECT" }
        if (!links.isNullOrEmpty()) {
            webView.loadUrl(links[0].href)
        } else {
            webViewLoadListener?.onError(
                createResponse = createResponse,
                code = -1,
                message = "REDIRECT link is not available."
            )
        }
    }

    fun show() {
        if (!sheetDialog.isShowing) {
            loadingLayout.visibility = VISIBLE
            loadInitialUrl()
            sheetDialog.show()
        }
    }

    fun dismiss() = sheetDialog.dismiss()

}