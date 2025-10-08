package com.example.tutorfinderapp.app

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// The comments below are meant to help you understand what each function does - Vikram

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
                $CONTACTPHONE_COL TEXT,
                $CONTACTEMAIL_COL TEXT,
                $STUDENTS_COL TEXT
            )
        """.trimIndent()

        db.execSQL(createTableQuery)
    }
    // Runs once when the database is first created and when it is updated

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
    // Runs when upgrading database version; recreates table(see up there)

    fun addTutor(
        name: String,
        ageRange: String,
        payment: String,
        subjects: String,
        availability: String, // Simple plain text like "Mon 3–5 PM, Wed 1–2 PM"
        givesHomework: String, // can't use a boolean so will use string that will say "true" or "false"
        contactPhone: String,
        contactEmail: String,
        students: String // Comma list of names in place of an arraylist "Alice, Bob, Charlie"
    ) {
        val values = ContentValues().apply {
            put(NAME_COL, name)
            put(AGERANGE_COL, ageRange)
            put(PAYMENT_COL, payment)
            put(SUBJECTS_COL, subjects)
            put(AVAILABILITY_COL, availability)
            put(GIVESHOMEWORK_COL, givesHomework)
            put(CONTACTPHONE_COL, contactPhone)
            put(CONTACTEMAIL_COL, contactEmail)
            put(STUDENTS_COL, students)
        }

        writableDatabase.use { db ->
            db.insert(TABLE_NAME, null, values)
        }
    }
    // Inserts a new tutor into the database

    fun getTutors(): Cursor {
        return readableDatabase.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }
    // Returns all tutors in the database

    fun getTutorById(tutorId: Int): Cursor {
        return readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $ID_COL = ?",
            arrayOf(tutorId.toString())
        )
    }
    // Returns one tutor by their ID

    companion object {
        private const val DATABASE_NAME = "Tutors"
        private const val DATABASE_VERSION = 3 // incremented since schema changed
        const val TABLE_NAME = "tutor_table"

        const val ID_COL = "id"
        const val NAME_COL = "name"
        const val AGERANGE_COL = "age_range"
        const val PAYMENT_COL = "payment"
        const val SUBJECTS_COL = "subjects"
        const val AVAILABILITY_COL = "availability"
        const val GIVESHOMEWORK_COL = "gives_homework"
        const val CONTACTPHONE_COL = "contact_phone"
        const val CONTACTEMAIL_COL = "contact_email"
        const val STUDENTS_COL = "students"
    }
}
