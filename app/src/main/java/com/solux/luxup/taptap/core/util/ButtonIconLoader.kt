package com.solux.luxup.taptap.core.util

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import com.solux.luxup.taptap.R

// drawable 폴더에 "bt_" 로 시작하는 png 아이콘을 추가하면 자동으로 인식됨 (bt_ 로 시작해도 xml 벡터는 제외)
fun loadButtonIconResIds(context: Context): List<Int> =
    R.drawable::class.java.fields
        .filter { it.name.startsWith("bt_") }
        .sortedBy { it.name }
        .mapNotNull { field -> runCatching { field.getInt(null) }.getOrNull() }
        .filter { resId ->
            runCatching { ContextCompat.getDrawable(context, resId) }.getOrNull() is BitmapDrawable
        }