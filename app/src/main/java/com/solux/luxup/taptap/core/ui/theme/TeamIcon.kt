package com.solux.luxup.taptap.core.ui.theme

import androidx.annotation.DrawableRes
import com.solux.luxup.taptap.R

/**
 * 팀 프로필에 사용하는 고정 아이콘 5종.
 * 서버 iconName 문자열과 drawable 리소스를 매핑한다.
 *
 * TODO: key / drawable 이름 백엔드 확정 후 교체
 */
enum class TeamIcon(
    val key: String,
    @DrawableRes val resId: Int,
) {
    PEOPLE("people", R.drawable.ic_team_people),
    HOME("home", R.drawable.ic_team_home),
    HEART("heart", R.drawable.ic_team_heart),
    STUDY("study", R.drawable.ic_team_study),
    EXERCISE("exercise", R.drawable.ic_team_exercise),
    ;

    companion object {
        val DEFAULT = PEOPLE

        /** 아이콘 선택 모달에 노출할 순서 */
        val all: List<TeamIcon> = entries

        fun from(key: String?): TeamIcon {
            if (key.isNullOrBlank()) return DEFAULT
            return entries.firstOrNull {
                it.key.equals(key.trim(), ignoreCase = true)
            } ?: DEFAULT
        }
    }
}