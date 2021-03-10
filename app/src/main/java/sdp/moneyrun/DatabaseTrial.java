package sdp.moneyrun;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseTrial {
    //TODO: Setup a SQLite database
    //TODO:Setup a local database object
    //TODO: Setup a query method
    //TODO: Setup an insert method
    //TODO: Setup a delete method

    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DatabaseTrial databaseTrialInstance;
    Cursor cursor = null;

    //private constructor
    private DatabaseTrial(Context context){
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseTrial getDatabaseTrialInstance(Context context){
        if(databaseTrialInstance == null)
            databaseTrialInstance = new DatabaseTrial(context);
        return databaseTrialInstance;
    }
    // Open the connection to the remote database
    public void openDatabase(){
        this.db = openHelper.getWritableDatabase();
    }
    //Close the connection to the database
    public void closeDatabase(){
        if(db != null)
            db.close();
    }

    //TODO: have an insert method so new Player's info can be stored in the database

    //Query the result from the database
    // TODO: setup database, here we assume the result will be of type String but might not be the case
    // also assume the key is also a String
    public String query(int key){
        cursor = db.rawQuery("SELECT * FROM TABLE1TRIALSDP WHERE KEY ='" + key + "'", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while(cursor.moveToNext()){
            String result = cursor.getString(0);
            String result2  = cursor.getString(1);
            String result3  = cursor.getString(2);
            String result4  = cursor.getString(3);
            buffer.append("PlayerId: "+result + " lives in " + result2 + " has " + result3 + " games where he died and " + result4+ " is his name" );
        }
        return buffer.toString();
    }
    //TODO: also setup in MainActivity so on click query occurs // need to change layout file to incorporate button textView...
}

