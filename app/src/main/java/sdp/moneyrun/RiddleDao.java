package sdp.moneyrun;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface RiddleDao{
    @Query("SELECT * FROM riddles")
    Riddle[] getRiddle();
}



