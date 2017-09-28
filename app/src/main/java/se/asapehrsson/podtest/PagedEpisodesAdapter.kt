package se.asapehrsson.podtest

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup


class PagedEpisodesAdapter(private val lazyLoadedEpisodeList: LazyLoadedEpisodeList, private val context: Context, episodeViewer: EpisodeViewer) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var presenter: EpisodeContract.Presenter = EpisodeListViewPresenter(episodeViewer)
    var firstTime: Boolean = true

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val result: RecyclerView.ViewHolder
        if (viewType == EPISODE_VIEW_TYPE) {
            val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.episode_listitem, viewGroup, false)
            result = EpisodeViewHolder(view, context)
        } else {
            val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.loading_listitem, viewGroup, false)
            result = object : RecyclerView.ViewHolder(view) {
            }
        }
        return result
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is EpisodeViewHolder) {
            // set presenter
            val viewHolder = holder
            viewHolder.setPresenter(presenter)
            presenter.update(lazyLoadedEpisodeList.getEpisode(position), viewHolder)
        } else {
            lazyLoadedEpisodeList.loadEpisodes()
        }
    }

    override fun getItemCount(): Int {
        var noOfItems = lazyLoadedEpisodeList.noOfLoadedItems

        if (noOfItems == 0) {
            //just the loading spinner row
            noOfItems = 1

        } else if (lazyLoadedEpisodeList.noOfLoadedItems < lazyLoadedEpisodeList.totalNoOfItems) {
            noOfItems++
        }

        return noOfItems
    }

    override fun getItemViewType(positionInList: Int): Int {
        val result: Int
        if (positionInList == itemCount - 1 && !lazyLoadedEpisodeList.allItemsLoaded()) {
            result = SPINNER_VIEW_TYPE
        } else {
            result = EPISODE_VIEW_TYPE
        }
        return result
    }

    companion object {
        private val SPINNER_VIEW_TYPE = 1
        private val EPISODE_VIEW_TYPE = 2
    }
}
