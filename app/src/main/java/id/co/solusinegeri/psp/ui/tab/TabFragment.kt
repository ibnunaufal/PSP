package id.co.solusinegeri.psp.ui.tab

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.co.solusinegeri.psp.R

class TabFragment : Fragment() {

    companion object {
        fun newInstance() = TabFragment()
    }

    private lateinit var viewModel: TabViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.tab_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TabViewModel::class.java)
        // TODO: Use the ViewModel
    }

}