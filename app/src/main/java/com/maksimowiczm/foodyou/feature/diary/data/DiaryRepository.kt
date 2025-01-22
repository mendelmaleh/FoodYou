package com.maksimowiczm.foodyou.feature.diary.data

import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryDay
import com.maksimowiczm.foodyou.feature.diary.data.model.Portion
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface DiaryRepository {
    fun getSelectedDate(): LocalDate

    suspend fun setSelectedDate(date: LocalDate)

    fun observePortionsByMealDate(meal: Meal, date: LocalDate): Flow<List<Portion>>

    fun observeDiaryDay(date: LocalDate): Flow<DiaryDay>
}
