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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jesusd0897.enzonapay.EXTRA_PAY_ITEM_TAG
import com.jesusd0897.enzonapay.PayCreateResponse
import com.jesusd0897.enzonapay.databinding.FragmentWebviewBinding
import com.jesusd0897.enzonapay.webview.PaymentViewClient

class PayWebViewFragment : Fragment() {

    companion object {
        fun newInstance(createResponse: PayCreateResponse): PayWebViewFragment {
            val fragment = PayWebViewFragment()
            val args = Bundle()
            args.putParcelable(EXTRA_PAY_ITEM_TAG, createResponse)
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: FragmentWebviewBinding? = null
    private val binding get() = _binding

    private lateinit var createResponse: PayCreateResponse

    var webViewLoadListener: WebViewLoadListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        restoreData()
    }

    private fun restoreData() {
        createResponse = requireArguments().getParcelable(EXTRA_PAY_ITEM_TAG)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentWebviewBinding.inflate(inflater, container, false)
        binding?.apply {
            webView.webViewClient = PaymentViewClient(
                returnUrl = createResponse.returnUrl,
                cancelUrl = createResponse.cancelUrl,
                returnAction = { webViewLoadListener?.onPaymentConfirmed(createResponse = createResponse) },
                cancelAction = { webViewLoadListener?.onPaymentCanceled(createResponse = createResponse) },
                onLoading = { webViewLoadListener?.onLoading(createResponse = createResponse) },
                onLoadFinish = { webViewLoadListener?.onLoadFinish(createResponse = createResponse) },
                onError = { _, errorCode, message ->
                    webViewLoadListener?.onError(
                        createResponse = createResponse,
                        code = errorCode,
                        message = message
                    )
                },
            )
            webView.apply {
                settings.domStorageEnabled = true
                settings.javaScriptEnabled = true
            }.also { wv ->
                val links = createResponse.links.filter { it.method == "REDIRECT" }
                if (!links.isNullOrEmpty()) {
                    wv.loadUrl(links[0].href)
                } else {
                    webViewLoadListener?.onError(
                        createResponse = createResponse,
                        code = -1,
                        message = "REDIRECT link is not available."
                    )
                }
            }

        }
        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}