package ru.tech.imageresizershrinker.draw_screen


import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Redo
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.size.Size
import com.t8rin.drawbox.presentation.compose.DrawBox
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.draw_screen.viewModel.DrawViewModel
import ru.tech.imageresizershrinker.theme.icons.Eraser
import ru.tech.imageresizershrinker.theme.mixedColor
import ru.tech.imageresizershrinker.theme.onMixedColor
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.LocalConfettiController
import ru.tech.imageresizershrinker.utils.coil.filters.SaturationFilter
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.decodeBitmapByUri
import ru.tech.imageresizershrinker.utils.helper.ContextUtils.requestStoragePermission
import ru.tech.imageresizershrinker.utils.modifier.block
import ru.tech.imageresizershrinker.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.utils.modifier.fabBorder
import ru.tech.imageresizershrinker.utils.modifier.navBarsPaddingOnlyIfTheyAtTheBottom
import ru.tech.imageresizershrinker.utils.modifier.navBarsPaddingOnlyIfTheyAtTheEnd
import ru.tech.imageresizershrinker.utils.storage.LocalFileController
import ru.tech.imageresizershrinker.utils.storage.Picker
import ru.tech.imageresizershrinker.utils.storage.localImagePickerMode
import ru.tech.imageresizershrinker.utils.storage.rememberImagePicker
import ru.tech.imageresizershrinker.widget.LoadingDialog
import ru.tech.imageresizershrinker.widget.LocalToastHost
import ru.tech.imageresizershrinker.widget.TopAppBarEmoji
import ru.tech.imageresizershrinker.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.widget.showError
import ru.tech.imageresizershrinker.widget.text.Marquee
import ru.tech.imageresizershrinker.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.widget.utils.isScrollingUp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawScreen(
    uriState: Uri?,
    onGoBack: () -> Unit,
    viewModel: DrawViewModel = viewModel()
) {
    val settingsState = LocalSettingsState.current
    val context = LocalContext.current as ComponentActivity
    val toastHostState = LocalToastHost.current
    val themeState = LocalDynamicThemeState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val scope = rememberCoroutineScope()
    val confettiController = LocalConfettiController.current
    val showConfetti: () -> Unit = {
        scope.launch {
            confettiController.showEmpty()
        }
    }

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (viewModel.bitmap != null) showExitDialog = true
        else onGoBack()
    }

    LaunchedEffect(uriState) {
        uriState?.let {
            viewModel.setUri(it)
            context.decodeBitmapByUri(
                uri = it,
                onGetMimeType = viewModel::updateMimeType,
                onGetExif = {},
                onGetBitmap = { bmp ->
                    viewModel.updateBitmap(
                        bitmap = bmp, newBitmap = true
                    )
                },
                onError = {
                    scope.launch {
                        toastHostState.showError(context, it)
                    }
                }
            )
        }
    }
    LaunchedEffect(viewModel.bitmap) {
        viewModel.bitmap?.let {
            if (allowChangeColor) {
                themeState.updateColorByImage(
                    SaturationFilter(context, 2f).transform(it, Size.ORIGINAL)
                )
            }
        }
    }

    val pickImageLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Single)
        ) { uris ->
            uris.takeIf { it.isNotEmpty() }?.firstOrNull()?.let {
                viewModel.setUri(it)
                context.decodeBitmapByUri(
                    uri = it,
                    onGetMimeType = {},
                    onGetExif = {},
                    onGetBitmap = { bmp ->
                        viewModel.updateBitmap(
                            bitmap = bmp, newBitmap = true
                        )
                    },
                    onError = {
                        scope.launch {
                            toastHostState.showError(context, it)
                        }
                    }
                )
            }
        }

    val pickImage = {
        pickImageLauncher.pickImage()
    }

    var showSaveLoading by rememberSaveable { mutableStateOf(false) }

    val fileController = LocalFileController.current
    val saveBitmap: (Bitmap) -> Unit = {
        showSaveLoading = true
        viewModel.saveBitmap(
            bitmap = it,
            fileController = fileController,
        ) { success ->
            if (!success) context.requestStoragePermission()
            else {
                scope.launch {
                    toastHostState.showToast(
                        context.getString(
                            R.string.saved_to,
                            fileController.savingPath
                        ),
                        Icons.Rounded.Save
                    )
                }
                showConfetti()
            }
            showSaveLoading = false
        }
    }


    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scrollState = rememberScrollState()

    val portrait =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            confirmValueChange = {
                when (it) {
                    SheetValue.Hidden -> false
                    else -> true
                }
            }
        )
    )

    val content: @Composable (PaddingValues) -> Unit = { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (viewModel.bitmap == null) {
                    LargeTopAppBar(
                        scrollBehavior = scrollBehavior,
                        modifier = Modifier.drawHorizontalStroke(),
                        title = {
                            Marquee(
                                edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                            ) {
                                Text(stringResource(R.string.draw))
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                3.dp
                            )
                        ),
                        navigationIcon = {
                            IconButton(
                                onClick = onBack
                            ) {
                                Icon(Icons.Rounded.ArrowBack, null)
                            }
                        },
                        actions = {
                            TopAppBarEmoji()
                        }
                    )
                } else {
                    TopAppBar(
                        modifier = Modifier.drawHorizontalStroke(),
                        title = {
                            Marquee(
                                edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                            ) {
                                Text(stringResource(R.string.draw))
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                                            scaffoldState.bottomSheetState.partialExpand()
                                        } else {
                                            scaffoldState.bottomSheetState.expand()
                                        }
                                    }
                                },
                            ) {
                                Icon(Icons.Rounded.Build, null)
                            }
                            IconButton(
                                onClick = {
                                    /*TODO*/
                                },
                                enabled = viewModel.bitmap != null
                            ) {
                                Icon(Icons.Outlined.Share, null)
                            }
                            IconButton(
                                onClick = {
                                    viewModel.resetBitmap()
                                },
                                enabled = viewModel.bitmap != null && viewModel.isBitmapChanged
                            ) {
                                Icon(Icons.Outlined.RestartAlt, null)
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                3.dp
                            )
                        ),
                        navigationIcon = {
                            IconButton(
                                onClick = onBack
                            ) {
                                Icon(Icons.Rounded.ArrowBack, null)
                            }
                        },
                    )
                }
                viewModel.bitmap?.let {
                    if (portrait) {
                        Column {
                            DrawBox(
                                modifier = Modifier.fillMaxSize(),
                                drawController = viewModel.drawController,
                                onGetDrawController = viewModel::updateDrawController
                            ) {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentScale = ContentScale.Inside,
                                    contentDescription = null
                                )
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.navBarsPaddingOnlyIfTheyAtTheEnd(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier.weight(0.8f)
                            ) {

                            }
                            Box(
                                Modifier
                                    .fillMaxHeight()
                                    .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                                    .background(MaterialTheme.colorScheme.outlineVariant())
                            )

                            ExtensionGroup(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .navBarsPaddingOnlyIfTheyAtTheBottom(),
                                orientation = Orientation.Vertical,
                                enabled = viewModel.bitmap != null,
                                mimeTypeInt = viewModel.mimeType,
                                onMimeChange = {
                                    viewModel.updateMimeType(it)
                                }
                            )
                            Box(
                                Modifier
                                    .fillMaxHeight()
                                    .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                                    .background(MaterialTheme.colorScheme.outlineVariant())
                                    .padding(start = 20.dp)
                            )
                            Column(
                                Modifier
                                    .padding(horizontal = 20.dp)
                                    .fillMaxHeight()
                                    .navigationBarsPadding(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                FloatingActionButton(
                                    onClick = pickImage,
                                    modifier = Modifier.fabBorder(),
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                    content = {
                                        Icon(Icons.Rounded.AddPhotoAlternate, null)
                                    }
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                FloatingActionButton(
                                    onClick = {
                                        /*TODO*/
                                    },
                                    modifier = Modifier.fabBorder(),
                                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                ) {
                                    Icon(Icons.Rounded.Save, null)
                                }
                            }
                        }
                    }
                } ?: Column(Modifier.verticalScroll(scrollState)) {
                    ImageNotPickedWidget(
                        onPickImage = pickImage,
                        modifier = Modifier
                            .padding(bottom = 88.dp, top = 20.dp, start = 20.dp, end = 20.dp)
                            .navigationBarsPadding()
                    )
                }
            }

            if (viewModel.bitmap == null) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .navigationBarsPadding()
                        .align(settingsState.fabAlignment)
                ) {
                    ExtendedFloatingActionButton(
                        onClick = pickImage,
                        modifier = Modifier.fabBorder(),
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                        text = {
                            Text(stringResource(R.string.pick_image_alt))
                        },
                        icon = {
                            Icon(Icons.Rounded.AddPhotoAlternate, null)
                        }
                    )
                }
            }
        }
    }

    if (portrait && viewModel.bitmap != null) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 80.dp + WindowInsets.navigationBars.asPaddingValues()
                .calculateBottomPadding(),
            sheetDragHandle = null,
            sheetShape = RectangleShape,
            sheetContent = {
                BottomAppBar(
                    modifier = Modifier.drawHorizontalStroke(true),
                    actions = {
                        IconButton(
                            onClick = { viewModel.drawController?.undo() },
                            enabled = !viewModel.drawController?.paths.isNullOrEmpty()
                        ) {
                            Icon(Icons.Rounded.Undo, null)
                        }
                        IconButton(
                            onClick = { viewModel.drawController?.redo() },
                            enabled = !viewModel.drawController?.undonePaths.isNullOrEmpty()
                        ) {
                            Icon(Icons.Rounded.Redo, null)
                        }
                        val isEraserOn = viewModel.drawController?.isEraserOn == true
                        OutlinedIconButton(
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = animateColorAsState(
                                    if (isEraserOn) MaterialTheme.colorScheme.mixedColor
                                    else Color.Transparent
                                ).value,
                                contentColor = animateColorAsState(
                                    if (isEraserOn) MaterialTheme.colorScheme.onMixedColor
                                    else MaterialTheme.colorScheme.onSurface
                                ).value,
                                disabledContainerColor = Color.Transparent
                            ),
                            border = BorderStroke(
                                max(settingsState.borderWidth, 1.dp), animateColorAsState(
                                    if (isEraserOn) MaterialTheme.colorScheme.outlineVariant
                                    else Color.Transparent
                                ).value
                            ),
                            onClick = { viewModel.drawController?.toggleEraser() }
                        ) {
                            Icon(Icons.Rounded.Eraser, null)
                        }
                    },
                    floatingActionButton = {
                        Row {
                            FloatingActionButton(
                                onClick = pickImage,
                                modifier = Modifier.fabBorder(),
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                            ) {
                                val expanded =
                                    scrollState.isScrollingUp() && viewModel.bitmap == null
                                val horizontalPadding by animateDpAsState(targetValue = if (expanded) 16.dp else 0.dp)
                                Row(
                                    modifier = Modifier.padding(horizontal = horizontalPadding),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Rounded.AddPhotoAlternate, null)
                                    AnimatedVisibility(visible = expanded) {
                                        Row {
                                            Spacer(Modifier.width(8.dp))
                                            Text(stringResource(R.string.pick_image_alt))
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            FloatingActionButton(
                                onClick = {
                                    /*TODO*/
                                },
                                modifier = Modifier.fabBorder(),
                                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                            ) {
                                Icon(Icons.Rounded.Save, null)
                            }
                        }
                    }
                )
                Divider()
                Column(
                    Modifier
                        .padding(16.dp)
                        .block()
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            stringResource(R.string.color),
                            modifier = Modifier.padding(top = 16.dp),
                            fontSize = 18.sp
                        )
                    }
                    Box {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.2.dp * 50 + 32.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(defaultColorList) { color ->
                                val alphaColor = ColorUtils.setAlphaComponent(
                                    color.toArgb(),
                                    viewModel.drawController?.paintOptions?.alpha ?: 255
                                )
                                Box(
                                    Modifier
                                        .size(
                                            animateDpAsState(
                                                50.dp.times(
                                                    if (viewModel.drawController?.paintOptions?.color == alphaColor) {
                                                        1.2f
                                                    } else 1f
                                                )
                                            ).value
                                        )
                                        .border(
                                            width = settingsState.borderWidth,
                                            color = MaterialTheme.colorScheme.outlineVariant(onTopOf = color),
                                            shape = CircleShape
                                        )
                                        .clip(CircleShape)
                                        .background(color)
                                        .clickable {
                                            viewModel.drawController?.setColor(alphaColor)
                                        }
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .width(6.dp)
                                .height(1.2.dp * 50 + 32.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        0f to MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                                        1f to Color.Transparent
                                    )
                                )
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .width(6.dp)
                                .height(1.2.dp * 50 + 32.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        0f to Color.Transparent,
                                        1f to MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                                    )
                                )
                        )
                    }
                }
                Column(
                    Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .block()
                        .animateContentSize()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.line_width),
                            modifier = Modifier
                                .padding(
                                    top = 16.dp,
                                    end = 16.dp,
                                    start = 16.dp
                                )
                                .weight(1f)
                        )
                        Text(
                            text = "${viewModel.drawController?.paintOptions?.strokeWidth ?: 8f}",
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.5f
                            ),
                            modifier = Modifier.padding(top = 16.dp),
                            lineHeight = 18.sp
                        )
                        Text(
                            maxLines = 1,
                            text = "Px",
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.5f
                            ),
                            modifier = Modifier.padding(
                                start = 4.dp,
                                top = 16.dp,
                                end = 16.dp
                            )
                        )
                    }
                    Slider(
                        modifier = Modifier
                            .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
                            .offset(y = (-2).dp)
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = CircleShape
                            )
                            .height(40.dp)
                            .border(
                                width = settingsState.borderWidth,
                                color = MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer),
                                shape = CircleShape
                            )
                            .padding(horizontal = 10.dp),
                        colors = SliderDefaults.colors(
                            inactiveTrackColor =
                            MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                        ),
                        value = viewModel.drawController?.paintOptions?.strokeWidth ?: 8f,
                        valueRange = 1f..100f,
                        onValueChange = {
                            viewModel.drawController?.setStrokeWidth((it * 100).toInt() / 100f)
                        }
                    )
                }
                Divider()
                ExtensionGroup(
                    modifier = Modifier
                        .padding(16.dp)
                        .navigationBarsPadding(),
                    orientation = Orientation.Horizontal,
                    enabled = viewModel.bitmap != null,
                    mimeTypeInt = viewModel.mimeType,
                    onMimeChange = {
                        viewModel.updateMimeType(it)
                    }
                )
            },
            content = content
        )
    } else {
        content(PaddingValues())
    }

    if (showSaveLoading || viewModel.isLoading) {
        LoadingDialog()
    }

    ExitWithoutSavingDialog(
        onExit = onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    BackHandler(onBack = onBack)
}

private val defaultColorList = listOf(
    Color(0xFFf8130d),
    Color(0xFFb8070d),
    Color(0xFF7a000b),
    Color(0xFF8a3a00),
    Color(0xFFff7900),
    Color(0xFFfcf721),
    Color(0xFFf8df09),
    Color(0xFFc0dc18),
    Color(0xFF88dd20),
    Color(0xFF07ddc3),
    Color(0xFF01a0a3),
    Color(0xFF59cbf0),
    Color(0xFF005FFF),
    Color(0xFFfa64e1),
    Color(0xFFfc50a6),
    Color(0xFFd7036a),
    Color(0xFFdb94fe),
    Color(0xFFb035f8),
    Color(0xFF7b2bec),
    Color(0xFF022b6d),
    Color(0xFFFFFFFF),
    Color(0xFF768484),
    Color(0xFF333333),
    Color(0xFF0a0c0b),
)