package com.maksimowiczm.foodyou.feature.diary.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.diary.data.DiaryRepository
import com.maksimowiczm.foodyou.feature.diary.data.SystemInfoRepository
import com.maksimowiczm.foodyou.feature.diary.data.TodayDateProvider
import kotlinx.coroutines.launch
import java.time.LocalDate

class DiaryViewModel(
    private val systemInfoRepository: SystemInfoRepository,
    private val diaryRepository: DiaryRepository,
    todayDateProvider: TodayDateProvider
) : ViewModel() {
    val today = todayDateProvider.observe()

    val weekDayNamesShort: Array<String>
        get() = systemInfoRepository.weekDayNamesShort

    val initialDate: LocalDate = diaryRepository.getSelectedDate()

    fun getFirstDayOfWeek(date: LocalDate) = systemInfoRepository.getFirstDayOfWeek(date)

    fun formatMonthYear(date: LocalDate): String = systemInfoRepository.formatMonthYear(date)

    fun formatFullDate(date: LocalDate): String = systemInfoRepository.formatFullDate(date)

    fun observeDiaryDay(date: LocalDate) = diaryRepository.observeDiaryDay(date)

    fun selectDate(date: LocalDate) {
        viewModelScope.launch {
            diaryRepository.setSelectedDate(date)
        }
    }
}
