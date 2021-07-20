package tbc.uncagedmist.newgames.FavDB.DataSource;

import java.util.List;

import io.reactivex.Flowable;
import tbc.uncagedmist.newgames.FavDB.Favourites;

public interface IFavDataSource {
    Flowable<List<Favourites>> getAllFavourites();
    void insertFav(Favourites...favourites);
    void updateFav(Favourites...favourites);
    void deleteFav(Favourites...favourites);
    void deleteAllFav();
}
