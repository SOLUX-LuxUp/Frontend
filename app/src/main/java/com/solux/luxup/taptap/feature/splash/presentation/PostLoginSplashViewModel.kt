package com.solux.luxup.taptap.feature.splash.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.feature.home.main.data.ButtonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 로그인 직후 스플래시에서 버튼을 하나도 만들지 않은 유저인지 확인해,
 * 온보딩 화면(템플릿 선택)으로 보낼지 메인 홈으로 바로 보낼지 정한다.
 */
@HiltViewModel
class PostLoginSplashViewModel @Inject constructor(
    private val buttonRepository: ButtonRepository,
) : ViewModel() {

    /** null이면 아직 확인 중. true면 버튼(또는 즐겨찾기)이 하나 이상 있어 온보딩을 건너뛴다. */
    var hasButtons by mutableStateOf<Boolean?>(null)
        private set

    init {
        viewModelScope.launch {
            buttonRepository.getButtons()
                .onSuccess { hasButtons = it.habitButtons.isNotEmpty() || it.favorites.isNotEmpty() }
                .onFailure { hasButtons = false }
        }
    }
}