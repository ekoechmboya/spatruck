package com.example.spatruck

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import com.google.firebase.database.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.example.spatruck.models.Customer
import com.example.spatruck.models.Transporter


class TransporterActivity : ComponentActivity() {
    private lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                Scaffold(topBar = {
                    TopAppBar(backgroundColor = Color.Black,
                        title = {
                            Text(
                                text = "Transporter Registration",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                        })
                }) {
                    Column(modifier = Modifier.padding(it)) {
                        // getting firebase instance and the database reference
                        val firebaseDatabase = FirebaseDatabase.getInstance()
                        val databaseReference = firebaseDatabase.getReference("transporters")
                        // storage reference
                        // instance of the storage service
                        val storage  = Firebase.storage
                        storageReference = storage.reference.child("transporterImages")
                        TransporterRegForm(LocalContext.current,databaseReference,storageReference)
                    }
                }
            }

        }
    }
}
@Composable
fun TransporterRegForm(
    context: Context,
    databaseReference: DatabaseReference,
    storageReference: StorageReference
) {
    //variables to store the users input



            val name = remember { mutableStateOf(TextFieldValue()) }
            val phone_Number = remember { mutableStateOf(TextFieldValue()) }
            val current_Location = remember { mutableStateOf(TextFieldValue()) }
            val target_Location = remember { mutableStateOf(TextFieldValue()) }
            val vehicle_Number = remember { mutableStateOf(TextFieldValue()) }


    val backgroundImage = painterResource(id = R.drawable.background_image)
    // composable set of textfields for users to add details
    Box(

        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        ) {
        Image(
            painter = backgroundImage,
            contentDescription = null,
            modifier = Modifier.fillMaxSize())


        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(Color.White),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Register To Offer Transport Service",
                modifier = Modifier.padding(7.dp),
                style = TextStyle(
                    color = Color.Black, fontSize = 10.sp
                ),
                fontWeight = FontWeight.Bold
            )



            OutlinedTextField(
                value = name.value,
                onValueChange = { name.value = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = phone_Number.value,
                onValueChange = { phone_Number.value = it },
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = current_Location.value,
                onValueChange = { current_Location.value = it },
                label = { Text("Current Location") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = target_Location.value,
                onValueChange = { target_Location.value = it },
                label = { Text("Target Location") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = vehicle_Number.value,
                onValueChange = { vehicle_Number.value = it },
                label = { Text("Vehicle Number") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(5.dp))
            val selectedUri = remember { mutableStateOf<Uri?>(null) }
            //reference to the launcher
            val launcher =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
                    // save selection path to our state variable
                    selectedUri.value = it
                }
            // Button action to select an image from my phone gallery
            // 1. A state to hold our upload value
            // 2. A launcherforActivityResult instance : start an activity : access other apps within our android device (gallery,documents)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                ) {item{
                    Box(
                        modifier = Modifier
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { launcher.launch("image/*") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF64DD17))
                            ) {
                                Text(text = "Upload Image", color = Color.White)
                            }

                            Button(
                                onClick = {
                                    selectedUri.value?.let {
                                        val imageName = "image_${System.currentTimeMillis()}"
                                        val imageRef = storageReference.child(imageName)
                                        imageRef.putFile(it).addOnSuccessListener {
                                            imageRef.downloadUrl.addOnSuccessListener {
                                                val imagePath = it.toString()
                                                val newProduct_Reference = databaseReference.push()
                                                val transporterId = newProduct_Reference.key
                                                val customer = transporterId?.let {
                                                    Transporter(
                                                        it,
                                                        name.value.text,
                                                        phone_Number.value.text,
                                                        current_Location.value.text,
                                                        target_Location.value.text,
                                                        vehicle_Number.value.text,
                                                        imagePath
                                                    )
                                                }

                                                databaseReference.addValueEventListener(object :
                                                    ValueEventListener {
                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                        newProduct_Reference.setValue(customer)
                                                        Toast.makeText(
                                                            context,
                                                            "Transporter has been added successfully!!",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                        Log.d("Product Push", snapshot.toString())
                                                    }

                                                    override fun onCancelled(error: DatabaseError) {
                                                        Toast.makeText(
                                                            context,
                                                            "Transporter failed to be added!!",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                        Log.d("Product Push", error.message)
                                                    }

                                                })
                                            }
                                        }
                                    }

                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                enabled = selectedUri.value != null,
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF2962FF))
                            ) {
                                Text(text = "Register", color = Color.White)
                            }

                            val activityLauncher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartActivityForResult()
                            ) { activityResult ->

                            }

                            Button(
                                onClick = {
                                    val intent = Intent(context, TransporterProfile::class.java)
                                    activityLauncher.launch(intent)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFD600))
                            ) {
                                Text(text = "View Profiles", color = Color.Black)
                            }

                            Button(
                                onClick = {
                                    FirebaseAuth.getInstance().signOut()
                                    changeUi(context, activityLauncher)

                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD32F2F))
                            ) {
                                Text(text = "Sign Out", color = Color.White)
                            }
                        }
                    }
                }

                }
            }

        }


    }
}






