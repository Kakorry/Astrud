package com.github.korblu.astrud.data.repos

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import com.github.korblu.astrud.data.datastore.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepo @Inject constructor(
    private val userPreferencesDataStore : DataStore<UserPreferences>
){
    val userPreferencesFlow : Flow<UserPreferences> = userPreferencesDataStore.data
        .catch { e ->
            if(e is IOException) {
                emit(UserPreferences.getDefaultInstance())
            } else {
                throw e
            }
        }

    suspend fun updateAstrudMusicUri(uri: String) {
        userPreferencesDataStore.updateData{ currentPreferences ->
            currentPreferences.toBuilder()
                .setAstrudmusicUri(uri)
                .build()
        }
    }

    suspend fun clearAstrudMusicUri() {
        userPreferencesDataStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .clearAstrudmusicUri()
                .build()
        }
    }
}