package br.com.thiagoluis.appgists.net;

import android.content.Context;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class RestServices {
    private static Context context;
    private static Services services;

    public static void setContext(Context context) {
        RestServices.context = context.getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    static {
        RestAdapter adapter = new RestAdapter.Builder()
                .setConverter(new GsonWithExposeHttpMessageConverter())
                .setEndpoint(NetworkUtils.ROOT_URL)
                .setRequestInterceptor(new RetrofitInterceptor())
                .build();

        services = adapter.create(Services.class);
    }

    public static Services getServices(){
        return services;
    }

    private static class RetrofitInterceptor implements RequestInterceptor {

        @Override
        public void intercept(RequestFacade requestFacade) {
            requestFacade.addHeader("Cache-Control", "no-cache");
            requestFacade.addHeader("charset", "utf-8");
        }
    }
}