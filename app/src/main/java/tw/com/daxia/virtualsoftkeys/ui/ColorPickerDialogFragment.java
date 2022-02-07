package tw.com.daxia.virtualsoftkeys.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

import tw.com.daxia.virtualsoftkeys.R;

/**
 * Created by daxia on 2016/11/25.
 */

public class ColorPickerDialogFragment extends DialogFragment implements View.OnClickListener {


    public interface colorPickerCallback {
        void onColorChange(int colorCode);
    }

    private int oldColor;

    private ColorPicker picker;

    private colorPickerCallback callback;


    public static ColorPickerDialogFragment newInstance(int oldColor) {
        Bundle args = new Bundle();
        ColorPickerDialogFragment fragment = new ColorPickerDialogFragment();
        args.putInt("oldColor", oldColor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        final Window window = dialog.getWindow();
        if (window != null) {
            window.requestFeature(Window.FEATURE_NO_TITLE);
        }
        if (getArguments() != null) {
            oldColor = getArguments().getInt("oldColor", Color.BLACK);
        }
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Dialog dialog = this.getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(true);
        }
        View rootView = inflater.inflate(R.layout.dialog_color_picker, container);

        picker = rootView.findViewById(R.id.picker);
        Button But_setting_change_color, But_setting_cancel;
        SVBar svBar = rootView.findViewById(R.id.svbar);
        OpacityBar opacitybar =  rootView.findViewById(R.id.opacitybar);
        But_setting_change_color = rootView.findViewById(R.id.But_setting_change_color);
        But_setting_cancel = rootView.findViewById(R.id.But_setting_cancel);

        picker.addSVBar(svBar);
        picker.addOpacityBar(opacitybar);
        picker.setOldCenterColor(oldColor);
        picker.setColor(oldColor);

        But_setting_change_color.setOnClickListener(this);
        But_setting_cancel.setOnClickListener(this);
        return rootView;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = (colorPickerCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context
                    + " must implement colorPickerCallback");
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.But_setting_change_color:
                callback.onColorChange(picker.getColor());
                dismiss();
                break;
            case R.id.But_setting_cancel:
                dismiss();
                break;
        }


    }

}