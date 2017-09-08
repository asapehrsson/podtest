package se.asapehrsson.podtest

import android.view.View
import se.asapehrsson.podtest.model.Episode
import se.asapehrsson.podtest.model.Episodes
import se.asapehrsson.podtest.model.Pagination
import java.util.*

class LazyLoadedEpisodeList : Result {

    private var pagination: Pagination? = null
    private val episodes = ArrayList<Episode>()
    private var changeListener: ChangeListener<List<Episode>>? = null
    private var isLoading: Boolean = false

    init {
        loadEpisodes()
    }

    fun getEpisode(index: Int): Episode? {
        var result: Episode? = null
        if (index < episodes.size) {
            result = episodes[index]
        }
        return result
    }

    //Trådpool
    fun loadEpisodes() {
        if (!isLoading) {
            isLoading = true
            if (pagination == null) {
                Thread(
                        FetchEpisodesTask(this, START_URL)).start()
            } else if (pagination?.nextpage != null) {
                Thread(
                        FetchEpisodesTask(this, pagination!!.nextpage)).start()
            }
        }
    }

    val noOfLoadedItems: Int
        get() = episodes.size

    val totalNoOfItems: Int
        get() = pagination?.totalhits ?: 0

    override fun onSuccess(value: Episodes) {
        pagination = value.pagination
        episodes.addAll(value.episodes)
        isLoading = false
        changeListener?.onChange(episodes)
    }

    fun allItemsLoaded(): Boolean {
        return pagination?.nextpage == null
    }

    override fun onFailed() {
        // Handle error!!
        isLoading = false
    }

    fun setOnClickListener(listener: (View) -> Unit) {

    }

    fun setChangeListener(changeListener: ChangeListener<List<Episode>>?) {

        this.changeListener = changeListener
    }

    companion object {
        //Start and end dates as variables
        private val START_URL = "http://api.sr.se/api/v2/episodes/index?programid=2519&fromdate=2017-01-01&todate=2017-06-01&urltemplateid=3&audioquality=hi&format=json"
    }
}
