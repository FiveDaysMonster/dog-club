package com.example.lcd3;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class vp_book extends Page {
    public vp_book(Context context) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.l_book, null);
        addView(view);
    }

}
