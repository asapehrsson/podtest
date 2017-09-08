package se.asapehrsson.podtest

import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import se.asapehrsson.podtest.data.Episode

class MainActivity : AppCompatActivity(), EpisodeViewer, ChangeListener<List<Episode>> {

    @BindView(R.id.recyclerview) internal lateinit var recyclerView: RecyclerView
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

    override fun onChange(event: List<Episode>) {
        runOnUiThread { adapter?.notifyDataSetChanged() }
    }
    private fun setupEpisodeList() {
        val lazyLoadedEpisodeList = LazyLoadedEpisodeList()

        adapter = PagedEpisodesAdapter(lazyLoadedEpisodeList, this, this)
        lazyLoadedEpisodeList.setChangeListener(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupMiniPlayer() {
        episodeMiniPlayerPresenter = EpisodeMiniPlayerPresenter(this)

        LayoutInflater.from(ContextThemeWrapper(this, R.style.CardViewStyle)).inflate(R.layout.episode_listitem, playerContainer, true)
        miniPlayerViewHolder = EpisodeViewHolder(playerContainer, this)
        miniPlayerViewHolder?.setPresenter(episodeMiniPlayerPresenter as EpisodeMiniPlayerPresenter)

        showMiniPlayer(false)

        //Swipe to dismiss mini player
//        val swipeDismissBehavior = SwipeDismissBehavior()
//        swipeDismissBehavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_ANY)
//        swipeDismissBehavior.setListener(object : SwipeDismissBehavior.OnDismissListener {
//            override fun onDismiss(view: View) {
//                episodeMiniPlayerPresenter!!.close()
//
//                //Restore miniplayer visibility and position
//                val layoutParams = view.layoutParams as CoordinatorLayout.LayoutParams
//                layoutParams.setMargins(0, 0, 0, 0)
//                view.alpha = 1.0f
//                showMiniPlayer(false)
//            }
//
//            override fun onDragStateChanged(state: Int) {
//
//            }
//        })
//
//        val coordinatorParams = playerContainer!!.layoutParams as CoordinatorLayout.LayoutParams
//        coordinatorParams.behavior = swipeDismissBehavior
    }

    override fun showInfo(episode: Episode) {
        episodeDetailsPresenter?.update(episode)
        episodeDetailsBottomSheetDialog?.show()
    }

    override fun play(episode: Episode) {
        episodeMiniPlayerPresenter?.close()

        showMiniPlayer(true)

        episodeMiniPlayerPresenter?.update(episode, miniPlayerViewHolder)
        episodeMiniPlayerPresenter?.startPlayer()
    }

    private fun showMiniPlayer(show: Boolean) {
        val layoutParams = recyclerView.layoutParams as ViewGroup.MarginLayoutParams

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
