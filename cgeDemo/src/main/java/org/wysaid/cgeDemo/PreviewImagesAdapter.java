package org.wysaid.cgeDemo;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;

import org.wysaid.cgeDemo.databinding.LayoutPreviewItemBinding;
import org.wysaid.nativePort.CGENativeLibrary;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class PreviewImagesAdapter extends RecyclerView.Adapter<PreviewImagesAdapter.ViewHolder> {

    private List<String> configs;
    private String imageUri;
    private OnItemInteractListener onItemInteractListener;

    public PreviewImagesAdapter(List<String> configs, OnItemInteractListener onItemInteractListener) {
        this.configs = configs;
        this.onItemInteractListener = onItemInteractListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_preview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(configs.get(position));
    }

    @Override
    public int getItemCount() {
        return configs.size();
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LayoutPreviewItemBinding mBinding;

        public ViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            mBinding.imgFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemInteractListener != null) {
                        onItemInteractListener.onConfigSelected(configs.get(getAdapterPosition()));
                    }
                }
            });
        }

        public void bind(final String config) {
            if(!TextUtils.isEmpty(imageUri)) {
                Glide.with(itemView.getContext())
                        .asBitmap()
                        .load(imageUri)
                        .apply(RequestOptions.centerCropTransform())
                        .apply(RequestOptions.bitmapTransform(new BitmapTransformation() {
                            @Override
                            protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
                                return CGENativeLibrary.filterImage_MultipleEffects(toTransform, config, 1.0f);
                            }

                            @Override
                            public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
                                messageDigest.update(config.getBytes());
                            }
                        })).into(new BitmapImageViewTarget(mBinding.imgFilter) {

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(mBinding.imgFilter.getContext().getResources(), resource);
                        roundedBitmapDrawable.setCornerRadius(4F);
                        getView().setImageDrawable(roundedBitmapDrawable);
                    }
                });
            } else {
                Glide.with(itemView.getContext())
                        .asBitmap()
                        .load(R.drawable.default_image_preview)
                        .apply(RequestOptions.centerCropTransform())
                        .apply(RequestOptions.bitmapTransform(new BitmapTransformation() {
                            @Override
                            protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
                                return CGENativeLibrary.filterImage_MultipleEffects(toTransform, config, 1.0f);
                            }

                            @Override
                            public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
                                messageDigest.update(config.getBytes());
                            }
                        })).into(new BitmapImageViewTarget(mBinding.imgFilter) {

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(mBinding.imgFilter.getContext().getResources(), resource);
                        roundedBitmapDrawable.setCornerRadius(4F);
                        getView().setImageDrawable(roundedBitmapDrawable);
                    }
                });
            }
        }
    }

    interface OnItemInteractListener {

        void onConfigSelected(String selectedConfig);
    }
}
