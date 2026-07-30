package com.solux.luxup.taptap.feature.home.buttondetail.data

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 메모/이모지 다이얼로그의 이모지 피커(+)로 프리셋에 없는 이모지를 새로 고르면,
 * 다음에 열었을 때도 그리드에 남아있도록 기기에 저장해둔다. 서버에는 저장되지 않는 로컬 전용 값이다.
 */
@Singleton
class CustomEmojiStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getCustomEmojis(): List<String> =
        prefs.getString(KEY_EMOJIS, null)
            ?.split(DELIMITER)
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

    fun addCustomEmoji(emoji: String) {
        val current = getCustomEmojis()
        if (emoji in current) return
        prefs.edit { putString(KEY_EMOJIS, (current + emoji).joinToString(DELIMITER)) }
    }

    private companion object {
        const val PREFS_NAME = "custom_emoji_prefs"
        const val KEY_EMOJIS = "custom_emojis"
        const val DELIMITER = "||"
    }
}