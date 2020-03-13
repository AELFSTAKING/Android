package io.cex.exchange.kotlin.coreutil

import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Process

/**
 * 使用常规配置（applicationContext 和 Private 模式）获取一个 sp 对象。
 */
fun Context.getSp(filename: String): SharedPreferences =
        applicationContext.getSharedPreferences(filename, Context.MODE_PRIVATE)

/**
 * 获取 pid 对应的进程名称。如果没有获取到则返回 null。
 *
 * @param pid 进程 pid
 */
fun Context.getProcessName(pid: Int): String? {
    val am = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            ?: return null
    val processInfos = am.runningAppProcesses ?: return null
    val process = processInfos.firstOrNull {
        it.pid == pid
    }
    return process?.processName
}

/**
 * 判断当前执行代码的上下文是否在主进程中。
 */
fun Context.isInMainProcess(): Boolean {
    return packageName == getProcessName(Process.myPid())
}

fun Context.copyTextIntoClipboard(sequence: CharSequence) {
    val cbs = applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            ?: return
    val clipData = ClipData.newPlainText("", sequence)
    cbs.primaryClip = clipData
}