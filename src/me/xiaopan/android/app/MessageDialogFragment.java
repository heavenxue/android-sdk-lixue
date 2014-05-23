/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaopan.android.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.Locale;

public class MessageDialogFragment extends DialogFragment {
    public static int BEST_TIMES = 1000;
    public static String FRAGMENT_TAG_MESSAGE_DIALOG = "FRAGMENT_TAG_MESSAGE_DIALOG";
    public static String DEFAULT_CONFIRM_BUTTON_NAME_CHINA = "确定";
    public static String DEFAULT_CONFIRM_BUTTON_NAME_OTHER = "Confirm";

    private long showTime;
    private Builder builder;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog messageDialog = new AlertDialog.Builder(getActivity()).create();
        applyParams(messageDialog);
        return messageDialog;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        showTime = System.currentTimeMillis();
        super.show(manager, tag);
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        showTime = System.currentTimeMillis();
        return super.show(transaction, tag);
    }

    @Override
    public void dismiss() {
        long dismissTime = System.currentTimeMillis();
        int useTime = (int) (dismissTime - showTime);
        if(useTime < BEST_TIMES){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MessageDialogFragment.super.dismiss();
                }
            }, BEST_TIMES - useTime);
        }else{
            super.dismiss();
        }
    }

    /**
     * 设置Builder
     * @param builder Builder
     */
    private void setBuilder(Builder builder) {
        this.builder = builder;
    }

    /**
     * 应用参数
     * @param messageDialog 对话框
     */
    private void applyParams(AlertDialog messageDialog){
        if(builder == null) throw new IllegalArgumentException("builder 为null 你需要调用setBuilder()方法设置Builder");
        if(builder.getMessage() == null) throw new IllegalArgumentException("请调用Builder.setMessage()设置消息");

        messageDialog.setTitle(builder.getTitle());
        messageDialog.setMessage(builder.getMessage());
        messageDialog.setButton(AlertDialog.BUTTON_POSITIVE, builder.getConfirmButtonName()!=null?builder.getConfirmButtonName():(Locale.CHINA.equals(Locale.getDefault())?DEFAULT_CONFIRM_BUTTON_NAME_CHINA:DEFAULT_CONFIRM_BUTTON_NAME_OTHER), builder.getConfirmButtonClickListener());
        messageDialog.setButton(AlertDialog.BUTTON_NEGATIVE, builder.getCancelButtonName(), builder.getCancelButtonClickListener());
        messageDialog.setOnCancelListener(builder.getOnCancelListener());
        messageDialog.setOnDismissListener(builder.getOnDismissListener());
        messageDialog.setOnKeyListener(builder.getOnKeyListener());
        messageDialog.setOnShowListener(builder.getOnShowListener());
        setCancelable(builder.isCancelable());
    }

    /**
     * 更新
     */
    public void update(){
        Dialog dialog = getDialog();
        if(dialog == null || !(dialog instanceof AlertDialog)) return;
        applyParams((AlertDialog) dialog);
    }

    /**
     * 显示一个消息对话框
     * @param fragmentManager Fragment管理器
     * @param builder 构建器
     */
    public static void show(FragmentManager fragmentManager, MessageDialogFragment.Builder builder){
        if(fragmentManager == null) return;

        Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG_MESSAGE_DIALOG);
        boolean old = fragment != null && fragment instanceof MessageDialogFragment;
        MessageDialogFragment messageDialogFragment = old?(MessageDialogFragment) fragment:new MessageDialogFragment();
        messageDialogFragment.setBuilder(builder);
        if(old){
            messageDialogFragment.update();
        }else{
            messageDialogFragment.show(fragmentManager, FRAGMENT_TAG_MESSAGE_DIALOG);
        }
    }

    /**
     * 关闭消息对话框
     * @param fragmentManager Fragment管理器
     */
    public static void close(FragmentManager fragmentManager){
        if(fragmentManager == null) return;

        Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG_MESSAGE_DIALOG);
        if(fragment != null && fragment instanceof MessageDialogFragment){
            ((MessageDialogFragment) fragment).dismiss();
        }
    }

    public static class Builder{
        private String title;
        private String message;
        private String confirmButtonName;
        private String cancelButtonName;
        private boolean cancelable = true;
        private DialogInterface.OnClickListener confirmButtonClickListener;
        private DialogInterface.OnClickListener cancelButtonClickListener;
        private DialogInterface.OnShowListener onShowListener;
        private DialogInterface.OnDismissListener onDismissListener;
        private DialogInterface.OnCancelListener onCancelListener;
        private DialogInterface.OnKeyListener onKeyListener;

        public Builder(String message) {
            this.message = message;
        }

        /**
         * 设置标题
         * @param title 标题
         * @return Builder
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * 设置消息
         * @param message 消息
         * @return Builder
         */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * 设置确定按钮名称
         * @param confirmButtonName 确定按钮名称
         * @return Builder
         */
        public Builder setConfirmButtonName(String confirmButtonName) {
            this.confirmButtonName = confirmButtonName;
            return this;
        }

        /**
         * 设置取消按钮名称
         * @param cancelButtonName 取消按钮名称
         * @return Builder
         */
        public Builder setCancelButtonName(String cancelButtonName) {
            this.cancelButtonName = cancelButtonName;
            return this;
        }

        /**
         * 设置是否可以取消
         * @param cancelable 是否可以取消
         * @return Builder
         */
        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        /**
         * 设置确定按钮点击监听器
         * @param confirmButtonClickListener 确定按钮点击监听器
         * @return Builder
         */
        public Builder setConfirmButtonClickListener(DialogInterface.OnClickListener confirmButtonClickListener) {
            this.confirmButtonClickListener = confirmButtonClickListener;
            return this;
        }

        /**
         * 设置取消按钮点击监听器
         * @param cancelButtonClickListener 取消按钮点击监听器
         * @return Builder
         */
        public Builder setCancelButtonClickListener(DialogInterface.OnClickListener cancelButtonClickListener) {
            this.cancelButtonClickListener = cancelButtonClickListener;
            return this;
        }

        /**
         * 获取标题
         * @return 标题
         */
        public String getTitle() {
            return title;
        }

        /**
         * 获取消息
         * @return 消息
         */
        public String getMessage() {
            return message;
        }

        /**
         * 获取确定按钮名称
         * @return 确定按钮名称
         */
        public String getConfirmButtonName() {
            return confirmButtonName;
        }

        /**
         * 获取取消按钮名称
         * @return 取消按钮名称
         */
        public String getCancelButtonName() {
            return cancelButtonName;
        }

        /**
         * 是否可以取消
         * @return 是否可以取消
         */
        public boolean isCancelable() {
            return cancelable;
        }

        /**
         * 获取确定按钮点击监听器
         * @return 确定按钮点击监听器
         */
        public DialogInterface.OnClickListener getConfirmButtonClickListener() {
            return confirmButtonClickListener;
        }

        /**
         * 获取取消按钮点击监听器
         * @return 取消按钮点击监听器
         */
        public DialogInterface.OnClickListener getCancelButtonClickListener() {
            return cancelButtonClickListener;
        }

        public DialogInterface.OnShowListener getOnShowListener() {
            return onShowListener;
        }

        public Builder setOnShowListener(DialogInterface.OnShowListener onShowListener) {
            this.onShowListener = onShowListener;
            return this;
        }

        public DialogInterface.OnDismissListener getOnDismissListener() {
            return onDismissListener;
        }

        public Builder setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
            this.onDismissListener = onDismissListener;
            return this;
        }

        public DialogInterface.OnCancelListener getOnCancelListener() {
            return onCancelListener;
        }

        public Builder setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
            this.onCancelListener = onCancelListener;
            return this;
        }

        public DialogInterface.OnKeyListener getOnKeyListener() {
            return onKeyListener;
        }

        public Builder setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
            this.onKeyListener = onKeyListener;
            return this;
        }

        /**
         * 显示
         * @param fragmentManager Fragment管理器
         */
        public void show(FragmentManager fragmentManager){
            MessageDialogFragment.show(fragmentManager, this);
        }
    }
}