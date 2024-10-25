package com.geeui.face

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.geeui.face.databinding.ActivityExoplayerBinding

import java.io.File

class ExoplayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExoplayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExoplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        play()
    }

    @OptIn(UnstableApi::class)
    fun play() {
        val player = ExoPlayer.Builder(this@ExoplayerActivity).build()
        binding.playerView.player = player

// 设置透明背景
        binding.playerView.background = ColorDrawable(Color.TRANSPARENT)

// 创建 MediaSource
        var file=File("sdcard/assets/video/h0005.mp4")
//        var file=File("sdcard/b.mp4")
       val videoUri= Uri.fromFile(file)
//        val videoUri =  Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.a1);
        val mediaSource =
            ProgressiveMediaSource.Factory(DefaultDataSource.Factory(this@ExoplayerActivity))
                .createMediaSource(MediaItem.fromUri(videoUri))

// 准备播放器并播放视频

        player.setMediaSource(mediaSource)
        player.prepare()
        player.play()

    }

}