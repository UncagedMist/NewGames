package tbc.uncagedmist.newgames.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tbc.uncagedmist.newgames.Common.Common;
import tbc.uncagedmist.newgames.FavDB.Favourites;
import tbc.uncagedmist.newgames.Model.Wallpapers;
import tbc.uncagedmist.newgames.R;
import tbc.uncagedmist.newgames.ViewHolder.CategoryViewHolder;
import tbc.uncagedmist.newgames.ViewWallpaperActivity;

public class MyFavAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

    private Context context;
    private List<Favourites> favourites;

    private InterstitialAd mInterstitialAd;

    public MyFavAdapter(Context context, List<Favourites> favourites) {
        this.context = context;
        this.favourites = favourites;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_wallpaper_item,parent,false);

        int height = parent.getMeasuredHeight() / 2;
        itemView.setMinimumHeight(height);

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(
                context,
                context.getString(R.string.fullscreen),
                adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Log.d("TAG", "The ad was dismissed.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                mInterstitialAd = null;
                                Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });

        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryViewHolder holder, final int position) {

        if (mInterstitialAd != null) {
            mInterstitialAd.show((Activity) context);
        }
        else {
            holder.progressBar.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(favourites.get(position).getImageLink())
                    .into(holder.wallpaperImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            holder.setItemClickListener((view, position1) -> {
                Intent intent = new Intent(context, ViewWallpaperActivity.class);
                Wallpapers wallpapers = new Wallpapers();
                wallpapers.setImageLink(favourites.get(position1).getImageLink());
                Common.selected_background = wallpapers;
                Common.selected_background_key = favourites.get(position1).getKey();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return favourites.size();
    }
}