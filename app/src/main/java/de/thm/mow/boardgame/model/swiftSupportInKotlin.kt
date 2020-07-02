package de.thm.mow.boardgame.model

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


annotation class argLabel(val name: String)
annotation class tuple