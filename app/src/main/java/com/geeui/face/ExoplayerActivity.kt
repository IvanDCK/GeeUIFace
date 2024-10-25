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

        // Setting a transparent background
        binding.playerView.background = ColorDrawable(Color.TRANSPARENT)

        // Establish MediaSource
        val file=File("sdcard/assets/video/h0005.mp4")
//        var file=File("sdcard/b.mp4")
       val videoUri= Uri.fromFile(file)
//        val videoUri =  Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.a1);
        val mediaSource =
            ProgressiveMediaSource.Factory(DefaultDataSource.Factory(this@ExoplayerActivity))
                .createMediaSource(MediaItem.fromUri(videoUri))

        // Prepare the player and play the video

        player.setMediaSource(mediaSource)
        player.prepare()
        player.play()

    }

}