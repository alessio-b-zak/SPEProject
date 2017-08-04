package com.bitbusters.android.speproject

/**
 * CDEAsyncReturn is only used as a return object for CDEPointRatingsAPI.
 * @see CDEPointRatingsAPI
 */
data class CDEAsyncReturn(val cdePoint: CDEPoint, val classification: String) {}