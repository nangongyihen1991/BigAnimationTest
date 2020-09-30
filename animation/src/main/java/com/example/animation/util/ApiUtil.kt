package com.example.animation.util

import com.example.animation.api.Api
import com.example.animation.api.VAPApi
import com.example.animation.bean.AniBean

object ApiUtil {
    fun getApi(bean: AniBean): Api {
        return when (bean.type) {
            0 -> VAPApi()
            else -> VAPApi()
        }.also {
            it.setAniBean(bean)
        }
    }
}