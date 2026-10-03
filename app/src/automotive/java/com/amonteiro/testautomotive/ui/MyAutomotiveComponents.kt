package com.amonteiro.testautomotive.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

//Dans le buildVariant automotive
@Composable
fun MyError(
    modifier: Modifier = Modifier,
    errorMessage: String? = null
) {
    Text(
        text = if (errorMessage.isNullOrBlank()) "You can't use this feature while driving" else errorMessage,
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.onError,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.error)
    )
}
