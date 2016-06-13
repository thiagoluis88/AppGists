package br.com.thiagoluis.appgists.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit.client.Request;
import retrofit.client.UrlConnectionClient;

public class AppGistsRetrofitClient extends UrlConnectionClient {

    protected HttpURLConnection openConnection(Request request) throws IOException {
        HttpURLConnection connection =
                (HttpURLConnection) new URL(request.getUrl()).openConnection();
        connection.setConnectTimeout(NetworkUtils.CONNECT_TIMEOUT);
        connection.setReadTimeout(NetworkUtils.READ_TIMEOUT);
        connection.setRequestProperty("Connection", "close");
        return connection;
    }

}