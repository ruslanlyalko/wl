package com.ruslanlyalko.wl.presentation.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by Ruslan Lyalko
 * on 07.08.2018.
 */
public class ImageLoader {

    public static void loadPhoto(String url, ImageView imageView) {
        Glide.with(imageView)
                .load(url)
                .into(imageView);
    }

    public static void loadCirclePhoto(String url, ImageView imageView) {
        Glide.with(imageView)
                .load(url)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }
}
