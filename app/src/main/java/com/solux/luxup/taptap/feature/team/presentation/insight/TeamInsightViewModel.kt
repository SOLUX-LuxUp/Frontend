package com.solux.luxup.taptap.feature.team.presentation.insight

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.feature.insight.daily.util.shiftDate
import com.solux.luxup.taptap.feature.insight.monthly.util.shiftMonth
import com.solux.luxup.taptap.feature.insight.weekly.util.shiftWeek
import com.solux.luxup.taptap.feature.team.data.TeamRepository
import com.solux.luxup.taptap.feature.team.model.TeamInsightDaily
import com.solux.luxup.taptap.feature.team.model.TeamInsightMonthly
import com.solux.luxup.taptap.feature.team.model.TeamInsightWeekly
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val IsoDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

/**
 * 팀 인사이트(8.1.8) — 일간/주간/월간 3개 엔드포인트를 하나의 ViewModel에서 관리한다.
 * 팀 상세(teamDetail/{teamId}) 엔트리에 스코프해서, 인사이트 탭과 "더보기"(버튼 전체 랭킹) 화면이
 * 같은 조회 결과를 공유하도록 한다.
 */
@HiltViewModel(assistedFactory = TeamInsightViewModel.Factory::class)
class TeamInsightViewModel @AssistedInject constructor(
    @Assisted("teamId") private val teamId: Long,
    private val teamRepository: TeamRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("teamId") teamId: Long): TeamInsightViewModel
    }

    var targetDate by mutableStateOf(LocalDate.now().format(IsoDateFormatter))
        private set

    var weekStart by mutableStateOf(startOfWeek(LocalDate.now()).format(IsoDateFormatter))
        private set

    var year by mutableStateOf(LocalDate.now().year)
        private set

    var month by mutableStateOf(LocalDate.now().monthValue)
        private set

    var daily by mutableStateOf<TeamInsightDaily?>(null)
        private set

    var weekly by mutableStateOf<TeamInsightWeekly?>(null)
        private set

    var monthly by mutableStateOf<TeamInsightMonthly?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        refreshAll()
    }

    /**
     * 활동 탭에서 탭 기록을 남기고 인사이트 탭으로 돌아와도 반영되도록,
     * 탭 전환마다(같은 팀 방문 안에서 ViewModel이 재사용되는 동안) 호출한다.
     */
    fun refreshAll() {
        loadDaily()
        loadWeekly()
        loadMonthly()
    }

    fun loadDaily() {
        viewModelScope.launch {
            teamRepository.getDailyInsight(teamId, targetDate)
                .onSuccess { daily = it }
                .onFailure { errorMessage = it.message ?: "일간 인사이트를 불러오지 못했어요." }
        }
    }

    fun loadWeekly() {
        viewModelScope.launch {
            teamRepository.getWeeklyInsight(teamId, weekStart)
                .onSuccess { weekly = it }
                .onFailure { errorMessage = it.message ?: "주간 인사이트를 불러오지 못했어요." }
        }
    }

    fun loadMonthly() {
        viewModelScope.launch {
            teamRepository.getMonthlyInsight(teamId, year, month)
                .onSuccess { monthly = it }
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

    private fun startOfWeek(date: LocalDate): LocalDate {
        val daysFromMonday = date.dayOfWeek.value - DayOfWeek.MONDAY.value
        return date.minusDays(daysFromMonday.toLong())
    }
}
