package se.asapehrsson.podtest.backgroundservice

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import java.util.*

/**
 * Created by aasapehrsson on 2017-09-29.
 */
class MediaQueueHandler {
    val maxQueueSize: Int = 25

    internal var activeQueueIndex: Int = -1
    internal val mediaQueue = ArrayList<MediaSessionCompat.QueueItem>()

    fun hasItemAt(index: Int): Boolean = index in -1..mediaQueue.size

    fun hasNextItem(): Boolean = if (activeQueueIndex == -1) false else (hasItemAt(activeQueueIndex + 1))

    fun hasPreviousItem(): Boolean = if (activeQueueIndex == -1) false else (hasItemAt(activeQueueIndex - 1))

    fun addItem(item: MediaDescriptionCompat) {
        mediaQueue.add(MediaSessionCompat.QueueItem(item, Integer.parseInt(item.mediaId).toLong()))
    }

    fun addItemAt(index: Int, item: MediaDescriptionCompat) {
        mediaQueue.add(index, MediaSessionCompat.QueueItem(item, Integer.parseInt(item.mediaId).toLong()))
    }

    fun removeItem(id: Long) {
        var index = getItemIndex(id)
        if (index != -1) {
            mediaQueue.removeAt(index)
        } else throw IllegalArgumentException("Item not found")
    }

    fun removeItemAt(index: Int) {
        mediaQueue.removeAt(index)
    }

    fun setActiveItemAt(index: Int) {
        activeQueueIndex = if (hasItemAt(activeQueueIndex)) index else throw IllegalArgumentException("Index is out of bounds")
    }

    fun setActiveItemById(id: Long) {
        var index = getItemIndex(id)
        activeQueueIndex = if (index != -1) index else throw IllegalArgumentException("Item not found")
    }

    private fun getItemIndex(id: Long): Int {
        for ((index, element) in mediaQueue.withIndex()) {
            if (element.queueId == id) {
                return index
            }
        }
        return -1
    }

    fun getActiveItem(): MediaSessionCompat.QueueItem? =
            if (hasItemAt(activeQueueIndex)) mediaQueue[activeQueueIndex] else null


    fun skipToNextItem(): MediaSessionCompat.QueueItem? {
        var result: MediaSessionCompat.QueueItem? = null
        if (hasNextItem()) {
            activeQueueIndex++
            result = getActiveItem()
        }

        return result;
    }

    fun skipToPreviousItem(): MediaSessionCompat.QueueItem? {
        var result: MediaSessionCompat.QueueItem? = null
        if (hasPreviousItem()) {
            activeQueueIndex--
            result = getActiveItem()
        }

        return result;
    }
}