package br.com.thiagoluis.appgists.net;

import java.util.List;

import br.com.thiagoluis.appgists.model.Gist;
import br.com.thiagoluis.appgists.model.GistDetail;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface Services {
    @GET(NetworkUtils.GISTS)
    void getGists(@Query("page") int page, CustomCallback<List<Gist>> callback);

    @GET(NetworkUtils.GIST_DETAIL)
    void getGistsDetail(@Path("gistId") String gistId, CustomCallback<GistDetail> callback);
}
