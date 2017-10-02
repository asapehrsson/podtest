package se.asapehrsson.podtest.player

import io.reactivex.subjects.BehaviorSubject

/**
 * Created by aasapehrsson on 2017-09-27.
 */

interface IMediaPlayer {
    fun getMediaState(): BehaviorSubject<Int>?

    fun loadAndPlay(url: String)

    fun play()

    fun pause()

    fun release()

    fun isPlaying(): Boolean

    fun seekTo(positionInMillis: Long)

    fun setVolume(fl: Float)

    fun currentPositionInMillis(): Long
}
