package se.asapehrsson.podtest

import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.SwipeDismissBehavior
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import org.jetbrains.anko.doAsync
import se.asapehrsson.podtest.data.Episode

class MainActivity : AppCompatActivity(), EpisodeViewer, ChangeListener<SparseArray<Episode>> {

    @BindView(R.id.recycler_view_swipe_container) internal lateinit var recyclerViewSwipeContainer: SwipeRefreshLayout
    @BindView(R.id.recycler_view) internal lateinit var recyclerView: RecyclerView
    @BindView(R.id.player_container) internal lateinit var playerContainer: CardView
    @BindView(R.id.episode_details) internal lateinit var episodeDetails: View

    private var adapter: PagedEpisodesAdapter? = null
    private var miniPlayerViewHolder: EpisodeViewHolder? = null
    private var episodeMiniPlayerPresenter: EpisodeMiniPlayerPresenter? = null
    private var episodeDetailsPresenter: EpisodeDetailsPresenter? = null
    private var episodeDetailsBottomSheetDialog: BottomSheetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        setupEpisodeList()
        setupMiniPlayer()
        setupDetailsView()
    }

    private fun setupDetailsView() {
        val view = this.layoutInflater.inflate(R.layout.episode_details, null)
        episodeDetailsPresenter = EpisodeDetailsPresenter(EpisodeViewHolder(view, this))

        episodeDetailsBottomSheetDialog = BottomSheetDialog(this)
        episodeDetailsBottomSheetDialog!!.setContentView(view)
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
        episodeMiniPlayerPresenter = EpisodeMiniPlayerPresenter(this)

        LayoutInflater.from(ContextThemeWrapper(this, R.style.CardViewStyle)).inflate(R.layout.miniplayer, playerContainer, true)
        miniPlayerViewHolder = EpisodeViewHolder(playerContainer, this)
        miniPlayerViewHolder?.setPresenter(episodeMiniPlayerPresenter as EpisodeMiniPlayerPresenter)

        showMiniPlayer(false)

        //Swipe to dismiss mini player
        val swipeDismissBehavior = SwipeDismissBehavior<CardView>()
        swipeDismissBehavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_ANY)
        swipeDismissBehavior.setListener(object : SwipeDismissBehavior.OnDismissListener {
            override fun onDismiss(view: View) {
                episodeMiniPlayerPresenter!!.close()

                //Restore miniplayer visibility and position
                val layoutParams = view.layoutParams as CoordinatorLayout.LayoutParams
                layoutParams.setMargins(0, 0, 0, 0)
                view.alpha = 1.0f
                showMiniPlayer(false)
            }

            override fun onDragStateChanged(state: Int) {

            }
        })

        val coordinatorParams = playerContainer!!.layoutParams as CoordinatorLayout.LayoutParams
        coordinatorParams.behavior = swipeDismissBehavior
    }

    override fun showInfo(episode: Episode) {
        episodeDetailsPresenter?.update(episode)
        episodeDetailsBottomSheetDialog?.show()
    }

    override fun play(episode: Episode) {
        episodeMiniPlayerPresenter?.close()

        showMiniPlayer(true)

        episodeMiniPlayerPresenter?.update(episode, miniPlayerViewHolder)
        doAsync {
            episodeMiniPlayerPresenter?.startPlayer()
        }
    }

    private fun showMiniPlayer(show: Boolean) {
        val layoutParams = recyclerViewSwipeContainer.layoutParams as ViewGroup.MarginLayoutParams

        if (show) {
            playerContainer.visibility = View.VISIBLE
            layoutParams.bottomMargin = playerContainer!!.height
        } else {
            playerContainer.visibility = View.INVISIBLE
            layoutParams.bottomMargin = 0
        }
        recyclerView.layoutParams = layoutParams
    }

    override fun onPause() {
        super.onPause()
        episodeMiniPlayerPresenter?.close()
    }
}
