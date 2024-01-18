package com.oiakushev.silesabot.screen.reaction.edit;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.oiakushev.silesabot.R;
import com.oiakushev.silesabot.db.AppDatabase;
import com.oiakushev.silesabot.db.AppDatabaseHelper;
import com.oiakushev.silesabot.db.StoredReaction;
import com.oiakushev.silesabot.screen.ExtendedFragment;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class EditReactionFragment extends ExtendedFragment {
    private static String TAG = EditReactionFragment.class.getName();

    private AlertDialog alertDeleteDialog;
    private EditText questionsView;
    private EditText answersView;
    private EditText customClassView;
    private CheckBox isTeachableSwitch;
    private Button deleteReactionButton;
    private Button updateReactionButton;

    private StoredReaction reaction;
    private AppDatabase appDatabase;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_reaction, container, false);

        if (getArguments() != null) {
            reaction = getArguments().getParcelable(StoredReaction.class.getName());
        } else {
            reaction = createEmptyReaction();
        }

        appDatabase = AppDatabaseHelper.Instance.getInstance(this.getContext());

        alertDeleteDialog = createDeleteDialog();

        questionsView = v.findViewById(R.id.questionsView);
        answersView = v.findViewById(R.id.answersView);
        customClassView = v.findViewById(R.id.customClassView);
        isTeachableSwitch = v.findViewById(R.id.isTeachableSwitch);
        deleteReactionButton = v.findViewById(R.id.deleteReactionButton);
        updateReactionButton = v.findViewById(R.id.updateReactionButton);

        deleteReactionButton.setOnClickListener(view -> {
            alertDeleteDialog.show();
        });

        updateReactionButton.setOnClickListener(view -> {
            updateReactionFromUI();

            if (reaction.id != 0) {
                doUpdateReaction();
            } else {
                doInsertReaction();
            }
        });

        updateUIFromReaction();

        return v;
    }

    private AlertDialog createDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getMainActivity());
        builder.setMessage("Вы уверены что хотите удалить эту реакцию?");
        builder.setPositiveButton("Да", (dialog, id) -> {
            dialog.dismiss();

            if (reaction.id != 0) {
                doDeleteReaction();
            } else {
                Toast.makeText(getContext(), "Нельзя удалить не существующую реакцию", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Нет", (dialog, id) -> dialog.dismiss());
        return builder.create();
    }

    private void disableViews() {
        questionsView.setEnabled(false);
        answersView.setEnabled(false);
        customClassView.setEnabled(false);
        isTeachableSwitch.setEnabled(false);
        deleteReactionButton.setEnabled(false);
        updateReactionButton.setEnabled(false);
    }

    private void enableViews() {
        questionsView.setEnabled(true);
        answersView.setEnabled(true);
        customClassView.setEnabled(true);
        isTeachableSwitch.setEnabled(true);
        deleteReactionButton.setEnabled(true);
        updateReactionButton.setEnabled(true);
    }

    private StoredReaction createEmptyReaction() {
        StoredReaction newReaction = new StoredReaction();

        newReaction.questions = "";
        newReaction.answers = "";
        newReaction.customClass = "";
        newReaction.isTeachable = true;

        return newReaction;
    }

    public static EditReactionFragment createEditReactionFragment(StoredReaction storedReaction) {
        EditReactionFragment editReactionFragment = new EditReactionFragment();
        Bundle bundle = new Bundle();

        bundle.putParcelable(StoredReaction.class.getName(), storedReaction);
        editReactionFragment.setArguments(bundle);

        return editReactionFragment;
    }

    private void updateReactionFromUI() {
        reaction.questions = questionsView.getText().toString();
        reaction.answers = answersView.getText().toString();
        reaction.customClass = customClassView.getText().toString();
        reaction.isTeachable = isTeachableSwitch.isChecked();
    }

    private void updateUIFromReaction() {
        questionsView.setText(reaction.questions);
        answersView.setText(reaction.answers);
        customClassView.setText(reaction.customClass);
        isTeachableSwitch.setChecked(reaction.isTeachable);
    }

    private void doUpdateReaction() {
        doAsyncOperation(appDatabase.reactionDao().update(reaction));
    }

    private void doInsertReaction() {
        doAsyncOperation(appDatabase.reactionDao().insert(reaction));
    }

    private void doDeleteReaction() {
        doAsyncOperation(appDatabase.reactionDao().delete(reaction));
    }

    private void doAsyncOperation(Completable completable) {
        disableViews();
        completable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        enableViews();
                        Toast.makeText(getMainActivity(), R.string.done_text, Toast.LENGTH_LONG).show();
                        getMainActivity().onBackPressed();
                    }

                    @Override
                    public void onError(@NonNull Throwable error) {
                        Log.e(TAG, error.getMessage(), error);
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                        enableViews();
                    }
                });
    }
}
