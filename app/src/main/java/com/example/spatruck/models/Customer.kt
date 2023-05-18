package com.example.spatruck.models

data class Customer(
    val customerId: String,
    val name: String,
    val phone_Number: String,
    val current_Location: String,
    val target_Location: String,
    val goods_Type: String,
    val goods_Nature: String,
    val proposed_Price: String,
    val goods_Photo_Url: String
){
    constructor() : this("","","","","","","","","")
}
