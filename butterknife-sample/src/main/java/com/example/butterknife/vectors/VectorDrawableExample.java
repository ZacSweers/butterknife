package com.example.butterknife.vectors;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.VectorDrawable;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.view.View;

import com.example.butterknife.R;

import butterknife.BindAVD;
import butterknife.BindVD;
import butterknife.ButterKnife;

public final class VectorDrawableExample {

  @BindVD(R.drawable.ic_arrow_back)
  VectorDrawable backVector;

  @BindAVD(R.drawable.avd_likes)
  AnimatedVectorDrawable avdLikes;

  @BindVD(R.drawable.ic_arrow_back)
  VectorDrawableCompat backVectorCompat;

  @BindAVD(R.drawable.avd_likes)
  AnimatedVectorDrawableCompat avdLikesCompat;

  public VectorDrawableExample(View view) {
    ButterKnife.bind(this, view);
  }
}
