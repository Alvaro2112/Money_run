package sdp.moneyrun;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    DatabaseProxy db;
    EditText et;
    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            System.out.println(snapshot.getValue(Player.class).getName());
           // et.setText(snapshot.getValue(Player.class).getName());
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // et.setText("Foobar");

    }

    @Override
    protected void onStart(){
        super.onStart();
        et = (EditText) findViewById(R.id.helloWorld);
        DatabaseProxy db = new DatabaseProxy();
        Player player = new Player(654321,"Martin","somewhere", 0 , 0 );
        db.putPlayer(player);
        DatabaseReference fdb = FirebaseDatabase.getInstance().getReference().child("players");
       // fdb.addValueEventListener( listener);
        Button button = (Button) findViewById(R.id.buttonMain);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeView();
            }
        });

    }

    private void changeView(){
        db.getPlayer(1236).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
            //    et.setText(task.getResult().getValue(Player.class).getName());
                System.out.println(task.getResult().getValue(Player.class).getName());

            }
        });
    }


}