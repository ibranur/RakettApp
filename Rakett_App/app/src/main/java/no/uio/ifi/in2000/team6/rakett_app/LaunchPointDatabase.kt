package no.uio.ifi.in2000.team6.rakett_app

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [LaunchPoint::class],
    version = 2
)
abstract class LaunchPointDatabase: RoomDatabase() {

    abstract val dao: LaunchPointDao
}