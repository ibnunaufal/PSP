package id.co.solusinegeri.psp.ui.home

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.co.solusinegeri.psp.R
import id.co.solusinegeri.psp.data.responses.ContentLog
import kotlinx.android.synthetic.main.item_log.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class RecycleViewLog (
    private val context: Context,
    private var resultTransaction: ArrayList<ContentLog>
) : RecyclerView.Adapter<RecycleViewLog.ViewHolderTransaction>() {

    private val TAG = javaClass.simpleName

    companion object {
        private const val VIEW_TYPE_DATA = 0;
        private const val VIEW_TYPE_PROGRESS = 1;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTransaction {
        return when (viewType) {
            VIEW_TYPE_DATA -> {//inflates row layout
                val view = LayoutInflater.from(parent?.context)
                    .inflate(R.layout.item_log, parent, false)
                ViewHolderTransaction(view)
            }
            VIEW_TYPE_PROGRESS -> {//inflates progressbar layout
                val view = LayoutInflater.from(parent?.context)
                    .inflate(R.layout.item_loading, parent, false)
                ViewHolderTransaction(view)
            }
            else -> throw IllegalArgumentException("Different View type")
        }
    }

//    override fun getItemCount(): StringBuffer = resultTransaction
//    fun refreshAdapter(resultTransaction: StringBuffer) {
//        this.resultTransaction = resultTransaction
//        notifyItemRangeChanged(0, this.resultTransaction.size)
//    }

    override fun getItemViewType(position: Int): Int {
        return if (resultTransaction[position] == null) {
            VIEW_TYPE_PROGRESS
        } else {
            VIEW_TYPE_DATA
        }
    }

//    fun clearData(resultTransaction: StringBuffer){
//        this.resultTransaction.notifyAll()
//        notifyItemRangeChanged(0, this.resultTransaction)
//    }


    inner class ViewHolderTransaction(itemView: View?) : RecyclerView.ViewHolder(itemView!!)



    fun convertISOTimeToDate(isoTime: String): String? {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        var convertedDate: Date? = null
        var formattedDate: String? = null
        try {
            convertedDate = sdf.parse(isoTime)
            formattedDate = SimpleDateFormat("dd-MM-yyyy HH:mm").format(convertedDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return formattedDate
    }

    override fun onBindViewHolder(holder: RecycleViewLog.ViewHolderTransaction, position: Int) {
        if (holder.itemViewType == VIEW_TYPE_DATA) {

            val resultItem = resultTransaction[position]
            val name = resultItem.accountName
            try {
                holder.itemView.txt_item_name.text = resultItem.accountName
                holder.itemView.txt_item_category.text = resultItem.category
                holder.itemView.txt_item_title.text = resultItem.title
                holder.itemView.txt_item_phone.text = resultItem.phone
                holder.itemView.txt_datetime.text = resultItem.dateTime
                holder.itemView.txt_status.text = resultItem.status
                var x = resultItem.status
                if(x != "SMS_DELIVERED" && x  != "SMS_SENT"){
                    holder.itemView.txt_status.setTextColor(Color.parseColor("#ff0000"))
                }
            }catch (e : Exception){
            }
//            holder.itemView.clickitem.setOnClickListener{
//                val activity = holder.itemView.context as? NotifikasiActivity
//                Log.d("Data :", resultItem.dateAttendance)
//                val args = Bundle()
//                args.putString("id",resultItem.id)
//                args.putString("nfcId",resultItem.nfcid)
//                args.putString("date",resultItem.dateAttendance)
//                val newFragment: DialogFragment = DialogofflinekFragment()
//                newFragment.setArguments(args)
//                if (activity != null) {
//                    newFragment.show(activity.supportFragmentManager, "BottomSheetDialog")
//                }
//            }
        }

    }



    override fun getItemCount(): Int = resultTransaction.size


}