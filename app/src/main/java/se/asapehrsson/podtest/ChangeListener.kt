package se.asapehrsson.podtest

interface ChangeListener<T> {
    fun onChange(event: T)
}
