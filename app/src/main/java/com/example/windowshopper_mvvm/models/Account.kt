package com.example.windowshopper_mvvm.models

import java.io.Serializable

class Account( val username: String,
               var email: String,
               val password: String)
                : Serializable {

    constructor() : this("",
        "",
        ""
    )

}