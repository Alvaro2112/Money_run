package sdp.moneyrun;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DatabaseProxyTest {
    @Test
    public void getPlayerFromDatabase() throws Throwable {

        final Player player = new Player(1236);
        player.setAddress("FooBarr");
        player.setName("John Doe");
        final DatabaseProxy db = new DatabaseProxy();
        db.putPlayer(player);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }        Task<DataSnapshot> task = db.getPlayer(player.getPlayerId());

        Thread.sleep(1000);
        while (!task.isComplete()){}
        assert player.equals(db.deserializePlayer(task.getResult().getValue().toString()));
    }

//    @Test
//    public void addListenerTest() throws Throwable {
//        final Player player = new Player(1236);
//        player.setAddress("FooBarr");
//        player.setName("John Doe");
//        final DatabaseProxy db = new DatabaseProxy();
//        db.putPlayer(player);
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        runOnUiThread(()-> {
//            System.out.println("Entered UI thread thing");
//            Task<DataSnapshot> task = db.getPlayer(player.getPlayerId());
//            task.addOnCompleteListener(task1 -> {
//                System.out.println("Entered is complete");
//                 assert player.equals(db.deserializePlayer(task.getResult().getValue().toString()));
//
//            });
//           // assert(task.isComplete());
//        });
//    }

//    @Rule
//    public ExpectedException exception = ExpectedException.none();

    @Test(expected = IllegalArgumentException.class)
    public void deserializeThrowsErrorOnNullArgument(){
            DatabaseProxy db = new DatabaseProxy();
            db.deserializePlayer(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserializeThrowsErrorOnEmptyArgument(){
        DatabaseProxy db = new DatabaseProxy();
        db.deserializePlayer("");
    }

    @Test
    public void deserializeReturnsNullOnWrongPlayedGamesFormat(){
        String s = "{address=FooBarr, numberOfPlayedGames=abc, name=John Doe, numberOfDiedGames=0, playerId=1236}";
        DatabaseProxy db = new DatabaseProxy();
        assert(db.deserializePlayer(s) == null);
    }

    @Test
    public void deserializeReturnsNullOnWrongDiedGamesFormat(){
        String s = "{address=FooBarr, numberOfPlayedGames=0, name=John Doe, numberOfDiedGames=abc, playerId=1236}";
        DatabaseProxy db = new DatabaseProxy();
        assert(db.deserializePlayer(s) == null);
    }

    @Test
    public void deserializeReturnsNullOnWrongIdFormat(){
        String s = "{address=FooBarr, numberOfPlayedGames=0, name=John Doe, numberOfDiedGames=0, playerId=abc}";
        DatabaseProxy db = new DatabaseProxy();
        assert(db.deserializePlayer(s) == null);
    }
}
