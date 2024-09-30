package com.example.ead.fragments.Shopping


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ead.R

/**
 * A simple [Fragment] subclass.
 * Use the [CompletedOrderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CompletedOrderFragment : Fragment() {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_completed_order, container, false)
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CompletedOrderFragment.
         */
        fun newInstance(param1: String, param2: String): CompletedOrderFragment {
            val fragment = CompletedOrderFragment()
            val args = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
