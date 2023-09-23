import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasechat.activity.LawyerDetails
import com.example.firebasechat.databinding.CardviewBinding

class LawyerAdapter(private val lawyerList: List<LawyerDetails>) :
    RecyclerView.Adapter<LawyerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lawyer = lawyerList[position]

        // Bind lawyer data to the ViewHolder
        holder.bind(lawyer)

        // Set a click listener for opening the dialer
        holder.itemView.setOnClickListener {
            val phoneNumber = lawyer.contact
            openDialer(holder.itemView.context, phoneNumber)
        }
    }

    override fun getItemCount(): Int {
        return lawyerList.size
    }

    inner class ViewHolder(private val binding: CardviewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(lawyer: LawyerDetails) {
            binding.lawyername.text = lawyer.name
            binding.lawyerfirmname.text = lawyer.firmName
            binding.lawyerConatctinfo.text = lawyer.contact
            binding.lawyerpracticearea.text = lawyer.practiceArea
        }
    }

    private fun openDialer(context: Context, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Handle the case where the dialer app is not installed or supported.
            // You can show a message to the user indicating that they need to set up a dialer or take appropriate action.
            Log.e("Dialer", "Dialer app not found")
        }

    }
}
