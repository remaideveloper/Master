package com.remaistudio.master.holder;


import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.remaistudio.master.R;
import com.remaistudio.master.model.JsonItemContent;
import com.remaistudio.master.util.ToastUtil;
import com.remaistudio.master.util.UrlHelper;
import com.squareup.picasso.Picasso;

public class ItemOfferViewHolder extends RecyclerView.ViewHolder {

    private TextView mTextViewTitle;
    private ImageView mImageViewItem;

    public ItemOfferViewHolder(View v) {
        super(v);
        mTextViewTitle = v.findViewById(R.id.textViewTitle);
        mImageViewItem = v.findViewById(R.id.imageViewItem);
    }

    public void setHolder(JsonItemContent offer) {
        //Text
        mTextViewTitle.setText(offer.getName());

        //OnClick
        View.OnClickListener onClickListener = view -> {
            String packageName = offer.getFileLink().substring(offer.getFileLink().lastIndexOf('=') + 1);
            if (offer.getFileLink().contains("play.google.com")) {
                try {
                    itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    try {
                        itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
                    } catch (Exception e) {
                        ToastUtil.show(itemView.getContext(), R.string.error);
                    }
                }
            } else {
                itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(UrlHelper.onVerificationUrl(offer.getFileLink()))));
            }
        };

        //Image
        mImageViewItem.setOnClickListener(onClickListener);

        if (offer.getImageLink() != null && !offer.getImageLink().isEmpty()) {
            Picasso
                    .get()
                    .load(offer.getImageLink())
                    .placeholder(R.drawable.empty_image)
                    .into(mImageViewItem);
        }

        itemView.setOnClickListener(onClickListener);

    }
}
