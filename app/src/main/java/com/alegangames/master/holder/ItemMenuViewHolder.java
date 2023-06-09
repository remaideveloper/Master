package com.alegangames.master.holder;


import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.alegangames.master.R;
import com.alegangames.master.activity.ActivityContent;
import com.alegangames.master.activity.ActivityMain;
import com.alegangames.master.model.JsonItemContent;
import com.alegangames.master.util.FragmentUtil;
import com.alegangames.master.util.firebase.FirebaseAnalyticsHelper;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import static com.alegangames.master.activity.ActivityAppParent.FRAGMENT_DATA;

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
            if (jsonItemContent.getFileLink().equalsIgnoreCase("#instagram")){
                Uri address = Uri.parse("https://www.instagram.com/master_for_minecraft_pe/");
                Intent openLinkIntent = new Intent(Intent.ACTION_VIEW, address);
                itemView.getContext().startActivity(openLinkIntent);
            } else if (jsonItemContent.getFileLink() != null && !jsonItemContent.getFileLink().isEmpty()) {
//                FragmentUtil.onTransactionFragmentByName(((FragmentActivity) itemView.getContext()), jsonItemContent.getFileLink());
//                ((ActivityMain) itemView.getContext()).mNavigationViewUtil.openFragment();
//                ((ActivityMain) itemView.getContext()).countShowAd = 0;
                Intent intent = new Intent(itemView.getContext(), ActivityContent.class);
                intent.putExtra(FRAGMENT_DATA, jsonItemContent.getFileLink());
                itemView.getContext().startActivity(intent);
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
