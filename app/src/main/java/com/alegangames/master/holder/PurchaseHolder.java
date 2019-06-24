package com.alegangames.master.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alegangames.master.R;
import com.alegangames.master.util.ButtonColorManager;
import com.alegangames.master.util.ColorList;
import com.alegangames.master.util.billing.Product;

public class PurchaseHolder {

    private View root;
    private Product product;
    private PurchaseListener listener;

    private Button btBuy;

    public PurchaseHolder(Context context, ViewGroup parent, Product product, PurchaseListener listener) {
        root = LayoutInflater.from(context).inflate(R.layout.layout_item_purchase, parent, false);
        this.product = product;
        this.listener = listener;
        initLayout();
    }

    private void initLayout() {
        TextView description = root.findViewById(R.id.textPurchaseDesc);
        description.setText(product.getDescription());

        TextView price = root.findViewById(R.id.textSubDescription);
        price.setText(product.getSubDescription());

        ImageView iv = root.findViewById(R.id.imagePurchaseIcon);
        iv.setImageResource(product.getImageId());

        btBuy = root.findViewById(R.id.buttonBuy);
        btBuy.setOnClickListener((v) -> listener.onPurchase(product));

        root.setOnClickListener((v) -> listener.onPurchase(product));

        ButtonColorManager.setBackgroundButton(btBuy, ColorList.BLUE);
        ButtonColorManager.setTextColorButton(btBuy, ColorList.WHITE);
    }

    public void setButtonText(String text) {
        if(btBuy != null) {
            btBuy.setText(text);
        }
    }

    public void setButtonEnabled(boolean isEnabled) {
        btBuy.setEnabled(isEnabled);
        int color = isEnabled ? ColorList.BLUE : ColorList.GRAY;
        ButtonColorManager.setBackgroundButton(btBuy, color);
    }

    public View getRoot() {
        return root;
    }

    public interface PurchaseListener {
        void onPurchase(Product product);
    }
}
