package com.example.animation.api

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import com.example.animation.R
import com.example.animation.bean.AniBean
import com.example.animation.manager.OnAnimationListener
import com.tencent.qgame.animplayer.AnimConfig
import com.tencent.qgame.animplayer.AnimView
import com.tencent.qgame.animplayer.inter.IAnimListener
import com.tencent.qgame.animplayer.inter.IFetchResource
import com.tencent.qgame.animplayer.inter.OnResourceClickListener
import com.tencent.qgame.animplayer.mix.Resource
import java.io.File
import java.util.*

/**
 * 使用企鹅电竞的VAP框架播放动画
 */
class VAPApi : Api, IAnimListener {
    companion object {
        private const val TAG = "VAPApi"
    }

    private lateinit var mAniBean: AniBean
    private var mAnimView: AnimView? = null
    private var mListener: OnAnimationListener? = null
    override fun getView(context: Context): View? {
        mAnimView = AnimView(context)
        init()
        return mAnimView
    }

    override fun setAnimationListener(listener: OnAnimationListener?) {
        mListener = listener
    }

    override fun setAniBean(bean: AniBean) {
        mAniBean = bean
    }

    override fun startAni() {
        // 播放前强烈建议检查文件的md5是否有改变
        // 因为下载或文件存储过程中会出现文件损坏，导致无法播放
        val file = File(mAniBean.url)
        if (file.isFile && file.exists()) {
            mAnimView?.startPlay(file)
        } else {
            mListener?.onFinish()
        }
    }

    override fun finishAni() {
        if (mAnimView?.isRunning() == true) {
            Log.i(TAG, "finishAni stopPlay")
            mAnimView?.stopPlay()
        }
    }


    private fun init() {
        /**
         * 注册资源获取类
         */
        mAnimView?.setFetchResource(object : IFetchResource {
            /**
             * 获取图片资源
             * 无论图片是否获取成功都必须回调 result 否则会无限等待资源
             */
            override fun fetchImage(resource: Resource, result: (Bitmap?) -> Unit) {
                /**
                 * srcTag是素材中的一个标记，在制作素材时定义
                 * 解析时由业务读取tag决定需要播放的内容是什么
                 * 比如：一个素材里需要显示多个头像，则需要定义多个不同的tag，表示不同位置，需要显示不同的头像，文字类似
                 */
                val srcTag = resource.tag

                if (srcTag == "[sImg1]") { // 此tag是已经写入到动画配置中的tag
                    val options = BitmapFactory.Options()
                    options.inScaled = false
                    result(
                        BitmapFactory.decodeResource(
                            mAnimView?.resources,
                            R.drawable.head1,
                            options
                        )
                    )
                } else {
                    result(null)
                }
            }

            /**
             * 获取文字资源
             */
            override fun fetchText(resource: Resource, result: (String?) -> Unit) {
                val str = "恭喜 No.${1000 + Random().nextInt(8999)}用户 升神"
                val srcTag = resource.tag

                if (srcTag == "[sTxt1]") { // 此tag是已经写入到动画配置中的tag
                    result(str)
                } else {
                    result(null)
                }
            }

            /**
             * 播放完毕后的资源回收
             */
            override fun releaseResource(resources: List<Resource>) {
                resources.forEach {
                    it.bitmap?.recycle()
                }
            }
        })

        // 注册点击事件监听
        mAnimView?.setOnResourceClickListener(object : OnResourceClickListener {
            override fun onClick(resource: Resource) {
            }
        })

        // 注册动画监听
        mAnimView?.setAnimListener(this)
    }

    /**
     * 视频信息准备好后的回调，用于检查视频准备好后是否继续播放
     * @return true 继续播放 false 停止播放
     */
    override fun onVideoConfigReady(config: AnimConfig): Boolean {
        mAnimView?.post {
            val w = getDisplayWidth(mAnimView!!.context)
            val lp = mAnimView!!.layoutParams
            lp.width = if (w == 0) dp2px(mAnimView!!.context, 400f).toInt() else w
            lp.height = (w * config.height * 1f / config.width).toInt()
            mAnimView?.layoutParams = lp
        }
        return true
    }

    /**
     * 视频开始回调
     */
    override fun onVideoStart() {
        Log.i(TAG, "onVideoStart")
    }

    /**
     * 视频渲染每一帧时的回调
     * @param frameIndex 帧索引
     */
    override fun onVideoRender(frameIndex: Int, config: AnimConfig?) {
    }

    /**
     * 视频播放结束(失败也会回调onComplete)
     */
    override fun onVideoComplete() {
        Log.i(TAG, "onVideoComplete")
        mListener?.onFinish()
    }

    /**
     * 播放器被销毁情况下会调用onVideoDestroy
     */
    override fun onVideoDestroy() {
        Log.i(TAG, "onVideoDestroy")
    }

    /**
     * 失败回调
     * 一次播放时可能会调用多次，建议onFailed只做错误上报
     * @param errorType 错误类型
     * @param errorMsg 错误消息
     */
    override fun onFailed(errorType: Int, errorMsg: String?) {
        Log.i(TAG, "onFailed errorType=$errorType errorMsg=$errorMsg")
    }


    private fun dp2px(context: Context, dp: Float): Float {
        val scale = context.resources.displayMetrics.density
        return dp * scale + 0.5f
    }

    private fun getDisplayWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }
}