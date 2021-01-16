package com.apartas.kotlin.detail.event

import com.apartas.kotlin.model.EventDetail


interface EventDetailView{
    fun showLoading()
    fun hideLoading()
    fun showEventDetail(data: List<EventDetail>)
}