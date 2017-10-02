package se.asapehrsson.podtest.player

import android.content.Context
import android.net.Uri
import android.support.v4.media.session.PlaybackStateCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import io.reactivex.subjects.BehaviorSubject


class ExoMediaPlayer(private val context: Context) : IMediaPlayer {
    private var player: SimpleExoPlayer? = null
    private var playbackPosition: Long = 0
    private var currentWindow: Int = 0
    private var playWhenReady = true

    private var eventLogger: EventLogger? = null
    private var behaviorSubject = BehaviorSubject.create<@PlaybackStateCompat.State Int>()

    private var playerEventListener = object : Player.EventListener {
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {}

        override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {}

        override fun onPlayerError(error: ExoPlaybackException?) {}

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> {
                    behaviorSubject.onNext(PlaybackStateCompat.STATE_BUFFERING)
                }
                Player.STATE_READY -> {
                    behaviorSubject.onNext(if (playWhenReady) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED)
                }
                Player.STATE_ENDED -> {
                    behaviorSubject.onNext(PlaybackStateCompat.STATE_STOPPED)
                }
            }
        }

        override fun onLoadingChanged(isLoading: Boolean) {}

        override fun onPositionDiscontinuity() {}

        override fun onRepeatModeChanged(repeatMode: Int) {}

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {}

    }

    override fun setVolume(fl: Float) {
        TODO("not implemented")
    }

    override fun currentPositionInMillis(): Long {
        return player?.contentPosition ?: 0L
    }

    override fun getMediaState(): BehaviorSubject<Int>? {
        return behaviorSubject;
    }

    override fun loadAndPlay(url: String) {
        initializePlayer()
        behaviorSubject.onNext(PlaybackStateCompat.STATE_NONE)
        var mediaSource: MediaSource
        if (url.endsWith("mp3")) {
            mediaSource = buildMediaSourceForMP3(Uri.parse(url))
        } else {
            mediaSource = buildMediaSourceForMPD(Uri.parse(url));
        }

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

    override fun isPlaying(): Boolean = player?.playWhenReady ?: false

    override fun seekTo(positionInMillis: Long) {
        player?.seekTo(positionInMillis)
    }


    private fun initializePlayer() {
        if (player == null) {
            //var trackSelector :
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
                it.addListener(playerEventListener)
            }
        }
    }

    private fun buildMediaSourceForMPD(uri: Uri): MediaSource {
        val dataSourceFactory = DefaultHttpDataSourceFactory("ua", DefaultBandwidthMeter())
        val dashChunkSourceFactory = DefaultDashChunkSource.Factory(dataSourceFactory)
        return DashMediaSource(uri, dataSourceFactory, dashChunkSourceFactory, null, null)
    }

    private fun buildMediaSourceForMP3(uri: Uri): MediaSource {
        val dataSourceFactory = DefaultHttpDataSourceFactory("ua", DefaultBandwidthMeter())
        return ExtractorMediaSource(uri, dataSourceFactory, DefaultExtractorsFactory(), null, null)
    }
}
