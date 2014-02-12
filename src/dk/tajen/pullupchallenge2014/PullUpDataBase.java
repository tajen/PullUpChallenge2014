/**
 * 
 */
package dk.tajen.pullupchallenge2014;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.util.Log;
/**
 * @author Tajen
 *
 */
public class PullUpDataBase extends SQLiteOpenHelper {

	
	// Database version
	private static final int DATABASE_VERSION = 3;
	// Database name
	private static final String DATABASE_NAME = "PullUpChallenge2014.db";
	
    // Table name
    private static final String TABLE_PULL_UP_SET = "PullUpSet";

    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_SET_COUNT = "SetCount";
    private static final String KEY_REGISTRATION = "Registration";

    private static final String[] COLUMNS = {KEY_ID,KEY_SET_COUNT,KEY_REGISTRATION};
	
	/**
	 * @param context
	 */
	public PullUpDataBase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// SQL statement to create book table        
		String CREATE_BOOK_TABLE = "CREATE TABLE PullUpSet ( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_SET_COUNT + " INTEGER, "+ KEY_REGISTRATION + " INTEGER )";         
		
		// create books table        
		db.execSQL(CREATE_BOOK_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older books table if existed        
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PULL_UP_SET);         
		
		// create fresh books table        
		this.onCreate(db);
	}

	public long AddPullUpSet(RegistrationElement element)
	{
		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		
		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put(KEY_SET_COUNT, element.Count);
		values.put(KEY_REGISTRATION, element.Registrated.getTime());
		
		// 3. insert
		long retval = db.insert(TABLE_PULL_UP_SET, null, values);
		element.Id = (int)retval;
		
		// 4. close
		db.close();
		
		Log.d("AddPullUpSet", element.toString());
		
		return retval;
	}
	
	public RegistrationElement GetPullUpSet(int id) {
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		// Build query
		Cursor _cursor = db.query(TABLE_PULL_UP_SET, COLUMNS, KEY_ID + " = ? ", new String[] { String.valueOf(id) }, null, null, null);
		
		if (_cursor != null)
			_cursor.moveToFirst();
		
		RegistrationElement _set = new RegistrationElement(_cursor.getInt(1), ToDate(_cursor.getLong(2)));
		_set.Id = _cursor.getInt(0);
		
		return _set;
	}
	
	public List<RegistrationElement> GetAllPullUpSets()
	{
		List<RegistrationElement> _sets = new LinkedList<RegistrationElement>();
		
		// 1. Build query
		String _query = "SELECT * FROM " + TABLE_PULL_UP_SET;
		
		// 2. Get reference to writeable DB
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor _cursor = db.rawQuery(_query, null);
		
		// 3. go over each row, build book and add it to list
		RegistrationElement element = null;
		if(_cursor.moveToFirst()) {
			do {
				element = new RegistrationElement();
				element.Id = _cursor.getInt(0);
				element.Count = _cursor.getInt(1);
				long date = _cursor.getLong(2);
				element.Registrated = ToDate(date);
				
				// Add to list
				_sets.add(element);
			} while (_cursor.moveToNext());
			
			Log.d("GetAllPullUpSets()", _sets.toString());
		}
		
		return _sets;
	}
	
	public int UpdateSet(RegistrationElement element) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_SET_COUNT, element.Count);
		values.put(KEY_REGISTRATION, element.Registrated.getTime());
		
	    // 3. updating row    
		int i = db.update(TABLE_PULL_UP_SET, //table            
				values, // column/value            
				KEY_ID+" = ?", // selections            
				new String[] { String.valueOf(element.Id) }); //selection args 
		
		db.close();
		
		return i;
	}
	
	public void DeleteSet(RegistrationElement element) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_PULL_UP_SET, KEY_ID+" = ?", new String[] { String.valueOf(element.Id) });
		
		db.close();
		
		Log.d("DeleteSet", element.toString());
	}
	public void DeleteSet(int id) {
		RegistrationElement element = new RegistrationElement();
		element.Id = id;
		DeleteSet(element);
	}
	
	public static Date ToDate(long milliSeconds) {
		Date date = new Date(milliSeconds);
		return date;
	}
}
