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