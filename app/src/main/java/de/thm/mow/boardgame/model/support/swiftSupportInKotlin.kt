package de.thm.mow.boardgame.model.support

import kotlinx.coroutines.*
import kotlin.math.abs

fun assert(value: Boolean, message: String) {
    assert(value) {message}
}


fun <T> MutableList(item: T, times: Int) : MutableList<T> {
    return MutableList<T>(times) {item}
}

fun <T> MutableList<MutableList<T>>.add(item: Array<T>) {
    this.add(item.toMutableList())
}

operator fun <T> MutableList<T>.plus(other: MutableList<T>) : MutableList<T> {
    val newList = this.toMutableList()
    newList.addAll(other)
    return newList
}

fun <T> MutableList<T>.copy() : MutableList<T> {
    return this.toMutableList()
}


fun Double(value: Int) : Double {
    return value.toDouble()
}

val Double.absoluteValue : Double
    get() = abs(this)

val Int.absoluteValue : Int
    get() = abs(this)

annotation class argLabel(val name: String)
annotation class tuple

// Concurrency
fun <T, R> Iterable<T>.asyncMap(transform: (T) -> R, processResult: (List<R>) -> Unit) {
    GlobalScope.launch(Dispatchers.Main) {
        val result = mutableListOf<R>()
        val deferred = this@asyncMap.map { GlobalScope.async { transform(it) } }
        deferred.forEach { result.add(it.await()) }
        processResult(result)
    }
}
