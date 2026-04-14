package com.orbboard.boardoar.nfre.domain.model

import com.google.gson.annotations.SerializedName


private const val ORB_BOARD_A = "com.orbboard.boardoar"
private const val ORB_BOARD_B = "orbboard-ab2ec"
data class OrbBoardParam (
    @SerializedName("af_id")
    val orbBoardAfId: String,
    @SerializedName("bundle_id")
    val orbBoardBundleId: String = ORB_BOARD_A,
    @SerializedName("os")
    val orbBoardOs: String = "Android",
    @SerializedName("store_id")
    val orbBoardStoreId: String = ORB_BOARD_A,
    @SerializedName("locale")
    val orbBoardLocale: String,
    @SerializedName("push_token")
    val orbBoardPushToken: String,
    @SerializedName("firebase_project_id")
    val orbBoardFirebaseProjectId: String = ORB_BOARD_B,

    )