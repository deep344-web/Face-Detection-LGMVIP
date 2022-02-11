package com.example.facedetection;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class Alertfragment extends DialogFragment {

    private final String success = "Face got detected !";
    private final String failure = "Face couldn't be detected !";

    TextView textView;
    ImageView imageView;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_layout, null);

        textView = (TextView) v.findViewById(R.id.text);
        imageView = v.findViewById(R.id.thumbsSign);

        Bundle bundle = getArguments();
        Boolean faceDetected = bundle.getBoolean("faceDetected",false);

        if (!faceDetected){
            textView.setText(failure);
            imageView.setImageResource(R.drawable.ic_thumb_down);
        }
        else {

            textView.setText(success);
            imageView.setImageResource(R.drawable.ic_thumb_up);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);

        return builder.create();
    }
}
