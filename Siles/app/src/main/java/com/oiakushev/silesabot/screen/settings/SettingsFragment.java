package com.oiakushev.silesabot.screen.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.oiakushev.silesabot.R;
import com.oiakushev.silesabot.model.datamodel.PerceptronModel;
import com.oiakushev.silesabot.screen.ExtendedFragment;
import com.oiakushev.silesabot.screen.reaction.list.ListReactionFragment;
import com.oiakushev.silesabot.tool.SettingsStorage;

import java.io.IOException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SettingsFragment extends ExtendedFragment {
    private static final String TAG = PerceptronModel.class.getName();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private CheckBox voiceEnabledCheckBox;
    private CheckBox nameInMessageCheckBox;
    private Button learnSettingsButton, editDataSettingsButton, applySettingsButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        voiceEnabledCheckBox = v.findViewById(R.id.voiceEnabledCheckBox);
        nameInMessageCheckBox = v.findViewById(R.id.nameInMessageCheckBox);
        applySettingsButton = v.findViewById(R.id.applySettingsButton);
        learnSettingsButton = v.findViewById(R.id.learnSettingsButton);
        editDataSettingsButton = v.findViewById(R.id.editDataSettingsButton);

        applySettingsButton.setOnClickListener(view -> doApplySettingsAndBack());
        learnSettingsButton.setOnClickListener(view -> doLearn());
        editDataSettingsButton.setOnClickListener(view -> {
            FragmentManager fm = this.getMainActivity().getSupportFragmentManager();

            fm.beginTransaction()
                    .replace(R.id.mainFragmentContainer, new ListReactionFragment())
                    .addToBackStack(ListReactionFragment.class.getName())
                    .commit();
        });

        try {
            SettingsStorage.Instance.load(getMainActivity());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        voiceEnabledCheckBox.setChecked(SettingsStorage.Instance.isVoiceEnabled());
        nameInMessageCheckBox.setChecked(SettingsStorage.Instance.isNameInMessage());

        return v;
    }

    private void disableViews() {
        voiceEnabledCheckBox.setEnabled(false);
        nameInMessageCheckBox.setEnabled(false);
        applySettingsButton.setEnabled(false);
        learnSettingsButton.setEnabled(false);
        editDataSettingsButton.setEnabled(false);
    }

    private void enableViews() {
        voiceEnabledCheckBox.setEnabled(true);
        nameInMessageCheckBox.setEnabled(true);
        applySettingsButton.setEnabled(true);
        learnSettingsButton.setEnabled(true);
        editDataSettingsButton.setEnabled(true);
    }

    private void doApplySettingsAndBack() {
        disableViews();

        SettingsStorage.Instance.setVoiceEnabled(voiceEnabledCheckBox.isChecked());
        SettingsStorage.Instance.setNameInMessage(nameInMessageCheckBox.isChecked());

        try {
            SettingsStorage.Instance.save(getMainActivity());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            Toast.makeText(getMainActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            enableViews();

            getMainActivity().onBackPressed();
        }
    }

    private void doLearn() {
        disableViews();

        compositeDisposable.add(
                getMainActivity().getDataController().rxLearn()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                (result) -> {
                                    enableViews();
                                    Toast.makeText(getMainActivity(), R.string.done_text, Toast.LENGTH_LONG).show();
                                },
                                (error) -> {
                                    enableViews();
                                    Log.e(TAG, error.getMessage(), error);
                                    Toast.makeText(getMainActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                        )
        );
    }
}