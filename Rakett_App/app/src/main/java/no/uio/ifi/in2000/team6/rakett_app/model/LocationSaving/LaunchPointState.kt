package no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving

data class LaunchPointState(

    val launchPoints: List<LaunchPoint> = emptyList(),
    val latitude: String = "",
    val longitude: String = "",
    val name: String = "",
    val isAddingLaunchPoint: Boolean = false,
    val isUpdatingLaunchPoint: Boolean = false,
)
