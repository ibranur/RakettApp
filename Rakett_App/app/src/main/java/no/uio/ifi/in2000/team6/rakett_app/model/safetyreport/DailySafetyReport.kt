package no.uio.ifi.in2000.team6.rakett_app.model.safetyreport

data class DailySafetyReport(
    val safetyReports: List<SafetyReport>,
    val score: Double

)
