package se.asapehrsson.podtest

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide

class EpisodeViewHolder(val container: android.view.View, private val context: Context) : EpisodeContract.View, RecyclerView.ViewHolder(container) {

    //TODO setter och getter?

    //Optional fields
    @BindView(R.id.icon_image)
    @JvmField
    var iconImage: ImageView? = null

    @BindView(R.id.episode_image)
    @JvmField
    var episodeImage: ImageView? = null

    @BindView(R.id.buffering)
    @JvmField
    var buffering: ProgressBar? = null

    //Mandatory fields
    @BindView(R.id.first_line) lateinit var firstRow: TextView
    @BindView(R.id.second_line) lateinit var secondRow: TextView

    private var presenter: EpisodeContract.Presenter? = null

    init {
        ButterKnife.bind(this, container)
        container.setOnClickListener {
            container.tag?.let { handleClick(EpisodeContract.Request.PLAY_EPISODE) }
        }
        iconImage?.let {
            it.setOnClickListener {
                container.tag?.let { handleClick(EpisodeContract.Request.SHOW_DETAILS) }
            }
        }
    }

    private fun handleClick(request: EpisodeContract.Request) {
        presenter?.let {
            it.itemClicked(container.tag, request)
        }
    }

    override fun setFirstRow(text: String?) {
        firstRow.text = text
    }

    override fun setSecondRow(text: String?) {
        secondRow.text = text
    }

    override fun setThumbnail(url: String?) {
        if (episodeImage != null) {
            Glide.with(context)
                    .load(url)
                    .into(episodeImage)
        }
    }

    override fun setIcon(drawableResourceId: Int) {
        if (drawableResourceId == 0) {
            buffering?.visibility = View.VISIBLE;
            iconImage?.visibility = View.GONE;
        } else {
            buffering?.visibility = View.GONE;
            iconImage?.visibility = View.VISIBLE;
            iconImage?.setImageResource(drawableResourceId)
        }
    }

    override fun setTag(tag: Any) {
        container.tag = tag
    }

    override fun setPresenter(presenter: EpisodeContract.Presenter) {
        this.presenter = presenter
    }
}
