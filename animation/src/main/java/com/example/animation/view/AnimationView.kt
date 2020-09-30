package com.example.animation.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.example.animation.api.Api
import com.example.animation.bean.AniBean
import com.example.animation.manager.OnAnimationListener
import com.example.animation.util.ApiUtil

class AnimationView(
    private val mContext: Context,
    mAttr: AttributeSet?,
    defStyleAttr: Int
) : FrameLayout(mContext, mAttr, defStyleAttr) {

    companion object {
        private val ANIMATION_VIEW_ID: Int = com.example.animation.R.id.animation_view_id
    }


    constructor(mContext: Context) : this(mContext, null, 0)

    private var mApi: Api? = null
    fun show(bean: AniBean, listener: OnAnimationListener) {
        finish()
        mApi = ApiUtil.getApi(bean)
        val view = mApi?.getView(mContext)
        if (view != null) {
            view.id = ANIMATION_VIEW_ID
            mApi?.setAnimationListener(listener)
            addView(view)
            mApi?.startAni()
        } else {
            listener?.onFinish()
        }
    }

    fun finish() {
        mApi?.setAnimationListener(null)
        mApi?.finishAni()
        findViewById<View>(ANIMATION_VIEW_ID)?.let {
            removeView(it)
        }
    }

}