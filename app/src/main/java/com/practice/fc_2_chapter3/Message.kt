package com.practice.fc_2_chapter3

import com.google.gson.annotations.SerializedName

data class Message (
    // 난독화 과정 때문에 -> SerializedName("message") 사용해줌 -> val b 이런식으로 해도 message를 받아올 수 있음
    @SerializedName("message")
    val message: String
)