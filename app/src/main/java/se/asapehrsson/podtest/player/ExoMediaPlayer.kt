package se.asapehrsson.podtest.player

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory

/**
 * Created by aasapehrsson on 2017-09-27.
 */

class ExoMediaPlayer(private val context: Context) : IMediaPlayer {
    private var player: SimpleExoPlayer? = null
    private var playbackPosition: Long = 0
    private var currentWindow: Int = 0
    private var playWhenReady = true

    private var eventLogger: EventLogger? = null

    private fun initializePlayer() {
        if (player != null) {
            // a factory to create an AdaptiveVideoTrackSelection
            val adaptiveTrackSelectionFactory = AdaptiveTrackSelection.Factory(DefaultBandwidthMeter())
            // using a DefaultTrackSelector with an adaptive video selection factory
            val trackSelector = DefaultTrackSelector(adaptiveTrackSelectionFactory)
            player = ExoPlayerFactory.newSimpleInstance(DefaultRenderersFactory(context), trackSelector, DefaultLoadControl())

            eventLogger = EventLogger(trackSelector, player)
            player?.let {
                it.addListener(eventLogger)
                it.addMetadataOutput(eventLogger)
                it.setAudioDebugListener(eventLogger)
                it.setVideoDebugListener(eventLogger)

                it.playWhenReady = playWhenReady
                it.seekToDefaultPosition()
            }
        }
    }


    override fun loadAndPlay(url: String) {
        initializePlayer()
        var mediaSource = buildMediaSource(Uri.parse("http://live-cdn.sr.se/pool2/p1/p1.isml/p1.mpd"));
        player?.prepare(mediaSource, true, false);
    }

    override fun play() {
        player?.playWhenReady = true
    }

    override fun pause() {
        player?.playWhenReady = false
    }

    override fun release() {
        player?.let {
            playbackPosition = it.currentPosition
            currentWindow = it.currentWindowIndex
            playWhenReady = it.playWhenReady
            it.setVideoDebugListener(null)
            it.setAudioDebugListener(null)
            it.release()
            player = null
        }
    }

    override fun isPlaying(): Boolean {
        return player?.playWhenReady ?: false
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory = DefaultHttpDataSourceFactory("ua", DefaultBandwidthMeter())
        val dashChunkSourceFactory = DefaultDashChunkSource.Factory(dataSourceFactory)
        return DashMediaSource(uri, dataSourceFactory, dashChunkSourceFactory, null, null)
    }
}
