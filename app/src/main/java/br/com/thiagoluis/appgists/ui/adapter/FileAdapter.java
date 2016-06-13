package br.com.thiagoluis.appgists.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import br.com.thiagoluis.appgists.R;
import br.com.thiagoluis.appgists.model.GistFile;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    private List<GistFile> files;

    public FileAdapter(List<GistFile> files) {
        this.files = files;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GistFile gistFile = files.get(position);
        holder.fileTitleText.setText(gistFile.getFileName());
        holder.fileContentText.setText(gistFile.getContent());
        holder.fileSizeText.setText(getStringSize(gistFile.getSize()));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    private String getStringSize(long size) {
        String msg;

        if (size > 1024) {
            size = size / 1024;
            msg = String.format(Locale.getDefault(), "%d mb", size);
        } else {
            msg = String.format(Locale.getDefault(), "%d kb", size);
        }

        return msg;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView fileContentText;
        TextView fileTitleText;
        TextView fileSizeText;

        public ViewHolder(View itemView) {
            super(itemView);

            fileContentText = (TextView) itemView.findViewById(R.id.fileContentText);
            fileTitleText = (TextView) itemView.findViewById(R.id.fileTitleText);
            fileSizeText = (TextView) itemView.findViewById(R.id.fileSizeText);
        }
    }
}
