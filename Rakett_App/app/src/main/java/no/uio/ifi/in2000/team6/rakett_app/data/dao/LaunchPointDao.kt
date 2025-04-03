package no.uio.ifi.in2000.team6.rakett_app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPoint


@Dao
interface LaunchPointDao {

    @Upsert
    suspend fun upsertLaunchPoint(launchPoint: LaunchPoint)

    @Delete
    suspend fun deleteLaunchPoint(launchPoint: LaunchPoint)

    @Query("SELECT * FROM launchpoint ORDER BY id ASC")
    fun getAllLaunchPoints(): Flow<List<LaunchPoint>>

    @Update
    suspend fun updateLaunchPoint(launchPoint: LaunchPoint)

    @Query("UPDATE launchpoint SET selected = 0 WHERE selected = 1")
    suspend fun deselectAllLaunchPoints()
}