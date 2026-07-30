package com.solux.luxup.taptap.feature.home.buttondetail.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.core.ui.theme.ButtonIcons
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.feature.home.buttondetail.data.CustomEmojiStore
import com.solux.luxup.taptap.feature.home.buttondetail.model.ButtonDetail
import com.solux.luxup.taptap.feature.home.buttondetail.model.ButtonRecordEntry
import com.solux.luxup.taptap.feature.home.buttondetail.model.ButtonRecordSummary
import com.solux.luxup.taptap.feature.home.main.data.ButtonRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 30

/**
 * 버튼 상세(기록) 화면 — 버튼을 길게 눌렀을 때 진입.
 * 버튼 이름·아이콘은 홈/인사이트 화면이 이미 들고 있는 값을 그대로 받아 쓰고(개인 버튼 단건 조회 API가 없음),
 * "최근 기록" 배너와 타임라인만 API로 채운다.
 *
 *  - GET .../records/summary   : 최근 기록 배너
 *  - GET .../records/timeline  : 커서 기반 30건씩
 */
@HiltViewModel(assistedFactory = ButtonDetailViewModel.Factory::class)
class ButtonDetailViewModel @AssistedInject constructor(
    @Assisted("buttonId") private val buttonId: Long,
    @Assisted("title") title: String?,
    @Assisted("iconName") iconName: String?,
    @Assisted("iconColor") iconColor: String?,
    private val buttonRepository: ButtonRepository,
    private val customEmojiStore: CustomEmojiStore,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("buttonId") buttonId: Long,
            @Assisted("title") title: String?,
            @Assisted("iconName") iconName: String?,
            @Assisted("iconColor") iconColor: String?,
        ): ButtonDetailViewModel
    }

    val detail: ButtonDetail = ButtonDetail(
        buttonId = buttonId,
        title = title.orEmpty(),
        category = "",
        iconRes = ButtonIcons.resOf(iconName),
        iconTint = IconColor.from(iconColor).color,
    )

    var summary by mutableStateOf<ButtonRecordSummary?>(null)
        private set

    var records by mutableStateOf<List<ButtonRecordEntry>>(emptyList())
        private set

    var hasMore by mutableStateOf(false)
        private set

    private var nextCursor: Long? = null

    var isLoading by mutableStateOf(true)
        private set

    /** 더보기 진행 중 — 중복 요청 방지 */
    var isLoadingMore by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var customEmojis by mutableStateOf(customEmojiStore.getCustomEmojis())
        private set

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            isLoading = true

            buttonRepository.getButtonSummary(buttonId)
                .onSuccess { summary = it }
                .onFailure { errorMessage = it.message ?: "최근 기록을 불러오지 못했어요." }

            buttonRepository.getTimeline(buttonId, cursor = null, limit = PAGE_SIZE)
                .onSuccess { page ->
                    records = page.records
                    hasMore = page.hasMore
                    nextCursor = page.nextCursor
                }
                .onFailure { errorMessage = it.message ?: "기록을 불러오지 못했어요." }

            isLoading = false
        }
    }

    /** 더보기 — 30건씩 확장 */
    fun loadMore() {
        if (isLoadingMore || !hasMore) return
        val cursor = nextCursor ?: return

        viewModelScope.launch {
            isLoadingMore = true

            buttonRepository.getTimeline(buttonId, cursor = cursor, limit = PAGE_SIZE)
                .onSuccess { page ->
                    records = records + page.records
                    hasMore = page.hasMore
                    nextCursor = page.nextCursor
                }
                .onFailure { errorMessage = it.message ?: "기록을 불러오지 못했어요." }

            isLoadingMore = false
        }
    }

    fun deleteRecord(record: ButtonRecordEntry) {
        viewModelScope.launch {
            buttonRepository.deleteTimelineRecord(buttonId, record.recordId)
                .onSuccess { load() }
                .onFailure { errorMessage = "기록을 삭제하지 못했어요.\n잠시 후 다시 시도해 주세요." }
        }
    }

    fun saveMemo(record: ButtonRecordEntry, memo: String?, emoji: String?) {
        viewModelScope.launch {
            buttonRepository.updateRecordDetail(buttonId, record.recordId, memo, emoji)
                .onSuccess { load() }
                .onFailure { errorMessage = "메모를 저장하지 못했어요.\n잠시 후 다시 시도해 주세요." }
        }
    }

    fun consumeError() {
        errorMessage = null
    }

    fun addCustomEmoji(emoji: String) {
        customEmojiStore.addCustomEmoji(emoji)
        customEmojis = customEmojiStore.getCustomEmojis()
    }
}