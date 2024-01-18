package com.oiakushev.silesabot.screen.reaction.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oiakushev.silesabot.R;
import com.oiakushev.silesabot.db.StoredReaction;

import java.util.List;

public class ReactionListViewAdapter extends RecyclerView.Adapter<ReactionListViewAdapter.ReactionListViewHolder> {
    private List<StoredReaction> reactions;
    private Context context;
    private OnClickStoredReactionListener reactionListener;

    public ReactionListViewAdapter(Context context, List<StoredReaction> reactions) {
        this.context = context;
        this.reactions = reactions;
    }

    class ReactionListViewHolder extends RecyclerView.ViewHolder {
        TextView reactionTextView;

        public ReactionListViewHolder(@NonNull View itemView) {
            super(itemView);

            reactionTextView = itemView.findViewById(R.id.reactionItemView);
        }
    }

    @NonNull
    @Override
    public ReactionListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.reaction_item_view, parent, false);
        return new ReactionListViewAdapter.ReactionListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReactionListViewHolder holder, int position) {
        holder.reactionTextView.setText(reactions.get(position).questions);

        if (reactionListener != null) {
            holder.reactionTextView.setOnClickListener(
                    (v) -> reactionListener.onClick(reactions.get(position)));
        }
    }

    @Override
    public int getItemCount() {
        return reactions.size();
    }

    interface OnClickStoredReactionListener {
        void onClick(StoredReaction reaction);
    }

    public void setOnClickListener(OnClickStoredReactionListener reactionListener) {
        this.reactionListener = reactionListener;
    }
}
