package com.junhao.hetty_computer_warehouse_system.data

data class Product(
    val productName :String?=null,
    val productBarcode :String?=null,
    val productRack : String?=null,
    val productType:String?=null,
    val productPrice:String?=null,
    val productQuantity : String?=null,
    val minimumAlertQty : String?=null,
    val alertChk :String?=null,
    val productImg : String?=null
    )