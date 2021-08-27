package com.example.lcd3;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class vp_forum extends Page {
    public vp_forum(Context context) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.l_forum, null);
        addView(view);
    }

}
