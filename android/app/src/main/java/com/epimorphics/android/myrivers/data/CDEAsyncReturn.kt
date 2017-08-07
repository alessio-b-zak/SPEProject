package com.epimorphics.android.myrivers.data

import com.epimorphics.android.myrivers.apis.CDEPointRatingsAPI

/**
 * CDEAsyncReturn is only used as a return object for CDEPointRatingsAPI.
 * @see CDEPointRatingsAPI
 */
data class CDEAsyncReturn(val cdePoint: CDEPoint, val classification: String)