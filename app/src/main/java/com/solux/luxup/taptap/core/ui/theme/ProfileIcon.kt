package com.solux.luxup.taptap.core.ui.theme

import androidx.annotation.DrawableRes
import com.solux.luxup.taptap.R

/**
 * 유저 프로필에 사용하는 고정 아이콘 4종.
 *
 * User API의 profileImageUrl 은 문자열 필드 하나뿐이라(iconName 같은 별도 필드 없음),
 * 로컬 아이콘을 골랐을 때는 "icon:{key}" 토큰으로 인코딩해 그 필드에 그대로 저장한다.
 * 실제 이미지 URL과는 [PREFIX] 유무로 구분한다.
 */
enum class ProfileIcon(
    val key: String,
    @DrawableRes val resId: Int,
) {
    BLUE("blue", R.drawable.ic_profile_blue),
    ORANGE("orange", R.drawable.ic_profile_orange),
    GREEN("green", R.drawable.ic_profile_green),
    PINK("pink", R.drawable.ic_profile_pink),
    ;

    companion object {
        private const val PREFIX = "icon:"

        val DEFAULT = BLUE
        val all: List<ProfileIcon> = entries

        fun from(key: String?): ProfileIcon {
            if (key.isNullOrBlank()) return DEFAULT
            return entries.firstOrNull { it.key.equals(key.trim(), ignoreCase = true) } ?: DEFAULT
        }

        /** profileImageUrl 값이 로컬 아이콘 토큰이면 해당 아이콘을, 아니면(실제 이미지 URL) null 을 반환 */
        fun parseToken(profileImageUrl: String?): ProfileIcon? {
            if (profileImageUrl == null || !profileImageUrl.startsWith(PREFIX)) return null
            return from(profileImageUrl.removePrefix(PREFIX))
        }

        fun toToken(icon: ProfileIcon): String = "$PREFIX${icon.key}"
    }
}