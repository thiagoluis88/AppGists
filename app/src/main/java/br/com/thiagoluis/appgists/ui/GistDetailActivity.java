package br.com.thiagoluis.appgists.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.com.thiagoluis.appgists.BaseActivity;
import br.com.thiagoluis.appgists.R;
import br.com.thiagoluis.appgists.database.DBHelper;
import br.com.thiagoluis.appgists.model.Gist;
import br.com.thiagoluis.appgists.model.GistDetail;
import br.com.thiagoluis.appgists.model.GistFile;
import br.com.thiagoluis.appgists.net.CustomCallback;
import br.com.thiagoluis.appgists.net.RestServices;
import br.com.thiagoluis.appgists.ui.adapter.FileAdapter;
import retrofit.client.Response;

public class GistDetailActivity extends BaseActivity {

    public static final String GIST_KEY = "gist_key";

    private TextView filesText;
    private TextView commentsText;
    private TextView forksText;
    private ImageView gistImage;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView filesList;
    private Gist gist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gist_detail);

        filesText = (TextView) findViewById(R.id.filesText);
        commentsText = (TextView) findViewById(R.id.commentsText);
        forksText = (TextView) findViewById(R.id.forksText);
        gistImage = (ImageView) findViewById(R.id.gistImage);
        filesList = (RecyclerView) findViewById(R.id.filesList);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);

        collapsingToolbarLayout.setTitleEnabled(true);
        filesList.setLayoutManager(new LinearLayoutManager(this));

        gist = getIntent().getParcelableExtra(GIST_KEY);

        configToolbar();
        setUpButtonEnable();

        init(gist);
    }

    private void checkGistDetails(String gistId) {
        if (checkNetworkAvailable()) {
            loadGistDetail(gistId);
        } else {
            loadGistDetailFromDatabase(gistId);
        }
    }

    private void init(Gist gist) {
        if (gist.getOwner() != null) {
            Picasso.with(this)
                    .load(gist.getOwner().getAvatarUrl())
                    .into(gistImage);

            collapsingToolbarLayout.setTitle(gist.getOwner().getLogin());
        } else {
            gistImage.setImageResource(R.drawable.no_image);
            collapsingToolbarLayout.setTitle(getString(R.string.gist_anonymous));
        }

        checkGistDetails(gist.getGistId());
    }

    private void loadGistDetail(String gistId) {
        RestServices.getServices().getGistsDetail(gistId, new CustomCallback<GistDetail>(this) {
            @Override
            public void onError(ErrorResponse response) {
                Toast.makeText(GistDetailActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(GistDetail gist, Response response) {
                setGistFiles(gist);
            }

            @Override
            public void switchVisibility() {
            }
        });
    }

    private void loadGistDetailFromDatabase(String gistId) {
        AsyncTaskCompat.executeParallel(new AsyncTask<String, Void, GistDetail>() {
            @Override
            protected GistDetail doInBackground(String... params) {
                DBHelper dbHelper = new DBHelper(GistDetailActivity.this);
                return dbHelper.getGistDetail(params[0]);
            }

            @Override
            protected void onPostExecute(GistDetail gistDetail) {
                setGistFiles(gistDetail);
            }
        }, gistId);
    }

    private void setGistFiles(GistDetail gistDetail) {
        int filesSize = gistDetail.getFiles().size();
        int commentsSize = gistDetail.getComments();
        filesText.setText(getResources().getQuantityString(R.plurals.files, filesSize, filesSize));
        commentsText.setText(getResources().getQuantityString(R.plurals.comments, commentsSize, commentsSize));
        forksText.setText(getResources().getQuantityString(R.plurals.forks,
                gistDetail.getForks().size(), gistDetail.getForks().size()));

        List<GistFile> files = new ArrayList<>(gistDetail.getFiles().values());
        FileAdapter adapter = new FileAdapter(files);
        filesList.setAdapter(adapter);

        insertGistFiles(gistDetail, files);
    }

    private void insertGistFiles(final GistDetail gistDetail, final List<GistFile> gistFiles) {
        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                DBHelper dbHelper = new DBHelper(GistDetailActivity.this);
                dbHelper.insertGistDetail(gist.getGistId(), gistDetail);
                dbHelper.insertGistFiles(gistDetail.getGistId(), gistFiles);
                return null;
            }
        });
    }
}
