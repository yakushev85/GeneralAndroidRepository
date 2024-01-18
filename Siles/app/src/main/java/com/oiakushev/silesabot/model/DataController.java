package com.oiakushev.silesabot.model;

import android.content.Context;

import com.oiakushev.silesabot.db.StoredReaction;
import com.oiakushev.silesabot.model.datamodel.MainModel;
import com.oiakushev.silesabot.model.datamodel.PerceptronModel;
import com.oiakushev.silesabot.model.datamodel.reaction.BackReaction;
import com.oiakushev.silesabot.model.datamodel.reaction.Reaction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.reactivex.rxjava3.core.Observable;

public class DataController {
    private final MainModel mainModel;
    private BackReaction currentBackReaction;
    private OnUpdateDataListener onUpdateDataListener;

    public DataController(MainModel mainModel) {
        currentBackReaction = null;
        this.mainModel = mainModel;
    }

    public interface OnUpdateDataListener {
        void onUpdate(StoredReaction newReaction);
    }

    public void setOnUpdateDataListener(OnUpdateDataListener onUpdateDataListener) {
        this.onUpdateDataListener = onUpdateDataListener;
    }

    private List<String> getAnswer(String uMsg) {
        List<String> result = new ArrayList<>();

        if (currentBackReaction != null) {
            String filteredUserQuestion =
                    currentBackReaction.getMsg()
                            .replaceAll("[\\.\\!\\?\\,\\;\\:]", " ")
                            .replaceAll("\\s+", " ")
                            .toLowerCase()
                            .trim();

            if (!uMsg.trim().isEmpty()) {
                if (onUpdateDataListener != null) {
                    onUpdateDataListener.onUpdate(new StoredReaction(filteredUserQuestion, uMsg));
                }
            } else {
                result.add("Ну и ладно, мне всё знать не обязательно!");
                return result;
            }

            mainModel.getReactionModel().removeFirstBackReaction();
            currentBackReaction = null;
        } else {
            for (String sentence : uMsg.split("[\\.\\!\\?]\\s")) {
                String currentAnswer = mainModel.evaluateAnswer(sentence);

                if (currentAnswer.isEmpty()) {
                    ArrayList<BackReaction> backReactions =
                            mainModel.getReactionModel().getBackReactions();

                    if (backReactions.size() > 0) {
                        currentBackReaction = backReactions.get(0);
                    }

                    if (currentBackReaction != null) {
                        result.add(currentBackReaction.generateQuestion());
                        return result;
                    }
                } else {
                    result.add(currentAnswer);
                }
            }
        }

        return result;
    }

    private boolean learn() throws IOException {
        mainModel.getPerceptronModel().learn();

        return true;
    }

    private File generateReactionsFile(Context context, String filename, boolean isTeachableReactions) throws IOException {
        File reactionFile = new File(context.getCacheDir(), filename);

        List<Reaction> reactionsToSave = (isTeachableReactions) ?
                mainModel.getReactionModel().getTeachReactions() :
                mainModel.getReactionModel().getUserReactions();

        try (FileWriter fileWriter = new FileWriter(reactionFile)) {
            fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<reactions>\n");

            for (Reaction itemReaction : reactionsToSave) {
                fileWriter.write(" <reaction>\n");

                // pattern
                for (String pattern : itemReaction.getPatternTags()) {
                    fileWriter.write("  <pattern>" + pattern + "</pattern>\n");
                }

                // out-reaction
                List<String> outReactions = itemReaction.getReactions();
                if (outReactions.isEmpty()) {
                    fileWriter.write("  <outreaction/>\n");
                } else {
                    for (String outReaction : outReactions) {
                        fileWriter.write("  <outreaction>" + outReaction + "</outreaction>\n");
                    }
                }

                // custom class
                String customClass = itemReaction.getCustomClass();
                if (customClass != null && !customClass.isEmpty()) {
                    fileWriter.write("  <customclass>" + customClass + "</customclass>\n");
                }

                fileWriter.write(" </reaction>\n");
            }

            fileWriter.write("</reactions>\n");
            fileWriter.flush();
        }

        return reactionFile;
    }

    private File generateExportZipFile(Context context) throws IOException {
        File zipFile = new File(context.getCacheDir(), "teachData.zip");
        File[] filesToAdd = {
                generateReactionsFile(context, "treactions.xml", true),
                generateReactionsFile(context, "ureactions.xml", false)
        };

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (File fileToAdd : filesToAdd) {
                try (FileInputStream fileInputStream = new FileInputStream(fileToAdd)) {
                    ZipEntry zipEntry = new ZipEntry(fileToAdd.getName());

                    zipOutputStream.putNextEntry(zipEntry);

                    byte[] buffer = new byte[2048];
                    int len;

                    while ((len = fileInputStream.read(buffer)) > 0) {
                        zipOutputStream.write(buffer, 0, len);
                    }

                    zipOutputStream.closeEntry();
                }
            }
        }

        return zipFile;
    }

    public Observable<List<String>> getRxAnswer(String userMessage) {
        return Observable.create(emitter -> {
            try {
                emitter.onNext(getAnswer(userMessage));
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    public Observable<Boolean> rxLearn() {
        return Observable.create(emitter -> {
            try {
                emitter.onNext(learn());
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    public Observable<File> rxGenerateExportZipFile(Context context) {
        return Observable.create(emitter -> {
            try {
                emitter.onNext(generateExportZipFile(context));
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }
}
