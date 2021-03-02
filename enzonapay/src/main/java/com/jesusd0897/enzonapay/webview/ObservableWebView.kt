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

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView

class ObservableWebView : WebView {

    interface OnScrollChangeListener {
        fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int)
    }

    var onScrollChangedCallback: OnScrollChangeListener? = null

    init {
        settings.domStorageEnabled = true
        settings.javaScriptEnabled = true
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int)
            : super(context, attrs, defStyle)

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        onScrollChangedCallback?.onScrollChanged(l, t, oldl, oldt)
    }

}