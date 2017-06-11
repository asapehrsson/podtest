package podtest.jiki.se.podtest.start;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import podtest.jiki.se.podtest.ChangeListener;
import podtest.jiki.se.podtest.LazyLoadedEpisodeList;
import podtest.jiki.se.podtest.R;
import podtest.jiki.se.podtest.model.Episode;

public class MainActivity extends AppCompatActivity implements EpisodeViewer {
    @BindView(R.id.recyclerview) RecyclerView recyclerView;
    @BindView(R.id.player_container) CardView playerContainer;

    private LazyLoadedEpisodeList lazyLoadedEpisodeList;
    private PagedEpisodesAdapter adapter;
    private EpisodeViewHolder miniPlayer;
    private EpisodeMiniPlayerPresenter episodeMiniPlayerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        lazyLoadedEpisodeList = new LazyLoadedEpisodeList();

        lazyLoadedEpisodeList.setChangeListener(new ChangeListener<List<Episode>>() {
            @Override
            public void onChange(List<Episode> event) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });

        adapter = new PagedEpisodesAdapter(lazyLoadedEpisodeList, this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        LayoutInflater.from(new ContextThemeWrapper(this, R.style.CardViewStyle)).inflate(R.layout.episode_listitem, playerContainer, true);
        miniPlayer = new EpisodeViewHolder(playerContainer, this);
        playerContainer.setVisibility(View.GONE);

        SwipeDismissBehavior<CardView> swipeDismissBehavior = new SwipeDismissBehavior();
        swipeDismissBehavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_ANY);
        swipeDismissBehavior.setListener(new SwipeDismissBehavior.OnDismissListener() {
            @Override
            public void onDismiss(View view) {
                episodeMiniPlayerPresenter.close();

                //Restore miniplayer
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
                layoutParams.setMargins(0, 0, 0, 0);
                view.setAlpha(1.0f);
                setMiniPlayerVisibility(View.GONE);
            }

            @Override
            public void onDragStateChanged(int state) {

            }
        });

        CoordinatorLayout.LayoutParams coordinatorParams = (CoordinatorLayout.LayoutParams) playerContainer.getLayoutParams();
        coordinatorParams.setBehavior(swipeDismissBehavior);
    }


    @Override
    public void showInfo(Episode episode) {
        setMiniPlayerVisibility(playerContainer.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    @Override
    public void play(Episode episode) {
        if (episodeMiniPlayerPresenter != null) {
            episodeMiniPlayerPresenter.close();
        }

        setMiniPlayerVisibility(View.VISIBLE);
        episodeMiniPlayerPresenter = new EpisodeMiniPlayerPresenter(this);
        miniPlayer.setPresenter(episodeMiniPlayerPresenter);
        episodeMiniPlayerPresenter.update(episode, miniPlayer);
    }

    private void setMiniPlayerVisibility(int visibility) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) recyclerView.getLayoutParams();

        if (visibility == View.VISIBLE) {
            playerContainer.setVisibility(View.VISIBLE);
            layoutParams.bottomMargin = playerContainer.getHeight();
        } else {
            playerContainer.setVisibility(View.INVISIBLE);
            layoutParams.bottomMargin = 0;
        }
        recyclerView.setLayoutParams(layoutParams);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (episodeMiniPlayerPresenter != null) {
            episodeMiniPlayerPresenter.close();
        }
    }
}
