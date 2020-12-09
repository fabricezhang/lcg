package top.easelink.lcg.ui.main.source.local;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import top.easelink.lcg.BuildConfig;
import top.easelink.lcg.appinit.LCGApp;
import top.easelink.lcg.ui.main.source.model.ArticleEntity;
import top.easelink.lcg.ui.main.source.model.HistoryEntity;

/**
 * author : junzhang
 * date   : 2019-07-26 13:53
 * desc   :
 */
@Database(entities = {ArticleEntity.class, HistoryEntity.class}, version = 3, exportSchema = false)
public abstract class ArticlesDatabase extends RoomDatabase {
    private static final Object sLock = new Object();
    private static ArticlesDatabase mInstance;

    public static ArticlesDatabase getInstance() {
        synchronized (sLock) {
            if (mInstance == null) {
                mInstance = Room
                    .databaseBuilder(
                        LCGApp.getContext(),
                        ArticlesDatabase.class,
                        BuildConfig.DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
            }
        }
        return mInstance;
    }

    public abstract ArticlesDao articlesDao();
}
