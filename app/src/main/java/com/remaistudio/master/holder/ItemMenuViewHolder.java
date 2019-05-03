package com.remaistudio.master.holder;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.remaistudio.master.R;
import com.remaistudio.master.model.JsonItemContent;
import com.remaistudio.master.util.FragmentUtil;
import com.remaistudio.master.util.StorageUtil;
import com.remaistudio.master.util.firebase.FirebaseAnalyticsHelper;
import com.squareup.picasso.Picasso;

public class ItemMenuViewHolder extends RecyclerView.ViewHolder {

    private TextView mTitle;
    private ImageView mImageView;
    private ConstraintLayout mConstraintLayout;

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
                FirebaseAnalyticsHelper.sendEvent(itemView.getContext(), "item_menu_view_click", "fragment", jsonItemContent.getFileLink());
            }
        };

        //ImageView
        mImageView.setOnClickListener(onClickListener);
        Picasso
                .get()
                .load(StorageUtil.checkUrlPrefix(jsonItemContent.getImageLink()))
                .placeholder(R.drawable.empty_image)
                .into(mImageView);

        //RelativeLayout
        mConstraintLayout.setOnClickListener(onClickListener);
    }
}
