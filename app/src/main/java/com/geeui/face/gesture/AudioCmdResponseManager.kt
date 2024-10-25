package com.geeui.face.gesture

import android.content.Context
import android.os.RemoteException
import android.util.Log
import com.renhejia.robot.commandlib.consts.RobotRemoteConsts
import com.renhejia.robot.commandlib.parser.motion.Motion
import com.renhejia.robot.gesturefactory.parser.GestureData
import com.renhejia.robot.letianpaiservice.ILetianpaiService

/**
 * 语音命令执行单元
 *
 * @author liujunbin
 */
class AudioCmdResponseManager private constructor(context: Context) {
    private var mContext: Context? = null

    init {
        init(context)
    }

    private fun init(context: Context) {
        this.mContext = context
    }

    fun responseGestures(
        list: ArrayList<GestureData?>,
        taskId: Int,
        iLetianpaiService: ILetianpaiService
    ) {
        GestureDataThreadExecutor.instance.execute {
            Log.d("AudioCmdResponseManager", "run start: taskId:$taskId")
            for (gestureData in list) {
                responseGestureData(gestureData, iLetianpaiService)
                try {
                    if (gestureData?.interval == 0L) {
                        Thread.sleep(2000)
                    } else {
                        gestureData?.let { Thread.sleep(it.interval) }
                    }
                } catch (e: InterruptedException) {
                    throw RuntimeException(e)
                }
            }
            Log.d("AudioCmdResponseManager", "run end: taskId:$taskId")
            GestureCallback.instance.setGesturesComplete("list", taskId)
        }
    }

    companion object {
        private var instance: AudioCmdResponseManager? = null
        fun getInstance(context: Context): AudioCmdResponseManager {
            synchronized(AudioCmdResponseManager::class.java) {
                if (instance == null) {
                    instance = AudioCmdResponseManager(context.applicationContext)
                }
                return instance!!
            }
        }

        fun responseGestureData(gestureData: GestureData?, iLetianpaiService: ILetianpaiService) {
            logGestureData(gestureData)
            if (gestureData == null) {
                return
            }
            try {
                if (gestureData.ttsInfo != null) {
                    //响应单元在Launcher
//                RhjAudioManager.getInstance().speak(gestureData.getTtsInfo().getTts());
                    iLetianpaiService.setTTS("speakText", gestureData.ttsInfo!!.tts)
                }
                if (gestureData.expression != null) {
                    //响应单元在Launcher
//                iLetianpaiService.setCommand(new LtpCommand(RobotRemoteConsts.COMMAND_TYPE_FACE, ((gestureData.getExpression()).toString())));
                    iLetianpaiService.setExpression(
                        RobotRemoteConsts.COMMAND_TYPE_FACE,
                        (gestureData.expression).toString()
                    )
                }
                if (gestureData.antennalight != null) {
                    //响应单元在MCUservice
//                iLetianpaiService.setCommand(new LtpCommand(RobotRemoteConsts.COMMAND_TYPE_ANTENNA_LIGHT, ((gestureData.getAntennalight()).toString())));
                    iLetianpaiService.setMcuCommand(
                        RobotRemoteConsts.COMMAND_TYPE_ANTENNA_LIGHT,
                        (gestureData.antennalight).toString()
                    )
                }
                if (gestureData.soundEffects != null) {
                    //响应单元在AudioService
//                iLetianpaiService.setCommand(new LtpCommand(RobotRemoteConsts.COMMAND_TYPE_SOUND, ((gestureData.getSoundEffects()).toString())));
                    iLetianpaiService.setAudioEffect(
                        RobotRemoteConsts.COMMAND_TYPE_SOUND,
                        (gestureData.soundEffects).toString()
                    )
                }
                if (gestureData.footAction != null) {
                    //响应单元在MCUservice
//                iLetianpaiService.setCommand(new LtpCommand(RobotRemoteConsts.COMMAND_TYPE_MOTION, (gestureData.getFootAction()).toString()));
                    iLetianpaiService.setMcuCommand(
                        RobotRemoteConsts.COMMAND_TYPE_MOTION,
                        (gestureData.footAction).toString()
                    )
                } else {
                    val motion = Motion()
                    //0会立即停止当前的动作
                    motion.number = 0
                    //                iLetianpaiService.setCommand(new LtpCommand(RobotRemoteConsts.COMMAND_TYPE_MOTION, motion.toString()));
                    iLetianpaiService.setMcuCommand(
                        RobotRemoteConsts.COMMAND_TYPE_MOTION,
                        motion.toString()
                    )
                }
                if (gestureData.earAction != null) {
                    //响应单元在MCUservice
//                iLetianpaiService.setCommand(new LtpCommand(RobotRemoteConsts.COMMAND_TYPE_ANTENNA_MOTION, (gestureData.getEarAction()).toString()));
                    iLetianpaiService.setMcuCommand(
                        RobotRemoteConsts.COMMAND_TYPE_ANTENNA_MOTION,
                        (gestureData.earAction).toString()
                    )
                } else {
                    //天线
//                AntennaMotion antennaMotion=new AntennaMotion("sturn");
//                iLetianpaiService.setCommand(new LtpCommand(RobotRemoteConsts.COMMAND_TYPE_ANTENNA_MOTION, (gestureData.getEarAction()).toString()));
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        private fun logGestureData(gestureData: GestureData?) {
//        String log = "";
//        if (gestureData.getFootAction() != null && !gestureData.getFootAction().getMotion().isEmpty()) {
//            log += "   动作:" + gestureData.getFootAction().getDesc();
//        }
//        if (gestureData.getExpression() != null && !gestureData.getExpression().getFace().isEmpty()) {
//            log += "   表情：" + gestureData.getExpression().getDesc();
//        }
//        if (gestureData.getSoundEffects() != null && !gestureData.getSoundEffects().getSound().isEmpty()) {
//            log += "   声音：" + gestureData.getSoundEffects().getDesc();
//        }
//        if (gestureData.getEarAction() != null && !gestureData.getEarAction().getAntenna_motion().isEmpty()) {
//            log += "   耳朵：" + gestureData.getEarAction().getAntenna_motion().isEmpty();
//        }
//
//        if (gestureData.getAntennalight() != null && !gestureData.getAntennalight().getAntenna_light().isEmpty()) {
//            log += "    天线" + gestureData.getAntennalight().getAntenna_light();
//        }
//
            Log.d("AudioCmdResponseManager", "解析给实际执行单元 $gestureData")
        }
    }
}
