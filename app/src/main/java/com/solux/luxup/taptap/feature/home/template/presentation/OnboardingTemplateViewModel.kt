package com.solux.luxup.taptap.feature.home.template.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.feature.home.template.data.OnboardingTemplateRepository
import com.solux.luxup.taptap.feature.home.template.model.OnboardingTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 온보딩 "어떤 일상을 기록하고싶나요?" 화면.
 *
 *  - GET  /api/templates       카드 목록
 *  - POST /api/templates/skip  건너뛰기
 *
 * 카드를 고르면 API 호출 없이 templateId만 다음 화면(메인 홈)으로 넘긴다.
 * 카테고리·프리셋 조회(GET /api/templates/{id}/recommendations)와
 * 버튼 생성(POST /api/templates/{id}/apply)은 메인 홈에서 이뤄진다.
 */
@HiltViewModel
class OnboardingTemplateViewModel @Inject constructor(
    private val repository: OnboardingTemplateRepository,
) : ViewModel() {

    var templates by mutableStateOf<List<OnboardingTemplate>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var isSkipping = false

    init {
        loadTemplates()
    }

    private fun loadTemplates() {
        viewModelScope.launch {
            isLoading = true
            repository.listTemplates()
                .onSuccess { templates = it }
                .onFailure { errorMessage = it.message ?: "템플릿을 불러오지 못했어요." }
            isLoading = false
        }
    }

    /**
     * POST /api/templates/skip
     * 이미 온보딩을 완료한 유저(버튼을 만들었다가 전부 지운 경우 등)는 서버가 400을 내려주는데,
     * "건너뛰기"의 목적은 어차피 메인 홈으로 이동하는 것이라 API 결과와 무관하게 항상 다음 화면으로 넘어간다.
     */
    fun skipTemplate(onSuccess: () -> Unit) {
        if (isSkipping) return
        isSkipping = true

        viewModelScope.launch {
            repository.skipTemplate()
            isSkipping = false
            onSuccess()
        }
    }

    fun consumeError() {
        errorMessage = null
    }
}