package no.uio.ifi.in2000.team6.rakett_app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LaunchPoint(
    val latitude: Double,
    val longitude: Double,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
