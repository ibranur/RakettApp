package no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving

sealed interface LaunchPointEvent {
    object saveLaunchPoint: LaunchPointEvent
    data class setLatitude(val latitude: String): LaunchPointEvent
    data class setLongitude(val longitude: String): LaunchPointEvent
    data class setName(val name: String): LaunchPointEvent
    data class DeleteLaunchPoint(val launchPoint: LaunchPoint): LaunchPointEvent
    data class UpdateLaunchPoint(val launchPoint: LaunchPoint): LaunchPointEvent
    object ShowDialog: LaunchPointEvent
    object HideDialog: LaunchPointEvent
    object ShowEditDialog: LaunchPointEvent
    object HideEditDialog: LaunchPointEvent
    data class SetCurrentEditLocation(val launchPoint: LaunchPoint): LaunchPointEvent
}