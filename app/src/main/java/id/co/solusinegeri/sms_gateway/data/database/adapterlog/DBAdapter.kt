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
            db.execSQL("CREATE TABLE $TABLE_OFFLINEGATEWAY (_id INTEGER PRIMARY KEY AUTOINCREMENT, $COL_ACCOUNT_NAME TEXT, $COL_CATEGORY TEXT,$COL_TITLE TEXT,$COL_phone TEXT,$COL_STATUS TEXT);")
        }

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (db != null) {
            db.execSQL("DROP TABLE $TABLE_OFFLINEGATEWAY;")
        }
    }
    inner class TableOfflineAttendance {
        fun addAttoffline(accountName: String, category: String,title: String,phone : String,status : String) {
            val db = writableDatabase;
            val newContentoffline = ContentValues()
            newContentoffline.put(COL_ACCOUNT_NAME, accountName)
            newContentoffline.put(COL_CATEGORY, category)
            newContentoffline.put(COL_TITLE, title)
            newContentoffline.put(COL_phone, phone)
            newContentoffline.put(COL_STATUS, status)
            db.insert(TABLE_OFFLINEGATEWAY, null, newContentoffline)
        }
        fun deleteData(id: String): Int {
            val db = writableDatabase
            return db.delete(TABLE_OFFLINEGATEWAY, "_id = ?", arrayOf(id))
        }
        fun getAlltrxoffline(): ArrayList<ContentLog> {
            val db = writableDatabase
            val res = db.rawQuery("SELECT * FROM $TABLE_OFFLINEGATEWAY", null)
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
        val COL_CATEGORY = "category"
        val COL_TITLE = "title"
        val COL_phone = "phone"
        val COL_STATUS = "status"


    }
}