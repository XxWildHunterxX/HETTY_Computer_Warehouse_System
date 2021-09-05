package com.junhao.hetty_computer_warehouse_system.data

data class Product(
    val id :String,
    val name :String,
    val productBarcode :String,
    val productRack : String,
    val productType:String,
    val productDescription : String,
    val minimumAlertQty : String
    )