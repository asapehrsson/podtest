package podtest.jiki.se.podtest.start;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import podtest.jiki.se.podtest.ChangeListener;
import podtest.jiki.se.podtest.LazyLoadedEpisodeList;
import podtest.jiki.se.podtest.R;
import podtest.jiki.se.podtest.model.Episode;

/**
 * Created by asapehrsson on 2017-06-07.
 */

public class PageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int SPINNER_VIEW_TYPE = 1;
    private static final int EPISODE_VIEW_TYPE = 2;
    private LazyLoadedEpisodeList lazyLoadedEpisodeList;
    private RowItemContract.EpisodePresenter presenter;
    private Context context;

    public PageAdapter(LazyLoadedEpisodeList lazyLoadedEpisodeList, Context context) {
        this.lazyLoadedEpisodeList = lazyLoadedEpisodeList;
        presenter = new EpisodeViewPresenter();
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder result;
        if (viewType == EPISODE_VIEW_TYPE) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.episode_listitem, viewGroup, false);
            result = new EpisodeViewHolder(view, context);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.loading_listitem, viewGroup, false);
            result = new RecyclerView.ViewHolder(view) {
            };
        }
        return result;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EpisodeViewHolder) {
            // set presenter
            EpisodeViewHolder viewHolder = (EpisodeViewHolder) holder;
            viewHolder.setPresenter(presenter);
            presenter.update(lazyLoadedEpisodeList.getEpisode(position), viewHolder);
        } else {
            lazyLoadedEpisodeList.loadEpisodes();
        }
    }

    @Override
    public int getItemCount() {
        int noOfItems = lazyLoadedEpisodeList.getNoOfLoadedItems();

        if (noOfItems == 0) {
            //just the loading spinner row
            noOfItems = 1;

        } else if (lazyLoadedEpisodeList.getNoOfLoadedItems() < lazyLoadedEpisodeList.getTotalNoOfItems()) {
            noOfItems++;
        }

        return noOfItems;
    }

    @Override
    public int getItemViewType(int positionInList) {
        int result;
        if (positionInList == getItemCount() - 1 && !lazyLoadedEpisodeList.allItemsLoaded()) {
            result = SPINNER_VIEW_TYPE;
        } else {
            result = EPISODE_VIEW_TYPE;
        }
        return result;
    }
}
