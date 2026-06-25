package com.example.daterra.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Creamos la instancia de DataStore como una extensión del Contexto
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "daterra_prefs")

class TokenManager(private val context: Context) {

    companion object {
        // Definimos la llave única para el token
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
    }

    // Función para guardar el token de forma asíncrona
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    // Flujo (Flow) para leer el token en tiempo real. Devuelve null si no existe.
    val getToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    // Función para borrar el token (útil para el Cerrar Sesión)
    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }
}