package no.uio.ifi.in2000.team6.rakett_app.data.repository

import kotlinx.coroutines.flow.Flow
import no.uio.ifi.in2000.team6.rakett_app.data.dao.LaunchPointDao
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPoint

/**
 * Grensesnitt for LaunchPointRepository som definerer metoder for databaseoperasjoner.
 * Brukes for å tilby et konsistent API uavhengig av implementasjon.
 */
interface LaunchPointRepositoryInterface {
    fun getAllLaunchPoints(): Flow<List<LaunchPoint>>
    suspend fun upsertLaunchPoint(launchPoint: LaunchPoint)
    suspend fun deleteLaunchPoint(launchPoint: LaunchPoint)
    suspend fun updateLaunchPoint(launchPoint: LaunchPoint)
    suspend fun deselectAllLaunchPoints()
}

/**
 * Implementasjon av LaunchPointRepositoryInterface som bruker Room DAO for databasetilgang.
 * Følger repository-mønsteret for å isolere databaselogikk fra resten av applikasjonen.
 */
class LaunchPointRepository(
    private val dao: LaunchPointDao
) : LaunchPointRepositoryInterface {

    // Henter alle oppskytningspunkter som en Flow
    override fun getAllLaunchPoints(): Flow<List<LaunchPoint>> =
        dao.getAllLaunchPoints()

    // Legger til eller oppdaterer et oppskytningspunkt
    override suspend fun upsertLaunchPoint(launchPoint: LaunchPoint) {
        dao.upsertLaunchPoint(launchPoint)
    }

    // Sletter et oppskytningspunkt
    override suspend fun deleteLaunchPoint(launchPoint: LaunchPoint) {
        dao.deleteLaunchPoint(launchPoint)
    }

    // Oppdaterer et eksisterende oppskytningspunkt
    override suspend fun updateLaunchPoint(launchPoint: LaunchPoint) {
        dao.updateLaunchPoint(launchPoint)
    }

    // Fjerner markering fra alle oppskytningspunkter
    override suspend fun deselectAllLaunchPoints() {
        dao.deselectAllLaunchPoints()
    }
}