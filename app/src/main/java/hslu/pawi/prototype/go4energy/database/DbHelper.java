package hslu.pawi.prototype.go4energy.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by Andy on 14.11.2015.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static String TABLE_CATEGORY = "CATEGORY";
    public static String TABLE_QUESTION = "QUESTION";
    public static String TABLE_ANSWER = "ANSWER";

    public static String COLUMN_ID = "ID";
    public static String COLUMN_DESC ="DESCRIPTION";
    public static String COLUMN_DIFFICULTY = "DIFFICULTY";
    public static String COLUMN_CATEGORY = "CATEGORY";
    public static String COLUMN_INFORMATIONS = "INFORMATIONS";
    public static String COLUMN_ACTIVE = "ACTIVE";
    public static String COLUMN_QUESTION="QUESTION";
    public static String COLUMN_CORRECT= "CORRECT";
    public static String COLUMN_VALUE = "VALUE";

    public DbHelper(final Context context) {
        super(context, DbAdapter.DB_NAME, null, DbAdapter.DB_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            createCategoryTable(db);
            createQuestionTable(db);
            createAnswerTable(db);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createCategoryTable(SQLiteDatabase db) throws SQLException {
        String CREATE_CATEGORY_TABLE = "CREATE TABLE "+this.TABLE_CATEGORY+" ("+
                this.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                this.COLUMN_DESC +" TEXT);";
        db.execSQL(CREATE_CATEGORY_TABLE);
        String CREATE_INIT_VALUES =
                "INSERT INTO "+this.TABLE_CATEGORY+" "+
                        "Select 0, 'Mobility' "+
                        "UNION SELECT 1, 'Allgemein' "+
                        "UNION SELECT 2, 'Wohnen';";
        db.execSQL(CREATE_INIT_VALUES);
    }

    private void createQuestionTable(SQLiteDatabase db) throws SQLException {
        String CREATE_QUESTION_TABLE = "CREATE TABLE "+this.TABLE_QUESTION+" ("+
                this.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                this.COLUMN_DIFFICULTY + " INTEGER, "+
                this.COLUMN_DESC +" TEXT, "+
                this.COLUMN_INFORMATIONS+" TEXT, "+
                this.COLUMN_CATEGORY +" INTEGER, "+
                this.COLUMN_ACTIVE + " INTEGER," +
                "FOREIGN KEY("+COLUMN_CATEGORY+") REFERENCES "+this.TABLE_CATEGORY+"("+COLUMN_ID+")" +
                "ON UPDATE CASCADE ON DELETE SET NULL)";
        db.execSQL(CREATE_QUESTION_TABLE);
        String CREATE_INIT_VALUES =
                "INSERT INTO "+this.TABLE_QUESTION+" "+
                        "Select 0, 0, 'Die Energieeffizienz von Geräten (zum Beispiel Kühlschrank, Fernseher usw.) wird mit Buchstaben klassifiziert.','http://www.bfe.admin.ch/energieetikette/index.html?lang=de', 2, 1 "+
                        "UNION SELECT 1, 0, 'Wieviel Benzin kann mit der richtigen Reifenwahl eingespart werden?','http://www.bfe.admin.ch/energieetikette/00886/04758/index.html?lang=de', 0, 1 "+
                        "UNION SELECT 2, 0, 'In der Schweiz wird am meisten Elektrizität (Strom) erzeugt mit?','Schweizerische Gesamtenergiestatistik 2014 \n Institution: BFE\n Erschienen: 20.07.2015\n', 1, 1 "+
                        "UNION SELECT 3, 1, 'Wie viel Energie kann eingespart werden, wenn die Raumtemperatur um 1 °C (zum Beispiel von 21 auf 20 °C) abgesenkt wird?','http://www.energieschweiz.ch/de-ch/wohnen/heizen/3-temperatur-richtig-einstellen.aspx', 2, 1 "+
                        "UNION SELECT 4, 1, 'Wie kann man beim Autofahren kräftig Treibstoff sparen mit?','http://www.energieschweiz.ch/de-ch/mobilitaet/effizientes-fahren/ecodrive.aspx ', 0, 1 "+
                        "UNION SELECT 5, 1, 'Wenn alle Schweizer Haushalte während ihren Ferien die elektrischen Geräte abschalten (keine Standby-Verluste) oder den Stecker ziehen entspricht die eingesparte Energie dem Strombedarf pro Jahr für ','energeia Nr. 4 / 2015 Newsletter des Bundesamts für Energie BFE', 2, 1 "+
                        "UNION SELECT 6, 2, 'Erdwärme ist mit untiefer (oberflächennaher) und tiefer Geothermie nutzbar. Untiefe Bohrungen sind in der Regel?','BFE/ECH, Fakten zur Energie Nr. 2: Erneuerbare Energien', 2, 1 "+
                        "UNION SELECT 7, 2, 'Wie gross ist der Heizwert von Pellet aus Spänen, Sägemehl?','http://www.holzenergie.ch/uploads/tx_ttproducts/datasheet/403energieinhalt_graueEnergie_DFI_05.pdf.', 0, 1 "+
                        "UNION SELECT 8, 2, 'Wie viel kWh elektrische Energie produziert ein Quadratmeter Photovoltaikzelle im Schweizer Mittelland durchschnittlich pro Jahr?','http://www.energieschweiz.ch/de-ch/erneuerbare-energien/meine-solaranlage/solarrechner.aspx', 1, 1;";
        db.execSQL(CREATE_INIT_VALUES);
    }

    private void createAnswerTable(SQLiteDatabase db) throws SQLException {
        String CREATE_ANSWER_TABLE = "CREATE TABLE "+this.TABLE_ANSWER+" ("+
                this.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                this.COLUMN_QUESTION + " INTEGER, "+
                this.COLUMN_CORRECT +" INTEGER, "+
                this.COLUMN_VALUE + " TEXT, "+
                this.COLUMN_ACTIVE + " INTEGER," +
                "FOREIGN KEY("+this.COLUMN_QUESTION+") REFERENCES "+this.TABLE_QUESTION+"("+COLUMN_ID+") " +
                "ON UPDATE CASCADE ON DELETE CASCADE);";
        db.execSQL(CREATE_ANSWER_TABLE);
        String CREATE_INIT_VALUES =
                "INSERT INTO "+this.TABLE_ANSWER+" "+
                        "SELECT 0,0,1,'A ist das Gerät mit dem niedrigsten Energieverbrauch',1 "+
                        "UNION SELECT 1,0,0,'G ist das Gerät mit dem niedrigsten Energieverbrauch',1 "+
                        "UNION SELECT 2,1,0,'bis zu 5%',1 "+
                        "UNION SELECT 3,1,0,'bis zu 10%',1 "+
                        "UNION SELECT 4,1,1,'bis zu 20%',1 "+
                        "UNION SELECT 5,1,0,'bis zu 25%',1 "+
                        "UNION SELECT 6,2,0,'der Sonne',1 "+
                        "UNION SELECT 7,2,1,'Wasserkraft',1 "+
                        "UNION SELECT 8,2,0,'Kernkraftwerken',1 "+
                        "UNION SELECT 9,2,0,'Gaskraftwerke',1 "+
                        "UNION SELECT 10,3,0,'Man spart keine Energie',1 "+
                        "UNION SELECT 11,3,0,'bis zu 2%',1 "+
                        "UNION SELECT 12,3,0,'bis zu 4%',1 "+
                        "UNION SELECT 13,3,1,'bis zu 6%',1 "+
                        "UNION SELECT 14,4,1,'früh schalten, hohen Gang fahren',1 "+
                        "UNION SELECT 15,4,0,'spät schalten, hohen Gang fahren',1 "+
                        "UNION SELECT 16,4,0,'früh schalten, tiefer Gang',1 "+
                        "UNION SELECT 17,4,0,'spät schalten, tiefer Gang',1 "+
                        "UNION SELECT 18,5,0,'3 000 Haushalte',1 "+
                        "UNION SELECT 19,5,0,'5 000 Haushalte',1 "+
                        "UNION SELECT 20,5,0,'8 000 Haushalte',1 "+
                        "UNION SELECT 21,5,0,'15 000 Haushalte',1 "+
                        "UNION SELECT 22,5,1,'18 000 Haushalte',1 "+
                        "UNION SELECT 23,6,0,'50m - 100m tief',1 "+
                        "UNION SELECT 24,6,0,'100m tief',1 "+
                        "UNION SELECT 25,6,1,'100m - 300m tief',1 "+
                        "UNION SELECT 26,6,0,'200m - 500m tief',1 "+
                        "UNION SELECT 27,7,0,'2.5 kWh/kg',1 "+
                        "UNION SELECT 28,7,0,'3.7 kW/kg',1 "+
                        "UNION SELECT 29,7,0,'4.8 kW/kg',1 "+
                        "UNION SELECT 30,7,1,'4.8 kWh/kg',1 "+
                        "UNION SELECT 31,8,0,'50 - 80 kWh /m^2 a',1 "+
                        "UNION SELECT 32,8,0,'80 - 120 kWh /m^2 a',1 "+
                        "UNION SELECT 33,8,1,'140 - 160 kWh /m^2 a',1 "+
                        "UNION SELECT 34,8,0,'150 - 190 kWh /m^2 a',1;"+
                        "";
        db.execSQL(CREATE_INIT_VALUES);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}

