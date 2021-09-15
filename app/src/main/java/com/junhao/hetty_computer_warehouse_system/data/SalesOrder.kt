package com.junhao.hetty_computer_warehouse_system.data

data class SalesOrder(
    val salesOrderID : String? = null,
    val salesProductBarcode: String? = null,
    val salesProductName: String? = null,
    val salesCustomerName: String? = null,
    val salesCustomerPhone: String? = null,
    val salesQuantity: String? = null,
    val salesDate: String? = null,
    val salesPrice: String? = null,
    val salesPaymentType: String? = null
)
