package br.com.thiagoluis.appgists.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import br.com.thiagoluis.appgists.BaseActivity;
import br.com.thiagoluis.appgists.R;
import br.com.thiagoluis.appgists.database.DBHelper;
import br.com.thiagoluis.appgists.model.Gist;
import br.com.thiagoluis.appgists.model.GistDetail;
import br.com.thiagoluis.appgists.model.GistFile;
import br.com.thiagoluis.appgists.net.CustomCallback;
import br.com.thiagoluis.appgists.net.RestServices;
import br.com.thiagoluis.appgists.ui.adapter.GistAdapter;
import br.com.thiagoluis.appgists.ui.widget.EndlessRecyclerView;
import retrofit.client.Response;

public class GistsActivity extends BaseActivity {

    private EndlessRecyclerView gistsList;
    private SwipeRefreshLayout swipeLayout;
    private ProgressBar progress;
    private View errorLayout;
    private Button reloadButton;
    private GistAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gists);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        gistsList = (EndlessRecyclerView) findViewById(R.id.gistsList);
        progress = (ProgressBar) findViewById(R.id.progress);
        reloadButton = (Button) findViewById(R.id.reloadButton);
        errorLayout = findViewById(R.id.errorLayout);
        gistsList.setHasFixedSize(true);

        swipeLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkNetworkOnSwipeRefresh();
            }
        });

        gistsList.setOnPagedRecyclerViewListener(new EndlessRecyclerView.OnPagedRecyclerViewListener() {
            @Override
            public void onPageChanged(int currentPage) {
                loadGists(currentPage);
            }
        });

        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadGists(0);
            }
        });

        configToolbar();
        loadGists(0);
    }

    private void loadGists(int page) {
        if (checkNetworkAvailable()) {
            loadGistsOnService(page);
        } else {
            loadGistsOnDatabase(page);
        }
    }

    private void checkNetworkOnSwipeRefresh() {
        if (checkNetworkAvailable()) {
            if (adapter != null) {
                adapter.clearAllGists();
            }
            gistsList.setCurrentPage(0);
            loadGists(0);
        } else {
            swipeLayout.setRefreshing(false);
            Snackbar.make(gistsList, "Sem conexão com a internet. Tente novamente mais tarde", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void loadGistsOnService(int page) {
        gistsList.startLoading();
        errorLayout.setVisibility(View.GONE);
        gistsList.setVisibility(View.GONE);

        if (adapter == null) {
            progress.setVisibility(View.VISIBLE);
        } else {
            swipeLayout.setRefreshing(true);
        }

        RestServices.getServices().getGists(page, new CustomCallback<List<Gist>>(this) {
            @Override
            public void onSuccess(List<Gist> gists, Response response) {
                if (gists != null && !gists.isEmpty()) {
                    gistsList.setVisibility(View.VISIBLE);
                    gistsList.finishLoading(gists.size());
                    addGistsInDatabase(gists);
                    setLanguageAndType(gists);
                    setGists(gists);
                } else {
                    gistsList.finishLoading(0);
                    errorLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(ErrorResponse response) {
                Toast.makeText(GistsActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void switchVisibility() {
                swipeLayout.setRefreshing(false);
                progress.setVisibility(View.GONE);
            }
        });
    }

    private void loadGistsOnDatabase(int page) {
        gistsList.startLoading();

        if (adapter == null) {
            progress.setVisibility(View.VISIBLE);
        } else {
            swipeLayout.setRefreshing(true);
        }

        AsyncTaskCompat.executeParallel(new AsyncTask<Integer, Void, List<Gist>>() {
            @Override
            protected List<Gist> doInBackground(Integer... params) {
                DBHelper dbHelper = new DBHelper(GistsActivity.this);
                return dbHelper.getAllGists(params[0]);
            }

            @Override
            protected void onPostExecute(List<Gist> gists) {
                progress.setVisibility(View.GONE);
                swipeLayout.setRefreshing(false);

                if (gists != null && !gists.isEmpty()) {
                    gistsList.finishLoading(gists.size());
                    setGists(gists);
                } else {
                    gistsList.finishLoading(0);
                }
            }
        }, page);
    }

    private void setGists(List<Gist> gists) {
        if (adapter == null) {
            adapter = new GistAdapter(this, gists);
            adapter.setHasStableIds(true);
            adapter.setOnGistSelectedListener(new GistAdapter.OnGistSelectedListener() {
                @Override
                public void onGistSelected(int position) {
                    Intent intent = new Intent(GistsActivity.this, GistDetailActivity.class);
                    intent.putExtra(GistDetailActivity.GIST_KEY, adapter.getGist(position));
                    startActivity(intent);
                }
            });
            gistsList.setAdapter(adapter);
        } else {
            adapter.addNewGists(gists);
        }
    }

    private void addGistsInDatabase(List<Gist> gists) {
        AsyncTaskCompat.executeParallel(new AsyncTask<List<Gist>, Void, Void>() {
            @Override
            protected Void doInBackground(List<Gist>... params) {
                DBHelper dbHelper = new DBHelper(GistsActivity.this);
                dbHelper.insertGists(params[0]);
                return null;
            }
        }, gists);
    }

    private void setLanguageAndType(List<Gist> gists) {
        int size = gists.size();
        for (int i = 0; i < size; i++) {
            Gist gist = gists.get(i);
            Map<String, GistFile> files = gist.getFiles();
            for (String key : files.keySet()) {
                GistFile gistFile = files.get(key);

                gist.setLanguage(gistFile.getLanguage());
                gist.setGistType(gistFile.getType());
                break;
            }
        }
    }
}
