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

package com.jesusd0897.enzonapay

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Info(
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("code") val code: Int = -1,
    @SerializedName("message") val message: String? = null,
) : Parcelable

//////////////////////////////////////////////

@Parcelize
data class PayCreateResponseWrapper(
    @SerializedName("info") val info: Info,
    @SerializedName("data") val data: PayCreateResponse? = null,
) : Parcelable

@Parcelize
data class PayCheckoutResponseWrapper(
    @SerializedName("info") val info: Info,
    @SerializedName("data") val data: PayCheckoutResponse? = null,
) : Parcelable

@Parcelize
data class PayCreateBody(
    @SerializedName("id_item") val itemId: String,
    @SerializedName("currency") val currency: String, //currency name
    @SerializedName("payment_method") val paymentMethod: String,
) : Parcelable

@Parcelize
data class PayCreateResponse(
    @SerializedName("transaction_uuid") val transactionUUID: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("status_denom") val statusName: String,
    @SerializedName("links") val links: List<Link>,
    @SerializedName("commission") val commission: String? = null,
    @SerializedName("return_url") val returnUrl: String,
    @SerializedName("cancel_url") val cancelUrl: String,
) : Parcelable

@Parcelize
data class PayCheckoutResponse(
    @SerializedName("transaction_uuid") val transactionUUID: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("status_denom") val statusName: String,
    @SerializedName("links") val links: List<Link>,
    @SerializedName("commission") val commission: String? = null,
) : Parcelable

@Parcelize
data class Link(
    @SerializedName("method") val method: String, //enum
    @SerializedName("rel") val rel: String, //enum
    @SerializedName("href") val href: String, //link
) : Parcelable

@Parcelize
data class PayCompleteBody(
    @SerializedName("id_item") val itemId: String,
    @SerializedName("transaction_uuid") val transactionUUID: String, //currency name
) : Parcelable

@Parcelize
data class PayCompleteResponse(
    @SerializedName("transaction_uuid") val transactionUUID: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("status_denom") val statusName: String,
    @SerializedName("links") val links: List<Link>,
    @SerializedName("commission") val commission: String? = null,
) : Parcelable

@Parcelize
data class PayCompleteResponseWrapper(
    @SerializedName("info") val info: Info,
    @SerializedName("data") val data: PayCompleteResponse? = null,
) : Parcelable