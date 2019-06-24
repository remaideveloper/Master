package com.alegangames.master.holder;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.alegangames.master.R;
import com.alegangames.master.activity.ActivityMain;
import com.alegangames.master.model.JsonItemContent;
import com.alegangames.master.util.FragmentUtil;
import com.alegangames.master.util.firebase.FirebaseAnalyticsHelper;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

public class ItemMenuViewHolder extends RecyclerView.ViewHolder {

    private TextView mTitle;
    private ImageView mImageView;
    private CardView mConstraintLayout;

    public ItemMenuViewHolder(View v) {
        super(v);
        mTitle = v.findViewById(R.id.textViewTitle);
        mImageView = v.findViewById(R.id.imageViewItem);
        mConstraintLayout = v.findViewById(R.id.constraintLayout);
    }

    public void setHolder(JsonItemContent jsonItemContent) {

        //Title
        mTitle.setText(jsonItemContent.getName());

        View.OnClickListener onClickListener = v -> {
            if (itemView.getContext() == null) {
                return;
            }
            if (jsonItemContent.getFileLink() != null && !jsonItemContent.getFileLink().isEmpty()) {
                FragmentUtil.onTransactionFragmentByName(((FragmentActivity) itemView.getContext()), jsonItemContent.getFileLink());
                ((ActivityMain) itemView.getContext()).mNavigationViewUtil.openFragment();
            }
        };

        //ImageView
        mImageView.setOnClickListener(onClickListener);
        Glide
                .with(itemView.getContext())
                .load("file:///android_asset/"+jsonItemContent.getImageLink())
                .placeholder(R.drawable.empty_image)
                .into(mImageView);

        //RelativeLayout
        mConstraintLayout.setOnClickListener(onClickListener);
    }
}
