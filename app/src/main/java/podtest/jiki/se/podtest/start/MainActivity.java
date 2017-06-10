package podtest.jiki.se.podtest.start;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import podtest.jiki.se.podtest.ChangeListener;
import podtest.jiki.se.podtest.LazyLoadedEpisodeList;
import podtest.jiki.se.podtest.R;
import podtest.jiki.se.podtest.model.Episode;

public class MainActivity extends AppCompatActivity implements ViewLauncher{
    @BindView(R.id.recyclerview) RecyclerView recyclerView;
    LazyLoadedEpisodeList lazyLoadedEpisodeList;
    private PageAdapter adapter;

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

        adapter = new PageAdapter(lazyLoadedEpisodeList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void load(View view) {
        lazyLoadedEpisodeList.loadEpisodes();
    }

    @Override
    public void openDetailsView() {

    }
}
