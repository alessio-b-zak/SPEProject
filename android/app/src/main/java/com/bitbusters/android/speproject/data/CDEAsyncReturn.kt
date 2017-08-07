package com.bitbusters.android.speproject.data

import com.bitbusters.android.speproject.apis.CDEPointRatingsAPI

/**
 * CDEAsyncReturn is only used as a return object for CDEPointRatingsAPI.
 * @see CDEPointRatingsAPI
 */
data class CDEAsyncReturn(val cdePoint: CDEPoint, val classification: String)