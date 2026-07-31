package com.faicalculer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class CustomActionMenuHelper {

    public static class ActionOption {
        private String icon;
        private String title;
        private int colorHex;
        private View.OnClickListener onClickListener;

        public ActionOption(String icon, String title, View.OnClickListener onClickListener) {
            this.icon = icon;
            this.title = title;
            this.colorHex = Color.parseColor("#F8FAFC");
            this.onClickListener = onClickListener;
        }

        public ActionOption(String icon, String title, int colorHex, View.OnClickListener onClickListener) {
            this.icon = icon;
            this.title = title;
            this.colorHex = colorHex;
            this.onClickListener = onClickListener;
        }

        public String getIcon() { return icon; }
        public String getTitle() { return title; }
        public int getColorHex() { return colorHex; }
        public View.OnClickListener getOnClickListener() { return onClickListener; }
    }

    public static void showMenu(@NonNull Context context, String title, List<ActionOption> options) {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_custom_action_menu, null);
        dialog.setContentView(dialogView);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvTitle = dialogView.findViewById(R.id.tv_dialog_action_title);
        ImageView btnClose = dialogView.findViewById(R.id.btn_dialog_action_close);
        LinearLayout optionsContainer = dialogView.findViewById(R.id.ll_dialog_options_container);

        if (tvTitle != null) {
            tvTitle.setText(title != null ? title : "Acciones");
        }

        if (btnClose != null) {
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        if (optionsContainer != null && options != null) {
            optionsContainer.removeAllViews();
            for (final ActionOption opt : options) {
                View itemView = LayoutInflater.from(context).inflate(R.layout.item_dialog_action_option, optionsContainer, false);
                TextView tvIcon = itemView.findViewById(R.id.tv_option_icon);
                TextView tvText = itemView.findViewById(R.id.tv_option_text);

                if (tvIcon != null) tvIcon.setText(opt.getIcon());
                if (tvText != null) {
                    tvText.setText(opt.getTitle());
                    tvText.setTextColor(opt.getColorHex());
                }

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (opt.getOnClickListener() != null) {
                            opt.getOnClickListener().onClick(v);
                        }
                    }
                });

                optionsContainer.addView(itemView);
            }
        }

        dialog.show();
    }
}
