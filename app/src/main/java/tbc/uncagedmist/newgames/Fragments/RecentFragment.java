package tbc.uncagedmist.newgames.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import tbc.uncagedmist.newgames.Adapter.MyRecyclerAdapter;
import tbc.uncagedmist.newgames.Common.Common;
import tbc.uncagedmist.newgames.R;
import tbc.uncagedmist.newgames.RecentDB.DB_Recent.RecentDataSource;
import tbc.uncagedmist.newgames.RecentDB.DB_Recent.RecentDatabase;
import tbc.uncagedmist.newgames.RecentDB.DataSource.RecentRepository;
import tbc.uncagedmist.newgames.RecentDB.Recent;

public class RecentFragment extends Fragment {

    private static RecentFragment INSTANCE = null;

    RecyclerView recyclerView;

    List<Recent> recentList;
    MyRecyclerAdapter adapter;

    Context context;

    CompositeDisposable compositeDisposable;
    RecentRepository recentRepository;

    @SuppressLint("ValidFragment")
    public RecentFragment(Context context) {
        this.context = context;

        compositeDisposable = new CompositeDisposable();
        RecentDatabase database = RecentDatabase.getInstance(context);
        recentRepository = RecentRepository.getInstance(RecentDataSource.getInstance(database.recentDAO()));
    }

    public RecentFragment() {
    }

    public static RecentFragment getInstance(Context context)    {

        if (INSTANCE == null)   {
            INSTANCE = new RecentFragment(context);
        }
        return INSTANCE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);

        recyclerView = view.findViewById(R.id.recycler_recents);

        recyclerView.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(gridLayoutManager);

        recentList = new ArrayList<>();

        adapter = new MyRecyclerAdapter(context,recentList);
        recyclerView.setAdapter(adapter);

        if (Common.isConnectedToInternet(getContext())) {
            loadRecent();
        }
        else
            Toast.makeText(getContext(), "Please Connect to Internet...", Toast.LENGTH_SHORT).show();

        return view;
    }

    private void loadRecent() {
        Disposable disposable = recentRepository.getAllRecent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(recent ->
                                onGetAllRecentSuccess(recent),
                        throwable ->
                                Log.d("ERROR", throwable.getMessage()));
        compositeDisposable.add(disposable);
    }

    private void onGetAllRecentSuccess(List<Recent> recent) {
        recentList.clear();
        recentList.addAll(recent);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}