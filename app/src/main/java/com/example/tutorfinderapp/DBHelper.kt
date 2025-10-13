package com.example.tutorfinderapp.app

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Tutor Table
        val createTutorTableQuery = """
            CREATE TABLE $TUTOR_TABLE (
                $TUTOR_ID INTEGER PRIMARY KEY AUTOINCREMENT,
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

        // Student Table
        val createStudentTableQuery = """
            CREATE TABLE $STUDENT_TABLE (
                $STUDENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $STUDENT_NAME_COL TEXT,
                $STUDENT_EMAIL_COL TEXT,
                $ASSIGNED_TUTOR_ID INTEGER,
                FOREIGN KEY($ASSIGNED_TUTOR_ID) REFERENCES $TUTOR_TABLE($TUTOR_ID)
            )
        """.trimIndent()

        db.execSQL(createTutorTableQuery)
        db.execSQL(createStudentTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TUTOR_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $STUDENT_TABLE")
        onCreate(db)
    }

    // ---------------------- TUTOR FUNCTIONS ----------------------

    fun addTutor(
        name: String,
        ageRange: String,
        payment: String,
        subjects: String,
        availability: String,
        givesHomework: String,
        contactPhone: String,
        contactEmail: String,
        students: String
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
            db.insert(TUTOR_TABLE, null, values)
        }
    }

    fun getTutors(): Cursor {
        return readableDatabase.rawQuery("SELECT * FROM $TUTOR_TABLE", null)
    }

    fun getTutorById(tutorId: Int): Cursor {
        return readableDatabase.rawQuery(
            "SELECT * FROM $TUTOR_TABLE WHERE $TUTOR_ID = ?",
            arrayOf(tutorId.toString())
        )
    }

    // ---------------------- STUDENT FUNCTIONS ----------------------

    fun addStudent(name: String, email: String) {
        val values = ContentValues().apply {
            put(STUDENT_NAME_COL, name)
            put(STUDENT_EMAIL_COL, email)
            put(ASSIGNED_TUTOR_ID, -1) // not assigned yet
        }

        writableDatabase.use { db ->
            db.insert(STUDENT_TABLE, null, values)
        }
    }

    fun getStudents(): Cursor {
        return readableDatabase.rawQuery("SELECT * FROM $STUDENT_TABLE", null)
    }

    fun assignStudentToTutor(studentId: Int, tutorId: Int) {
        val values = ContentValues().apply {
            put(ASSIGNED_TUTOR_ID, tutorId)
        }

        writableDatabase.update(
            STUDENT_TABLE,
            values,
            "$STUDENT_ID = ?",
            arrayOf(studentId.toString())
        )

        // Optional: update tutor’s "students" column with the new student name
        val cursor = getTutorById(tutorId)
        if (cursor.moveToFirst()) {
            val currentStudents = cursor.getString(cursor.getColumnIndexOrThrow(STUDENTS_COL)) ?: ""
            val studentCursor = readableDatabase.rawQuery(
                "SELECT $STUDENT_NAME_COL FROM $STUDENT_TABLE WHERE $STUDENT_ID = ?",
                arrayOf(studentId.toString())
            )
            if (studentCursor.moveToFirst()) {
                val studentName = studentCursor.getString(0)
                val updatedStudents = if (currentStudents.isBlank()) studentName else "$currentStudents, $studentName"

                val tutorValues = ContentValues().apply {
                    put(STUDENTS_COL, updatedStudents)
                }
                writableDatabase.update(
                    TUTOR_TABLE,
                    tutorValues,
                    "$TUTOR_ID = ?",
                    arrayOf(tutorId.toString())
                )
            }
            studentCursor.close()
        }
        cursor.close()
    }

    fun getStudentsForTutor(tutorId: Int): Cursor {
        return readableDatabase.rawQuery(
            "SELECT * FROM $STUDENT_TABLE WHERE $ASSIGNED_TUTOR_ID = ?",
            arrayOf(tutorId.toString())
        )
    }

    companion object {
        private const val DATABASE_NAME = "TutorFinderDB"
        private const val DATABASE_VERSION = 4

        // Tutor Table
        const val TUTOR_TABLE = "tutor_table"
        const val TUTOR_ID = "id"
        const val NAME_COL = "name"
        const val AGERANGE_COL = "age_range"
        const val PAYMENT_COL = "payment"
        const val SUBJECTS_COL = "subjects"
        const val AVAILABILITY_COL = "availability"
        const val GIVESHOMEWORK_COL = "gives_homework"
        const val CONTACTPHONE_COL = "contact_phone"
        const val CONTACTEMAIL_COL = "contact_email"
        const val STUDENTS_COL = "students"

        // Student Table
        const val STUDENT_TABLE = "student_table"
        const val STUDENT_ID = "student_id"
        const val STUDENT_NAME_COL = "student_name"
        const val STUDENT_EMAIL_COL = "student_email"
        const val ASSIGNED_TUTOR_ID = "assigned_tutor_id"
    }
}
