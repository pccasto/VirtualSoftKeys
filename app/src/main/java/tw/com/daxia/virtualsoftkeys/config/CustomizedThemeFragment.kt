package tw.com.daxia.virtualsoftkeys.config

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tw.com.daxia.virtualsoftkeys.MainActivity
import tw.com.daxia.virtualsoftkeys.common.Link
import tw.com.daxia.virtualsoftkeys.common.SPFManager
import tw.com.daxia.virtualsoftkeys.databinding.FragmentCustomizedthemeBinding


class CustomizedThemeFragment : Fragment() {

    private val TAG = "CustomizedThemeFragment"

    private var _binding: FragmentCustomizedthemeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): CustomizedThemeFragment {
            return CustomizedThemeFragment()
        }
    }

    private lateinit var mainActivity: MainActivity


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
            _binding = FragmentCustomizedthemeBinding.inflate(inflater,
            container,
            false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        initSharedConfig()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    /*override fun onAttach(context: Context?) {
        Log.d(TAG, "onAttach")
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }*/


    private fun initSharedConfig() {
        //revert home long click
        val action = SPFManager.getHomeLongClickStartAction(activity)
        when (action) {
            Link.GOOGLE_APP_PACKAGE_NAME ->
                binding.RBLongclickActionGoogle.isChecked = true
            Intent.ACTION_VOICE_COMMAND ->
                binding.RBLongclickActionGoogleAssistant.isChecked = true
        }
        //set home long click  listener
        binding.RGLongclickAction.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.RBLongclickActionGoogle.id -> {
                    SPFManager.setHomeLongClickStartAction(activity, Link.GOOGLE_APP_PACKAGE_NAME)
                }
                binding.RBLongclickActionGoogleAssistant.id -> {
                    SPFManager.setHomeLongClickStartAction(activity, Intent.ACTION_VOICE_COMMAND)
                }
            }
        }
    }


}