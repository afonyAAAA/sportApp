package ru.fi.sportapp.firebase

interface FirebaseRepository {
    suspend fun getUrl() : String
}