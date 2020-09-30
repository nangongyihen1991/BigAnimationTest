package com.example.animationtest

import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.animation.bean.AniBean
import com.example.animation.manager.AnimationManager
import com.example.animation.util.FileUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private var mAnimationManager: AnimationManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        mAnimationManager = AnimationManager(window.decorView as FrameLayout)
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            play()
        }
    }

    private fun play() {
        val files = Array(1) {
            "vapx.mp4"
        }
        FileUtil.copyAssetsToStorage(this, dir, files) {
            runOnUiThread {
                for (i in 0..1) {
                    val bean = AniBean(0, dir + "/" + files[0])
                    mAnimationManager?.addOrStartAni(bean)
                }
            }
        }
    }

    private val dir by lazy {
        // 存放在sdcard应用缓存文件中
        getExternalFilesDir(null)?.absolutePath ?: Environment.getExternalStorageDirectory().path
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mAnimationManager?.recycle()
    }
}