package se.asapehrsson.podtest.details

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import se.asapehrsson.podtest.EpisodeContract
import se.asapehrsson.podtest.R

class DetailsView : LinearLayout, EpisodeContract.View {


    @BindView(R.id.first_line) lateinit var firstRow: TextView
    @BindView(R.id.second_line) lateinit var secondRow: TextView

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
        inflater.inflate(R.layout.episode_details, this)
        init()
    }

    private fun init() {
        ButterKnife.bind(this)
    }
    override fun setFirstRow(text: String?) {
        firstRow.text = text
    }

    override fun setSecondRow(text: String?) {
        secondRow.text = text
    }

    override fun setThumbnail(url: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPresenter(presenter: EpisodeContract.Presenter) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}