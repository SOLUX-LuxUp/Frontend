package com.solux.luxup.taptap.core.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * 버튼·팀 아이콘에 사용하는 고정 색상 팔레트 12색.
 *
 * 서버는 색상 이름 문자열("red", "orange")로 내려주고 프론트에서 hex로 매핑한다.
 * 다만 일부 API가 아직 hex(#RRGGBB)로 내려올 수 있어 양쪽 모두 파싱한다.
 *
 * TODO: 모든 API가 이름 문자열로 통일된 것이 확인되면 hex 분기 제거
 */
enum class IconColor(
    val key: String,
    val color: Color,
) {
    RED("red", Color(0xFFFF7171)),
    ORANGE("orange", Color(0xFFFFC760)),
    YELLOW("yellow", Color(0xFFFCED66)),
    GREEN("green", Color(0xFF83FF65)),
    CYAN("cyan", Color(0xFF63F2FF)),
    BLUE("blue", Color(0xFF536AFF)),
    INDIGO("indigo", Color(0xFF282471)),
    PURPLE("purple", Color(0xFFDE76FB)),
    PINK("pink", Color(0xFFFF8EEA)),
    GREY("grey", Color(0xFFB7B7B7)),
    DARK_GREY("darkgrey", Color(0xFF707070)),
    BLACK("black", Color(0xFF1A1A1A)),
    ;

    /** 서버 전송 또는 디버깅용 hex 문자열 (#RRGGBB) */
    val hex: String
        get() = "#%06X".format(color.toArgb() and 0xFFFFFF)

    companion object {
        val DEFAULT = BLUE

        /** 아이콘 선택 모달의 그리드 배열 (2행 × 6열) */
        val palette: List<IconColor> = entries

        /**
         * 서버가 내려준 iconColor 문자열을 파싱한다.
         * 색상 이름("red")과 hex("#FF7171") 모두 지원.
         */
        fun from(value: String?): IconColor {
            if (value.isNullOrBlank()) return DEFAULT
            val normalized = value.trim()

            entries.firstOrNull { it.key.equals(normalized, ignoreCase = true) }
                ?.let { return it }

            val hex = normalized.removePrefix("#").uppercase()
            return entries.firstOrNull { it.hex.removePrefix("#") == hex } ?: DEFAULT
        }
    }
}

/**
 * 팔레트에 없는 임의 hex도 렌더해야 하는 경우 사용.
 * 파싱 실패 시 fallback 색을 반환한다.
 *
 * 기존 각 파일에 흩어져 있던 private hex 파싱 extension을 대체한다.
 */
fun parseHexColor(
    hex: String?,
    fallback: Color = IconColor.DEFAULT.color,
): Color {
    if (hex.isNullOrBlank()) return fallback
    return runCatching {
        Color(("FF" + hex.trim().removePrefix("#")).toLong(16))
    }.getOrDefault(fallback)
}