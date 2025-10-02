package com.example.sqlstuff

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.gson.Gson

//The. comments you'll see are made to help you understand what each function does -Vikram

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $ID_COL INTEGER PRIMARY KEY AUTOINCREMENT,
                $NAME_COL TEXT,
                $AGERANGE_COL TEXT,
                $PAYMENT_COL TEXT,
                $SUBJECTS_COL TEXT,
                $AVAILABILITY_COL TEXT,
                $GIVESHOMEWORK_COL TEXT,
                $CONTACT_COL TEXT
            )
        """.trimIndent()

        db.execSQL(createTableQuery)
    }
    //This runs when the db is created and isn't run again until onUpgrade runs

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
    //This updates the db with a new tutor, runs when new tutor is inserted

    fun addTutor(name: String, ageRange: String, payment: String, subjects: String, availability: String,
                 givesHomework: String, contactInfo: ContactInfo) {
        val gson = Gson()
        val contactJson = gson.toJson(contactInfo)

        val values = ContentValues().apply {
            put(NAME_COL, name)
            put(AGERANGE_COL, ageRange)
            put(PAYMENT_COL, payment)
            put(SUBJECTS_COL, subjects)
            put(AVAILABILITY_COL, availability)
            put(GIVESHOMEWORK_COL, givesHomework)
            put(CONTACT_COL, contactJson)
        }

        writableDatabase.use { db ->
            db.insert(TABLE_NAME, null, values)
        }
    }
    //Cretes new row in table for new tutor and inserts it in

    fun getTutors(): Cursor {
        return readableDatabase.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    fun getTutorById(tutorId: Int): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $ID_COL = ?", arrayOf(tutorId.toString()))
    }

    //To use these functions, you have to do "DBHelper.getTutorById = someVariable"
    //The cursor returned is a set of successful searches for the info and you can
    //loop through all of the info with a while(!someVariable.isAfterLast()) which checks if the
    //selector has not gone out of bounds



    data class ContactInfo(
        val phone: String,
        val email: String
    )

    companion object {
        private const val DATABASE_NAME = "Tutors"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "tutor_table"

        const val ID_COL = "id"
        const val NAME_COL = "name"
        const val AGERANGE_COL = "age_range"
        const val PAYMENT_COL = "payment"
        const val SUBJECTS_COL = "subjects"
        const val AVAILABILITY_COL = "availability"
        const val GIVESHOMEWORK_COL = "gives_homework"
        const val CONTACT_COL = "contact_info"
    }
}
