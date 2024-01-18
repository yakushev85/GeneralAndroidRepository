package com.oiakushev.silesabot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;

import com.oiakushev.silesabot.db.AppDatabase;
import com.oiakushev.silesabot.db.AppDatabaseHelper;
import com.oiakushev.silesabot.db.DefaultReactions;
import com.oiakushev.silesabot.db.StoredReaction;
import com.oiakushev.silesabot.db.StoredReactionsHelper;
import com.oiakushev.silesabot.model.DataController;
import com.oiakushev.silesabot.model.datamodel.MainModel;
import com.oiakushev.silesabot.model.datamodel.Message;
import com.oiakushev.silesabot.model.datamodel.PerceptronModel;
import com.oiakushev.silesabot.model.datamodel.reaction.Reaction;
import com.oiakushev.silesabot.screen.chat.ChatFragment;
import com.oiakushev.silesabot.screen.settings.SettingsFragment;
import com.oiakushev.silesabot.tool.SettingsStorage;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, DataController.OnUpdateDataListener {
    private static final String TAG = PerceptronModel.class.getName();

    private DataController dataController;
    private TextToSpeech tts;
    private final ArrayList<Message> messages = new ArrayList<>();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tts = new TextToSpeech(this, this);

        appDatabase = AppDatabaseHelper.Instance.getInstance(this);

        try {
            SettingsStorage.Instance.load(this);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        loadReactionData(false);

        FragmentManager fm = this.getSupportFragmentManager();

        if (fm.findFragmentById(R.id.mainFragmentContainer) == null) {
            fm.beginTransaction()
                    .add(R.id.mainFragmentContainer, new ChatFragment())
                    .commit();
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            Locale locale = new Locale("ru");
            int result = tts.setLanguage(locale);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Not supported russian voice language!", Toast.LENGTH_LONG).show();
            }

        } else {
            Log.e(TAG, "TTS error with status code=" + status);
        }
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings_menu_item) {
            FragmentManager fm = this.getSupportFragmentManager();

            fm.beginTransaction()
                    .replace(R.id.mainFragmentContainer, new SettingsFragment())
                    .addToBackStack(SettingsFragment.class.getName())
                    .commit();
        } else if (id == R.id.export_menu_item) {
            doShareFileViaIntent();
        }

        return super.onOptionsItemSelected(item);
    }

    private void doShareFileViaIntent() {
        compositeDisposable.add(dataController.rxGenerateExportZipFile(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((shareFile) -> {
                            Uri exportUri = FileProvider
                                    .getUriForFile(this, "com.oiakushev.silesabot", shareFile);

                            Intent exportIntent = new Intent();
                            exportIntent.setAction(Intent.ACTION_SEND);
                            exportIntent.setDataAndType(exportUri, "application/zip");
                            exportIntent.addFlags(
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            startActivity(exportIntent);

                        }
                        , (error) -> {
                            Log.e(TAG, error.getMessage(), error);
                            Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();

                        }));
    }

    public void speakText(String textToSpeak) {
        if (SettingsStorage.Instance.isVoiceEnabled()) {
            String utteranceId = UUID.randomUUID().toString();
            tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        }
    }

    public DataController getDataController() {
        return dataController;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message msg) {
        messages.add(msg);
    }

    private void loadReactionData(boolean isNeedTeach) {
        compositeDisposable.add(appDatabase.reactionDao().getAllReactions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((reactions) -> {
                            if (reactions.isEmpty()) {
                                setDefaultReactions();
                            } else {
                                initDataController(reactions);
                            }

                            if (isNeedTeach) {
                                doLearn();
                            }

                        }
                        , (error) -> {
                            Log.e(TAG, error.getMessage(), error);
                            Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();

                        }));
    }

    private void setDefaultReactions() {
        initDataController(DefaultReactions.defaultStoredReactions);

        appDatabase.reactionDao().insertAll(DefaultReactions.defaultStoredReactions)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        doLearn();
                    }

                    @Override
                    public void onError(@NonNull Throwable error) {
                        Log.e(TAG, error.getMessage(), error);
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void initDataController(List<StoredReaction> storedReactions) {
        List<Reaction> userReactions =
                StoredReactionsHelper.getReactionsByTeachable(storedReactions, false);
        List<Reaction> teachReactions =
                StoredReactionsHelper.getReactionsByTeachable(storedReactions, true);

        try {
            dataController =
                    new DataController(new MainModel(this, userReactions, teachReactions));

            dataController.setOnUpdateDataListener(this);
        } catch (IOException | XmlPullParserException e) {
            Log.e(TAG, e.getMessage(), e);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void doLearn() {
        Toast.makeText(this, R.string.relearn, Toast.LENGTH_LONG).show();

        compositeDisposable.add(
                getDataController().rxLearn()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                (result) -> {
                                    Log.i(TAG, "doLearn is done.");
                                    Toast.makeText(this, R.string.relearn_done, Toast.LENGTH_LONG).show();
                                },
                                (error) -> {
                                    Log.e(TAG, error.getMessage(), error);
                                    Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                        )
        );
    }

    @Override
    public void onUpdate(StoredReaction newReaction) {
        appDatabase.reactionDao().insert(newReaction)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        loadReactionData(true);
                    }

                    @Override
                    public void onError(@NonNull Throwable error) {
                        Log.e(TAG, error.getMessage(), error);
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}