package id.co.solusinegeri.psp.data.database.adapterlog

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import id.co.solusinegeri.psp.data.responses.ContentLog

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
                    "$COL_DATETIME TEXT," +
                    "$COL_SYNCED TEXT," +
                    "$COL_STATUS TEXT);")
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
            title: String,message: String,phone : String, dateTime: String, status: String
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
            newContentoffline.put(COL_SYNCED, "false")
            newContentoffline.put(COL_STATUS, status)
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
        /*
        getData SMS
        kirim satu-satu
        jika berhasil
            status SENT
        jika gagal
            status FAILED
        jika status != queue
            upload callback ke BE dengan masing2 status, set synced jadi true
         */
        fun updateStatus(idNotif: String, status: String){
            val db = writableDatabase
            db.execSQL("UPDATE $TABLE_OFFLINEGATEWAY SET $COL_STATUS = '$status' WHERE $COL_ID_NOTIF = '$idNotif'")
        }
        fun updateSynced(idNotif: String){
            val db = writableDatabase
            val tr = true
            db.execSQL("UPDATE $TABLE_OFFLINEGATEWAY SET $COL_SYNCED = '$tr' WHERE $COL_ID_NOTIF = '$idNotif'")
        }
        fun getById(idNotif: String): ArrayList<ContentLog> {
            val db = writableDatabase
            val xx = "SMS_DELIVERED"
            val xy = "SMS_SENT"
            val res = db.rawQuery("SELECT * FROM $TABLE_OFFLINEGATEWAY " +
                    "WHERE $COL_ID_NOTIF <> '$idNotif' " +
                    "ORDER BY $COL_DATETIME DESC", null)
            val useList = ArrayList<ContentLog>()
            if (res.moveToFirst()) {
                while (!res.isAfterLast) {
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
                            res.getString(9),
                            res.getString(10),
                        )

                    useList.add(model)
                    res.moveToNext()
                }
            }
            res.close()
            return useList
        }
        fun getSynced(): ArrayList<ContentLog> {
            val db = writableDatabase
            val xx = "false"
            val res = db.rawQuery("SELECT * FROM $TABLE_OFFLINEGATEWAY " +
                    "WHERE $COL_SYNCED = '$xx' " +
                    "ORDER BY $COL_DATETIME DESC", null)
            val useList = ArrayList<ContentLog>()
            if (res.moveToFirst()) {
                while (!res.isAfterLast) {
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
                            res.getString(9),
                            res.getString(10),
                        )

                    useList.add(model)
                    res.moveToNext()
                }
            }
            res.close()
            return useList
        }
        fun getAlltrxoffline(): ArrayList<ContentLog> {
            val db = writableDatabase
            val res = db.rawQuery("SELECT * FROM $TABLE_OFFLINEGATEWAY ORDER BY $COL_DATETIME DESC", null)
            val useList = ArrayList<ContentLog>()
            if (res.moveToFirst()) {
                while (!res.isAfterLast) {
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
                            res.getString(9),
                            res.getString(10),
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
        val COL_STATUS = "status"
        val COL_SYNCED = "synced"


    }
}