package tbc.uncagedmist.newgames.ViewHolder;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import tbc.uncagedmist.newgames.Helper.ItemClickListener;
import tbc.uncagedmist.newgames.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    public ProgressBar progressBar;
    public ImageView wallpaperImage;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CategoryViewHolder(View itemView) {
        super(itemView);

        progressBar = itemView.findViewById(R.id.progress_bar);
        wallpaperImage = itemView.findViewById(R.id.imageView);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition());
    }
}