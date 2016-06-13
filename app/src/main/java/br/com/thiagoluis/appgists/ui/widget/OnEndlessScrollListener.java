package br.com.thiagoluis.appgists.ui.widget;

import android.support.v7.widget.RecyclerView;

public abstract class OnEndlessScrollListener extends RecyclerView.OnScrollListener {

    @Override
    public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (!recyclerView.canScrollVertically(1)) {
            onScrolledToBottom();
        }
    }

    public abstract void onScrolledToBottom();
}