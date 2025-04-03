package no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LaunchPoint(
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val selected: Boolean,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
