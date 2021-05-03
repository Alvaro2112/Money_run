package sdp.moneyrun.database;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import sdp.moneyrun.R;
import sdp.moneyrun.map.Riddle;

public class RiddlesDatabase {

    private static RiddlesDatabase obj;
    private ArrayList<Riddle> db;
    private int loc;

    /**\
     *
     * @param context the current context
     */
    private RiddlesDatabase(Context context) {


        InputStream inputStream =  context.getResources().openRawResource(R.raw.database);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        db = new ArrayList<Riddle>();

        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(";");
                db.add(new Riddle(row[0].substring(0, row[0].length() - 3).replace("\"", ""),
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

    /**\
     *  Will create a unique instance of the database by loading all the riddles that are present in the text file into an array of Riddles.
     * @param context current context
     * @return the created instance
     */
    public static RiddlesDatabase createInstance(Context context){
        if(obj != null)
            throw new RuntimeException("Instance already exists");

        obj = new RiddlesDatabase(context);
        return  obj;
    }

    /**\
     *
     * @return the next Riddle in the shuffled array
     */
    public Riddle getRandomRiddle(){
        loc = (loc + 1) % db.size();
        return db.get(loc);
    }

    public static void reset(){
        obj = null;
    }


}
