package com.akvelon.bitbuckler.model.repository.analytics

import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import java.io.Serializable

@Suppress("UNCHECKED_CAST")
fun <V> Map<String, V>.toBundle(bundle: Bundle = Bundle()): Bundle = bundle.apply {
    forEach {
        val k = it.key
        val v = it.value
        when (v) {
            is String -> putString(k, v)
            is Array<*> -> putStringArray(k, v as Array<out String>)
            is ArrayList<*> -> putStringArrayList(k, v as ArrayList<String>)
            is IBinder -> putBinder(k, v)
            is Bundle -> putBundle(k, v)
            is Byte -> putByte(k, v)
            is ByteArray -> putByteArray(k, v)
            is Char -> putChar(k, v)
            is CharArray -> putCharArray(k, v)
            is CharSequence -> putCharSequence(k, v)
            is Float -> putFloat(k, v)
            is FloatArray -> putFloatArray(k, v)
            is Parcelable -> putParcelable(k, v)
            is Serializable -> putSerializable(k, v)
            is Short -> putShort(k, v)
            is ShortArray -> putShortArray(k, v)
            else -> throw IllegalArgumentException("$v is of a type that is not currently supported")
        }
    }
}