package com.junhao.hetty_computer_warehouse_system.data

data class TrackingItemDetails(
    var trackDate : String ?=null,
    var trackDesc:String? = null,
    var trackTime:String? = null,
    var trackLatitude:Double? = null,
    var trackLongitude:Double? = null,
    var trackNoOrder:String?=null

)
