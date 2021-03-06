package ru.deliveon.lists.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Objects;

import ru.deliveon.lists.GlideApp;
import ru.deliveon.lists.R;

public class BigPhotoFragment extends DialogFragment {
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = getDialog().getWindow();
            Objects.requireNonNull(window).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = Objects.requireNonNull(getActivity()).getLayoutInflater().inflate(R.layout.big_foto, null);
        final ImageView imageViewBigPhoto = view.findViewById(R.id.imageViewBigPhoto);

        Uri uri = null;
        if (getArguments() != null) {
            uri = Uri.parse(getArguments().getString("url"));
        }

        GlideApp.with(getActivity())
                .load(uri)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .error(R.drawable.no_photo)
                .into(imageViewBigPhoto);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(view);
        return builder.create();
    }
}
