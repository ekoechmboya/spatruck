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
import com.google.firebase.database.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.example.spatruck.models.Customer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class CustomerActivity : ComponentActivity() {
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
                                text = "Customer Registration",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                        })
                }) {
                    Column(modifier = Modifier.padding(it)) {
                        // getting firebase instance and the database reference
                        val firebaseDatabase = FirebaseDatabase.getInstance()
                        val databaseReference = firebaseDatabase.getReference("customers")
                        // storage reference
                        // instance of the storage service
                        val storage  = Firebase.storage
                        storageReference = storage.reference.child("goodsImages")
                        CustomerRegForm(LocalContext.current,databaseReference,storageReference)
                    }
                }
            }

        }
    }
}
@Composable
fun CustomerRegForm(
    context: Context,
    databaseReference: DatabaseReference,
    storageReference: StorageReference
) {
    //variables to store the users input
    val isLoading = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current


    val name = remember { mutableStateOf(TextFieldValue()) }
    val phone_Number = remember { mutableStateOf(TextFieldValue()) }
    val current_Location = remember { mutableStateOf(TextFieldValue()) }
    val target_Location = remember { mutableStateOf(TextFieldValue()) }
    val goods_Type = remember { mutableStateOf(TextFieldValue()) }
    val goods_Nature = remember { mutableStateOf(TextFieldValue()) }
    val proposed_Price = remember { mutableStateOf(TextFieldValue()) }
    val Goods_Photo_Url = remember { mutableStateOf(TextFieldValue()) }
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
                text = "Register To Be Transported For",
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
            Spacer(modifier = Modifier.height(1.dp))
            OutlinedTextField(
                value = phone_Number.value,
                onValueChange = { phone_Number.value = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(1.dp))
            OutlinedTextField(
                value = current_Location.value,
                onValueChange = { current_Location.value = it },
                label = { Text("Current Location") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(1.dp))
            OutlinedTextField(
                value = target_Location.value,
                onValueChange = { target_Location.value = it },
                label = { Text("Target Location") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(1.dp))
            OutlinedTextField(
                value = goods_Type.value,
                onValueChange = { goods_Type.value = it },
                label = { Text("Type of Goods") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(1.dp))
            OutlinedTextField(
                value = goods_Nature.value,
                onValueChange = { goods_Nature.value = it },
                label = { Text("Nature of Goods") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(1.dp))
            OutlinedTextField(
                value = proposed_Price.value,
                onValueChange = { proposed_Price.value = it },
                label = { Text("Proposed Price of Service") },
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
                                onClick = {
                                    isLoading.value = true
                                    // Simulating a delay to show loading indicator
                                    lifecycleOwner.lifecycleScope.launch {
                                        delay(2000) // Simulating some background task
                                        isLoading.value = false}
                                    launcher.launch("image/*")
                                          },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF64DD17))
                            ) {
                                if (isLoading.value) {
                                    CircularProgressIndicator(color = Color.Black)
                                } else {
                                Text(text = "Upload Image", color = Color.White)}
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
                                                val customerId = newProduct_Reference.key
                                                val customer = customerId?.let {
                                                    Customer(
                                                        it,
                                                        name.value.text,
                                                        phone_Number.value.text,
                                                        current_Location.value.text,
                                                        target_Location.value.text,
                                                        goods_Type.value.text,
                                                        goods_Nature.value.text,
                                                        proposed_Price.value.text,
                                                        imagePath
                                                    )
                                                }

                                                databaseReference.addValueEventListener(object :
                                                    ValueEventListener {
                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                        newProduct_Reference.setValue(customer)
                                                        Toast.makeText(
                                                            context,
                                                            "Product has been added successfully!!",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                        Log.d("Product Push", snapshot.toString())
                                                    }

                                                    override fun onCancelled(error: DatabaseError) {
                                                        Toast.makeText(
                                                            context,
                                                            "Product failed to be added!!",
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
                                if (isLoading.value) {
                                    CircularProgressIndicator(color = Color.Black)
                                } else {
                                Text(text = "Post", color = Color.White)
                                }
                            }

                            val activityLauncher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartActivityForResult()
                            ) { activityResult ->

                            }

                            Button(
                                onClick = {
                                    isLoading.value = true
                                    // Simulating a delay to show loading indicator
                                    lifecycleOwner.lifecycleScope.launch {
                                        delay(2000) // Simulating some background task
                                        isLoading.value = false
                                        val intent = Intent(context, CustomerDashboard::class.java)
                                        activityLauncher.launch(intent)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFD600))
                            ) {
                                if (isLoading.value) {
                                    CircularProgressIndicator(color = Color.Black)
                                } else {
                                    Text(text = "View Profiles", color = Color.Black)
                                }
                            }


                            Button(
                                onClick = {
                                    isLoading.value = true
                                    // Simulating a delay to show loading indicator
                                    lifecycleOwner.lifecycleScope.launch {
                                        delay(2000) // Simulating some background task
                                        isLoading.value = false}
                                    FirebaseAuth.getInstance().signOut()
                                    changeUi(context, activityLauncher)

                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD32F2F))
                            ) {
                                if (isLoading.value) {
                                    CircularProgressIndicator(color = Color.Black)
                                } else {
                                Text(text = "Sign Out", color = Color.White)}
                            }
                        }
                    }
                }

                }
            }

            }


                    }
                }



    @Composable
    fun imageUploader(
        activity: ComponentActivity,
        storageReference: StorageReference,
        databaseReference: DatabaseReference
    ) {
        // state to hold image uri
        val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
        //activity result launcher to start image picker
        val launcher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
                selectedImageUri.value = it
            }

        Column() {
            //button to launch the image picker
            Button(onClick = {
                launcher.launch("image/*")
            }) {
                Text(text = "select product image")
            }
        }
    }


fun changeUi(
    context: Context,
    activitylauncher: ManagedActivityResultLauncher<Intent,
    ActivityResult>
) {
    val intent = Intent(context, MainActivity::class.java)
    activitylauncher.launch(intent)
}




