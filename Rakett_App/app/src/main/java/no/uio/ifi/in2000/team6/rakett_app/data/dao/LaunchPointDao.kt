package no.uio.ifi.in2000.team6.rakett_app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPoint


/**
 * Data Access Object (DAO) for oppskytningspunkter.
 * Definerer alle databaseoperasjoner for LaunchPoint-entiteten.
 */
@Dao
interface LaunchPointDao {

    // Setter inn eller oppdaterer et oppskytningspunkt
    @Upsert
    suspend fun upsertLaunchPoint(launchPoint: LaunchPoint)

    // Sletter et oppskytningspunkt
    @Delete
    suspend fun deleteLaunchPoint(launchPoint: LaunchPoint)

    // Henter alle oppskytningspunkter sortert etter ID
    @Query("SELECT * FROM launchpoint ORDER BY id ASC")
    fun getAllLaunchPoints(): Flow<List<LaunchPoint>>

    // Oppdaterer et eksisterende oppskytningspunkt
    @Update
    suspend fun updateLaunchPoint(launchPoint: LaunchPoint)

    // Fjerner markering fra alle oppskytningspunkter
    @Query("UPDATE launchpoint SET selected = 0 WHERE selected = 1")
    suspend fun deselectAllLaunchPoints()
}