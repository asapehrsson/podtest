package se.asapehrsson.podtest

import android.util.SparseArray
import android.view.View
import okhttp3.Request
import se.asapehrsson.podtest.data.Episode
import se.asapehrsson.podtest.data.Episodes
import se.asapehrsson.podtest.data.Pagination

class LazyLoadedEpisodeList : Result {

    private var pagination: Pagination? = null
    private var episodes = SparseArray<Episode>()
    private var changeListener: ChangeListener<SparseArray<Episode>>? = null
    private var isLoading: Boolean = false

    init {
        loadEpisodes()
    }

    fun getEpisode(index: Int): Episode? {
        var result: Episode? = null
        if (index < episodes.size()) {
            result = episodes.valueAt(index)
        }
        return result
    }

    //Trådpool
    fun loadEpisodes() {
        if (!isLoading) {
            isLoading = true
            if (pagination == null) {
                val request = Request.Builder()
                        .url(START_URL)
                        .build()
                Thread(FetchEpisodesTask(this, request)).start()

            } else if (pagination?.nextpage != null) {
                val request = Request.Builder()
                        .url(pagination!!.nextpage)
                        .build()
                Thread(FetchEpisodesTask(this, request)).start()
            }
        }
    }

    val noOfLoadedItems: Int
        get() = episodes.size()

    val totalNoOfItems: Int
        get() = pagination?.totalhits ?: 0

    override fun onSuccess(value: Episodes) {
        pagination = value.pagination
        val offset = (pagination!!.page - 1) * 10

        for (i in 0 until pagination!!.size) {
            episodes.put(offset + i, value.episodes!![i])
        }

        //episodes = ArrayList(value.episodes) //instead of addAll()

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

    fun setChangeListener(changeListener: ChangeListener<SparseArray<Episode>>?) {
        this.changeListener = changeListener
    }

    //Class constants
    companion object {
        //Start and end dates as variables
        private val START_URL = "http://api.sr.se/api/v2/episodes/index?programid=3381&fromdate=2017-01-01&todate=2017-09-01&urltemplateid=3&audioquality=hi&format=json"
    }
}
