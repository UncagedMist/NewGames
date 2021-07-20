package tbc.uncagedmist.newgames;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import tbc.uncagedmist.newgames.Common.Common;
import tbc.uncagedmist.newgames.FavDB.DB_Fav.FavouriteDatabase;
import tbc.uncagedmist.newgames.FavDB.DB_Fav.FavouritesDataSource;
import tbc.uncagedmist.newgames.FavDB.DataSource.FavouriteRepository;
import tbc.uncagedmist.newgames.FavDB.Favourites;
import tbc.uncagedmist.newgames.Helper.SaveImageHelper;
import tbc.uncagedmist.newgames.Model.Rating;
import tbc.uncagedmist.newgames.Model.Wallpapers;
import tbc.uncagedmist.newgames.RecentDB.DB_Recent.RecentDataSource;
import tbc.uncagedmist.newgames.RecentDB.DB_Recent.RecentDatabase;
import tbc.uncagedmist.newgames.RecentDB.DataSource.RecentRepository;
import tbc.uncagedmist.newgames.RecentDB.Recent;

public class ViewWallpaperActivity extends AppCompatActivity {

    AdView aboveBanner;

    public static final int  PERMISSION_REQUEST_CODE = 21;

    float rate;

    CollapsingToolbarLayout collapsingToolbarLayout;
    LinearLayout llWallpaper,llDownload,llShare,ll_rate;
    ImageView imageView;
    CoordinatorLayout rootLayout;
    TextView txtWallpaperName,txtWallpaperDescription;

    CompositeDisposable compositeDisposable;
    RecentRepository recentRepository;

    FavouriteRepository favouriteRepository;

    FloatingActionButton fabFav;

    TextView textView,txtDownloads;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference ratingTbl;

    RatingBar ratingBar;

    private InterstitialAd mInterstitialAd;

    ProgressBar progressBar;

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());

            try {
                wallpaperManager.setBitmap(bitmap);
                Snackbar.make(rootLayout,"Wallpaper was set", Snackbar.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case PERMISSION_REQUEST_CODE:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                    AlertDialog dialog = new SpotsDialog(ViewWallpaperActivity.this);
                    dialog.show();
                    dialog.setMessage("Please wait...");

                    String fileName = UUID.randomUUID().toString()+".png";

                    Picasso.get()
                            .load(Common.selected_background.getImageLink())
                            .into(new SaveImageHelper(getBaseContext(),
                                    dialog,
                                    getApplicationContext().getContentResolver(),
                                    fileName,
                                    "Live Wallpaper Image"));
                }
                else    {
                    Toast.makeText(this, "PERMISSION DENIED...", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        setContentView(R.layout.activity_view_wallpaper);

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(
                ViewWallpaperActivity.this,
                getString(R.string.fullscreen),
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

        compositeDisposable = new CompositeDisposable();
        //for recent
        RecentDatabase database = RecentDatabase.getInstance(this);
        recentRepository = RecentRepository.getInstance(RecentDataSource.getInstance(database.recentDAO()));

        //for favourites
        FavouriteDatabase favDatabase = FavouriteDatabase.getInstance(this);
        favouriteRepository = FavouriteRepository.getInstance(FavouritesDataSource.getInstance(favDatabase.favouritesDAO()));

        firebaseDatabase = FirebaseDatabase.getInstance();
        ratingTbl = firebaseDatabase.getReference("Rating");

        rootLayout = findViewById(R.id.root_layout);
        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);

        collapsingToolbarLayout.setTitle(getString(R.string.app_name));

        imageView = findViewById(R.id.imageThumb);
        txtWallpaperName = findViewById(R.id.wallpaper_name);
        txtWallpaperDescription = findViewById(R.id.txtDesc);
        fabFav = findViewById(R.id.fab_fav);

        ratingBar = findViewById(R.id.ratingBar);
        textView = findViewById(R.id.txtViews);
        txtDownloads = findViewById(R.id.txtDownloads);
        aboveBanner = findViewById(R.id.bottomBanner);

        progressBar = findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.VISIBLE);

        aboveBanner.loadAd(adRequest);

        txtWallpaperName.setText(getString(R.string.app_name));
        txtWallpaperDescription.setText(getString(R.string.app_name));

        Picasso.get()
                .load(Common.selected_background.getImageLink())
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(ViewWallpaperActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        if (Common.IS_FAV)  {
            fabFav.setImageResource(R.drawable.ic_baseline_favorite_24);
        }

        adMethod();

        textView.setText(String.valueOf(Common.selected_background.getViewCount()));
        txtDownloads.setText(String.valueOf(Common.selected_background.getDownloadCount()));

        getWallpaperRating(Common.selected_background.getImageId());

        fabFav.setOnClickListener(view -> {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(ViewWallpaperActivity.this);
            }
            else {
                addToFavourites();
            }
        });

        llShare = findViewById(R.id.ll_share);

        llShare.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String message = "Amazing Wallpaper in your way. Install Game Wallpaper App and Make your Display look Colourful! \n https://play.google.com/store/apps/details?id=tbc.uncagedmist.gamewallpaper";
            intent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(intent, "Share Game Wallpaper App Using"));
        });

        addToRecent();

        llWallpaper = findViewById(R.id.ll_setAs);

        llWallpaper.setOnClickListener(view -> {

            if (mInterstitialAd != null) {
                mInterstitialAd.show(ViewWallpaperActivity.this);
            }
            else {
                Picasso.get()
                        .load(Common.selected_background.getImageLink())
                        .into(target);
            }
        });


        llDownload = findViewById(R.id.ll_save);
        llDownload.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(ViewWallpaperActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)   {
                requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, PERMISSION_REQUEST_CODE);
            }
            else    {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(ViewWallpaperActivity.this);
                }
                else {
                    AlertDialog dialog = new SpotsDialog(ViewWallpaperActivity.this);
                    dialog.show();
                    dialog.setMessage("Please wait...");

                    String fileName = UUID.randomUUID().toString()+".png";

                    Picasso.get()
                            .load(Common.selected_background.getImageLink())
                            .into(new SaveImageHelper(getBaseContext(),
                                    dialog,
                                    getApplicationContext().getContentResolver(),
                                    fileName,
                                    "Live Wallpaper Image"));

//                    increaseDownloadCount();
                }
            }
        });

        ll_rate = findViewById(R.id.ll_rate);
        ll_rate.setOnClickListener(view -> {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(ViewWallpaperActivity.this);
            }
            else {
                showRatingDialog();
            }

        });

//        increaseViewCount();
    }

    private void adMethod() {
        aboveBanner.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
    }

    private void getWallpaperRating(String categoryId) {
        Query wallpaperRating = ratingTbl.orderByChild("wallpaperId").equalTo(categoryId);

        wallpaperRating.addValueEventListener(new ValueEventListener() {
            int count = 0,sum = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())    {

                    Rating item= postSnapshot.getValue(Rating.class);
                    sum += Integer.parseInt(item.getRateValue());
                    count++;
                }
                if (count != 0) {
                    float average = (sum / count);
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewWallpaperActivity.this);
        alertDialog.setTitle("Rate this Wallpaper");
        alertDialog.setMessage("Tell others what do you think of this wallpaper");
        alertDialog.setCancelable(false);

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_rate = inflater.inflate(R.layout.rating_layout,null);

        ScaleRatingBar scaleRatingBar = layout_rate.findViewById(R.id.simpleRatingBar);

        scaleRatingBar.setOnRatingChangeListener((ratingBar, rating, fromUser) ->
                rate = rating);

        alertDialog.setView(layout_rate);

        alertDialog.setPositiveButton("Rate ME", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                dialogInterface.dismiss();
                rateWallpaper((int) rate);

            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }


    private void addToFavourites() {
        Disposable disposable = Observable.create(e -> {
            Favourites favourites = new Favourites(
                    Common.selected_background.getImageLink(),
                    Common.selected_background.getImageId(),
                    String.valueOf(System.currentTimeMillis()),
                    Common.selected_background_key
            );
            favouriteRepository.insertFav(favourites);
            e.onComplete();
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(o -> {
                }, throwable -> Log.e("ERROR", throwable.getMessage()), () -> {

                });
        compositeDisposable.add(disposable);
        Common.IS_FAV = true;
    }

//    private void increaseViewCount() {
//        FirebaseDatabase.getInstance().getReference(Common.FB_DB_NAME)
//                .child(Common.select_background_key)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.hasChild("viewCount")) {
//
//                            WallpaperItem wallpaperItem = dataSnapshot.getValue(WallpaperItem.class);
//                            long count = wallpaperItem.getViewCount() + 1;
//
//                            Map<String,Object> update_view = new HashMap<>();
//                            update_view.put("viewCount",count);
//
//                            FirebaseDatabase.getInstance().getReference(Common.STR_WALLPAPER)
//                                    .child(Common.select_background_key)
//                                    .updateChildren(update_view)
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(ViewWallpaperActivity.this, "Can not update view count", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//                        else    {
//                            Map<String,Object> update_view = new HashMap<>();
//                            update_view.put("viewCount",Long.valueOf(1));
//
//                            FirebaseDatabase.getInstance().getReference(Common.STR_WALLPAPER)
//                                    .child(Common.select_background_key)
//                                    .updateChildren(update_view)
//                                    .addOnSuccessListener(aVoid -> {
//
//                                    }).addOnFailureListener(e -> Toast.makeText(ViewWallpaperActivity.this, "Can not set default view count", Toast.LENGTH_SHORT).show());
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//    }
//
//    private void increaseDownloadCount() {
//        FirebaseDatabase.getInstance().getReference(Common.STR_WALLPAPER)
//                .child(Common.select_background_key)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.hasChild("downloadCount")) {
//
//                            WallpaperItem wallpaperItem = dataSnapshot.getValue(WallpaperItem.class);
//                            long count = wallpaperItem.getDownloadCount() + 1;
//
//                            Map<String,Object> update_view = new HashMap<>();
//                            update_view.put("downloadCount",count);
//
//                            FirebaseDatabase.getInstance().getReference(Common.STR_WALLPAPER)
//                                    .child(Common.select_background_key)
//                                    .updateChildren(update_view)
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(ViewWallpaperActivity.this, "Can not update download count", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//                        else    {
//                            Map<String,Object> update_view = new HashMap<>();
//                            update_view.put("downloadCount",Long.valueOf(1));
//
//                            FirebaseDatabase.getInstance().getReference(Common.STR_WALLPAPER)
//                                    .child(Common.select_background_key)
//                                    .updateChildren(update_view)
//                                    .addOnSuccessListener(aVoid -> {
//
//                                    }).addOnFailureListener(e -> Toast.makeText(ViewWallpaperActivity.this, "Can not set default download count", Toast.LENGTH_SHORT).show());
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//    }

    private void addToRecent() {
        Disposable disposable = Observable.create(e -> {
            Recent recent = new Recent(
                    Common.selected_background.getImageLink(),
                    Common.selected_background.getImageId(),
                    String.valueOf(System.currentTimeMillis()),
                    Common.selected_background_key
            );
            recentRepository.insertRecent(recent);
            e.onComplete();
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(o -> {
                }, throwable -> Log.e("ERROR", throwable.getMessage()), () -> {

                });
        compositeDisposable.add(disposable);
    }


    @Override
    protected void onDestroy() {
        Picasso.get().cancelRequest(target);
        compositeDisposable.clear();
        super.onDestroy();
    }

    private void rateWallpaper(int value)    {
        Rating rating = new Rating(
                Common.selected_background.getImageId(),
                String.valueOf(value));

        ratingTbl.push()
                .setValue(rating).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ViewWallpaperActivity.this, "Thank You For Your FeedBack !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}