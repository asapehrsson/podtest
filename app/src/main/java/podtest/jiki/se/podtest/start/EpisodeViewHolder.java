package podtest.jiki.se.podtest.start;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import podtest.jiki.se.podtest.R;

public class EpisodeViewHolder extends RecyclerView.ViewHolder implements RowItemContract.EpisodeView {
    public View container;

    @BindView(R.id.first_line) TextView firstRow;
    @BindView(R.id.second_line) TextView secondRow;
    @BindView(R.id.left_image) ImageView leftImage;
    @BindView(R.id.right_image) ImageView rightImage;

    private RowItemContract.EpisodePresenter presenter;
    private Context context;

    public EpisodeViewHolder(View view, Context context) {
        super(view);

        this.context = context;
        this.container = view;

        ButterKnife.bind(this, container);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presenter != null) {
                    presenter.itemClicked(container.getTag(), RowItemContract.Source.CONTAINER);
                }
            }
        });
        rightImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presenter != null) {
                    presenter.itemClicked(container.getTag(), RowItemContract.Source.RIGHT_ICON);
                }
            }
        });
    }

    @Override
    public void setFirstRow(String text) {
        firstRow.setText(text);
    }

    @Override
    public void setSecondRow(String text) {
        secondRow.setText(text);
    }

    @Override
    public void setThumbnail(String url) {
        Glide.with(context)
                .load(url)
                .into(leftImage);
    }

    @Override
    public void setRightIcon(int drawableResourceId) {
        rightImage.setImageResource(drawableResourceId);
    }

    @Override
    public void setTag(Object tag) {
        container.setTag(tag);
    }

    @Override
    public void setPresenter(RowItemContract.EpisodePresenter presenter) {
        this.presenter = presenter;
    }
}
