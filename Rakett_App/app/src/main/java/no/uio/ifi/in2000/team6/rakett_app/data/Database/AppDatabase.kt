package no.uio.ifi.in2000.team6.rakett_app.data.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import no.uio.ifi.in2000.team6.rakett_app.data.dao.LaunchPointDao
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPoint

/**
 * Room database-konfigurasjon for applikasjonen.
 * Implementerer singleton-mønster for å sikre en enkelt databaseinstans.
 */
@Database(
    entities = [LaunchPoint::class],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    // Gir tilgang til LaunchPointDao
    abstract fun launchPointDao(): LaunchPointDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Oppretter eller returnerer eksisterende databaseinstans
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}