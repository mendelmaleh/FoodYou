package com.maksimowiczm.foodyou.feature.addfood.database

import androidx.room.Embedded
import com.maksimowiczm.foodyou.feature.product.database.ProductEntity

data class ProductSearchEntity(
    @Embedded(prefix = "p_")
    val product: ProductEntity,

    @Embedded(prefix = "m_")
    val weightMeasurement: WeightMeasurementEntity?,

    val todaysMeasurement: Boolean
)
