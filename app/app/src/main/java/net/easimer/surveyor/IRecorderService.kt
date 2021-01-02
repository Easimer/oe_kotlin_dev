package net.easimer.surveyor

interface IRecorderService {
    fun requestFullLocationUpdate(callback: (locs: List<net.easimer.surveyor.data.Location>) -> Unit): Boolean
}
