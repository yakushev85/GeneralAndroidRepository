package com.oiakushev.silesabot.db;

import com.oiakushev.silesabot.model.datamodel.reaction.Reaction;

import java.util.ArrayList;
import java.util.List;

public class StoredReactionsHelper {

    public static List<Reaction> getReactionsByTeachable(List<StoredReaction> storedReactions, boolean isTeachable) {
        List<Reaction> resultList = new ArrayList<>();

        for (StoredReaction itemReaction : storedReactions) {
            if (itemReaction.isTeachable == isTeachable) {
                resultList.add(itemReaction.toDataReaction());
            }
        }

        return resultList;
    }
}
