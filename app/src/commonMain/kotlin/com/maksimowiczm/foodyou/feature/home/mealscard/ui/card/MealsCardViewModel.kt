package com.maksimowiczm.foodyou.feature.home.mealscard.ui.card

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.data.DateProvider
import com.maksimowiczm.foodyou.data.DiaryRepository
import com.maksimowiczm.foodyou.data.StringFormatRepository
import com.maksimowiczm.foodyou.data.preferences.DiaryPreferences
import com.maksimowiczm.foodyou.infrastructure.datastore.get
import com.maksimowiczm.foodyou.infrastructure.datastore.observe
import com.maksimowiczm.foodyou.ui.DiaryViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class MealsCardViewModel(
    diaryRepository: DiaryRepository,
    private val stringFormatRepository: StringFormatRepository,
    dateProvider: DateProvider,
    dataStore: DataStore<Preferences>
) : DiaryViewModel(
    diaryRepository
) {
    val time = dateProvider.observeMinutes().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
    )

    val useTimeBasedSorting = dataStore
        .observe(DiaryPreferences.timeBasedSorting)
        .map { it ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { dataStore.get(DiaryPreferences.timeBasedSorting) ?: false }
        )

    val includeAllDayMeals = dataStore
        .observe(DiaryPreferences.includeAllDayMeals)
        .map { it ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking {
                dataStore.get(DiaryPreferences.includeAllDayMeals) ?: false
            }
        )

    fun formatTime(time: LocalTime): String = stringFormatRepository.formatTime(time)
}
