package com.example.allench.googleimagesearcher.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.allench.googleimagesearcher.R;
import com.example.allench.googleimagesearcher.models.QueryParam;

public class SettingsDialogFragment extends DialogFragment {

    public interface OnButtonClickListener {
        public void onButtonApplyClick(QueryParam param);
    }

    private OnButtonClickListener mListener;
    private View mView;
    private AlertDialog mDialog;
    private QueryParam mQueryParam;
    private Spinner spImageSize;
    private Spinner spImageType;
    private Spinner spColorFilter;
    private EditText etSiteFilter;

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        mListener = listener;
    }

    public void initQueryParam(QueryParam param) {
        mQueryParam = param;
    }

    private void setQueryParam() {
        spImageSize.setSelection(((ArrayAdapter) spImageSize.getAdapter()).getPosition(mQueryParam.imgsz));
        spImageType.setSelection(((ArrayAdapter) spImageType.getAdapter()).getPosition(mQueryParam.imgtype));
        spColorFilter.setSelection(((ArrayAdapter) spColorFilter.getAdapter()).getPosition(mQueryParam.imgcolor));
        etSiteFilter.setText(mQueryParam.as_sitesearch);
    }

    private QueryParam getQueryParam() {
        QueryParam param = new QueryParam();

        param.imgsz = spImageSize.getSelectedItemPosition() > 0 ? spImageSize.getSelectedItem().toString() : "";
        param.imgtype = spImageType.getSelectedItemPosition() > 0 ? spImageType.getSelectedItem().toString() : "";
        param.imgcolor = spColorFilter.getSelectedItemPosition() > 0 ? spColorFilter.getSelectedItem().toString() : "";
        param.as_sitesearch = etSiteFilter.getText().toString();

        return param;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        // ViewHolder
        mView = inflater.inflate(R.layout.fragment_settings_dialog, null);
        spImageType = (Spinner) mView.findViewById(R.id.spImageType);
        spImageSize = (Spinner) mView.findViewById(R.id.spImageSize);
        spColorFilter = (Spinner) mView.findViewById(R.id.spColorFilter);
        etSiteFilter = (EditText) mView.findViewById(R.id.etSiteFilter);

        // setup fields
        setQueryParam();

        // setupView
        builder.setView(mView);
        builder.setTitle("Restricts Settings:");

        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (mListener != null) {
                    mListener.onButtonApplyClick(getQueryParam());
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        mDialog = builder.create();

        // while press Enter on EditText, focus on APPLY button
        etSiteFilter.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    mDialog.getButton(DialogInterface.BUTTON_POSITIVE).requestFocus();
                }
                return false;
            }
        });

        return mDialog;
    }

}
