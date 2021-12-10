package com.example.windowshopper_mvvm.models

import java.io.Serializable

class Item(val id: Int,
           val title: String,
           val summary: String,
           val image: String,
           val price: Float,
           val reviews: HashMap<String, Any>?) : Serializable {

    constructor() : this(0,
        "",
        "",
        "",
        0.0F,
        null
    )

}