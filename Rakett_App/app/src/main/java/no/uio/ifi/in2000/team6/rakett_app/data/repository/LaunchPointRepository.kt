package no.uio.ifi.in2000.team6.rakett_app.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPoint
import no.uio.ifi.in2000.team6.rakett_app.data.dao.LaunchPointDao

interface LaunchPointRepositoryInterface {
    fun getAllLaunchPoints(): Flow<List<LaunchPoint>>
    suspend fun upsertLaunchPoint(launchPoint: LaunchPoint)
    suspend fun deleteLaunchPoint(launchPoint: LaunchPoint)
    suspend fun updateLaunchPoint(launchPoint: LaunchPoint)
    suspend fun deselectAllLaunchPoints()
}

class LaunchPointRepository(
    private val dao: LaunchPointDao
) : LaunchPointRepositoryInterface {
    private val TAG = "LaunchPointRepository"

    override fun getAllLaunchPoints(): Flow<List<LaunchPoint>> =
        dao.getAllLaunchPoints()
            .onEach { points ->
                Log.d(TAG, "Retrieved ${points.size} launch points")
                points.forEach { point ->
                    Log.d(TAG, "Point: ${point.name}, lat: ${point.latitude}, lon: ${point.longitude}, selected: ${point.selected}, id: ${point.id}")
                }
            }
            .catch { e ->
                Log.e(TAG, "Error getting launch points", e)
            }

    override suspend fun upsertLaunchPoint(launchPoint: LaunchPoint) {
        try {
            Log.d(TAG, "Upserting launch point: ${launchPoint.name}, lat: ${launchPoint.latitude}, lon: ${launchPoint.longitude}, selected: ${launchPoint.selected}")
            dao.upsertLaunchPoint(launchPoint)
            Log.d(TAG, "Launch point upserted successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error upserting launch point", e)
            throw e
        }
    }

    override suspend fun deleteLaunchPoint(launchPoint: LaunchPoint) {
        try {
            Log.d(TAG, "Deleting launch point: ${launchPoint.name}, id: ${launchPoint.id}")
            dao.deleteLaunchPoint(launchPoint)
            Log.d(TAG, "Launch point deleted successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting launch point", e)
            throw e
        }
    }

    override suspend fun updateLaunchPoint(launchPoint: LaunchPoint) {
        try {
            Log.d(TAG, "Updating launch point: ${launchPoint.name}, id: ${launchPoint.id}, selected: ${launchPoint.selected}")
            dao.updateLaunchPoint(launchPoint)
            Log.d(TAG, "Launch point updated successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating launch point", e)
            throw e
        }
    }

    override suspend fun deselectAllLaunchPoints() {
        try {
            Log.d(TAG, "Deselecting all launch points")
            dao.deselectAllLaunchPoints()
            Log.d(TAG, "All launch points deselected successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error deselecting all launch points", e)
            throw e
        }
    }
}