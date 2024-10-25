package com.geeui.face

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SimpleExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.geeui.face.databinding.ActivityMainBinding
import com.geeui.face.service.AutoService
import com.letianpai.robot.components.network.system.SystemUtil
import com.renhejia.robot.commandlib.consts.RobotRemoteConsts
import java.io.File
import java.io.IOException
import java.util.*


//EXO


@UnstableApi
class MainActivity : AppCompatActivity(),
    AutoService.OnFaceChangeListener {
    private var surfaceAvailable: Boolean = false
    private var mediaPlayer : ExoPlayer? = null
    private var isPlaying: Boolean = false
    private lateinit var binding: ActivityMainBinding
    private var dispatchService: AutoService? = null
    private var currentMode = ""
    private var currentFace = ""
    private var intentFace = ""
    private var playAudioAndVideo = false
    private var dispatchConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder: AutoService.MyBinder = service as AutoService.MyBinder
            dispatchService = binder.service
            dispatchService?.setFaceChangeListener(this@MainActivity)
            dispatchService?.setRobotMode(currentMode)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            dispatchService?.setFaceChangeListener(null)
            dispatchService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "onCreate: " + System.currentTimeMillis());
        super.onCreate(savedInstanceState)
        // Hide status bar (notification bar)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentMode =
            intent.getStringExtra("mode") ?: RobotRemoteConsts.COMMAND_VALUE_CHANGE_MODE_ROBOT
        intentFace = intent.getStringExtra("face") ?: "h0059"
        bindDispatchService()

        Log.d(
            "MainActivity",
            "onCreate: " + intent.getStringExtra("face") + "   " + intent.getStringExtra("mode")
        )
        val showVideo = SystemUtil.get("com.geeui.showVideo", "")
        Log.d("MainActivity", "onCreate: showVideo:=  $showVideo")

        if (showVideo.isNullOrEmpty() || showVideo == "true") {
            playAudioAndVideo = false
            //binding.tips.visibility = View.GONE
            binding.playerView.visibility = View.VISIBLE
            initSurface()
        } else if (showVideo == "playAudioAndVideo") {
            playAudioAndVideo = true
            //binding.tips.visibility = View.GONE
            binding.playerView.visibility = View.VISIBLE
            initSurface()
        } else {
            playAudioAndVideo = false
            binding.playerView.visibility = View.GONE
            //binding.tips.visibility = View.VISIBLE
        }
    }

    private fun initSurface() {
        mediaPlayer = ExoPlayer.Builder(this@MainActivity).build()
        binding.playerView.player = mediaPlayer
        surfaceAvailable = true
        Log.d("MainActivity", "surface created ")
        if (currentFace.isNullOrBlank() && intentFace.isNotEmpty()) {
            openVideo(intentFace)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(
            "MainActivity",
            "onNewIntent: " + intent?.getStringExtra("face") + "   " + intent?.getStringExtra("mode")
        )
        val face = intent?.getStringExtra("face")
        if (face != null) {
            openVideo(face)
        }
        val mode = intent?.getStringExtra("mode")
        if (mode != null) {
            if (dispatchService != null) {
                Log.d("MainActivity", "onNewIntent: $mode");
                dispatchService!!.setRobotMode(mode)
                currentMode = mode
            } else {
                currentMode = mode
                bindDispatchService()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Hide navigation bar
        hideNavigationBar()
        binding.root.keepScreenOn = true
        if (isPlaying) {
            mediaPlayer?.play()
        }

    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause: ");
        binding.root.keepScreenOn = false
        if (mediaPlayer != null) {
            isPlaying = mediaPlayer?.isPlaying ?: false
            if (isPlaying) {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
                Log.d("MainActivity", "onPause: mediaPlayer?.stop()");
                finish()
            }
        } else {
            isPlaying = false
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop: ");
    }

    private fun hideNavigationBar() {
        val decorView = window.decorView
        decorView.setOnSystemUiVisibilityChangeListener { visibility: Int ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                // Hide the navigation bar when it is visible
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            }
        }
    }

    private fun bindDispatchService() {
        val intent = Intent(this@MainActivity, AutoService::class.java)
        bindService(intent, dispatchConnection, Context.BIND_AUTO_CREATE)
    }

    //    private var mediaPlayer: IjkMediaPlayer? = null


    private fun openVideo(tNname: String) {
        binding.playerView.useController = false
        binding.playerView.controllerShowTimeoutMs = 0
        if (!surfaceAvailable) {
            Log.d("MainActivity", "openVideo: Does not show video");
            return
        }

        var name=tNname
        if (playAudioAndVideo) {
            name = getRandomString(arrayOf("h0064", "h0065", "h0066", "h0067", "h0068", "h0069", "h0070", "h0071", "h0072", "h0073", "h0074", "h0075"))
        }

        if (currentFace.equals(name)) {
            Log.d("MainActivity",
                "openVideo: Same name, no need to switch to a new face for now $name"
            )
            return
        }
        currentFace = name

        mediaPlayer?.release()

        mediaPlayer = ExoPlayer.Builder(this@MainActivity).build()
        Log.e(
            "MainActivity",
            "openVideo_name: $name surfaceAvailable: $surfaceAvailable"
        )

        mediaPlayer?.let {
            binding.playerView.player = mediaPlayer

            val listener = object : Player.Listener {
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        // Media resource is ready to play
                        binding.playerView.player = it // Attach the player to the PlayerView
                        it.playWhenReady = true
                        it.play()
                        it.repeatMode = Player.REPEAT_MODE_ALL
                    }
                }
            }

            //mediaPlayer!!.addListener(listener)
            it.addListener(listener)



//            var afd = resources.assets.openFd("video/" + name + ".mp4")
//            Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.test);
//            val assetManager = assets
//            val filename = "your_file_name.mp4"
//            val assetFileDescriptor = assetManager.openFd(filename)

//            val uri = Uri.parse("content://" + afd.fileDescriptor)
//            val uriStr = "android.resource://" + this.packageName + "/" +"R.raw.h0001.mp4"
//            val uri = Uri.parse(uriStr)
//            var file= File("sdcard/assets/video/h0001.mp4")
            //var file= File("sdcard/assets/video/"+name+".mp4")


            try {
                val assetManager = assets
                val afd = assetManager.openFd("video/$name.mp4")
                val uri = Uri.parse("file:///android_asset/video/$name.mp4")
                val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(this)

                val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(uri))

                mediaPlayer?.setMediaSource(mediaSource)
                mediaPlayer?.prepare()
                mediaPlayer?.playWhenReady=true
            } catch (e: IOException) {
                Log.e("MainActivity", "Error opening video file: ${e.message}")
            }
            /*
             val assetManager = assets
            val afd = assetManager.openFd("video/$name.mp4")
            var file= resources.assets.openFd("video/$name.mp4")
            val videoUri= Uri.fromFile(file)
            val mediaSource =
                ProgressiveMediaSource.Factory(DefaultDataSource.Factory(this))
                    .createMediaSource(MediaItem.fromUri(videoUri))
//            it.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            Log.d("openVideo", "openVideo: afd"+videoUri)
            it.setMediaSource(mediaSource)
            it.prepare()
             */

        }

    }

    private fun getRandomString(group: Array<String>): String {
        val r = Random()
        return group[r.nextInt(group.size)]
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(dispatchConnection)
        Log.d("MainActivity", "onDestroy: ");
    }

    private fun enableWifi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val specifier: WifiNetworkSpecifier =
                WifiNetworkSpecifier.Builder().setSsid("LETIANPAI-5G")
                    .setWpa2Passphrase("Renhejia0801").build()


            val request: NetworkRequest =
                NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .setNetworkSpecifier(specifier).build()

            val networkCallback: ConnectivityManager.NetworkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    Log.d("TAG", "onAvailable: " + network)
                    // Wi-Fi connection established
                    (getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager).bindProcessToNetwork(
                        network
                    )
                }

                override fun onUnavailable() {
                    Log.d("TAG", "onUnavailable: ")
                    // Wi-Fi connection not available
                }
            }

            (getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager).requestNetwork(
                request, networkCallback
            )

        } else {

        }
    }

    override fun changeFace(faceName: String?) {
        Log.d("MainActivity", "changeFace: $faceName");
        runOnUiThread{
            faceName?.let { openVideo(it) }
        }
    }

    override fun finishProcess() {
        finish()
    }
}



//
//class MainActivity : AppCompatActivity(),
//    AutoService.OnFaceChangeListener {
//    private var surfaceAvailable: Boolean = false
//    private var isPlaying: Boolean = false
//    private lateinit var binding: ActivityMainBinding
//    private var dispatchService: AutoService? = null
//    private var currentMode = ""
//    private var currentFace = ""
//    private var intentFace = ""
//    private var dispatchConnection = object : ServiceConnection {
//        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//            val binder: AutoService.MyBinder = service as AutoService.MyBinder
//            dispatchService = binder.service
//            dispatchService?.setFaceChangeListener(this@MainActivity)
//            dispatchService?.setRobotMode(currentMode)
//        }
//
//        override fun onServiceDisconnected(name: ComponentName?) {
//            dispatchService?.setFaceChangeListener(null)
//            dispatchService = null
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        Log.d("MainActivity", "onCreate: " + System.currentTimeMillis());
//        super.onCreate(savedInstanceState)
//        // 隐藏状态栏（通知栏）
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
//        window.decorView.systemUiVisibility =
//            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        currentMode =
//            intent.getStringExtra("mode") ?: RobotRemoteConsts.COMMAND_VALUE_CHANGE_MODE_ROBOT
//        intentFace = intent.getStringExtra("face") ?: "h0059"
//        bindDispatchService()
//
//        Log.d(
//            "MainActivity",
//            "onCreate: " + intent.getStringExtra("face") + "   " + intent.getStringExtra("mode")
//        );
//        initSurface()
//    }
//
//    private fun initSurface() {
//        binding.playerView.holder.addCallback(object : SurfaceHolder.Callback {
//            override fun surfaceCreated(holder: SurfaceHolder) {
//                surfaceAvailable = true
//                Log.d("MainActivity", "surfaceCreated: ");
//                if (currentFace.isNullOrBlank() && intentFace.isNotEmpty()) {
//                    openVideo(intentFace)
//                }
//            }
//
//            override fun surfaceChanged(
//                holder: SurfaceHolder, format: Int, width: Int, height: Int
//            ) {
//                Log.d("MainActivity", "surfaceChanged: ");
//            }
//
//            override fun surfaceDestroyed(holder: SurfaceHolder) {
//                surfaceAvailable = false
//                Log.d("MainActivity", "surfaceDestroyed: ");
//            }
//        })
//    }
//
//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        Log.d(
//            "MainActivity",
//            "onNewIntent: " + intent?.getStringExtra("face") + "   " + intent?.getStringExtra("mode")
//        )
//        var face = intent?.getStringExtra("face")
//        if (face != null) {
//            openVideo(face)
//        }
//        var mode = intent?.getStringExtra("mode")
//        if (mode != null) {
//            if (dispatchService != null) {
//                Log.d("MainActivity", "onNewIntent: $mode");
//                dispatchService!!.setRobotMode(mode)
//                currentMode = mode
//            } else {
//                currentMode = mode
//                bindDispatchService()
//            }
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        // 隐藏导航栏
//        hideNavigationBar()
//        binding.root.keepScreenOn = true
//        if (isPlaying) {
//            mediaPlayer?.start()
//        }
//    }
//
//
//    private fun hideNavigationBar() {
//        val decorView = window.decorView
//        decorView.setOnSystemUiVisibilityChangeListener { visibility: Int ->
//            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
//                // 当导航栏可见时，隐藏导航栏
//                window.decorView.systemUiVisibility =
//                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//            }
//        }
//    }
//
//    private fun bindDispatchService() {
//        var intent = Intent(this@MainActivity, AutoService::class.java)
//        bindService(intent, dispatchConnection, Context.BIND_AUTO_CREATE)
//    }
//
//    private var mediaPlayer: IjkMediaPlayer? = null
//
//    @Synchronized
//    private fun openVideo(tNname: String) {
//
//        if (!surfaceAvailable) {
//            Log.d("MainActivity", "openVideo: 不展示视频");
//            return
//        }
//
//        var name = tNname
//
////        if (currentFace.equals(name)) {
////            Log.d("MainActivity", "openVideo: 名字相同，暂不用切换新的表情" + name);
////            return
////        }
//        currentFace = name
//        mediaPlayer?.release()
//        mediaPlayer = null
//        mediaPlayer = IjkMediaPlayer()
//        mediaPlayer!!.isLooping = false
//        Log.e(
//            "MainActivity",
//            "openVideo_name: $name surfaceAvailable: $surfaceAvailable"
//        );
//        mediaPlayer?.let {
//            /**
//             * 播放器选项（General options）：
//
//            "reconnect": 设置断线自动重连。值为 "1" 表示开启，"0" 表示关闭。
//            "analyzemaxduration": 设置最大分析时长，单位为秒。
//            "probesize": 设置数据包大小，单位为字节。
//            "flush_packets": 设置是否立即刷新数据包。值为 "1" 表示立即刷新，"0" 表示不刷新。
//            "packet-buffering": 设置数据包缓冲。值为 "1" 表示开启，"0" 表示关闭。
//            解码器选项（Decoder options）：
//
//            "mediacodec": 设置是否使用 MediaCodec 进行硬解码。值为 "1" 表示开启，"0" 表示关闭。
//            "mediacodec-auto-rotate": 设置是否自动旋转视频。值为 "1" 表示开启，"0" 表示关闭。
//            "mediacodec-handle-resolution-change": 设置是否处理分辨率变化。值为 "1" 表示开启，"0" 表示关闭。
//            声音选项（Audio options）：
//
//            "mediacodec-he-aac": 设置是否支持高级音频编码 (HE-AAC)。值为 "1" 表示开启，"0" 表示关闭。
//            "opensles": 设置是否使用 OpenSL ES 进行音频输出。值为 "1" 表示开启，"0" 表示关闭。
//            视频选项（Video options）：
//
//            "overlay-format": 设置视频显示格式。常用的值有 "fcc-rv16"、"fcc-rv32"、"fcc-iyuv" 等。
//            "framedrop": 设置是否丢帧以保持音视频同步。值为 "1" 表示开启，"0" 表示关闭。
//            "max-fps": 设置最大帧率，单位为帧每秒。
//            "fps": 设置视频帧率，单位为帧每秒。
//            "video-pictq-size": 设置视频图片队列大小。
//
//             */
//            //视频硬件解码
//            it.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1)
//            //音频硬件解码
//            it.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1)
//            //设置跳帧
//            it.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1)
//
//            it.setOnPreparedListener { mp ->
//                if (surfaceAvailable && mediaPlayer != null) {
//                    mediaPlayer!!.setDisplay(binding.playerView.holder)
//                    mediaPlayer!!.start()
//
//                }
//            }
//            it.setOnCompletionListener {
//                Thread.sleep(100)
//                it?.start()
//            }
//            it.setOnErrorListener { mp, what, extra ->
//                Log.d(
//                    "TAG",
//                    "openVideo: onError: $mp  what:$what  $extra"
//                )
//                return@setOnErrorListener false
//            }
//
//            val afd = resources.assets.openFd("video/$name.mp4")
//            val assetsFile = RawDataSourceProvider(afd)
//            it.setDataSource(assetsFile)
//            it.prepareAsync()
//
////            GlobalScope.launch(Dispatchers.IO) {
////                var path = ContentProviderQuery.query("$name.mp4", this@MainActivity)
////                Log.i("TAG", "openVideo: path=" + path)
////                var uri = Uri.parse(path)
////                it.setDataSource(this@MainActivity, uri)
////
////                it.prepareAsync()
////            }
//        }
//    }
//
//    private fun getRandomString(group: Array<String>): String {
//        val r = Random()
//        return group[r.nextInt(group.size)]
//    }
//
//    override fun onPause() {
//        super.onPause()
//        Log.d("MainActivity", "onPause: ");
//        mediaPlayer?.stop()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        Log.d("MainActivity", "onStop: ");
//        mediaPlayer?.release()
//        mediaPlayer = null
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        unbindService(dispatchConnection)
//        Log.d("MainActivity", "onDestroy: ");
//    }
//
//    override fun changeFace(faceName: String?) {
//        Log.d("MainActivity", "changeFace: $faceName");
//        faceName?.let { openVideo(it) }
//    }
//
//    var finished = false
//    override fun finishProcess() {
//        if (!finished) {
//            finished = true
//            finish()
//        }
//    }
//}



