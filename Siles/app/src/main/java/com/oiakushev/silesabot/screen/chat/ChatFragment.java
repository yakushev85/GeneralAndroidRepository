package com.oiakushev.silesabot.screen.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oiakushev.silesabot.MainActivity;
import com.oiakushev.silesabot.R;
import com.oiakushev.silesabot.model.datamodel.Message;
import com.oiakushev.silesabot.model.datamodel.PerceptronModel;
import com.oiakushev.silesabot.screen.ExtendedFragment;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ChatFragment extends ExtendedFragment implements View.OnClickListener {
    private static final String TAG = PerceptronModel.class.getName();

    private RecyclerView chatView;
    private EditText userMsgText;
    private Button sendButton;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                      Bundle savedInstanceState) {
        View viewFragment = inflater.inflate(R.layout.fragment_chat, container, false);

        chatView = viewFragment.findViewById(R.id.chatView);
        userMsgText = viewFragment.findViewById(R.id.userMsgText);
        sendButton = viewFragment.findViewById(R.id.sendButton);

        sendButton.setOnClickListener(this);
        chatView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        chatView.setAdapter(new ChatListViewAdapter(this.getContext(),
                this.getMainActivity().getMessages()));

        return viewFragment;
    }

    @Override
    public void onClick(View view) {
        String userMessage = userMsgText.getText().toString().trim();

        if (!userMessage.isEmpty()) {
            processUserMessage(userMessage);
        }
    }

    private void enableViews() {
        chatView.setEnabled(true);
        userMsgText.setEnabled(true);
        sendButton.setEnabled(true);
    }

    private void disableViews() {
        chatView.setEnabled(false);
        userMsgText.setEnabled(false);
        sendButton.setEnabled(false);
    }

    private void displayResults() {
        userMsgText.setText("");

        chatView.setAdapter(new ChatListViewAdapter(this.getContext(),
                this.getMainActivity().getMessages()));
        chatView.invalidate();

        enableViews();
    }

    private void processUserMessage(String userMessage) {
        MainActivity mainActivity = this.getMainActivity();

        disableViews();
        mainActivity.addMessage(new Message(Message.USER, userMessage));

        compositeDisposable.add(mainActivity.getDataController().getRxAnswer(userMessage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (answers) -> {
                            StringBuilder textToSpeak = new StringBuilder();
                            for (String answer : answers) {
                                if (!answer.isEmpty()) {
                                    mainActivity.addMessage(new Message(Message.SILES, answer));
                                    textToSpeak.append(answer).append(' ');
                                }
                            }

                            mainActivity.speakText(textToSpeak.toString());

                            displayResults();
                        },
                        (error) -> {
                            Log.e(TAG, error.getMessage(), error);
                            displayResults();
                        }
                )
        );
    }
}
