package com.geeui.face.consts

/**
 * 姿态标签常量
 *
 * @author liujunbin
 */
object RGestureConsts {
    const val GESTURE_ID_REFORM_DEFAULT: Int = 1000
    const val GESTURE_ID_DEFAULT: Int = 1001 //默认姿态
    const val GESTURE_ID_STANDBY_DEFAULT: Int = 1002 //默认待机
    const val GESTURE_ID_FOUND_PEOPLE: Int = 1003
    const val GESTURE_ID_STAND_GESTURE: Int = 1004
    const val GESTURE_ID_RANDOM_GESTURE: Int = 1005
    const val GESTURE_ID_SEARCH_PEOPLE2: Int = 1006
    const val GESTURE_ID_STAND_RESET: Int = 1111
    const val GESTURE_ID_SEARCH_TIME_8: Int = 1008

    /**
     * 默认姿态
     */
    const val GESTURE_CHANGE_STANDBY: Int = 1011

    /**
     * 找人动作
     */
    const val GESTURE_CHANGE_PEOPLE: Int = 1012

    /**
     * 待机默认姿态
     */
    const val GESTURE_CHANGE_CLASS_B: Int = 1013

    /**
     * C类行为
     */
    const val GESTURE_CHANGE_CLASS_C: Int = 1014

    /**
     * D类行为
     */
    const val GESTURE_CHANGE_CLASS_D: Int = 1015

    /**
     * 定义的姿态库中的姿态随机
     */
    const val GESTURE_CHANGE_ALL: Int = 1016

    /**
     * 找人结果
     */
    const val GESTURE_SEARCH_PEOPLE_RESULT: Int = 1017

    /**
     * 常态姿态
     */
    const val GESTURE_CHANGE_COMMON_DISPLAY: Int = 1018

    /**
     * 找人执行完毕，关闭
     */
    const val GESTURE_SEARCH_PEOPLE_RESULT_CLOSE: Int = 1019
    const val GESTURE_NEW_ROBOT_POSE: Int = 1020

    /**
     * 远程漫步姿态执行完成
     */
    const val GESTURE_REMOTESTROLL: Int = 1021

    /**
     * 找人开始之后延时没有结果则自动结束
     */
    const val GESTURE_SEARCH_PEOPLE_START_DELAY_STOP: Int = 1022

    /**
     * 整点报时
     */
    const val GESTURE_ID_TIME_KEEPER: Int = 1099

    /**
     * 悬空开始
     */
    const val GESTURE_ID_DANGLING_START: Int = 2100

    /**
     * 悬空结束
     */
    const val GESTURE_ID_DANGLING_END: Int = 2101

    /**
     * 倒下开始
     */
    const val GESTURE_ID_FALLDOWN_START: Int = 2102

    /**
     * 倒下结束
     */
    const val GESTURE_ID_FALLDOWN_END: Int = 2103

    /**
     * 单击
     */
    const val GESTURE_ID_TAP: Int = 2104

    /**
     * 双击
     */
    const val GESTURE_ID_DOUBLE_TAP: Int = 2105

    /**
     * 长按
     */
    const val GESTURE_ID_LONG_PRESS: Int = 2106

    /**
     * 防跌落往前
     */
    const val GESTURE_ID_CLIFF_FORWARD: Int = 2107

    /**
     * 防跌落往后
     */
    const val GESTURE_ID_CLIFF_BACKEND: Int = 2108

    /**
     * 防跌落往左
     */
    const val GESTURE_ID_CLIFF_LEFT: Int = 2109

    /**
     * 防跌落往右
     */
    const val GESTURE_ID_CLIFF_RIGHT: Int = 2110

    /**
     * 24小时常态姿态
     */
    const val GESTURE_ID_24_HOUR: Int = 3107

    /**
     * 解除绑定
     */
    const val GESTURE_ID_REST_BIND: Int = 3108

    /**
     * 自我介绍姿态
     */
    const val GESTURE_ID_SELF_NTRODUCTION: Int = 3109
}
