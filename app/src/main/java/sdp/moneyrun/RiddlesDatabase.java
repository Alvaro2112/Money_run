package sdp.moneyrun;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class RiddlesDatabase {

    private static RiddlesDatabase obj;
    private ArrayList<Riddle> db;
    private int loc;

    // private constructor to force use of
    // getInstance() to create Singleton object
    private RiddlesDatabase(Context context) {


        InputStream inputStream =  context.getResources().openRawResource(R.raw.database);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        db = new ArrayList<Riddle>();

        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(";");
                this.db.add(new Riddle(row[0].replace("\"", "").replace("\n", ""),
                row[1].replace("\"", ""),
                row[2].replace("\"", ""),
                row[3].replace("\"", ""),
                row[4].replace("\"", ""),
                row[5].replace("\"", "")));

            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }

        Collections.shuffle(this.db);
        loc = 0;

    }

    public static RiddlesDatabase getInstance()
    {
        if (obj==null)
            throw new RuntimeException("Need to create a instance first");

        return obj;
    }

    public static RiddlesDatabase createInstance(Context context){
        if(obj != null)
            throw new RuntimeException("Instance already exists");

        return new RiddlesDatabase(context);
    }

    public Riddle getRandomRiddle(){
        loc = (loc + 1) % db.size();
        return db.get(loc);
    }


}
