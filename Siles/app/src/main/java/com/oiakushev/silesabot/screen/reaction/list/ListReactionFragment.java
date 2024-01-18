package com.oiakushev.silesabot.screen.reaction.list;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oiakushev.silesabot.R;
import com.oiakushev.silesabot.db.AppDatabase;
import com.oiakushev.silesabot.db.AppDatabaseHelper;
import com.oiakushev.silesabot.db.StoredReaction;
import com.oiakushev.silesabot.screen.ExtendedFragment;
import com.oiakushev.silesabot.screen.reaction.edit.EditReactionFragment;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ListReactionFragment extends ExtendedFragment {
    private static final String TAG = ListReactionFragment.class.getName();

    private RecyclerView reactionsView;
    private Button addReactionButton;

    private AppDatabase appDatabase;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_reaction, container, false);

        reactionsView = v.findViewById(R.id.reactionListView);

        appDatabase = AppDatabaseHelper.Instance.getInstance(this.getContext());

        addReactionButton = v.findViewById(R.id.addReactionButton);
        addReactionButton.setOnClickListener(view -> {
            FragmentManager fm = this.getMainActivity().getSupportFragmentManager();

            fm.beginTransaction()
                    .replace(R.id.mainFragmentContainer, new EditReactionFragment())
                    .addToBackStack(EditReactionFragment.class.getName())
                    .commit();
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        updateReactionList();
    }

    private void updateReactionList() {
        disableViews();

        compositeDisposable.add(appDatabase.reactionDao().getAllReactions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((reactions) -> {
                    applyReactions(reactions);
                    enableViews();
                }, (error) -> {
                    Log.e(TAG, error.getMessage(), error);
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    enableViews();
                }));
    }

    private void applyReactions(List<StoredReaction> reactions) {
        ReactionListViewAdapter reactionListViewAdapter =
                new ReactionListViewAdapter(this.getContext(), reactions);
        reactionListViewAdapter.setOnClickListener((reaction -> {
            EditReactionFragment editReactionFragment =
                    EditReactionFragment.createEditReactionFragment(reaction);

            FragmentManager fm = this.getMainActivity().getSupportFragmentManager();

            fm.beginTransaction()
                    .replace(R.id.mainFragmentContainer, editReactionFragment)
                    .addToBackStack(EditReactionFragment.class.getName())
                    .commit();
        }));

        reactionsView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        reactionsView.setAdapter(reactionListViewAdapter);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.reaction_item_delimiter));

        reactionsView.addItemDecoration(itemDecorator);
    }

    private void disableViews() {
        reactionsView.setEnabled(false);
        addReactionButton.setEnabled(false);
    }

    private void enableViews() {
        reactionsView.setEnabled(true);
        addReactionButton.setEnabled(true);
    }
}
