package org.wysaid.cgeDemo;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.wysaid.cgeDemo.databinding.ActivityPhotoPreviewBinding;
import org.wysaid.view.ImageGLSurfaceView;

import java.util.Arrays;

public class PreviewActivity extends AppCompatActivity {

    private ActivityPhotoPreviewBinding mBinding;
    private String photoUri;
    private Bitmap currentBitmap;
    private String currentConfig;

    public static Intent newIntent(Context context, String photoUri) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putExtra("photo-uri", photoUri);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_photo_preview);

        photoUri = getIntent().getStringExtra("photo-uri");

        mBinding.imageView.setDisplayMode(ImageGLSurfaceView.DisplayMode.DISPLAY_ASPECT_FILL);
        mBinding.imageView.setSurfaceCreatedCallback(new ImageGLSurfaceView.OnSurfaceCreatedCallback() {
            @Override
            public void surfaceCreated() {
                mBinding.imageView.setImageBitmap(currentBitmap);
                mBinding.imageView.setFilterWithConfig(currentConfig);
            }
        });

        mBinding.rcImgPreview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        PreviewImagesAdapter previewImagesAdapter = new PreviewImagesAdapter(Arrays.asList(MainActivity.EFFECT_CONFIGS), new PreviewImagesAdapter.OnItemInteractListener() {
            @Override
            public void onConfigSelected(final String selectedConfig) {
                mBinding.imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        currentConfig = selectedConfig;
                        mBinding.imageView.setFilterWithConfig(selectedConfig);
                    }
                });
            }
        });
        previewImagesAdapter.setImageUri(photoUri);
        mBinding.rcImgPreview.setAdapter(previewImagesAdapter);

        Glide.with(this)
                .asBitmap()
                .load(photoUri)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        currentBitmap = resource;
                        mBinding.imageView.setFilterWithConfig(currentConfig);
                        mBinding.imageView.setImageBitmap(currentBitmap);
                        return false;
                    }
                }).submit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBinding.imageView.release();
        mBinding.imageView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.imageView.onResume();
    }
}
