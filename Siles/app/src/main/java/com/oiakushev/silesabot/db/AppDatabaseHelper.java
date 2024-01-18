package com.oiakushev.silesabot.db;

import android.content.Context;

import androidx.room.Room;

public enum AppDatabaseHelper {
    Instance;

    private AppDatabase appDatabase;

    public AppDatabase getInstance(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context,
                    AppDatabase.class, "stored_reactions").build();
        }

        return appDatabase;
    }
}
