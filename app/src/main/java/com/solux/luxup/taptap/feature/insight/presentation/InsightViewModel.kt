package com.solux.luxup.taptap.feature.insight.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.feature.home.main.data.ButtonRepository
import com.solux.luxup.taptap.feature.insight.data.InsightRepository
import com.solux.luxup.taptap.feature.insight.daily.model.InsightButtonTapCount
import com.solux.luxup.taptap.feature.insight.daily.model.InsightDaily
import com.solux.luxup.taptap.feature.insight.daily.util.isFutureDate
import com.solux.luxup.taptap.feature.insight.daily.util.shiftDate
import com.solux.luxup.taptap.feature.insight.monthly.model.InsightMonthly
import com.solux.luxup.taptap.feature.insight.monthly.util.isFutureMonth
import com.solux.luxup.taptap.feature.insight.monthly.util.shiftMonth
import com.solux.luxup.taptap.feature.insight.weekly.model.InsightWeekly
import com.solux.luxup.taptap.feature.insight.weekly.util.isFutureWeek
import com.solux.luxup.taptap.feature.insight.weekly.util.shiftWeek
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private val IsoDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

/** 서버가 내려주는 시각은 오프셋 없이 한국 시간(Asia/Seoul) 기준이다 — "오늘"도 반드시 같은 존으로 맞춰야 한다. */
private val ServiceZone: ZoneId = ZoneId.of("Asia/Seoul")

/** buttonId → 카테고리. 카테고리 필터(ALL▾)에서 쓴다. */
private data class ButtonCategoryRef(val categoryId: Long?, val categoryName: String)

/**
 * 개인 인사이트("레포트" 탭) — 일간/주간/월간 3개 엔드포인트를 하나의 ViewModel에서 관리한다.
 * insightDaily/insightWeekly/insightMonthly 및 각 "전체보기" 화면이 부모 백스택 엔트리를 통해
 * 같은 인스턴스를 공유한다([com.solux.luxup.taptap.MainActivity]의 getBackStackEntry 참고).
 */
@HiltViewModel
class InsightViewModel @Inject constructor(
    private val insightRepository: InsightRepository,
    private val buttonRepository: ButtonRepository,
) : ViewModel() {

    // 인사이트 API의 buttonTapCounts엔 categoryId/categoryName이 내려오지 않아(⚠ API 미제공),
    // 카테고리 필터(ALL▾)가 항상 빈 결과를 내던 문제 — GET /api/buttons(내 버튼 목록)에서
    // buttonId → 카테고리를 조회해 채워 넣는다. 화면(ViewModel) 생명주기 동안 한 번만 조회해 재사용한다.
    private var buttonCategoryLookup: Map<Long, ButtonCategoryRef>? = null

    var targetDate by mutableStateOf(LocalDate.now(ServiceZone).format(IsoDateFormatter))
        private set

    var weekStart by mutableStateOf(startOfWeek(LocalDate.now(ServiceZone)).format(IsoDateFormatter))
        private set

    var year by mutableStateOf(LocalDate.now(ServiceZone).year)
        private set

    var month by mutableStateOf(LocalDate.now(ServiceZone).monthValue)
        private set

    var daily by mutableStateOf<InsightDaily?>(null)
        private set

    var weekly by mutableStateOf<InsightWeekly?>(null)
        private set

    var monthly by mutableStateOf<InsightMonthly?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // 카테고리 필터(ALL▾) 드롭다운 목록 — 메인 홈의 카테고리 드롭다운과 동일하게
    // GET /api/buttons/categories(현재 카테고리 목록)를 그대로 쓴다. 인사이트 API의 categoryTapCounts는
    // 과거 기록 기준 집계라 이미 이름이 바뀌었거나 삭제된 카테고리가 섞여 나올 수 있어 드롭다운 소스로 쓰지 않는다.
    var categoryNames by mutableStateOf<List<String>>(emptyList())
        private set

    init {
        refreshAll()
        loadCategoryNames()
    }

    fun refreshAll() {
        loadDaily()
        loadWeekly()
        loadMonthly()
    }

    fun loadCategoryNames() {
        viewModelScope.launch {
            buttonRepository.getCategories()
                .onSuccess { categoryNames = it.map { category -> category.name } }
        }
    }

    /** 미래 날짜는 기록이 존재할 수 없어 API 조회 없이 빈 상태로 바로 채운다 (서버가 미래 조회에 오류를 내려주는 것과 무관하게 동작). */
    fun loadDaily() {
        if (targetDate.isFutureDate()) {
            daily = InsightDaily(
                targetDate = targetDate,
                totalTapCount = 0,
                topButton = null,
                peakTimeSlot = null,
                peakTimeSlotSimple = null,
            )
            return
        }
        viewModelScope.launch {
            insightRepository.getDailyInsight(targetDate)
                .onSuccess { result ->
                    val lookup = loadButtonCategoryLookup()
                    daily = result.copy(buttonTapCounts = result.buttonTapCounts.withCategories(lookup))
                }
                .onFailure { errorMessage = it.message ?: "일간 인사이트를 불러오지 못했어요." }
        }
    }

    fun loadWeekly() {
        if (weekStart.isFutureWeek()) {
            weekly = InsightWeekly(
                weekStart = weekStart,
                weekEnd = LocalDate.parse(weekStart, IsoDateFormatter).plusDays(6).format(IsoDateFormatter),
                totalTapCount = 0,
            )
            return
        }
        viewModelScope.launch {
            insightRepository.getWeeklyInsight(weekStart)
                .onSuccess { result ->
                    val lookup = loadButtonCategoryLookup()
                    weekly = result.copy(buttonTapCounts = result.buttonTapCounts.withCategories(lookup))
                }
                .onFailure { errorMessage = it.message ?: "주간 인사이트를 불러오지 못했어요." }
        }
    }

    fun loadMonthly() {
        if (isFutureMonth(year, month)) {
            monthly = InsightMonthly(year = year, month = month, totalTapCount = 0)
            return
        }
        viewModelScope.launch {
            insightRepository.getMonthlyInsight(year, month)
                .onSuccess { result ->
                    val lookup = loadButtonCategoryLookup()
                    monthly = result.copy(buttonTapCounts = result.buttonTapCounts.withCategories(lookup))
                }
                .onFailure { errorMessage = it.message ?: "월간 인사이트를 불러오지 못했어요." }
        }
    }

    fun goToPreviousDay() {
        targetDate = targetDate.shiftDate(-1)
        loadDaily()
    }

    fun goToNextDay() {
        targetDate = targetDate.shiftDate(1)
        loadDaily()
    }

    fun goToPreviousWeek() {
        weekStart = weekStart.shiftWeek(-1)
        loadWeekly()
    }

    fun goToNextWeek() {
        weekStart = weekStart.shiftWeek(1)
        loadWeekly()
    }

    fun goToPreviousMonth() {
        val (y, m) = shiftMonth(year, month, -1)
        year = y
        month = m
        loadMonthly()
    }

    fun goToNextMonth() {
        val (y, m) = shiftMonth(year, month, 1)
        year = y
        month = m
        loadMonthly()
    }

    fun consumeError() {
        errorMessage = null
    }

    private suspend fun loadButtonCategoryLookup(): Map<Long, ButtonCategoryRef> {
        buttonCategoryLookup?.let { return it }
        val lookup = buttonRepository.getButtons().getOrNull()
            ?.habitButtons
            ?.associate { it.buttonId to ButtonCategoryRef(it.categoryId, it.category) }
            .orEmpty()
        buttonCategoryLookup = lookup
        return lookup
    }

    private fun List<InsightButtonTapCount>.withCategories(lookup: Map<Long, ButtonCategoryRef>): List<InsightButtonTapCount> =
        map { item ->
            val category = lookup[item.buttonId] ?: return@map item
            item.copy(categoryId = category.categoryId, categoryName = category.categoryName)
        }

    private fun startOfWeek(date: LocalDate): LocalDate {
        val daysFromMonday = date.dayOfWeek.value - DayOfWeek.MONDAY.value
        return date.minusDays(daysFromMonday.toLong())
    }
}