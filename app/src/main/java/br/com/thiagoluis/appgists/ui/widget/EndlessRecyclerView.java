package br.com.thiagoluis.appgists.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import br.com.thiagoluis.appgists.R;

public class EndlessRecyclerView extends RecyclerView {
    private int currentPage = 0;
    private int totalItemsPerPage = 10;
    private int newItemsPerPage = 0;
    private boolean isLoading = false;
    private boolean hasMoreItemsToLoad = false;
    private int columnWidth = -1;
    private GridLayoutManager manager;

    private OnPagedRecyclerViewListener listener;

    public interface OnPagedRecyclerViewListener {
        void onPageChanged(int currentPage);
    }

    public EndlessRecyclerView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public EndlessRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public EndlessRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    public void setOnPagedRecyclerViewListener(OnPagedRecyclerViewListener listener) {
        this.listener = listener;
    }

    public void startLoading() {
        this.isLoading = true;
    }

    public void finishLoading(int newItemsPerPage) {
        this.isLoading = false;
        updateItemsPerPage(newItemsPerPage);
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    private void updateItemsPerPage(int newItemsPerPage) {
        this.newItemsPerPage = newItemsPerPage;

        if (totalItemsPerPage == this.newItemsPerPage) {
            hasMoreItemsToLoad = true;
        } else {
            hasMoreItemsToLoad = false;
        }
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EndlessRecyclerView, defStyle, 0);
            totalItemsPerPage = a.getInt(R.styleable.EndlessRecyclerView_totalItemsPerPage, 30);

            int[] attrsArray = {
                    android.R.attr.columnWidth
            };
            TypedArray array = context.obtainStyledAttributes(attrs, attrsArray);
            columnWidth = array.getDimensionPixelSize(0, -1);
            array.recycle();
        }

        manager = new GridLayoutManager(getContext(), 1);
        setLayoutManager(manager);

        addOnScrollListener(new OnEndlessScrollListener() {
            @Override
            public void onScrolledToBottom() {
                if (listener != null && !isLoading && hasMoreItemsToLoad) {
                    currentPage++;
                    listener.onPageChanged(currentPage);
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (columnWidth > 0) {
            int spanCount = Math.max(1, getMeasuredWidth() / columnWidth);
            manager.setSpanCount(spanCount);
        }
    }
}
