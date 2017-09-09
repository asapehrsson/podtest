package se.asapehrsson.podtest

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide

class PlayerView : CardView, PlayerContract.View {
    @BindView(R.id.first_line) lateinit var firstRow: TextView
    @BindView(R.id.second_line) lateinit var secondRow: TextView
    @BindView(R.id.icon_image) lateinit var iconImage: ImageView
    @BindView(R.id.episode_image) lateinit var episodeImage: ImageView
    @BindView(R.id.buffering) lateinit var buffering: ProgressBar

    override var presenter: PlayerContract.Presenter? = null

    constructor(context: Context) : super(context) {
        inflateAndInit(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        inflateAndInit(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        inflateAndInit(context)
    }

    private fun inflateAndInit(context: Context) {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.miniplayer, this)
        init()
    }

    private fun init() {
        ButterKnife.bind(this)
        iconImage.setOnClickListener {
            presenter?.let {
                it.itemClicked()
            }
        }
    }

    override fun setFirstRow(text: String?) {
        firstRow.text = text
    }

    override fun setSecondRow(text: String?) {
        secondRow.text = text
    }

    override fun setThumbnail(url: String?) {
        Glide.with(context)
                .load(url)
                .into(episodeImage)
    }

    override fun setIconState(state: PlayerContract.State) {
        var isBuffering = false;
        when (state) {
            PlayerContract.State.PLAYING -> iconImage.setImageResource(R.drawable.ic_pause)
            PlayerContract.State.PAUSED -> iconImage.setImageResource(R.drawable.ic_play)
            else -> {
                isBuffering = true
            }
        }
        if (isBuffering) {
            iconImage.visibility = View.GONE
            buffering.visibility = View.VISIBLE
        } else {
            buffering.visibility = View.GONE
            iconImage.visibility = View.VISIBLE
        }
    }

}