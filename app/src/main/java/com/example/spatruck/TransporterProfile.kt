package com.example.spatruck

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.spatruck.models.Customer
import com.example.spatruck.models.Transporter
import com.example.spatruck.ui.theme.SpatruckTheme
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class TransporterProfile : ComponentActivity() {
    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
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

                    Column(modifier = Modifier.padding(10.dp)) {
                        // mutableStateListOf<String?>()
                        var productList = mutableStateListOf<Transporter?>()
                        // getting firebase instance and the database reference
                        val firebaseDatabase = FirebaseDatabase.getInstance()
                        val databaseReference = firebaseDatabase.getReference("transporters")
                        // to read data values ,we use the addChildEventListener
                        databaseReference.addChildEventListener(object : ChildEventListener {
                            override fun onChildAdded(
                                snapshot: DataSnapshot,
                                previousChildName: String?
                            ) {
                                // this method is called when a new child/record is added to our db
                                // we are adding that item to the list
                                val transporter = snapshot.getValue(Transporter::class.java)
                                productList.add(transporter)
                            }

                            override fun onChildChanged(
                                snapshot: DataSnapshot,
                                previousChildName: String?
                            ) {
                                // this method is called when a new child is added
                                // when a new child is added to our list of
                            }

                            override fun onChildRemoved(snapshot: DataSnapshot) {
                                // method is called when we remove a child from the db
                            }

                            override fun onChildMoved(
                                snapshot: DataSnapshot,
                                previousChildName: String?
                            ) {
                                // method is called when we move a record/child in the db.
                            }

                            override fun onCancelled(error: DatabaseError) {
//                                    if we get any firebase error
                                Toast.makeText(this@TransporterProfile,"Error !!," + error.message, Toast.LENGTH_LONG).show()
                                Log.d("FirebaseReading","Error is " + error.message)
                                Log.d("FirebaseReading","Error is " + error.details)
                                Log.d("FirebaseReading","Error is " + error.code)
                            }

                        })
                        // call to composable to display our user interface
                        tranprofile(LocalContext.current, productList)
                    }
                    NavHost(
                        navController = navController,
                        startDestination = "Transporter",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("Customer") { goToCustomerActivity(this@TransporterProfile) }
                        composable("Transporter") {   }
                        composable("Registration") { goToRegistrationActivity(this@TransporterProfile )  }
                    }
                }
            }


            }

        }
    }


@Composable
fun tranprofile(
    context: Context,
    productList: SnapshotStateList<Transporter?>,
){
    Column(modifier = Modifier
        .fillMaxHeight()
        .fillMaxWidth()
        .background(Color.White),
        verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "transporters Available",
            modifier = Modifier.padding(10.dp),
            style = TextStyle(
                color = Color.Black, fontSize = 16.sp
            ), fontWeight = FontWeight.Bold
        )

        LazyColumn{
            items(productList) {transporter ->
                // here have a custom UI for the list or quick set up
//                 make my composable
                // !! this is called the safe call operator
                // its use here is to unwrap the opting String? value from product list.
                transporterCard(transporter = transporter!!, context)
            }
        }
    }
}




@Composable
fun transporterCard(
    transporter: Transporter,
    context: Context
) {
    val showDialog = remember { mutableStateOf(false) }

    // Click handler for the composable
    val onComposableClicked = {
        showDialog.value = true
    }
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable{
                      onComposableClicked()
            },
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            Image(
                painter = rememberImagePainter(data = transporter.vehicle_Image),
                contentDescription = "Image of Goods",
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
            )


            // Content
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = transporter.name,
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "transporter Contact: ${transporter.phone_Number}")
                Text(text = "Current Location: ${transporter.current_Location}")
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
    TransporterDialog(
        showDialog = showDialog.value,
        onDismiss = { showDialog.value = false },
        transporter = transporter
    )
}


@Composable
fun TransporterDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    transporter: Transporter
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = { onDismiss() }
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        if (transporter != null) {
                            Image(
                                painter = rememberImagePainter(data = transporter.vehicle_Image),
                                contentDescription = "Goods Photo",
                                modifier = Modifier
                                    .size(300.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    if (transporter != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = 4.dp
                        ) {
                            Text(
                                text = transporter.name,
                                style = MaterialTheme.typography.h3,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(16.dp)
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {

                        item {
                            if (transporter != null) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    elevation = 4.dp
                                ) {
                                    Column() {
                                        Text(
                                            text = "Phone Number: ${transporter.phone_Number}",
                                            modifier = Modifier.padding(16.dp)
                                        )
                                        Text(
                                            text = "Current Location: ${transporter.current_Location}",
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }

                                }
                            }
                        }


                        item {
                            if (transporter != null) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    elevation = 4.dp
                                ) {
                                    Column() {
                                        Text(
                                            text = "Target Location: ${transporter.target_Location}",
                                            modifier = Modifier.padding(16.dp)
                                        )
                                        Text(
                                            text = "Vehicle Number: ${transporter.vehicle_Number}",
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }

                                }
                            }
                        }





                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { /* Perform action for the additional key */ },
                            modifier = Modifier.weight(1f) ,
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue)
                        ) {
                            Text("Additional Key", color = Color.White)
                        }

                        Button(
                            onClick = { onDismiss() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                        ) {
                            Text("OK", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}


