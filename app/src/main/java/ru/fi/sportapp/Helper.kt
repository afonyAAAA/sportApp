package ru.fi.sportapp

import ru.fi.sportapp.models.Casino

object Helper {
    var selectedCasino = Casino(
        "",
        "",
        "",
        "",
        emptyList(),
        emptyList(),
        null
    )
    var isClickedCardCasino : Boolean = false
}