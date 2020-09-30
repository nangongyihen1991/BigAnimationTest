package com.example.animation.manager

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.FrameLayout
import com.example.animation.bean.AniBean
import com.example.animation.view.AnimationView
import java.lang.ref.WeakReference
import java.util.*

class AnimationManager(parent: FrameLayout) :
    OnAnimationListener,
    Application.ActivityLifecycleCallbacks {
    companion object {
        private const val TAG = "AnimationManager"
    }

    private val mAniQue = LinkedList<AniBean>()
    private var mIsRunning = false
    private var mContainer: WeakReference<FrameLayout>? = null
    private var mAnimationView: AnimationView? = null
    private var mIsUseAni = false
    private val mHandler by lazy {
        android.os.Handler(Looper.getMainLooper())
    }

    init {
        mContainer = WeakReference(parent)
        (parent.context.applicationContext as Application).registerActivityLifecycleCallbacks(this)
    }

    fun addOrStartAni(bean: AniBean) {
        mAniQue.add(bean)
        startAni()
    }

    private fun startAni() {
        if (!mIsRunning && mIsUseAni) {
            val bean = mAniQue.peek()
            if (bean != null) {
                getContainer()?.run {
                    mIsRunning = true
                    mAniQue.remove()
                    if (mAnimationView == null) {
                        mAnimationView = AnimationView(context)
                        addView(mAnimationView)
                    } else {
                        mAnimationView?.finish()
                    }
                    mAnimationView?.show(bean, this@AnimationManager)
                }
            } else {
                mIsRunning = false
            }
        }
    }

    private fun removeAnimationView() {
        if (mAnimationView != null) {
            mAnimationView?.finish()
            getContainer()?.removeView(mAnimationView)
            mAnimationView = null
        }
    }

    fun recycle() {
        removeAnimationView()
        mAniQue.clear()
        (getContainer()?.context?.applicationContext as Application).unregisterActivityLifecycleCallbacks(
            this
        )
        mContainer?.clear()
    }

    private fun getContainer() = mContainer?.get()

    override fun onFinish() {
        mHandler.removeCallbacksAndMessages(null)
        mHandler.postDelayed({
            if (mIsRunning) {
                mIsRunning = false
                mAnimationView?.finish()
                startAni()
            }
        }, 100)
    }

    override fun onActivityPaused(p0: Activity) {
        Log.i(TAG, "onActivityPaused")
        mIsUseAni = false
        mIsRunning = false
        removeAnimationView()
    }

    override fun onActivityStarted(p0: Activity) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityResumed(p0: Activity) {
        Log.i(TAG, "onActivityResumed")
        mIsUseAni = true
        startAni()
    }
}