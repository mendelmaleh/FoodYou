package com.maksimowiczm.foodyou.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

interface NestedListScope {
    /**
     * Adds an item to the list with the given level of indentation. The level of indentation is the
     * number of spacers. The content of the item is defined by the [content] lambda.
     *
     * @param level The level of indentation. Default is 0.
     * @param content The content of the item.
     */
    fun item(level: Int = 0, content: @Composable ColumnScope.() -> Unit)
}

/**
 * A list that can have indented items. The indentation is done by adding a spacer to the start of
 * each item. The spacer is added for each level of indentation.
 *
 * @param spacer The spacer that is added for each level of indentation. Default is 16.dp.
 * @param content The content of the list.
 */
@Composable
fun IndentedList(
    modifier: Modifier = Modifier,
    spacer: @Composable () -> Unit = { Spacer(Modifier.width(16.dp)) },
    content: NestedListScope.() -> Unit
) {
    val items = mutableListOf<@Composable () -> Unit>()
    val scope = object : NestedListScope {
        override fun item(level: Int, content: @Composable ColumnScope.() -> Unit) {
            items.add {
                Row {
                    repeat(level) {
                        spacer()
                    }

                    Column {
                        content()
                    }
                }
            }
        }
    }
    content(scope)

    Column(
        modifier = modifier
    ) {
        items.forEach { it() }
    }
}
