package com.apartas.kotlin.event

import com.apartas.kotlin.model.Event


interface EventView{
    fun showLoading()
    fun hideLoading()
    fun showEventList(data: List<Event>)
}