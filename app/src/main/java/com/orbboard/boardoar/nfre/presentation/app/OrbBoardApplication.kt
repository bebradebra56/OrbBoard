package com.orbboard.boardoar.nfre.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.orbboard.boardoar.data.local.dao.CategoryDao
import com.orbboard.boardoar.data.local.entity.CategoryEntity
import com.orbboard.boardoar.di.appModule
import com.orbboard.boardoar.nfre.presentation.di.orbBoardModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface OrbBoardAppsFlyerState {
    data object OrbBoardDefault : OrbBoardAppsFlyerState
    data class OrbBoardSuccess(val orbBoardData: MutableMap<String, Any>?) :
        OrbBoardAppsFlyerState

    data object OrbBoardError : OrbBoardAppsFlyerState
}

interface OrbBoardAppsApi {
    @Headers("Content-Type: application/json")
    @GET(ORB_BOARD_LIN)
    fun orbBoardGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}

private const val ORB_BOARD_APP_DEV = "rSiPXbdyNPxFsAz4yECga4"
private const val ORB_BOARD_LIN = "com.orbboard.boardoar"

class OrbBoardApplication : Application() {

    private var orbBoardIsResumed = false
    ///////
    private var orbBoardConversionTimeoutJob: Job? = null
    private var orbBoardDeepLinkData: MutableMap<String, Any>? = null

    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        orbBoardSetDebufLogger(appsflyer)
        orbBoardMinTimeBetween(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(p0: DeepLinkResult) {
                when (p0.status) {
                    DeepLinkResult.Status.FOUND -> {
                        orbBoardExtractDeepMap(p0.deepLink)
                        Log.d(ORB_BOARD_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                    }

                    DeepLinkResult.Status.NOT_FOUND -> {
                        Log.d(ORB_BOARD_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                    }

                    DeepLinkResult.Status.ERROR -> {
                        Log.d(ORB_BOARD_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                    }
                }
            }

        })


        appsflyer.init(
            ORB_BOARD_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    //////////
                    orbBoardConversionTimeoutJob?.cancel()
                    Log.d(ORB_BOARD_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = orbBoardGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.orbBoardGetClient(
                                    devkey = ORB_BOARD_APP_DEV,
                                    deviceId = orbBoardGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(ORB_BOARD_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic" || resp?.get("af_status") == null) {
                                    orbBoardResume(
                                        OrbBoardAppsFlyerState.OrbBoardError
                                    )
                                } else {
                                    orbBoardResume(
                                        OrbBoardAppsFlyerState.OrbBoardSuccess(
                                            resp
                                        )
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(ORB_BOARD_MAIN_TAG, "Error: ${d.message}")
                                orbBoardResume(OrbBoardAppsFlyerState.OrbBoardError)
                            }
                        }
                    } else {
                        orbBoardResume(
                            OrbBoardAppsFlyerState.OrbBoardSuccess(
                                p0
                            )
                        )
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    /////////
                    orbBoardConversionTimeoutJob?.cancel()
                    Log.d(ORB_BOARD_MAIN_TAG, "onConversionDataFail: $p0")
                    orbBoardResume(OrbBoardAppsFlyerState.OrbBoardError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(ORB_BOARD_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(ORB_BOARD_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, ORB_BOARD_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(ORB_BOARD_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(ORB_BOARD_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
            }
        })
        ///////////
        orbBoardStartConversionTimeout()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@OrbBoardApplication)
            modules(
                listOf(
                    orbBoardModule, appModule
                )
            )
        }
        seedDefaultCategories()
    }

    private fun seedDefaultCategories() {
        val categoryDao: CategoryDao by inject()
        CoroutineScope(Dispatchers.IO).launch {
            val count = categoryDao.getCount()
            if (count == 0) {
                categoryDao.insertAll(
                    listOf(
                        CategoryEntity(name = "Work", colorHex = "#7A5CFF", iconName = "work"),
                        CategoryEntity(name = "Personal", colorHex = "#FF4FCB", iconName = "person"),
                        CategoryEntity(name = "Ideas", colorHex = "#FFD84A", iconName = "lightbulb"),
                        CategoryEntity(name = "Shopping", colorHex = "#5DFF8F", iconName = "shopping_cart"),
                        CategoryEntity(name = "Goals", colorHex = "#3ED2FF", iconName = "star")
                    )
                )
            }
        }
    }

    private fun orbBoardExtractDeepMap(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(ORB_BOARD_MAIN_TAG, "Extracted DeepLink data: $map")
        orbBoardDeepLinkData = map
    }
    /////////////////

    private fun orbBoardStartConversionTimeout() {
        orbBoardConversionTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(30000)
            if (!orbBoardIsResumed) {
                Log.d(ORB_BOARD_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
                orbBoardResume(OrbBoardAppsFlyerState.OrbBoardError)
            }
        }
    }

    private fun orbBoardResume(state: OrbBoardAppsFlyerState) {
        ////////////
        orbBoardConversionTimeoutJob?.cancel()
        if (state is OrbBoardAppsFlyerState.OrbBoardSuccess) {
            val convData = state.orbBoardData ?: mutableMapOf()
            val deepData = orbBoardDeepLinkData ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!orbBoardIsResumed) {
                orbBoardIsResumed = true
                orbBoardConversionFlow.value =
                    OrbBoardAppsFlyerState.OrbBoardSuccess(merged)
            }
        } else {
            if (!orbBoardIsResumed) {
                orbBoardIsResumed = true
                orbBoardConversionFlow.value = state
            }
        }
    }

    private fun orbBoardGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(ORB_BOARD_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun orbBoardSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun orbBoardMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun orbBoardGetApi(url: String, client: OkHttpClient?): OrbBoardAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    companion object {
        var orbBoardInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val orbBoardConversionFlow: MutableStateFlow<OrbBoardAppsFlyerState> = MutableStateFlow(
            OrbBoardAppsFlyerState.OrbBoardDefault
        )
        var ORB_BOARD_FB_LI: String? = null
        const val ORB_BOARD_MAIN_TAG = "OrbBoardMainTag"
    }
}