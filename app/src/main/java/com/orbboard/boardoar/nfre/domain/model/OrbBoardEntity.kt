package com.orbboard.boardoar.nfre.domain.model

import com.google.gson.annotations.SerializedName


data class OrbBoardEntity (
    @SerializedName("ok")
    val orbBoardOk: String,
    @SerializedName("url")
    val orbBoardUrl: String,
    @SerializedName("expires")
    val orbBoardExpires: Long,
)