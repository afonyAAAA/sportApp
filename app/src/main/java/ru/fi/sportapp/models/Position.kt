package ru.fi.sportapp.models

data class Position(
    //First - Column, //Second - Row, //Third - Position in row
    val position : Triple<Int, Int, Int>
)