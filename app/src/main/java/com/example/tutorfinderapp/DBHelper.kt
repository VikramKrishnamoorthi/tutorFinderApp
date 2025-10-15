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
        val createTutorTable = """
            CREATE TABLE $TUTOR_TABLE (
                $TUTOR_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $TUTOR_NAME TEXT,
                $TUTOR_AGE_RANGE TEXT,
                $TUTOR_PAYMENT TEXT,
                $TUTOR_SUBJECTS TEXT,
                $TUTOR_AVAILABILITY TEXT,
                $TUTOR_GIVES_HOMEWORK TEXT,
                $TUTOR_PHONE TEXT,
                $TUTOR_EMAIL TEXT,
                $TUTOR_STUDENTS TEXT
            )
        """.trimIndent()

        // Student Table (Simplified)
        val createStudentTable = """
            CREATE TABLE $STUDENT_TABLE (
                $STUDENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $STUDENT_NAME TEXT,
                $STUDENT_EMAIL TEXT,
                $STUDENT_TUTORS TEXT
            )
        """.trimIndent()

        db.execSQL(createTutorTable)
        db.execSQL(createStudentTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TUTOR_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $STUDENT_TABLE")
        onCreate(db)
    }

    // ───────────────────────────────
    // Tutor Functions
    // ───────────────────────────────
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
            put(TUTOR_NAME, name)
            put(TUTOR_AGE_RANGE, ageRange)
            put(TUTOR_PAYMENT, payment)
            put(TUTOR_SUBJECTS, subjects)
            put(TUTOR_AVAILABILITY, availability)
            put(TUTOR_GIVES_HOMEWORK, givesHomework)
            put(TUTOR_PHONE, contactPhone)
            put(TUTOR_EMAIL, contactEmail)
            put(TUTOR_STUDENTS, students)
        }

        writableDatabase.use { db -> db.insert(TUTOR_TABLE, null, values) }
    }

    fun getTutors(): Cursor {
        return readableDatabase.rawQuery("SELECT * FROM $TUTOR_TABLE", null)
    }

    fun updateTutorStudents(tutorEmail: String, newStudents: String) {
        val values = ContentValues().apply {
            put(TUTOR_STUDENTS, newStudents)
        }
        writableDatabase.update(
            TUTOR_TABLE,
            values,
            "$TUTOR_EMAIL = ?",
            arrayOf(tutorEmail)
        )
    }

    fun updateStudentTutors(studentEmail: String, newTutors: String): Boolean {
        val values = ContentValues().apply {
            put(STUDENT_TUTORS, newTutors)
        }

        val rowsAffected = writableDatabase.update(
            STUDENT_TABLE,
            values,
            "$STUDENT_EMAIL = ?",
            arrayOf(studentEmail)
        )

        return rowsAffected > 0
    }

    // ───────────────────────────────
    // Student Functions
    // ───────────────────────────────
    fun addStudent(name: String, email: String) {
        val values = ContentValues().apply {
            put(STUDENT_NAME, name)
            put(STUDENT_EMAIL, email)
            put(STUDENT_TUTORS,"")
        }

        writableDatabase.use { db -> db.insert(STUDENT_TABLE, null, values) }
    }

    fun getStudents(): Cursor {
        return readableDatabase.rawQuery("SELECT * FROM $STUDENT_TABLE", null)
    }

    companion object {
        private const val DATABASE_NAME = "TutorFinderApp.db"
        private const val DATABASE_VERSION = 3 // incremented since student schema changed

        // Tutor table
        const val TUTOR_TABLE = "tutors"
        const val TUTOR_ID = "id"
        const val TUTOR_NAME = "name"
        const val TUTOR_AGE_RANGE = "ageRange"
        const val TUTOR_PAYMENT = "payment"
        const val TUTOR_SUBJECTS = "subjects"
        const val TUTOR_AVAILABILITY = "availability"
        const val TUTOR_GIVES_HOMEWORK = "givesHomework"
        const val TUTOR_PHONE = "contactPhone"
        const val TUTOR_EMAIL = "contactEmail"
        const val TUTOR_STUDENTS = "students"

        // Student table (simplified)
        const val STUDENT_TABLE = "students"
        const val STUDENT_ID = "id"
        const val STUDENT_NAME = "name"
        const val STUDENT_EMAIL = "email"

        const val STUDENT_TUTORS = "tutors"
    }
    fun getTutorByEmail(email: String): Cursor {
        return readableDatabase.rawQuery(
            "SELECT * FROM $TUTOR_TABLE WHERE $TUTOR_EMAIL = ?",
            arrayOf(email)
        )
    }

    fun updateTutor(
        email: String,
        name: String,
        ageRange: String,
        payment: String,
        subjects: String,
        availability: String,
        givesHomework: String,
        phone: String,
        students: String
    ): Boolean {
        val db = writableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TUTOR_TABLE WHERE $TUTOR_EMAIL = ?", arrayOf(email))

        if (cursor.moveToFirst()) {
            val oldName = cursor.getString(cursor.getColumnIndexOrThrow(TUTOR_NAME))
            val oldAgeRange = cursor.getString(cursor.getColumnIndexOrThrow(TUTOR_AGE_RANGE))
            val oldPayment = cursor.getString(cursor.getColumnIndexOrThrow(TUTOR_PAYMENT))
            val oldSubjects = cursor.getString(cursor.getColumnIndexOrThrow(TUTOR_SUBJECTS))
            val oldAvailability = cursor.getString(cursor.getColumnIndexOrThrow(TUTOR_AVAILABILITY))
            val oldHomework = cursor.getString(cursor.getColumnIndexOrThrow(TUTOR_GIVES_HOMEWORK))
            val oldPhone = cursor.getString(cursor.getColumnIndexOrThrow(TUTOR_PHONE))
            val oldStudents = cursor.getString(cursor.getColumnIndexOrThrow(TUTOR_STUDENTS))

            val values = ContentValues()
            values.put(TUTOR_NAME, if (name.isNotBlank()) name else oldName)
            values.put(TUTOR_AGE_RANGE, if (ageRange.isNotBlank()) ageRange else oldAgeRange)
            values.put(TUTOR_PAYMENT, if (payment.isNotBlank()) payment else oldPayment)
            values.put(TUTOR_SUBJECTS, if (subjects.isNotBlank()) subjects else oldSubjects)
            values.put(TUTOR_AVAILABILITY, if (availability.isNotBlank()) availability else oldAvailability)
            values.put(TUTOR_GIVES_HOMEWORK, if (givesHomework.isNotBlank()) givesHomework else oldHomework)
            values.put(TUTOR_PHONE, if (phone.isNotBlank()) phone else oldPhone)
            values.put(TUTOR_STUDENTS, if (students.isNotBlank()) students else oldStudents)

            val rows = db.update(TUTOR_TABLE, values, "$TUTOR_EMAIL = ?", arrayOf(email))
            cursor.close()
            db.close()
            return rows > 0
        }

        cursor.close()
        db.close()
        return false
    }


    // Helper: get student name by email (returns null if not found)
    fun getStudentNameByEmail(email: String): String? {
        val cursor = readableDatabase.rawQuery(
            "SELECT $STUDENT_NAME FROM $STUDENT_TABLE WHERE $STUDENT_EMAIL = ?",
            arrayOf(email)
        )
        var name: String? = null
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow(STUDENT_NAME))
        }
        cursor.close()
        return name
    }

    // Optional: get whole tutor object as a simple map (convenience)
    fun getTutorMapByEmail(email: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val c = getTutorByEmail(email)
        if (c.moveToFirst()) {
            map[TUTOR_NAME] = c.getString(c.getColumnIndexOrThrow(TUTOR_NAME)) ?: ""
            map[TUTOR_AGE_RANGE] = c.getString(c.getColumnIndexOrThrow(TUTOR_AGE_RANGE)) ?: ""
            map[TUTOR_PAYMENT] = c.getString(c.getColumnIndexOrThrow(TUTOR_PAYMENT)) ?: ""
            map[TUTOR_SUBJECTS] = c.getString(c.getColumnIndexOrThrow(TUTOR_SUBJECTS)) ?: ""
            map[TUTOR_AVAILABILITY] = c.getString(c.getColumnIndexOrThrow(TUTOR_AVAILABILITY)) ?: ""
            map[TUTOR_GIVES_HOMEWORK] = c.getString(c.getColumnIndexOrThrow(TUTOR_GIVES_HOMEWORK)) ?: ""
            map[TUTOR_PHONE] = c.getString(c.getColumnIndexOrThrow(TUTOR_PHONE)) ?: ""
            map[TUTOR_EMAIL] = c.getString(c.getColumnIndexOrThrow(TUTOR_EMAIL)) ?: ""
            map[TUTOR_STUDENTS] = c.getString(c.getColumnIndexOrThrow(TUTOR_STUDENTS)) ?: ""
        }
        c.close()
        return map
    }

    fun String.splitStringintoArray(): List<String> {
        return this.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    fun studentsofTutor(email : String): List<Pair<String, String>>  {
        val studentsList = mutableListOf<Pair<String, String>>()
        val tutorCursor =
            getTutorByEmail(email)

        val studentEmailString = tutorCursor.getString(tutorCursor.getColumnIndexOrThrow(TUTOR_STUDENTS))
        val studentEmailarray = studentEmailString.splitStringintoArray()
        for(email in studentEmailarray){
            studentsList.add(Pair(email, getStudentNameByEmail(email)) as Pair<String, String>)
        }
        tutorCursor.close()

        return studentsList
    }

}
