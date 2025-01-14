package ru.tech.imageresizershrinker.presentation.root.widget.other

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.fadingEdges
import ru.tech.imageresizershrinker.presentation.root.widget.text.KeyboardFocusHandler

@Composable
fun SearchBar(
    searchString: String,
    onValueChange: (String) -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    val focusRequester = remember { FocusRequester() }
    val localFocusManager = LocalFocusManager.current
    val state = rememberScrollState()

    //TODO: Remove when library will be fixed
    KeyboardFocusHandler()

    LaunchedEffect(windowInfo) {
        snapshotFlow {
            windowInfo.isWindowFocused
        }.collect { isWindowFocused ->
            if (isWindowFocused) {
                focusRequester.requestFocus()
            }
        }
    }
    BasicTextField(
        modifier = Modifier
            .fillMaxWidth()
            .fadingEdges(state)
            .horizontalScroll(state)
            .focusRequester(focusRequester),
        value = searchString,
        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground),
        keyboardActions = KeyboardActions(
            onDone = { localFocusManager.clearFocus() }
        ),
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.tertiary),
        onValueChange = {
            onValueChange(it)
        }
    )
    if (searchString.isEmpty()) {
        Text(
            text = stringResource(R.string.search_here),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp),
            style = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onBackground.copy(
                    0.5f
                )
            )
        )
    }
}