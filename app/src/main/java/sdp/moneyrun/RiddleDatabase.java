package sdp.moneyrun;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Riddle.class}, version = 2, exportSchema = false)
public abstract class RiddleDatabase extends RoomDatabase {
    private static RiddleDatabase INSTANCE;
    public abstract RiddleDao riddleDao();
}
