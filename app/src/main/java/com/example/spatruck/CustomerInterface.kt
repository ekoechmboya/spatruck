package com.example.spatruck

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.spatruck.ui.theme.SpatruckTheme

class CustomerInterface : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            SpatruckTheme {
                val navController = rememberNavController()
                val items = listOf(
                    BottomNavItem("Customer", Icons.Default.Person),
                    BottomNavItem("Transporter", Icons.Default.AccountBox),
                    BottomNavItem("Registration", Icons.Default.Create),
                )

                Scaffold(
                    bottomBar = {
                        BottomNavigation {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentRoute = navBackStackEntry?.destination?.route

                            items.forEach { item ->
                                BottomNavigationItem(
                                    icon = { Icon(item.icon, contentDescription = null) },
                                    label = { Text(item.title) },
                                    selected = currentRoute == item.title,
                                    onClick = {
                                        navController.navigate(item.title) {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "Registration",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("Customer") { goToCustomerActivity(this@CustomerInterface) }
                        composable("Transporter") { goToTransporterActivity(this@CustomerInterface )  }
                        composable("Registration") {   }
                    }
                }
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.background_image),
                        contentDescription = "Background Image",
                        modifier = Modifier.fillMaxSize()
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { goToCustomerRegistration(this@CustomerInterface) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                        ) {
                            Text(text = "Register As Customer")
                        }

                        Button(
                            onClick = { goToTransporterRegistration(this@CustomerInterface) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                        ) {
                            Text(text = "Register As Transporter")
                        }
                    }
                }
            }

            }
        }
    }


fun goToCustomerRegistration(context: Activity) {
    val intent = Intent(context, CustomerActivity::class.java)
    context.startActivity(intent)
}
fun goToTransporterRegistration(context: Activity) {
    val intent = Intent(context, TransporterActivity::class.java)
    context.startActivity(intent)
}