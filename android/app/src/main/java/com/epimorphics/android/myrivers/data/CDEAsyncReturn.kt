package com.epimorphics.android.myrivers.data

import com.epimorphics.android.myrivers.apis.CDEPointRatingsAPI

/**
 * CDEAsyncReturn is only used as a return object for CDEPointRatingsAPI.
 *
 * @property cdePoint a CDEPoint
 * @property group a CDEPoint classification group
 *
 * @see CDEPoint
 * @see CDEPointRatingsAPI
 */
data class CDEAsyncReturn(val cdePoint: CDEPoint, val group: String)