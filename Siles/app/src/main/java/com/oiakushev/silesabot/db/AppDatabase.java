package com.oiakushev.silesabot.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {StoredReaction.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ReactionDAO reactionDao();
}