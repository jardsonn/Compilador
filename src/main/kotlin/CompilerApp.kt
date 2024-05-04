package com.jalloft.compilador

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.google.common.io.Resources
import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.Tokenizer
import com.jalloft.compilador.com.jalloft.compilador.ui.CompileScreen
import com.jalloft.compilador.com.jalloft.compilador.utils.printToken

@Composable
@Preview
fun App() {

    MaterialTheme {
        CompileScreen()
    }
}

fun main() = application {
    val state = rememberWindowState(
        placement = WindowPlacement.Maximized,
        position = WindowPosition(Alignment.Center),
        isMinimized = false,
        width = 900.dp,
        height = 700.dp
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "Compilador",
//        icon = painterResource("images/ic_states.svg"),
        state = state
    ) {
        App()
    }
}

//fun main() {
//    val sourcePath = "fontes/codigo4.txt"
//    val source = Resources.getResource(sourcePath).readText()
//    val lexer = Tokenizer(source)
//
//    var token = lexer.getNextToken()
//
//    while (token != null) {
//        printToken(token)
//        token = lexer.getNextToken()
//    }
//}

