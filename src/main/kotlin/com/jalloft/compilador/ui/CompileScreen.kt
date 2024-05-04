package com.jalloft.compilador.com.jalloft.compilador.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.common.io.Resources
import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.Tokenizer
import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.token.Token
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Cursor
import java.io.File

@Composable
fun CompileScreen() {
    val sourceFile = File("fontes/codigo2.txt")
    var code by remember { mutableStateOf(Resources.getResource(sourceFile.path).readText()) }
    val textMeasurer = rememberTextMeasurer()

    Column(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        drawDivider()
                        drawLineNumbers(textMeasurer, code.lines().size)
                    },
            ) {
                BasicTextField(
                    value = code,
                    onValueChange = { code = it },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 80.dp, vertical = 18.dp),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 16.sp, lineHeight = 20.sp)
                )
            }

            LexicalAnalysisPanel(Modifier.align(Alignment.BottomCenter), code)
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LexicalAnalysisPanel(
    modifier: Modifier = Modifier,
    code: String
) {
    val tokenizer = remember(code) { Tokenizer(code) }
    val tokenList = remember { mutableStateListOf<Token?>() }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var isAnalysing by remember { mutableStateOf(false) }

    Surface(modifier) {
        var panelHeight by remember { mutableStateOf(300.dp) }
        Column {
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .pointerHoverIcon(PointerIcon(Cursor(Cursor.N_RESIZE_CURSOR)))
                    .onDrag { offset -> panelHeight -= offset.y.dp }, contentAlignment = Alignment.BottomCenter
            ) {
                Divider()
            }
            Row(
                Modifier.fillMaxWidth().background(Color(0xFFF0EFF2)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Análise léxica",
                    modifier = Modifier.padding(start = 16.dp),
                    fontWeight = FontWeight.Black
                )
                Row(
                    Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    CompileButton(
                        onClick = {
                            if (tokenList.isNotEmpty() && tokenList.last() == null){
                                tokenList.clear()
                                tokenizer.restore()
                            }
                            val token = tokenizer.getNextToken()
                            tokenList.add(token)
                            scope.launch {
                                listState.animateScrollToItem(tokenList.size)
                            }
                            if (!tokenizer.hasNext()){
                                tokenList.add(null)
                            }

                        },
                        contentColor = Color(0xFF2CCA6B),
                        icon = if (tokenList.isNotEmpty() && tokenList.last() == null) "./icons/icon_restore.svg" else "./icons/icon_next.svg"
                    )

                    CompileButton(
                        onClick = {
                            tokenList.clear()
                            tokenizer.restore()

                            var token = tokenizer.getNextToken()
                            isAnalysing = true
                            scope.launch {
                                while (token != null) {
                                    tokenList.add(token)
                                    token = tokenizer.getNextToken()
                                    listState.animateScrollToItem(tokenList.size)
                                    delay(100)
                                }
                                isAnalysing = false
                                tokenList.add(null)
                                listState.animateScrollToItem(tokenList.size)
                            }
                        },
                        contentColor = Color(0xFFDA1A48),
                        icon = "./icons/icon_next_all.svg",
                        enabled = tokenizer.hasNext() && !isAnalysing
                    )
                }
            }
            Divider()
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF292E35))
                    .height(panelHeight)
                    .padding(16.dp),
                state = listState
            ) {
                items(tokenList) { item ->
                    if (item != null) {
                        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                            Text(
                                item.lexeme,
                                color = Color(0xFFF3B30D),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Icon(
                                painterResource("./icons/token_divider.svg"),
                                null,
                                tint = Color.White.copy(alpha = .5f)
                            )
                            Text(
                                item.classification.description,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        Text(
                            "Análise concluída com sucesso!".uppercase(),
                            color = Color(0xFF2CCA6B),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }
        }

    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CompileButton(onClick: () -> Unit, contentColor: Color, icon: String, enabled: Boolean = true) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier.size(28.dp).pointerHoverIcon(PointerIcon.Hand),
        color = Color.Transparent,
//        contentColor = contentColor.copy(alpha = if (enabled) 1f else .4f)
    ) {
        Icon(painterResource(icon), null, tint = contentColor.copy(alpha = if (enabled) 1f else .4f))
    }
}

fun DrawScope.drawDivider(
    color: Color = Color.Gray.copy(alpha = 0.5f),
    horizontal: Dp = 60.dp
) {
    inset(horizontal = horizontal.toPx()) {
        drawLine(
            color = color,
            start = Offset.Zero,
            end = Offset(0f, size.height),
            strokeWidth = 0.5f
        )
    }
}

fun DrawScope.drawLineNumbers(textMeasurer: TextMeasurer, lineCount: Int) {
    inset(horizontal = 30.dp.toPx()) {
        repeat(lineCount) { line ->
            val text = (line + 1).toString()
            val measuredText = textMeasurer.measure(
                text,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                ),
                constraints = Constraints.fixedHeight(
                    height = 20.dp.roundToPx(),
                ),
            )
            val x = 5.dp.toPx() - measuredText.size.width
            val offset = Offset(x, (line + 1) * measuredText.size.height.toFloat())
            drawText(
                measuredText,
                topLeft = offset,
                color = Color.Gray.copy(alpha = .7f)
            )
        }
    }
}

