package com.maksimowiczm.foodyou.feature.diary.data.model

sealed interface MeasurementId {
    data class Product(val measurementId: Long) : MeasurementId
}
