package mobileapp.ctemplar.com.ctemplarapp.main;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import mobileapp.ctemplar.com.ctemplarapp.R;

public class UpgradeToPrimeFragment extends DialogFragment {

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(getActivity(), R.style.DialogAnimation);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_upgrade_to_prime_dialog, container, false);

        TextView closeDialog = view.findViewById(R.id.fragment_upgrade_to_prime_dialog_not_now);
        closeDialog.setOnClickListener(v -> dismiss());

        TextView moreInformation = view.findViewById(R.id.fragment_upgrade_to_prime_dialog_more);
        moreInformation.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_prime_more)));
            startActivity(browserIntent);
        });

        Button upgradeButton = view.findViewById(R.id.fragment_upgrade_to_prime_dialog_upgrade);
        upgradeButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_prime_upgrade)));
            startActivity(browserIntent);
        });

        return view;
    }
}
