package com.example.spatruck.models

data class Transporter(
    val transporterId: String,
    val name: String,
    val phone_Number: String,
    val current_Location: String,
    val target_Location: String,
    val vehicle_Number: String,
    val vehicle_Image: String
){
    constructor() : this("","","","","","","")

}
