package se.asapehrsson.podtest.miniplayer

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import se.asapehrsson.podtest.R

class PlayerView : CardView, PlayerContract.View {
    @BindView(R.id.first_line) lateinit var firstRow: TextView
    @BindView(R.id.second_line) lateinit var secondRow: TextView
    @BindView(R.id.play_pause) lateinit var playPause: ImageView
    @BindView(R.id.next) lateinit var next: ImageView
    @BindView(R.id.previous) lateinit var previous: ImageView
    @BindView(R.id.episode_image) lateinit var episodeImage: ImageView
    @BindView(R.id.buffering) lateinit var buffering: ProgressBar
    @BindView(R.id.close) lateinit var close: ImageView
    @BindView(R.id.progress) lateinit var seekBar: SeekBar

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
        playPause.setOnClickListener {
            presenter?.event(PlayerContract.Source.PLAY_PAUSE)
        }
        previous.setOnClickListener {
            presenter?.event(PlayerContract.Source.PREV)
        }
        next.setOnClickListener {
            presenter?.event(PlayerContract.Source.NEXT)
        }
        close.setOnClickListener {
            presenter?.event(PlayerContract.Source.CLOSE)
        }

        var progressListener = object : OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                presenter?.event(PlayerContract.Source.SEEK_START)
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                presenter?.event(PlayerContract.Source.SEEK_DONE, p0?.progress!!)
            }
        }
        seekBar.setOnSeekBarChangeListener(progressListener)
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

    override fun setProgress(progress: Int, max: Int) {
        seekBar?.let {
            it.progress = progress
            it.max = max
        }
    }

    override fun setIconState(state: PlayerContract.State) {
        var isBuffering = false;
        when (state) {
            PlayerContract.State.PLAYING -> playPause.setImageResource(R.drawable.ic_pause)
            PlayerContract.State.PAUSED -> playPause.setImageResource(R.drawable.ic_play)
            else -> {
                isBuffering = true
            }
        }
        if (isBuffering) {
            playPause.visibility = View.GONE
            buffering.visibility = View.VISIBLE
            next.setImageResource(R.drawable.ic_next_inactive)
            previous.setImageResource(R.drawable.ic_previous_inactive)
        } else {
            buffering.visibility = View.GONE
            playPause.visibility = View.VISIBLE
            next.setImageResource(R.drawable.ic_next)
            previous.setImageResource(R.drawable.ic_previous)
        }
    }

}