package com.iakushev.oleksandr.englishwordsformemory;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by olkesandr on 12/20/15.
 */
public class WordArrayAdapter extends ArrayAdapter<String> {
    private Context wordContext;
    private List<String> htmlWords;
    private int listViewResourceId;

    public WordArrayAdapter(Context context, int listViewResourceId, List<String> inWords) {
        super(context, listViewResourceId, inWords);
        this.wordContext = context;
        this.htmlWords = inWords;
        this.listViewResourceId = listViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) wordContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView itemView = (TextView) inflater.inflate(listViewResourceId, parent, false);
        itemView.setText(Html.fromHtml(htmlWords.get(position)));

        return itemView;
    }
}
