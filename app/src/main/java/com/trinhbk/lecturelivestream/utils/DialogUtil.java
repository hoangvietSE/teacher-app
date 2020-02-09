package com.trinhbk.lecturelivestream.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.ArrayRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import java.util.ArrayList;

public class DialogUtil {
    private DialogUtil(){}
    public static void showMessageDialog(
            Context context,
            @StringRes int titleRes,
            @StringRes int messageRes,
            @StringRes int positiveRes,
            DialogInterface.OnClickListener listener
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleRes)
                .setMessage(messageRes)
                .setPositiveButton(positiveRes, listener);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showConfirmDialog(
            Context context,
            @StringRes int titleRes,
            @StringRes int messageRes,
            @StringRes int positiveRes,
            @StringRes int negativeRes,
            DialogInterface.OnClickListener positiveListener,
            DialogInterface.OnClickListener negativeListener
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleRes)
                .setCancelable(true)
                .setMessage(messageRes)
                .setPositiveButton(positiveRes, positiveListener)
                .setNegativeButton(negativeRes, negativeListener);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showConfirmDialog(
            Context context,
            @StringRes int titleRes,
            @StringRes int messageRes,
            @DrawableRes int icon,
            @StringRes int positiveRes,
            @StringRes int negativeRes,
            DialogInterface.OnClickListener positiveListener,
            DialogInterface.OnClickListener negativeListener
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleRes)
                .setIcon(icon)
                .setCancelable(true)
                .setMessage(messageRes)
                .setPositiveButton(positiveRes, positiveListener)
                .setNegativeButton(negativeRes, negativeListener);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showChooseItemDialog(
            Context context,
            @StringRes int titleRes,
            @ArrayRes int itemRes,
            DialogInterface.OnClickListener listener
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleRes)
                .setItems(itemRes, listener);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showMultiChoiceItemsDialog(
            Context context,
            @StringRes int titleRes,
            @ArrayRes int itemRes,
            @StringRes int positiveRes,
            @StringRes int negativeRes,
            ChoiceItemListener listener
    ) {
        ArrayList mSelectedItems = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMultiChoiceItems(itemRes, null, (dialogInterface, position, isChecked) -> {
            if (isChecked) {
                mSelectedItems.add(position);
            } else if (mSelectedItems.contains(position)) {
                mSelectedItems.remove(Integer.valueOf(position));
            }
        })
                .setPositiveButton(positiveRes, (dialog, position) -> {
                    listener.onPositiveClick(mSelectedItems);
                })
                .setNegativeButton(negativeRes, (dialog, position) -> {
                    listener.onNegativeClick();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showSingleChoiceItemsDialog(
            Context context,
            @StringRes int titleRes,
            @ArrayRes int itemRes,
            DialogInterface.OnClickListener listener
    ) {
        ArrayList mSelectedItems = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setSingleChoiceItems(itemRes, 0, listener);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    interface ChoiceItemListener {
        void onPositiveClick(ArrayList mSelectedItems);

        void onNegativeClick();
    }
}
