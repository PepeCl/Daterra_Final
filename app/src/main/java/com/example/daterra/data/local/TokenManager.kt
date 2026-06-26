package com.example.daterra.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "daterra_prefs")

class TokenManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        // NUEVAS LLAVES PARA EL PERFIL
        private val NOMBRE_KEY = stringPreferencesKey("user_nombre")
        private val CORREO_KEY = stringPreferencesKey("user_correo")
        private val COMUNA_KEY = stringPreferencesKey("user_comuna")
    }

    // 1. MODIFICAMOS saveToken PARA ACEPTAR LOS DATOS EXTRAS
    suspend fun saveTokenAndData(token: String, nombre: String, correo: String, comuna: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[NOMBRE_KEY] = nombre
            preferences[CORREO_KEY] = correo
            preferences[COMUNA_KEY] = comuna
        }
    }

    val getToken: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }

    // FLUJOS PARA LEER LOS DATOS DEL PERFIL
    val getNombre: Flow<String?> = context.dataStore.data.map { it[NOMBRE_KEY] }
    val getCorreo: Flow<String?> = context.dataStore.data.map { it[CORREO_KEY] }
    val getComuna: Flow<String?> = context.dataStore.data.map { it[COMUNA_KEY] }

    // 2. MODIFICAMOS clearToken PARA QUE BORRE TODO AL CERRAR SESIÓN
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear() // Esto borra el token y todos los datos guardados
        }
    }
}