package br.com.thiagoluis.appgists.net;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class CustomCallback<T> implements Callback<T>{

    private Activity activity;

    protected CustomCallback(Context context) {
        if( context instanceof Activity) {
            activity = (Activity) context;
        }
    }

    @Override
    public void success(T t, Response response) {
        switchVisibility();
        callSuccess(t, response);
    }

    @Override
    public void failure(RetrofitError cause) {
        ErrorResponse response = null;
        Log.e("CustomCallback", cause.getMessage() != null ? cause.getMessage() : cause.toString());

        String msg;
        String shortMsg;

        switch (cause.getKind()) {
            case NETWORK:
                msg = "Ops! Aconteceu um problema com a conexão. Tente novamente mais tarde";
                shortMsg = "Ops! Aconteceu um problema com a conexão";

                response = new ErrorResponse(msg, shortMsg);
                break;
            case HTTP:
                msg = "Ops! Aconteceu algo inesperado. Estamos trabalhando para resolver o mais rápido possível";
                shortMsg = "Ops! Aconteceu algo inesperado";

                response = new ErrorResponse(msg, shortMsg);
                break;
            case UNEXPECTED:
            case CONVERSION:
                msg = "Ops! Aconteceu algo inesperado. Estamos trabalhando para resolver o mais rápido possível";
                shortMsg = "Ops! Aconteceu algo inesperado";

                response = new ErrorResponse(msg, shortMsg);
                break;
        }

        switchVisibility();
        callError(response);
    }

    public void callSuccess(T t, Response response){
        onSuccess(t, response);
    };

    public void callError(ErrorResponse response){
        onError(response);
    }

    public abstract void onError(ErrorResponse response);
    public abstract void onSuccess(T t, Response response);

    public void switchVisibility(){
    }

    public class ErrorResponse{
        private String message;
        private String shortMessage;

        public ErrorResponse(String message, String shortMessage) {
            this.message = message;
            this.shortMessage = shortMessage;
        }

        public String getMessage() {
            return message;
        }

        public String getShortMessage() {
            return shortMessage;
        }
    }
}