package se.asapehrsson.podtest.mediaqueue

import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import se.asapehrsson.podtest.LazyLoadedEpisodeList

/**
 * Created by aasapehrsson on 2017-09-28.
 */


fun setQueue(mediaController: MediaControllerCompat?, items: LazyLoadedEpisodeList) {

    for (i in 0..items.noOfLoadedItems) {
        items.getEpisode(i)?.let {
            var extra = Bundle()
            // also add start offset
            extra.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, (it.listenpodfile?.duration!! * 1000).toLong())
            val description = MediaDescriptionCompat.Builder()
                    .setMediaId(it.id.toString())
                    .setTitle(it.title)
                    .setSubtitle(it.description)
                    .setIconUri(Uri.parse(it.imageurl))
                    .setMediaUri(Uri.parse(it.listenpodfile?.url ?: ""))
                    .setExtras(extra)
                    .build()
            mediaController?.addQueueItem(description)
        }
    }

    mediaController?.sendCommand("Bla bla vla", null, null)
}


