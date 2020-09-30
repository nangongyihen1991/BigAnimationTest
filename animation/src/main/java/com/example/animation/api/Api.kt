package com.example.animation.api

import android.content.Context
import android.view.View
import com.example.animation.bean.AniBean
import com.example.animation.manager.OnAnimationListener

interface Api {

    fun setAnimationListener(listener: OnAnimationListener?)

    fun getView(context: Context): View?

    fun setAniBean(bean: AniBean)

    fun startAni()

    fun finishAni()
}