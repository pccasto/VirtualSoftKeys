package tw.com.daxia.virtualsoftkeys.config

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import tw.com.daxia.virtualsoftkeys.MainActivity
import tw.com.daxia.virtualsoftkeys.R
import tw.com.daxia.virtualsoftkeys.common.Link.MY_GIT_HUB_URL
import tw.com.daxia.virtualsoftkeys.common.SPFManager
import tw.com.daxia.virtualsoftkeys.databinding.FragmentFunctionconfigBinding
import tw.com.daxia.virtualsoftkeys.service.ServiceFloating
import tw.com.daxia.virtualsoftkeys.ui.ColorPickerDialogFragment

class FunctionConfigFragment : Fragment(){

    private val TAG = "FunctionConfigFragment"

    companion object {
        fun newInstance(): FunctionConfigFragment {
            return FunctionConfigFragment()
        }
    }

    private var _binding: FragmentFunctionconfigBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        Log.d(TAG,"onAttach")
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
                _binding = FragmentFunctionconfigBinding.inflate(inflater,
                        container,
            false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG,"onViewCreated")
        initSharedConfig()
    }

    private fun initSharedConfig() {
        //StylusMode
        binding.CTVStylusOnlyMode.isChecked = SPFManager.getStylusOnlyMode(mainActivity)
        binding.CTVStylusOnlyMode.setOnClickListener {
            binding.CTVStylusOnlyMode.toggle()
            SPFManager.setStylusOnlyMode(mainActivity, binding.CTVStylusOnlyMode.isChecked)
            val mAccessibilityService = ServiceFloating.getSharedInstance()
            mAccessibilityService?.updateTouchViewConfigure()
        }
        //Disappear time
        initDisappearSpinner()
        //Reverse button position
        binding.CTVReverseButton.isChecked = SPFManager.getReverseFunctionButton(mainActivity)
        binding.CTVReverseButton.setOnClickListener {
            binding.CTVReverseButton.toggle()
            SPFManager.setReverseFunctionButton(mainActivity, binding.CTVReverseButton.isChecked)
            val mAccessibilityService = ServiceFloating.getSharedInstance()
            mAccessibilityService?.refreshSoftKey()
        }
        //make bar bg be transparent
        binding.IVBgColor.setImageDrawable(ColorDrawable(SPFManager.getSoftKeyBarBgGolor(mainActivity)))
        binding.IVBgColor.setOnClickListener {
            val secColorPickerFragment = ColorPickerDialogFragment.newInstance(SPFManager.getSoftKeyBarBgGolor(mainActivity))
            secColorPickerFragment.show(mainActivity.supportFragmentManager, "ColorPickerFragment")
        }
        //smart hieedn
        binding.CTVSmartHidden.isChecked = SPFManager.getSmartHidden(mainActivity)
        binding.CTVSmartHidden.setOnClickListener {
            binding.CTVSmartHidden.toggle()
            SPFManager.setSmartHidden(mainActivity, binding.CTVSmartHidden.isChecked)
            val mAccessibilityService = ServiceFloating.getSharedInstance()
            mAccessibilityService?.updateSmartHidden(binding.CTVSmartHidden.isChecked)
        }
        //hidden when rotate
        binding.CTVHiddenWhenRotate.isChecked = SPFManager.getRotateHidden(mainActivity)
        binding.CTVHiddenWhenRotate.setOnClickListener {
            binding.CTVHiddenWhenRotate.toggle()
            SPFManager.setRotateHidden(mainActivity, binding.CTVHiddenWhenRotate.isChecked)
            val mAccessibilityService = ServiceFloating.getSharedInstance()
            mAccessibilityService?.updateRotateHidden(binding.CTVHiddenWhenRotate.isChecked)
        }
        binding.IVMyGithub.setOnClickListener {
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(MY_GIT_HUB_URL))
                startActivity(browserIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initDisappearSpinner() {

        val disappearAdapter = ArrayAdapter(mainActivity, android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.bar_disappear_time))

        binding.SPBarDisappearTime.adapter = disappearAdapter
        binding.SPBarDisappearTime.setSelection(SPFManager.getDisappearPosition(mainActivity))
        binding.SPBarDisappearTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                var mAccessibilityService: ServiceFloating? = ServiceFloating.getSharedInstance()
                if (mAccessibilityService != null) {
                    mAccessibilityService.updateDisappearTime(position)
                    mAccessibilityService = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                //Do nothing
            }
        }
    }


    fun onColorChange(colorCode: Int) {
        binding.IVBgColor.setImageDrawable(ColorDrawable(colorCode))
        SPFManager.setSoftKeyBgGolor(mainActivity, colorCode)
        val mAccessibilityService = ServiceFloating.getSharedInstance()
        mAccessibilityService?.refreshSoftKey()
    }

}