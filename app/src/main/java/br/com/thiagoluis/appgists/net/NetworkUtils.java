package br.com.thiagoluis.appgists.net;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkUtils {
    public static final String ROOT_URL = "https://api.github.com/gists";

    public static final String GISTS = "/public";
    public static final String GIST_DETAIL = "/{gistId}";

    public static final int CONNECT_TIMEOUT = 3000;
    public static final int READ_TIMEOUT = 10000;

    public static boolean isNetworkAvailable(final Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }
}
