package tbc.uncagedmist.newgames.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import tbc.uncagedmist.newgames.Common.Common;
import tbc.uncagedmist.newgames.Model.Wallpapers;
import tbc.uncagedmist.newgames.R;
import tbc.uncagedmist.newgames.ViewHolder.CategoryViewHolder;
import tbc.uncagedmist.newgames.ViewWallpaperActivity;

public class CategoryFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference categoryBackground;

    FirebaseRecyclerOptions<Wallpapers> options;
    FirebaseRecyclerAdapter<Wallpapers, CategoryViewHolder> adapter;

    private static CategoryFragment INSTANCE = null;

    RecyclerView recyclerView;

    private InterstitialAd mInterstitialAd;

    public CategoryFragment() {
        database = FirebaseDatabase.getInstance();
        categoryBackground = database.getReference(Common.FB_DB_NAME);

        options = new FirebaseRecyclerOptions.Builder<Wallpapers>()
                .setQuery(categoryBackground,Wallpapers.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Wallpapers, CategoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull Wallpapers model) {
                Picasso.get()
                        .load(model.getImageLink())
                        .into(holder.wallpaperImage);

                holder.setItemClickListener((view, position1) -> {
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(getActivity());
                    }
                    else {
                        Common.CATEGORY_ID_SELECTED = adapter.getRef(position1).getKey();
                        startActivity(new Intent(getActivity(), ViewWallpaperActivity.class));
                    }
                });
            }

            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_wallpaper_item,parent,false);

                AdRequest adRequest = new AdRequest.Builder().build();

                InterstitialAd.load(
                        getContext(),
                        getContext().getString(R.string.fullscreen),
                        adRequest, new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                mInterstitialAd = interstitialAd;

                                mInterstitialAd.setFullScreenContentCallback(
                                        new FullScreenContentCallback(){
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
        };
    }

    public static CategoryFragment getInstance()    {

        if (INSTANCE == null)   {
            INSTANCE = new CategoryFragment();
        }
        return INSTANCE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        recyclerView = view.findViewById(R.id.recycler_category);

        recyclerView.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(gridLayoutManager);

        if (Common.isConnectedToInternet(getContext()))
            setCategory();
        else
            Toast.makeText(getContext(), "Please Connect to Internet...", Toast.LENGTH_SHORT).show();


        return view;
    }

    private void setCategory() {
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (adapter != null)    {
            adapter.startListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (adapter != null)    {
            adapter.startListening();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null)
            adapter.stopListening();
    }
}