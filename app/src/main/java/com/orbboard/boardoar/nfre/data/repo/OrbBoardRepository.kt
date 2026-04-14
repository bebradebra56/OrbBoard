package com.orbboard.boardoar.nfre.data.repo

import android.util.Log
import com.orbboard.boardoar.nfre.domain.model.OrbBoardEntity
import com.orbboard.boardoar.nfre.domain.model.OrbBoardParam
import com.orbboard.boardoar.nfre.presentation.app.OrbBoardApplication.Companion.ORB_BOARD_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OrbBoardApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun orbBoardGetClient(
        @Body jsonString: JsonObject,
    ): Call<OrbBoardEntity>
}


private const val ORB_BOARD_MAIN = "https://orbboard.com/"
class OrbBoardRepository {

    suspend fun orbBoardGetClient(
        orbBoardParam: OrbBoardParam,
        orbBoardConversion: MutableMap<String, Any>?
    ): OrbBoardEntity? {
        val gson = Gson()
        val api = orbBoardGetApi(ORB_BOARD_MAIN, null)

        val orbBoardJsonObject = gson.toJsonTree(orbBoardParam).asJsonObject
        orbBoardConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            orbBoardJsonObject.add(key, element)
        }
        return try {
            val orbBoardRequest: Call<OrbBoardEntity> = api.orbBoardGetClient(
                jsonString = orbBoardJsonObject,
            )
            val orbBoardResult = orbBoardRequest.awaitResponse()
            Log.d(ORB_BOARD_MAIN_TAG, "Retrofit: Result code: ${orbBoardResult.code()}")
            if (orbBoardResult.code() == 200) {
                Log.d(ORB_BOARD_MAIN_TAG, "Retrofit: Get request success")
                Log.d(ORB_BOARD_MAIN_TAG, "Retrofit: Code = ${orbBoardResult.code()}")
                Log.d(ORB_BOARD_MAIN_TAG, "Retrofit: ${orbBoardResult.body()}")
                orbBoardResult.body()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(ORB_BOARD_MAIN_TAG, "Retrofit: Get request failed")
            Log.d(ORB_BOARD_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun orbBoardGetApi(url: String, client: OkHttpClient?) : OrbBoardApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
