package com.oiakushev.silesabot.tool;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public enum SettingsStorage {
    Instance;

    private final String SETTINGS_FILENAME = "settings.properties";
    private final String SETTINGS_VOICE_ENABLED = "voiceEnabled";
    private final String SETTINGS_NAME_IN_MESSAGE = "nameInMessage";

    private boolean voiceEnabled = true;
    private boolean nameInMessage = false;

    public boolean isVoiceEnabled() {
        return voiceEnabled;
    }

    public void setVoiceEnabled(boolean voiceEnabled) {
        this.voiceEnabled = voiceEnabled;
    }

    public boolean isNameInMessage() {
        return nameInMessage;
    }

    public void setNameInMessage(boolean nameInMessage) {
        this.nameInMessage = nameInMessage;
    }

    public void save(Context context) throws IOException {
        Properties properties = new Properties();
        properties.setProperty(SETTINGS_VOICE_ENABLED, String.valueOf(voiceEnabled));
        properties.setProperty(SETTINGS_NAME_IN_MESSAGE, String.valueOf(nameInMessage));

        File settingsFile = new File(context.getFilesDir(), SETTINGS_FILENAME);
        FileOutputStream fileOutputStream = new FileOutputStream(settingsFile);
        properties.store(fileOutputStream, "Settings");
    }

    public void load(Context context) throws IOException {
        Properties properties = new Properties();

        File checkSettingsFile = new File(context.getFilesDir(), SETTINGS_FILENAME);

        if (!checkSettingsFile.exists()) {
            save(context);
        }

        File settingsFile = new File(context.getFilesDir(), SETTINGS_FILENAME);
        FileInputStream fileInputStream = new FileInputStream(settingsFile);

        properties.load(fileInputStream);

        voiceEnabled = Boolean.parseBoolean(properties.getProperty(SETTINGS_VOICE_ENABLED));
        nameInMessage = Boolean.parseBoolean(properties.getProperty(SETTINGS_NAME_IN_MESSAGE));
    }
}
