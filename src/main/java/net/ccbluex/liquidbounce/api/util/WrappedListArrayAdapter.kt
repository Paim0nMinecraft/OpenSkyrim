package net.ccbluex.liquidbounce.api.util

class WrappedListArrayAdapter<O, T>(val wrapped: MutableList<O>, val unwrapper: (T) -> O, val wrapper: (O) -> T) :
    IWrappedArray<T> {
    override fun get(index: Int): T = wrapper(wrapped[index])

    override fun set(index: Int, value: T) {
        wrapped[index] = unwrapper(value)
    }

    override fun iterator(): Iterator<T> = WrappedCollection.WrappedCollectionIterator(wrapped.iterator(), wrapper)
}