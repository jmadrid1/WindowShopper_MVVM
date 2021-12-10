package com.example.windowshopper_mvvm.models

import java.io.Serializable

class Review(val id: Int,
           val comment: String,
           val date: String,
           val rating: Int) : Serializable
{
    constructor() : this(0,
        "",
        "",
        4,
    )
}