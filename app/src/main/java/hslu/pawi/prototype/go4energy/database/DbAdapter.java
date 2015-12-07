package hslu.pawi.prototype.go4energy.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import hslu.pawi.prototype.go4energy.dto.AnswerDTO;
import hslu.pawi.prototype.go4energy.dto.CategoryDTO;
import hslu.pawi.prototype.go4energy.dto.QuestionDTO;

/**
 * Created by Andy on 14.11.2015.
 */
public class DbAdapter {
    public static String DB_NAME = "go4energy";
    public static int DB_Version = 1;

    private final DbHelper dbHelper;
    private SQLiteDatabase db;

    public DbAdapter(final Context context) {
        dbHelper = new DbHelper(context);
    }

    public void open() {
        if(db==null || !db.isOpen()) {
            db = dbHelper.getWritableDatabase();
        }
    }

    public void close() {
        dbHelper.close();
        db.close();
        db = null;
    }

    public CategoryDTO getCategoryByID(int id){
        CategoryDTO categoryDTO = null;
        String selectQuery = "Select * from "+DbHelper.TABLE_CATEGORY+
                " where "+DbHelper.COLUMN_ID+" = "+id;
        Cursor result = db.rawQuery(selectQuery,null);
        if(result!=null && result.getCount()>0) {
            result.moveToFirst();
            categoryDTO = createCategoryFromResult(result);
        }
        return categoryDTO;
    }

    public List<CategoryDTO> getAllCategorys(){
        List<CategoryDTO> allCategoryDTO = new ArrayList<>();
        String selectQuery = "Select * from "+DbHelper.TABLE_CATEGORY;
        Cursor result = db.rawQuery(selectQuery, null);
        if(result!=null && result.getCount()>0) {
            result.moveToPosition(-1);
            while (result.moveToNext()) {
                allCategoryDTO.add(createCategoryFromResult(result));
            }
        }
        else{
            allCategoryDTO = null;
        }
        result.close();

        return allCategoryDTO;
    }


    private CategoryDTO createCategoryFromResult(Cursor result) {
        CategoryDTO categoryDTO = null;

        if(result != null && result.getCount()>0){
            categoryDTO= new CategoryDTO();
            categoryDTO.setId(result.getInt(result.getColumnIndex(DbHelper.COLUMN_ID)));
            categoryDTO.setDescription(result.getString(result.getColumnIndex(DbHelper.COLUMN_DESC)));
        }
        return categoryDTO;
    }

    public AnswerDTO getAnswerByID(int id){
        String selectQuery = "Select * from "+DbHelper.TABLE_ANSWER+
                " where "+dbHelper.COLUMN_ID+" = "+id;
        Cursor result = db.rawQuery(selectQuery, null);
        AnswerDTO answerDTO = createAnswerFromResult(result);
        return answerDTO;
    }

    private AnswerDTO createAnswerFromResult(Cursor result) {
        AnswerDTO answerDTO = null;
        try {
            if (result != null && result.getCount() > 0) {
                answerDTO = new AnswerDTO();
                answerDTO.setId(result.getInt(result.getColumnIndex(DbHelper.COLUMN_ID)));
                answerDTO.setQid(result.getInt(result.getColumnIndex(DbHelper.COLUMN_QUESTION)));
                answerDTO.setValue(result.getString(result.getColumnIndex(DbHelper.COLUMN_VALUE)));
                answerDTO.setCorrect(result.getInt(result.getColumnIndex(DbHelper.COLUMN_CORRECT)) > 0);
                answerDTO.setActive(result.getInt(result.getColumnIndex(DbHelper.COLUMN_ACTIVE)) > 0);
            }
        }catch (Exception e){
            e.getStackTrace();
        }
        return answerDTO;
    }

    public List<AnswerDTO> getAllAnswersByQuestion(int questionID){
        List<AnswerDTO> allAnswerDTO = new ArrayList<>();
        String selectQuery = "Select * from "+DbHelper.TABLE_ANSWER+
                " where "+DbHelper.COLUMN_QUESTION + " = "+questionID;
        Cursor result = db.rawQuery(selectQuery, null);
        if(result!=null && result.getCount()>0) {
            result.moveToPosition(-1);
            while (result.moveToNext()) {
                allAnswerDTO.add(createAnswerFromResult(result));
            }
        }
        else{
            allAnswerDTO = null;
        }
        result.close();
        return allAnswerDTO;
    }

    public List<QuestionDTO> getAllQuestions() throws Exception{
        List<QuestionDTO> allQuestions = new ArrayList<>();
        try {
            String selectQuery = "Select * from " + dbHelper.TABLE_QUESTION;
            Cursor result = db.rawQuery(selectQuery, null);
            if (result != null && result.getCount() > 0) {
                result.moveToPosition(-1);
                while (result.moveToNext()) {
                    allQuestions.add(createQuestionFromResult(result));
                }
            }
            result.close();
        }
        catch (Exception e){
            throw new Exception(e.getMessage());
        }
        return  allQuestions;
    }

    public List<QuestionDTO> getAllQuestionsByDifficulty(int difficulty){
        List<QuestionDTO> allQuestions = new ArrayList<>();
        String selectQuery = "Select * from "+dbHelper.TABLE_QUESTION +" where "+
                dbHelper.COLUMN_DIFFICULTY +" = "+difficulty;
        Cursor result = db.rawQuery(selectQuery, null);
        if(result!=null && result.getCount()>0) {
            result.moveToPosition(-1);
            while (result.moveToNext()){
                allQuestions.add(createQuestionFromResult(result));
            }
        }
        result.close();

        return  allQuestions;
    }

    private QuestionDTO createQuestionFromResult(Cursor result) {
        QuestionDTO questionDTO = null;
        try {


            if (result != null && result.getCount() > 0) {
                questionDTO = new QuestionDTO();
                questionDTO.setId(result.getInt(result.getColumnIndex(DbHelper.COLUMN_ID)));
                questionDTO.setDescription(result.getString(result.getColumnIndex(DbHelper.COLUMN_DESC)));
                questionDTO.setAnswers(getAllAnswersByQuestion(questionDTO.getId()));
                questionDTO.setCategoryDTO(getCategoryByID(result.getInt(result.getColumnIndex(DbHelper.COLUMN_CATEGORY))));
                questionDTO.setDifficulty(result.getInt(result.getColumnIndex(DbHelper.COLUMN_DIFFICULTY)));
                questionDTO.setInformations(result.getString(result.getColumnIndex(DbHelper.COLUMN_INFORMATIONS)));
                questionDTO.setActive(result.getInt(result.getColumnIndex(DbHelper.COLUMN_ACTIVE)) > 0);
            }
        }catch (Exception e){
            e.getStackTrace();
        }
        return questionDTO;
    }

    public int getQuestionAmountByDifficulty(int difficulty){
        int amount = 0;

        String selectQuery = "Select Count(*) from "+dbHelper.TABLE_QUESTION +" where "+
                dbHelper.COLUMN_DIFFICULTY +" = "+difficulty;
        Cursor result = db.rawQuery(selectQuery, null);
        result.moveToFirst();
        amount = result.getInt(0);
        result.close();

        return amount;
    }
}

