package se.asapehrsson.podtest

import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.SwipeDismissBehavior
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
import se.asapehrsson.podtest.data.Episode
import se.asapehrsson.podtest.details.DetailsView
import se.asapehrsson.podtest.miniplayer.MediaPlayerPresenter
import se.asapehrsson.podtest.miniplayer.PlayerContract
import se.asapehrsson.podtest.miniplayer.PlayerView

class MainActivity : AppCompatActivity(), EpisodeViewer, ChangeListener<SparseArray<Episode>> {

    @BindView(R.id.recycler_view_swipe_container) internal lateinit var recyclerViewSwipeContainer: SwipeRefreshLayout
    @BindView(R.id.recycler_view) internal lateinit var recyclerView: RecyclerView
    @BindView(R.id.player) internal lateinit var playerView: PlayerView
    @BindView(R.id.episode_details) internal lateinit var episodeDetailsView: DetailsView

    private var adapter: PagedEpisodesAdapter? = null
    private var miniPlayerPresenter: PlayerContract.Presenter? = null
    private var episodeDetailsPresenter: EpisodeDetailsPresenter? = null
    // private var episodeDetailsBottomSheetDialog: BottomSheetDialog? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        setupEpisodeList()
        setupMiniPlayer()
    }

    override fun onChange(event: SparseArray<Episode>) {
        runOnUiThread { adapter?.notifyDataSetChanged() }
    }

    private fun setupEpisodeList() {
        val lazyLoadedEpisodeList = LazyLoadedEpisodeList()

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
        miniPlayerPresenter = MediaPlayerPresenter(this)
        miniPlayerPresenter?.init()
        playerView.presenter = miniPlayerPresenter

        showMiniPlayer(false)

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
        miniPlayerPresenter?.let {
            //it.close()
            showMiniPlayer(true)
            it.update(episode, playerView)
            doAsync {
                it.start()
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
}
