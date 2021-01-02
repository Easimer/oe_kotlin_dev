package net.easimer.surveyor

import android.location.Location

interface IGPSClient {
    fun setCallback(callback: (List<Location>) -> Unit)
    fun start()
    fun shutdown()
}