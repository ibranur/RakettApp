package no.uio.ifi.in2000.team6.rakett_app.data.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPoint
import no.uio.ifi.in2000.team6.rakett_app.data.dao.LaunchPointDao

@Database(
    entities = [LaunchPoint::class],
    version = 3
)
abstract class LaunchPointDatabase: RoomDatabase() {
    abstract val dao: LaunchPointDao
}