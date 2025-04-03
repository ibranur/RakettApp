package no.uio.ifi.in2000.team6.rakett_app

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction

@Composable
fun DropdownMenuWithDetails(
    launchPoint: LaunchPoint,
    onClick: (LaunchPointEvent) -> Unit,
    modifier: Modifier) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier

    ) {


        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // First section
            DropdownMenuItem(
                text = { Text("Select") },
                leadingIcon = { Icon(Icons.Outlined.Favorite, contentDescription = null) },
                onClick = { onClick(LaunchPointEvent.UpdateLaunchPoint(launchPoint.copy(selected = true)))}
            )

            HorizontalDivider()

            // Second section

            HorizontalDivider()

            // Third section
            DropdownMenuItem(
                text = { Text("Update") },
                leadingIcon = { Icon(Icons.Outlined.Build, contentDescription = null) },
                onClick = { }
            )
            DropdownMenuItem(
                text = { Text("Delete") },
                leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null) },
                onClick = { onClick(LaunchPointEvent.DeleteLaunchPoint(launchPoint))}
            )
            DropdownMenuItem(
                text = { Text("Help") },
                leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = null) },
                onClick = { }
            )
        }
    }
}

