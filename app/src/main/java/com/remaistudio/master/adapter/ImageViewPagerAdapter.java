package com.remaistudio.master.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.remaistudio.master.R;
import com.remaistudio.master.util.StorageUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class ImageViewPagerAdapter extends PagerAdapter {

    private static final String TAG = ImageViewPagerAdapter.class.getSimpleName();

    private List<String> list;
    private ImageView mImageView;
    private ViewPager viewPager;

    public ImageViewPagerAdapter(List<String> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.d(TAG, "instantiateItem: " + list.get(position));

        LayoutInflater layoutInflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.image_view_slider_layout, container, false);

        mImageView = view.findViewById(R.id.imageViewItem);
        Picasso.get().load(StorageUtil.checkUrlPrefix(list.get(position))).into(mImageView);
        mImageView.setScaleType(CENTER_CROP);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        this.viewPager.setAdapter(this);
    }

}

