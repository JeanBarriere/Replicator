package com.example.jean.replicator.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jean.replicator.R;
import com.example.jean.replicator.ReplicatorItem;

import java.util.List;

/*
** CustomAdapter adapter is the account listview adapter.
*/

public final class CustomAdapter extends ArrayAdapter<ReplicatorItem> {

    public CustomAdapter(Context context, int resource, List<ReplicatorItem> items) {
        super(context, resource, items);
    }

    @NonNull
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_row, null);

            if (getItem(position) != null) {
                final TextView tt1 = (TextView) v.findViewById(R.id.txt_row_key);
                final ProgressBar pb = (ProgressBar) v.findViewById(R.id.pb_row);

                // We set the progress bar and start a timer to generate a code
                pb.setProgress((int)getItem(position).getTimeLeft());
                final CountDownTimer timer = new CountDownTimer(30000, 50) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        if (getCount() > position) {
                            pb.setProgress((int) getItem(position).getTimeLeft());
                            if (getItem(position).getTimeLeft() >= 29000)
                                tt1.setText(getItem(position).getCode());
                        }
                    }

                    @Override
                    public void onFinish() {
                        if (getCount() > position) {
                            start();
                        }
                    }
                };
                timer.start();
            }
        }

        if (getItem(position) != null) {
            final TextView tt1 = (TextView) v.findViewById(R.id.txt_row_key);
            final TextView tt2 = (TextView) v.findViewById(R.id.txt_row_name);
            final ImageView iv = (ImageView) v.findViewById(R.id.iv_row);

            if (tt1 != null) {
                tt1.setText(getItem(position).getCode());
            }

            if (tt2 != null) {
                tt2.setText(getItem(position).getAccountName());
            }

            if (iv != null)
                iv.setImageResource(getItem(position).getIcon());
        }
        return v;
    }

}