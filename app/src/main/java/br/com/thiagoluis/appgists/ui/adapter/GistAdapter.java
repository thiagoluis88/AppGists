package br.com.thiagoluis.appgists.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import br.com.thiagoluis.appgists.R;
import br.com.thiagoluis.appgists.model.Gist;
import br.com.thiagoluis.appgists.model.Owner;

public class GistAdapter extends RecyclerView.Adapter<GistAdapter.ViewHolder> {

    private List<Gist> gists;
    private Context context;
    private OnGistSelectedListener listener;
    private SimpleDateFormat sdf;
    private int gistImageSize;

    public interface OnGistSelectedListener{
        void onGistSelected(int position);
    }

    public GistAdapter(Context context, List<Gist> gists) {
        this.gists = gists;
        this.context = context;
        sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        gistImageSize = context.getResources().getDimensionPixelSize(R.dimen.gist_image_size);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gist, parent, false);

        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Gist gist = gists.get(position);
        Owner owner = gist.getOwner();
        if (owner != null) {
            holder.gistOwnerNameText.setText(owner.getLogin());
            downloadGistImage(holder.gistImage, gist.getOwner().getAvatarUrl());
        } else {
            holder.gistOwnerNameText.setText(R.string.gist_anonymous);
            holder.gistImage.setImageResource(R.drawable.no_image);
        }

        holder.createdAtText.setText(sdf.format(gist.getCreatedAt()));
        holder.gistTypeText.setText(gist.getGistType());
        holder.gistLanguageText.setText(gist.getLanguage());
    }

    @Override
    public int getItemCount() {
        return gists.size();
    }

    @Override
    public long getItemId(int position) {
        Gist gist = gists.get(position);
        return Long.valueOf(Math.abs(gist.getGistId().hashCode()));
    }

    public void setOnGistSelectedListener(OnGistSelectedListener listener) {
        this.listener = listener;
    }

    public void addNewGists(List<Gist> newGists) {
        gists.addAll(newGists);
        notifyDataSetChanged();
    }

    public Gist getGist(int position) {
        return gists.get(position);
    }

    public void clearAllGists() {
        gists.clear();
    }

    private void downloadGistImage(ImageView imageView, String urlImage) {
        Picasso.with(context)
                .load(urlImage)
                .resize(gistImageSize, gistImageSize)
                .into(imageView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView gistImage;
        TextView gistOwnerNameText;
        TextView createdAtText;
        TextView gistTypeText;
        TextView gistLanguageText;
        View cardView;

        public ViewHolder(View itemView, final OnGistSelectedListener listener) {
            super(itemView);

            gistImage = (ImageView) itemView.findViewById(R.id.gistImage);
            gistOwnerNameText = (TextView) itemView.findViewById(R.id.gistOwnerNameText);
            createdAtText = (TextView) itemView.findViewById(R.id.createdAtText);
            gistTypeText = (TextView) itemView.findViewById(R.id.gistTypeText);
            gistLanguageText = (TextView) itemView.findViewById(R.id.gistLanguageText);
            cardView = itemView.findViewById(R.id.cardView);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onGistSelected(getAdapterPosition());
                    }
                }
            });

        }
    }
}
