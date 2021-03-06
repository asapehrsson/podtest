package se.asapehrsson.podtest

import android.content.ComponentName
import android.os.Bundle
import android.os.Handler
import android.os.RemoteException
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.SwipeDismissBehavior
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import org.jetbrains.anko.doAsync
import se.asapehrsson.podtest.backgroundservice.AudioService
import se.asapehrsson.podtest.data.Episode
import se.asapehrsson.podtest.details.DetailsView
import se.asapehrsson.podtest.mediaqueue.setQueue
import se.asapehrsson.podtest.miniplayer.MediaPlayerPresenter
import se.asapehrsson.podtest.miniplayer.PlayerContract
import se.asapehrsson.podtest.miniplayer.PlayerView
import se.asapehrsson.podtest.player.SimpleMediaPlayer
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric




class MainActivity : AppCompatActivity(), EpisodeViewer, ChangeListener<SparseArray<Episode>> {
    private val TAG = MainActivity::class.java.simpleName

    @BindView(R.id.recycler_view_swipe_container) internal lateinit var recyclerViewSwipeContainer: SwipeRefreshLayout
    @BindView(R.id.recycler_view) internal lateinit var recyclerView: RecyclerView
    @BindView(R.id.player) internal lateinit var playerView: PlayerView
    @BindView(R.id.episode_details) internal lateinit var episodeDetailsView: DetailsView

    private var adapter: PagedEpisodesAdapter? = null
    private var miniPlayerPresenter: PlayerContract.Presenter? = null
    private var episodeDetailsPresenter: EpisodeDetailsPresenter? = null
    private val lazyLoadedEpisodeList = LazyLoadedEpisodeList()
    // private var episodeDetailsBottomSheetDialog: BottomSheetDialog? = null

    private var mediaBrowser: MediaBrowserCompat? = null
    private var mediaController: MediaControllerCompat? = null

    private val bottomSheet: BottomSheetBehavior<DetailsView> by lazy {
        episodeDetailsPresenter = EpisodeDetailsPresenter(episodeDetailsView)
        var value = BottomSheetBehavior.from(episodeDetailsView)
        with(value) {
            setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    Log.e("MainActivity", "onSlide - slideOffset=" + slideOffset)
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    Log.e("MainActivity", "onStateChanged -  newState=" + newState)
                }
            })
        }
        value
    }

    private val mediaControllerCallback = object : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            if (state == null) {
                return
            }

            when (state.state) {
                PlaybackStateCompat.STATE_STOPPED -> {
                    miniPlayerPresenter?.close()
                    showMiniPlayer(false)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        Fabric.with(this, Crashlytics())
        setupMediaService()
        setupEpisodeList()
        setupMiniPlayer()
    }

    private fun setupMediaService() {
        val mediaBrowserConnectionCallback = object : MediaBrowserCompat.ConnectionCallback() {

            override fun onConnected() {
                super.onConnected()
                try {
                    mediaController = MediaControllerCompat(this@MainActivity, mediaBrowser?.sessionToken!!)
                    mediaController?.registerCallback(mediaControllerCallback)
                    MediaControllerCompat.setMediaController(this@MainActivity, mediaController)

                    if (miniPlayerPresenter is MediaPlayerPresenter) {
                        (miniPlayerPresenter as MediaPlayerPresenter).init(mediaController!!)
                    }

                } catch (e: RemoteException) {

                }
            }
        }

        mediaBrowser = MediaBrowserCompat(this, ComponentName(this, AudioService::class.java),
                mediaBrowserConnectionCallback,
                null //optional bundle
        )
        mediaBrowser?.connect()
    }

    override fun onChange(event: SparseArray<Episode>) {
        runOnUiThread { adapter?.notifyDataSetChanged() }
    }

    private fun setupEpisodeList() {
        adapter = PagedEpisodesAdapter(lazyLoadedEpisodeList, this, this)
        lazyLoadedEpisodeList.setChangeListener(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        recyclerViewSwipeContainer.setOnRefreshListener({
            Handler().postDelayed({
                recyclerViewSwipeContainer.setRefreshing(false)
            }, 1000)
        })

    }

    private fun setupMiniPlayer() {
        miniPlayerPresenter = MediaPlayerPresenter()
        playerView.presenter = miniPlayerPresenter

        showMiniPlayer(false)

        if (mediaController != null) {
            (miniPlayerPresenter as MediaPlayerPresenter).init(mediaController!!)
        }

        //enableSwipeToDismiss()
    }

    private fun enableSwipeToDismiss() {
        //Swipe to dismiss mini player
        val swipeDismissBehavior = SwipeDismissBehavior<CardView>()
        swipeDismissBehavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_ANY)
        swipeDismissBehavior.setListener(object : SwipeDismissBehavior.OnDismissListener {
            override fun onDismiss(view: View) {
                miniPlayerPresenter!!.close()

                //Restore miniplayer visibility and position
                val layoutParams = view.layoutParams as CoordinatorLayout.LayoutParams
                layoutParams.setMargins(0, 0, 0, 0)
                view.alpha = 1.0f
                showMiniPlayer(false)
            }

            override fun onDragStateChanged(state: Int) {

            }
        })

        val coordinatorParams = playerView.layoutParams as CoordinatorLayout.LayoutParams
        coordinatorParams.behavior = swipeDismissBehavior
    }

    override fun showInfo(episode: Episode) {
        when (bottomSheet.state) {
            BottomSheetBehavior.STATE_EXPANDED -> {
                bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
            }
            BottomSheetBehavior.STATE_COLLAPSED -> bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED

        }
        episodeDetailsPresenter?.update(episode)
    }

    override fun play(episode: Episode) {
        setQueue(mediaController, lazyLoadedEpisodeList)

        miniPlayerPresenter?.let {
            //it.close()
            showMiniPlayer(true)
            it.update(episode, playerView)
            doAsync {
                it.play(episode.id!!)
            }
        }
    }

    private fun showMiniPlayer(show: Boolean) {
        val layoutParams = recyclerViewSwipeContainer.layoutParams as ViewGroup.MarginLayoutParams

        if (show) {
            playerView.visibility = View.VISIBLE
            layoutParams.bottomMargin = playerView.height
        } else {
            playerView.visibility = View.INVISIBLE
            layoutParams.bottomMargin = 0
        }
        recyclerView.layoutParams = layoutParams
    }

    override fun onPause() {
        super.onPause()
        //miniPlayerPresenter?.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaBrowser?.disconnect()
    }
    public companion object {
        private val TAG = SimpleMediaPlayer::class.java.simpleName
        private val EXTRA_CURRENT_MEDIA_DESCRIPTION = "se.asapehrsson.podtest.current.media.description"
    }
}
