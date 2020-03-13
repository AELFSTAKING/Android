@file:Suppress("UNCHECKED_CAST")

package io.cex.exchange.kotlin.coreutil

import android.content.Context
import android.content.SharedPreferences
import io.alf.exchange.App


/**
 * 默认 SP 文件。
 */
private const val SP_FILE_COMMON = "sp_system_config"

/**
 * sp文件-用来处理欢迎页、引导页以及广告页。
 */
const val SP_FILE_PUBLIC = "coinPublicPage"

/**
 * 共享存储工具类
 */
const val SP_ROCK_DATA = "rock_data"


/**
 * 获取特定 SP 条目的值。
 */
@JvmOverloads
fun <T> getSpValue(context: Context = App.getContext(),
                   filename: String = SP_FILE_COMMON,
                   key: String,
                   defaultVal: T): T {
    val sp = context.getSp(filename)
    return when (defaultVal) {
        is Boolean -> sp.getBoolean(key, defaultVal) as T
        is String -> sp.getString(key, defaultVal) as T
        is Int -> sp.getInt(key, defaultVal) as T
        is Long -> sp.getLong(key, defaultVal) as T
        is Float -> sp.getFloat(key, defaultVal) as T
        is Set<*> -> sp.getStringSet(key, defaultVal as Set<String>) as T
        else -> throw IllegalArgumentException("Unrecognized default value $defaultVal")
    }
}

/**
 * 修改单条 SP 记录。如需修改多条或清空SP，推荐使用 [editSp]。
 */
@JvmOverloads
fun <T> putSpValue(context: Context = App.getContext(),
                   filename: String = SP_FILE_COMMON,
                   key: String,
                   value: T) {
    val editor = context.getSp(filename).edit()

    when (value) {
        is Boolean -> editor.putBoolean(key, value)
        is String -> editor.putString(key, value)
        is Int -> editor.putInt(key, value)
        is Long -> editor.putLong(key, value)
        is Float -> editor.putFloat(key, value)
        is Set<*> -> editor.putStringSet(key, value as Set<String>)
        else -> throw UnsupportedOperationException("Unrecognized value $value")
    }

    editor.apply()
}


/**
 * 批量修改或清空 SP。
 */
@JvmOverloads
fun editSp(context: Context = App.getContext(),
           filename: String = SP_FILE_COMMON,
           action: (SharedPreferences.Editor) -> Unit) {
    context.getSp(filename).edit().apply(action).apply()
}