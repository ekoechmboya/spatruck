package com.example.spatruck


import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.spatruck.models.Checkoutmodel
import com.example.spatruck.models.Customer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface CheckoutAPI {
    @POST("Checkout")
    fun simulateCheckout(@Body checkoutmodel : Checkoutmodel?) : Call<Checkoutmodel?>?
}

fun simulateCheckout( customer: Customer) {
    val response = ""
    val baseUrl = "http://192.168.254.195/"
    val retrofit = Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build()
    val checkoutApi = retrofit.create(CheckoutAPI::class.java)
    val datamodel = Checkoutmodel(customer.proposed_Price, customer.phone_Number)
    val call : Call<Checkoutmodel?>? = checkoutApi.simulateCheckout(datamodel)
    call!!.enqueue(object : Callback<Checkoutmodel?> {
        override fun onResponse(call: Call<Checkoutmodel?>, response: Response<Checkoutmodel?>) {
            Log.d("Response from server", response.toString())
        }

        override fun onFailure(call: Call<Checkoutmodel?>, t: Throwable) {
            t.message?.let { Log.d("Response from server", it) }
        }
})}