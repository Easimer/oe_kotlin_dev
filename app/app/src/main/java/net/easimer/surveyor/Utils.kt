package net.easimer.surveyor

fun <K, V> HashMap<K, V>.tryPop(key: K): V? {
    val ret = this[key]
    remove(key)
    return ret
}