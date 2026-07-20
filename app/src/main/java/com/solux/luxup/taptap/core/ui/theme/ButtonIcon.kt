package com.solux.luxup.taptap.core.ui.theme

import androidx.annotation.DrawableRes
import com.solux.luxup.taptap.R

/**
 * 버튼 아이콘 이름 ↔ drawable 매핑.
 *
 * iconName은 서버와 주고받는 문자열이므로 여기 목록이 곧 계약이다.
 * 리소스를 동적으로 훑지 않고 명시적으로 나열해, 순서와 이름을 프론트가 통제한다.
 *
 * TODO: 백엔드와 iconName 문자열 목록 확정 후 고정
 */
data class ButtonIconItem(
    val iconName: String,
    @DrawableRes val resId: Int,
)

object ButtonIcons {

    /** 아이콘 선택 화면 그리드 노출 순서 = 이 목록 순서 */
    val all: List<ButtonIconItem> = listOf(
        ButtonIconItem("book", R.drawable.ic_btn_book),
        ButtonIconItem("calendar", R.drawable.ic_btn_calendar),
        ButtonIconItem("call", R.drawable.ic_btn_call),
        ButtonIconItem("camera", R.drawable.ic_btn_camera),
        ButtonIconItem("car", R.drawable.ic_btn_car),
        ButtonIconItem("celebrate", R.drawable.ic_btn_celebrate),
        ButtonIconItem("chat", R.drawable.ic_btn_chat),
        ButtonIconItem("clean", R.drawable.ic_btn_clean),
        ButtonIconItem("clothes", R.drawable.ic_btn_clothes),
        ButtonIconItem("cup", R.drawable.ic_btn_cup),
        ButtonIconItem("dessert", R.drawable.ic_btn_dessert),
        ButtonIconItem("dog", R.drawable.ic_btn_dog),
        ButtonIconItem("door", R.drawable.ic_btn_door),
        ButtonIconItem("drink", R.drawable.ic_btn_drink),
        ButtonIconItem("fire", R.drawable.ic_btn_fire),
        ButtonIconItem("flower", R.drawable.ic_btn_flower),
        ButtonIconItem("food", R.drawable.ic_btn_food),
        ButtonIconItem("fruit", R.drawable.ic_btn_fruit),
        ButtonIconItem("gym", R.drawable.ic_btn_gym),
        ButtonIconItem("health", R.drawable.ic_btn_health),
        ButtonIconItem("labtop", R.drawable.ic_btn_labtop),
        ButtonIconItem("lightbulb", R.drawable.ic_btn_lightbulb),
        ButtonIconItem("lightning", R.drawable.ic_btn_lightning),
        ButtonIconItem("liquid", R.drawable.ic_btn_liquid),
        ButtonIconItem("lock", R.drawable.ic_btn_lock),
        ButtonIconItem("mask", R.drawable.ic_btn_mask),
        ButtonIconItem("medicine", R.drawable.ic_btn_medicine),
        ButtonIconItem("music1", R.drawable.ic_btn_music1),
        ButtonIconItem("music2", R.drawable.ic_btn_music2),
        ButtonIconItem("note", R.drawable.ic_btn_note),
        ButtonIconItem("pay", R.drawable.ic_btn_pay),
        ButtonIconItem("pencil", R.drawable.ic_btn_pencil),
        ButtonIconItem("person", R.drawable.ic_btn_person),
        ButtonIconItem("plant", R.drawable.ic_btn_plant),
        ButtonIconItem("selfcare", R.drawable.ic_btn_selfcare),
        ButtonIconItem("shoe", R.drawable.ic_btn_shoe),
        ButtonIconItem("shopping1", R.drawable.ic_btn_shopping1),
        ButtonIconItem("shopping2", R.drawable.ic_btn_shopping2),
        ButtonIconItem("shower", R.drawable.ic_btn_shower),
        ButtonIconItem("sleep", R.drawable.ic_btn_sleep),
        ButtonIconItem("sport1", R.drawable.ic_btn_sport1),
        ButtonIconItem("sport2", R.drawable.ic_btn_sport2),
        ButtonIconItem("sun", R.drawable.ic_btn_sun),
        ButtonIconItem("travel", R.drawable.ic_btn_travel),
    )

    /** 기본 아이콘 (서버 기본값은 랜덤이지만, 매칭 실패 시 폴백용) */
    val DEFAULT: ButtonIconItem = all.first()

    /** 서버가 준 iconName으로 drawable 찾기. 없으면 기본 아이콘 */
    @DrawableRes
    fun resOf(iconName: String?): Int =
        all.firstOrNull { it.iconName == iconName }?.resId ?: DEFAULT.resId
}