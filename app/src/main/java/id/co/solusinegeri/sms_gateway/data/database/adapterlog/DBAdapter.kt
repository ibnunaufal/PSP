package id.co.solusinegeri.sms_gateway.data.database.adapterlog

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import id.co.solusinegeri.sms_gateway.data.responses.ContentLog

class DbAdapter(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        if (db != null) {
            db.execSQL("CREATE TABLE $TABLE_OFFLINEGATEWAY (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COL_ID_NOTIF TEXT, " +
                    "$COL_ACCOUNT_ID TEXT, " +
                    "$COL_ACCOUNT_NAME TEXT, " +
                    "$COL_CATEGORY TEXT," +
                    "$COL_TITLE TEXT," +
                    "$COL_MESSAGE TEXT," +
                    "$COL_PHONE TEXT," +
                    "$COL_DATETIME TEXT);")
        }

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (db != null) {
            db.execSQL("DROP TABLE $TABLE_OFFLINEGATEWAY;")
        }
    }
    inner class TableOfflineAttendance {
        fun addAttoffline(
            idNotif:String, accountId: String, accountName: String, category: String,
            title: String,message: String,phone : String, dateTime: String
        ) {
            val db = writableDatabase;
            val newContentoffline = ContentValues()
            newContentoffline.put(COL_ID_NOTIF, idNotif)
            newContentoffline.put(COL_ACCOUNT_ID, accountId)
            newContentoffline.put(COL_ACCOUNT_NAME, accountName)
            newContentoffline.put(COL_CATEGORY, category)
            newContentoffline.put(COL_TITLE, title)
            newContentoffline.put(COL_MESSAGE, message)
            newContentoffline.put(COL_PHONE, phone)
            newContentoffline.put(COL_DATETIME, dateTime)
            db.insert(TABLE_OFFLINEGATEWAY, null, newContentoffline)
        }
        fun deleteData(id: String): Int {
            val db = writableDatabase
            return db.delete(TABLE_OFFLINEGATEWAY, "id = ?", arrayOf(id))
        }
        fun deleteAll(){
            val db = writableDatabase
            db.execSQL("DELETE FROM $TABLE_OFFLINEGATEWAY")
        }
        fun getAlltrxoffline(): ArrayList<ContentLog> {
            val db = writableDatabase
            val res = db.rawQuery("SELECT * FROM $TABLE_OFFLINEGATEWAY ORDER BY $COL_DATETIME DESC", null)
            val useList = ArrayList<ContentLog>()
            if (res.moveToFirst()) {
                while (!res.isAfterLast()) {
                    val model =
                        ContentLog(
                            res.getString(0),
                            res.getString(1),
                            res.getString(2),
                            res.getString(3),
                            res.getString(4),
                            res.getString(5),
                            res.getString(6),
                            res.getString(7),
                            res.getString(8),
                        )

                    useList.add(model)
                    res.moveToNext()
                }
            }
            res.close()
            return useList
        }

    }

    companion object {
        val DATABASE_NAME = "offline.db"
        val TABLE_OFFLINEGATEWAY = "gateway"
        /**
         * Kolom pada Tabel RegisteredNFCID
         */
        val COL_ACCOUNT_NAME = "account_name"
        val COL_ACCOUNT_ID = "accountId"
        val COL_ID_NOTIF = "idNotif"
        val COL_CATEGORY = "category"
        val COL_TITLE = "title"
        val COL_MESSAGE = "message"
        val COL_PHONE = "phone"
        val COL_DATETIME = "dateTime"


    }
}