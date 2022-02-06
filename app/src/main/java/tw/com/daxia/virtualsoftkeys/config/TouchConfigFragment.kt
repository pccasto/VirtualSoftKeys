package tw.com.daxia.virtualsoftkeys.config

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.SeekBar
import tw.com.daxia.virtualsoftkeys.MainActivity
import tw.com.daxia.virtualsoftkeys.R
import tw.com.daxia.virtualsoftkeys.common.SPFManager
import tw.com.daxia.virtualsoftkeys.common.ScreenHepler
import tw.com.daxia.virtualsoftkeys.common.ThemeHelper
import tw.com.daxia.virtualsoftkeys.databinding.FragmentTouchconfigBinding
import tw.com.daxia.virtualsoftkeys.service.ServiceFloating

class TouchConfigFragment : Fragment() {

    private val TAG = "TouchConfigFragment"

    private var _binding: FragmentTouchconfigBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): TouchConfigFragment {
            return TouchConfigFragment()
        }
    }

    /*
     * Config
     */
    private var screenWidth: Int = 0
    private lateinit var mainActivity: MainActivity

    /**
     * height listener
     */
    private val touchviewHeightSeekBarListener = object : SeekBar.OnSeekBarChangeListener {

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            if (mainActivity.isPortrait) {
                SPFManager.setTouchviewPortraitHeight(mainActivity, seekBar.progress)
            } else {
                SPFManager.setTouchviewLandscapeHeight(mainActivity, seekBar.progress)
            }
            var mAccessibilityService: ServiceFloating? = ServiceFloating.getSharedInstance()
            if (mAccessibilityService != null) {
                mAccessibilityService.updateTouchView(seekBar.progress, null, null)
                mAccessibilityService = null
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {

        }

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            val params = mainActivity.ViewTouchviewer.layoutParams as RelativeLayout.LayoutParams
            params.height = progress
            mainActivity.ViewTouchviewer.layoutParams = params
        }
    }

    /**
     * width listener
     */
    private val touchviewWidthSeekBarListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onStopTrackingTouch(seekBar: SeekBar) {
            //make touch view position rollback to default
            updateTouchViewPosition(seekBar.progress, -1)
            if (mainActivity.isPortrait) {
                SPFManager.setTouchviewPortraitPosition(mainActivity, binding.SeekTouchAreaPosition.progress)
            } else {
                SPFManager.setTouchviewLandscapePosition(mainActivity, binding.SeekTouchAreaPosition.progress)
            }
            var mAccessibilityService: ServiceFloating? = ServiceFloating.getSharedInstance()
            if (seekBar.progress == seekBar.max) {
                if (mainActivity.isPortrait) {
                    SPFManager.setTouchviewPortraitWidth(mainActivity, ViewGroup.LayoutParams.MATCH_PARENT)
                } else {
                    SPFManager.setTouchviewLandscapeWidth(mainActivity, ViewGroup.LayoutParams.MATCH_PARENT)

                }
                if (mAccessibilityService != null) {
                    mAccessibilityService.updateTouchView(null, ViewGroup.LayoutParams.MATCH_PARENT, binding.SeekTouchAreaPosition.progress)
                    mAccessibilityService = null
                }
            } else {
                if (mainActivity.isPortrait) {
                    SPFManager.setTouchviewPortraitWidth(mainActivity, seekBar.progress)
                } else {
                    SPFManager.setTouchviewLandscapeWidth(mainActivity, seekBar.progress)
                }
                if (mAccessibilityService != null) {
                    mAccessibilityService.updateTouchView(
                        null,
                        seekBar.progress,
                        binding.SeekTouchAreaPosition.progress
                    )
                    mAccessibilityService = null
                }
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {

        }

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            val params = mainActivity.ViewTouchviewer.layoutParams as RelativeLayout.LayoutParams
            params.width = progress
            mainActivity.ViewTouchviewer.layoutParams = params
        }
    }

    /**
     * Position listener
     */
    private val touchviewPositionSeekBarListener = object : SeekBar.OnSeekBarChangeListener {

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            if (mainActivity.isPortrait) {
                SPFManager.setTouchviewPortraitPosition(mainActivity, seekBar.progress)
            } else {
                SPFManager.setTouchviewLandscapePosition(mainActivity, seekBar.progress)
            }
            val mAccessibilityService = ServiceFloating.getSharedInstance()
            mAccessibilityService?.updateTouchView(null, null, seekBar.progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {

        }

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            val params = mainActivity.ViewTouchviewer.layoutParams as RelativeLayout.LayoutParams
            params.marginStart = progress
            mainActivity.ViewTouchviewer.layoutParams = params
        }
    }


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        Log.d(TAG,"onCreateView")
        _binding = FragmentTouchconfigBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG,"onViewCreated")
        initSeekBar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    /*override fun onAttach(context: Context?) {
        Log.d(TAG,"onAttach")
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }*/

    /*
     * To update touch view position or revert value form spf
     */
    private fun updateTouchViewPosition(toughviewWidth: Int, positionFromSPF: Int) {
        binding.SeekTouchAreaPosition.max = screenWidth - toughviewWidth
        if (positionFromSPF >= 0) {
            binding.SeekTouchAreaPosition.progress = positionFromSPF
        } else {
            //Default is in center horizontal
            binding.SeekTouchAreaPosition.progress = screenWidth / 2 - toughviewWidth / 2
        }
    }

    private fun initSeekBar() {
        initSeekBarStyle()
        initSeekBarListener()
        initSeekBarContent()
    }

    private fun initSeekBarStyle() {
        val configColor: Int
        if (mainActivity.isPortrait) {
            binding.TVConfigName.text = getString(R.string.config_name_portrait)
            configColor = ThemeHelper.getColorResource(mainActivity, R.color.config_portrait_color)
        } else {
            binding.TVConfigName.text = getString(R.string.config_name_landscape)

            configColor = ThemeHelper.getColorResource(mainActivity, R.color.config_landscape_color)
        }
        binding.TVConfigName.setTextColor(configColor)
        binding.SeekTouchAreaHeight.progressDrawable.colorFilter = PorterDuffColorFilter(configColor, PorterDuff.Mode.SRC_IN)
        binding.SeekTouchAreaHeight.thumb.colorFilter = PorterDuffColorFilter(configColor, PorterDuff.Mode.SRC_IN)
        binding.SeekTouchAreaWidth.progressDrawable.colorFilter = PorterDuffColorFilter(configColor, PorterDuff.Mode.SRC_IN)
        binding.SeekTouchAreaWidth.thumb.colorFilter = PorterDuffColorFilter(configColor, PorterDuff.Mode.SRC_IN)
        binding.SeekTouchAreaPosition.progressDrawable.colorFilter = PorterDuffColorFilter(configColor, PorterDuff.Mode.SRC_IN)
        binding.SeekTouchAreaPosition.thumb.colorFilter = PorterDuffColorFilter(configColor, PorterDuff.Mode.SRC_IN)
    }

    private fun initSeekBarListener(){
        binding.SeekTouchAreaHeight.setOnSeekBarChangeListener(touchviewHeightSeekBarListener)
        binding.SeekTouchAreaHeight.isSaveEnabled = false

        binding.SeekTouchAreaWidth.setOnSeekBarChangeListener(touchviewWidthSeekBarListener)
        binding.SeekTouchAreaWidth.isSaveEnabled = false

        binding.SeekTouchAreaPosition.setOnSeekBarChangeListener(touchviewPositionSeekBarListener)
        binding.SeekTouchAreaPosition.isSaveEnabled = false
    }

    private fun initSeekBarContent() {
        //Default
        val screenHeight = ScreenHepler.getScreenHeight(mainActivity)
        this.screenWidth = ScreenHepler.getScreenWidth(mainActivity)
        val touchviewWidth: Int
        //Default Height init
        binding.SeekTouchAreaHeight.max = screenHeight / MainActivity.MAX_HEIGHT_PERCENTAGE

        //Default width init
        binding.SeekTouchAreaWidth.max = screenWidth

        if (mainActivity.isPortrait) {
            touchviewWidth = SPFManager.getTouchviewPortraitWidth(mainActivity)
            //Height
            binding.SeekTouchAreaHeight.progress = SPFManager.getTouchviewPortraitHeight(mainActivity)
            //position  + Width
            //For match content
            if (touchviewWidth == ScreenHepler.getDefautlTouchviewWidth()) {
                binding.SeekTouchAreaWidth.progress = screenWidth
                //set position
                updateTouchViewPosition(screenWidth, SPFManager.getTouchviewPortraitPosition(mainActivity))
            } else {
                binding.SeekTouchAreaWidth.progress = touchviewWidth
                //set position
                updateTouchViewPosition(touchviewWidth, SPFManager.getTouchviewPortraitPosition(mainActivity))
            }
        } else {
            touchviewWidth = SPFManager.getTouchviewLandscapeWidth(mainActivity)
            //Height
            binding.SeekTouchAreaHeight.progress = SPFManager.getTouchviewLandscapeHeight(mainActivity)
            //position  + Width
            //For match content
            if (touchviewWidth == ScreenHepler.getDefautlTouchviewWidth()) {
                binding.SeekTouchAreaHeight.progress = screenWidth
                //set position
                updateTouchViewPosition(screenWidth, SPFManager.getTouchviewLandscapePosition(mainActivity))
            } else {
                binding.SeekTouchAreaWidth.progress = touchviewWidth
                //set position
                updateTouchViewPosition(touchviewWidth, SPFManager.getTouchviewLandscapePosition(mainActivity))
            }
        }


    }
}