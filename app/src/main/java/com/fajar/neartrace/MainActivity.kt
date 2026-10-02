package com.fajar.neartrace

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.ContextCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.fajar.neartrace.ui.screens.DashboardScreen
import com.fajar.neartrace.ui.screens.HistoryScreen
import com.fajar.neartrace.ui.screens.TrackingScreen
import com.fajar.neartrace.ui.theme.NearTraceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var pendingAction: (() -> Unit)? = null
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        if (result.values.all { it }) ensureBluetooth() else {
            pendingAction = null
            Toast.makeText(this, "Izin Bluetooth diperlukan untuk memindai perangkat.", Toast.LENGTH_LONG).show()
        }
    }
    private val bluetoothLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val enabled = bluetoothAdapter()?.isEnabled == true
        if (enabled) pendingAction?.invoke() else Toast.makeText(this, "Aktifkan Bluetooth untuk memulai pemindaian.", Toast.LENGTH_LONG).show()
        pendingAction = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NearTraceTheme {
                val navController = rememberNavController()
                val entry by navController.currentBackStackEntryAsState()
                val route = entry?.destination?.route
                val tabs = listOf(Tab("scanner", "Pindai", Icons.Rounded.Radar), Tab("history", "Riwayat", Icons.Rounded.History))
                Scaffold(bottomBar = {
                    if (route != null && !route.startsWith("tracking")) NavigationBar {
                        tabs.forEach { tab -> NavigationBarItem(selected = route == tab.route, onClick = { navController.navigate(tab.route) { popUpTo(navController.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true } }, icon = { Icon(tab.icon, null) }, label = { Text(tab.label) }) }
                    }
                }) { padding ->
                    NavHost(navController, startDestination = "scanner", modifier = androidx.compose.ui.Modifier.padding(padding)) {
                        composable("scanner") { DashboardScreen(onDevice = { requestBleReady { navController.navigate("tracking/${Uri.encode(it)}") } }, onStartRequested = { requestBleReady(it) }) }
                        composable("history") { HistoryScreen(onDevice = { requestBleReady { navController.navigate("tracking/${Uri.encode(it)}") } }) }
                        composable("tracking/{address}", arguments = listOf(navArgument("address") { type = androidx.navigation.NavType.StringType })) { TrackingScreen(onBack = { navController.navigateUp() }) }
                    }
                }
            }
        }
    }

    private fun requestBleReady(action: () -> Unit) {
        pendingAction = action
        val missing = requiredPermissions().filter { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
        if (missing.isNotEmpty()) permissionLauncher.launch(missing.toTypedArray()) else ensureBluetooth()
    }

    private fun ensureBluetooth() {
        if (bluetoothAdapter()?.isEnabled == true) { pendingAction?.invoke(); pendingAction = null }
        else bluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
    }

    private fun requiredPermissions(): List<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) listOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT) else listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private fun bluetoothAdapter() = (getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter
    private data class Tab(val route: String, val label: String, val icon: ImageVector)
}
