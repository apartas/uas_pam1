package com.apartas.kotlin.api

import java.net.URL

class ApiRepository{

    fun doRequest(url: String): String{
        return URL(url).readText()
    }
}