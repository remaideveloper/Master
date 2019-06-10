package com.alegangames.master.holder;


import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alegangames.master.R;
import com.alegangames.master.activity.ActivityAppParent;
import com.alegangames.master.model.JsonItemContent;
import com.alegangames.master.util.StorageUtil;
import com.alegangames.master.util.StringUtil;
import com.squareup.picasso.Picasso;

public class ItemContentViewHolder extends RecyclerView.ViewHolder {

    public static final String TAG = "ViewHolders";

    private String mQuery;
    private TextView mTitle;
    private ImageView mImageView;
    private TextView mTextViewPremium;

    public ItemContentViewHolder(View view) {
        super(view);
        mTitle = view.findViewById(R.id.textViewTitle);
        mImageView = view.findViewById(R.id.imageViewItem);
        mTextViewPremium = view.findViewById(R.id.textViewPremium);
    }


    public void setHolder(final JsonItemContent item, String query) {
        this.mQuery = query;

        //Title
        if (item.getName() != null && !item.getName().isEmpty() && mTitle != null) {
            if (mQuery != null && !mQuery.isEmpty()) {
                mTitle.setText(StringUtil.highLightSearchText(mQuery, item.getName()));
            } else {
                mTitle.setText(item.getName());
            }
        }

        View.OnClickListener onClickListener = v -> {
            ((ActivityAppParent) itemView.getContext()).onOpenItem(item);
            //FirebaseAnalyticsHelper.sendEvent(itemView.getContext(), "item_content_view_click", "content", item.getName());
        };

        //ImageView
        if (mImageView != null) {
            mImageView.setOnClickListener(onClickListener);
            Picasso
                    .get()
                    .load(StorageUtil.checkUrlPrefix(StringUtil.getFirstStringFromArray(item.getImageLink())))
                    .config(Bitmap.Config.RGB_565)
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.empty_image)
                    .into(mImageView);
        }

        //Premium
        if (mTextViewPremium != null) {
            if (item.isPremium()) {
                String text = itemView.getContext().getString(R.string.coins_amount_format, item.getPrice());
                mTextViewPremium.setText(text);
                mTextViewPremium.setVisibility(View.VISIBLE);
            } else {
                mTextViewPremium.setVisibility(View.GONE);
            }
        }
    }
}