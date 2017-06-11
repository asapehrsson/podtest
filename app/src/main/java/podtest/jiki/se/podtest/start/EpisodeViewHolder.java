package podtest.jiki.se.podtest.start;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import podtest.jiki.se.podtest.R;

public class EpisodeViewHolder extends RecyclerView.ViewHolder implements EpisodeContract.View {
    @BindView(R.id.first_line) TextView firstRow;
    @BindView(R.id.second_line) TextView secondRow;
    @Nullable
    @BindView(R.id.episode_image) ImageView episodeImage;
    @Nullable
    @BindView(R.id.icon_image) ImageView iconImage;
    private android.view.View container;
    private EpisodeContract.Presenter presenter;
    private Context context;

    public EpisodeViewHolder(android.view.View view, Context context) {
        super(view);

        this.context = context;
        this.container = view;

        ButterKnife.bind(this, container);
        container.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (presenter != null) {
                    presenter.itemClicked(container.getTag(), EpisodeContract.Source.CONTAINER);
                }
            }
        });
        if (iconImage != null) {
            iconImage.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    if (presenter != null) {
                        presenter.itemClicked(container.getTag(), EpisodeContract.Source.ICON_IMAGE);
                    }
                }
            });
        }
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
        if (episodeImage != null) {
            Glide.with(context)
                    .load(url)
                    .into(episodeImage);
        }
    }

    @Override
    public void setIcon(int drawableResourceId) {
        if (iconImage != null) {
            iconImage.setImageResource(drawableResourceId);
        }
    }

    @Override
    public void setTag(Object tag) {
        container.setTag(tag);
    }

    @Override
    public void setPresenter(EpisodeContract.Presenter presenter) {
        this.presenter = presenter;
    }
}
