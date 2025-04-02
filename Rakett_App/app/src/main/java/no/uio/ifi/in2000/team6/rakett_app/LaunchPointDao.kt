package no.uio.ifi.in2000.team6.rakett_app

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow


@Dao
interface LaunchPointDao {

    @Upsert
    suspend fun upsertLaunchPoint(launchPoint: LaunchPoint)

    @Delete
    suspend fun deleteLaunchPoint(launchPoint: LaunchPoint)

    @Query("SELECT * FROM launchpoint ORDER BY id ASC")
    fun getAllLaunchPoints(): Flow<List<LaunchPoint>>

}