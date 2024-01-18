package com.oiakushev.silesabot.db;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.oiakushev.silesabot.model.datamodel.reaction.Reaction;

import java.util.Arrays;

@Entity(tableName = "reaction")
public class StoredReaction implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "questions")
    public String questions;

    @ColumnInfo(name = "answers")
    public String answers;

    @ColumnInfo(name = "custom_class")
    public String customClass;

    @ColumnInfo(name = "is_teachable")
    public Boolean isTeachable;

    public StoredReaction() {
        super();
    }

    public StoredReaction(String questions, String answers, String customClass, Boolean isTeachable) {
        this.questions = questions;
        this.answers = answers;
        this.customClass = customClass;
        this.isTeachable = isTeachable;
    }

    public StoredReaction(String questions, String answers) {
        this.questions = questions;
        this.answers = answers;
        this.customClass = "";
        this.isTeachable = true;
    }

    protected StoredReaction(Parcel in) {
        id = in.readInt();
        questions = in.readString();
        answers = in.readString();
        customClass = in.readString();
        byte tmpIsTeachable = in.readByte();
        isTeachable = tmpIsTeachable == 0 ? null : tmpIsTeachable == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(questions);
        dest.writeString(answers);
        dest.writeString(customClass);
        dest.writeByte((byte) (isTeachable == null ? 0 : isTeachable ? 1 : 2));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StoredReaction> CREATOR = new Creator<StoredReaction>() {
        @Override
        public StoredReaction createFromParcel(Parcel in) {
            return new StoredReaction(in);
        }

        @Override
        public StoredReaction[] newArray(int size) {
            return new StoredReaction[size];
        }
    };

    public static StoredReaction createFromDataReaction(Reaction reaction, boolean isTeachable) {
        StoredReaction storedReaction = new StoredReaction();

        storedReaction.questions = String.join("\n", reaction.getPatternTags());
        storedReaction.answers = String.join("\n", reaction.getReactions());
        storedReaction.customClass = reaction.getCustomClass();
        storedReaction.isTeachable = isTeachable;

        return storedReaction;
    }

    public Reaction toDataReaction() {
        Reaction dataReaction = new Reaction();

        dataReaction.setPatternTags(Arrays.asList(questions.split("\n")));
        dataReaction.setReactions(Arrays.asList(answers.split("\n")));
        dataReaction.setCustomClass(customClass);

        return dataReaction;
    }
}
