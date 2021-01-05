package net.easimer.surveyor

interface IRecorderService {
    fun requestFullLocationUpdate(observer: LocationUpdateObserver): Boolean
    fun markPointOfInterest(title: String): Boolean
}
