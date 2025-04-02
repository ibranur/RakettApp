package no.uio.ifi.in2000.team6.rakett_app

data class LaunchPointState(

    val launchPoints: List<LaunchPoint> = emptyList(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isAddingLaunchPoint: Boolean = false
)
