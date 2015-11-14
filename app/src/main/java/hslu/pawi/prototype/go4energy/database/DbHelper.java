package hslu.pawi.prototype.go4energy.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by Andy on 14.11.2015.
 */
public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(final Context context) {
        super(context, DbAdapter.DB_NAME, null, DbAdapter.DB_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            throw new SQLException("TEST");
          //TODO DB definitions
        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}

