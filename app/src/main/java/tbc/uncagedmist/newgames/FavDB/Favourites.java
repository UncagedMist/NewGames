package tbc.uncagedmist.newgames.FavDB;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "favourites",primaryKeys = {"imageLink","imageId"})
public class Favourites {

    @ColumnInfo(name = "imageLink")
    @NonNull
    private String imageLink;

    @ColumnInfo(name = "imageId")
    @NonNull
    private String imageId;

    @ColumnInfo(name = "saveTime")
    private String saveTime;

    @ColumnInfo(name = "key")
    private String key;

    public Favourites(@NonNull String imageLink, @NonNull String imageId, String saveTime, String key) {
        this.imageLink = imageLink;
        this.imageId = imageId;
        this.saveTime = saveTime;
        this.key = key;
    }

    @NonNull
    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(@NonNull String imageLink) {
        this.imageLink = imageLink;
    }

    @NonNull
    public String getImageId() {
        return imageId;
    }

    public void setImageId(@NonNull String imageId) {
        this.imageId = imageId;
    }

    public String getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(String saveTime) {
        this.saveTime = saveTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}