package com.example.jean.replicator.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.jean.replicator.R;
import com.example.jean.replicator.ReplicatorItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collection;

/*
** Created by jean on 11/5/16.
** Preferences Singleton to add, get and remove accounts from sharedPreferences
*/

public class Preferences {
    private static Preferences self = null;
    private Gson _gson;
    private Context _ctx;

    private Preferences(Context ctx) {
        _gson = new Gson();
        _ctx = ctx;
    }

    public static Preferences get(Context ctx) {
        if (self == null)
            self = new Preferences(ctx);
        return self;
    }

    public boolean addItem(ReplicatorItem item) {
        SharedPreferences.Editor edit = _ctx.getSharedPreferences(_ctx.getString(R.string.prefs_key), Context.MODE_PRIVATE).edit();
        ArrayList<ReplicatorItem> items = getItems();
        for (ReplicatorItem it : items) {
            if (it.getAccountName().equals(item.getAccountName()) && it.getDeliverName().equals(item.getDeliverName())) {
                it.setSecretKey(item.getSecretKey());
                it.setDigits(item.getDigits());
                edit.putString("items", _gson.toJson(items, new TypeToken<ArrayList<ReplicatorItem>>() {}.getType()));
                edit.commit();
                return false;
            }
        }
        items.add(item);
        edit.putString("items", _gson.toJson(items, new TypeToken<ArrayList<ReplicatorItem>>() {}.getType()));
        edit.commit();
        return true;
    }

    public ArrayList<ReplicatorItem> getItems() {
        SharedPreferences prefs = _ctx.getSharedPreferences(_ctx.getString(R.string.prefs_key), Context.MODE_PRIVATE);
        ArrayList<ReplicatorItem> items = new ArrayList<>();
        if (!prefs.getString("items", "").equals("")) {
            items.addAll((Collection<? extends ReplicatorItem>) _gson.fromJson(prefs.getString("items", ""), new TypeToken<ArrayList<ReplicatorItem>>() {}.getType()));
        }
        return items;
    }

    public void removeItem(ReplicatorItem item) {
        SharedPreferences.Editor edit = _ctx.getSharedPreferences(_ctx.getString(R.string.prefs_key), Context.MODE_PRIVATE).edit();
        ArrayList<ReplicatorItem> items = getItems();
        for (ReplicatorItem it : items) {
            if (it.getAccountName().equals(item.getAccountName()) && it.getDeliverName().equals(item.getDeliverName())) {
                items.remove(it);
                break;
            }
        }
        edit.putString("items", _gson.toJson(items, new TypeToken<ArrayList<ReplicatorItem>>() {}.getType()));
        edit.commit();
    }
}
