package com.oiakushev.silesabot.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

@Dao
public interface ReactionDAO {
    @Query("SELECT * FROM reaction")
    Observable<List<StoredReaction>> getAllReactions();

    @Query("SELECT * FROM reaction WHERE is_teachable = 0")
    Observable<List<StoredReaction>> getUserReactions();

    @Query("SELECT * FROM reaction WHERE is_teachable = 1")
    Observable<List<StoredReaction>> getTeachReactions();

    @Insert
    Completable insert(StoredReaction reaction);

    @Insert
    Completable insertAll(List<StoredReaction> reactions);

    @Update
    Completable update(StoredReaction reaction);

    @Delete
    Completable delete(StoredReaction reaction);
}
