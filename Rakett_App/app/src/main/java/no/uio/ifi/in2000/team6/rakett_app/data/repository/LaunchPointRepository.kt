package no.uio.ifi.in2000.team6.rakett_app.data.repository

import kotlinx.coroutines.flow.Flow
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

    override fun getAllLaunchPoints(): Flow<List<LaunchPoint>> =
        dao.getAllLaunchPoints()

    override suspend fun upsertLaunchPoint(launchPoint: LaunchPoint) {
        dao.upsertLaunchPoint(launchPoint)
    }

    override suspend fun deleteLaunchPoint(launchPoint: LaunchPoint) {
        dao.deleteLaunchPoint(launchPoint)
    }

    override suspend fun updateLaunchPoint(launchPoint: LaunchPoint) {
        dao.updateLaunchPoint(launchPoint)
    }

    override suspend fun deselectAllLaunchPoints() {
        dao.deselectAllLaunchPoints()
    }
}