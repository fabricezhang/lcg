package top.easelink.lcg.ui.main.source.local;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import top.easelink.lcg.BuildConfig;
import top.easelink.lcg.LCGApp;
import top.easelink.lcg.ui.main.source.model.ArticleEntity;

/**
 * author : junzhang
 * date   : 2019-07-26 13:53
 * desc   :
 */
@Database(entities = {ArticleEntity.class}, version = 1, exportSchema = false)
public abstract class ArticlesDatabase extends RoomDatabase {
    private static ArticlesDatabase mInstance;

    private static final Object sLock = new Object();

    public abstract ArticlesDao articlesDao();

    public static ArticlesDatabase getInstance() {
        synchronized (sLock) {
            if (mInstance == null) {
                mInstance = Room.databaseBuilder(
                        LCGApp.getContext(),
                        ArticlesDatabase.class,
                        BuildConfig.DB_NAME).build();
            }
        }
        return mInstance;
    }
}
