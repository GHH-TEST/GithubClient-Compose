package com.ghh.test.githubclient.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ghh.test.githubclient.model.User
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "github_auth")
private val gson = Gson()

object DataStoreKeys {
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    val USER_TOKEN = stringPreferencesKey("user_token")
    val USER_INFO = stringPreferencesKey("user_info")
}

class DataStoreUtil(private val context: Context) {
    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data
        .map { it[DataStoreKeys.IS_LOGGED_IN] ?: false }

    val userTokenFlow: Flow<String> = context.dataStore.data
        .map { it[DataStoreKeys.USER_TOKEN] ?: "" }

    val userInfoFlow: Flow<User?> = context.dataStore.data
        .map {
            val userJson = it[DataStoreKeys.USER_INFO] ?: return@map null
            runCatching { gson.fromJson(userJson, User::class.java) }.getOrNull()
        }

    suspend fun saveLoginState(token: String, user: User) {
        context.dataStore.edit {
            it[DataStoreKeys.IS_LOGGED_IN] = true
            it[DataStoreKeys.USER_TOKEN] = token
            it[DataStoreKeys.USER_INFO] = gson.toJson(user)
        }
    }

    suspend fun clearLoginState() {
        context.dataStore.edit {
            it.remove(DataStoreKeys.IS_LOGGED_IN)
            it.remove(DataStoreKeys.USER_TOKEN)
            it.remove(DataStoreKeys.USER_INFO)
        }
    }
}