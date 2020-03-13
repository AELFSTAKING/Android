package io.alf.exchange.util;

import android.content.Context;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import io.alf.exchange.R;
import io.cex.exchange.kotlin.coreutil.TradePairsKt;
import io.tick.base.util.DensityUtil;


public class SpannableUtils {

    public static void setHtmlTextView(TextView textView, String content, String linkUrl) {
        String link = "<a href='%s'>%s</a>";
        textView.setText(Html.fromHtml(String.format(link, linkUrl, content)));
    }

    public static void setTextView(TextView textView, String firstString, int firstSize,
            int firstColor,
            String secondString, int secondSize, int secondColor, boolean hasSpace) {
        String source = hasSpace ? firstString + " " + secondString : firstString + secondString;
        SpannableString spannableString = new SpannableString(source);
        spannableString.setSpan(new AbsoluteSizeSpan(firstSize, true), 0, firstString.length(),
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(firstColor), 0, firstString.length(),
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(secondSize, true),
                spannableString.length() - secondString.length(), spannableString.length(),
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(secondColor),
                spannableString.length() - secondString.length(), spannableString.length(),
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        textView.setText(spannableString);
    }

    public static void setTextView(TextView textView, String firstString, int firstSize,
            String secondString, int secondSize, boolean hasSpace) {
        String source = hasSpace ? firstString + " " + secondString : firstString + secondString;
        SpannableString spannableString = new SpannableString(source);
        spannableString.setSpan(new AbsoluteSizeSpan(firstSize, true), 0, firstString.length(),
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(secondSize, true),
                spannableString.length() - secondString.length(), spannableString.length(),
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        textView.setText(spannableString);
    }

    public static void setTextView(TextView view, String bitPrice, String smallPrice) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(bitPrice);
        if (!TextUtils.isEmpty(smallPrice)) {
            buffer.append("  ");
            buffer.append(smallPrice);
        }
        String s = buffer.toString();
        SpannableString spannableString = new SpannableString(s);
        RelativeSizeSpan sizeSpan01 = new RelativeSizeSpan(1f);
        RelativeSizeSpan sizeSpan02 = new RelativeSizeSpan(0.8f);
        spannableString.setSpan(sizeSpan01, 0, bitPrice.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(sizeSpan02, bitPrice.length(), s.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        view.setText(spannableString);
    }

    public static void setTextView(TextView tv, String content, Context context, int startPx,
            int endPx) {
        if (!TextUtils.isEmpty(content)) {
            String first = TradePairsKt.splitTradePair(content).getFirst();
            SpannableString msp = new SpannableString(content);
            msp.setSpan(new AbsoluteSizeSpan(DensityUtil.dp2px(startPx)), 0, first.length(),
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            msp.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(context, R.color.textPrimary)),
                    0, first.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            msp.setSpan(new AbsoluteSizeSpan(DensityUtil.dp2px(endPx)), first.length(),
                    content.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            tv.setText(msp);
        } else {
            tv.setText(context.getResources().getString(R.string.ganggang));
        }

    }

    public static void setShowTextView(TextView tv, String content, Context context, int startSp,
            int endSp) {
        if (!TextUtils.isEmpty(content)) {
            SpannableString msp = new SpannableString(content);
            msp.setSpan(new AbsoluteSizeSpan(DensityUtil.sp2px(context, startSp)), 0,
                    content.indexOf("/"), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            msp.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(context, R.color.textPrimary)),
                    0, content.indexOf("/"), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            msp.setSpan(new AbsoluteSizeSpan(DensityUtil.sp2px(context, endSp)),
                    content.indexOf("/"), content.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            tv.setText(msp);
        } else {
            tv.setText(context.getResources().getString(R.string.ganggang));
        }
    }

    /**
     * 历史委托里面的
     */
    public static void setTextViewStyle(TextView tv, String content, Context context, int startPx,
            int endPx, boolean isFlag) {
        if (!TextUtils.isEmpty(content)) {
            SpannableString msp = new SpannableString(content);
            String first = TradePairsKt.splitTradePair(content).getFirst();
            msp.setSpan(new AbsoluteSizeSpan(DensityUtil.dp2px(startPx)), 0, first.length(),
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            if (isFlag) {
                msp.setSpan(
                        new ForegroundColorSpan(ContextCompat.getColor(context, R.color.textHint)),
                        0, first.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            } else {
                msp.setSpan(new ForegroundColorSpan(
                                ContextCompat.getColor(context, R.color.textPrimary)), 0,
                        first.length(),
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
            msp.setSpan(new AbsoluteSizeSpan(DensityUtil.dp2px(endPx)), first.length(),
                    content.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            tv.setText(msp);
        } else {
            tv.setText(context.getResources().getString(R.string.ganggang));
        }

    }

    /**
     * 设置背景
     *
     * @param tv      控件view
     * @param content 内容
     * @param start   开始的字段
     * @param context 上下文
     */
    public static void setTextViewBackGround(TextView tv, String content, String start,
            Context context) {
        if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(start)) {
            SpannableString msp = new SpannableString(content);
            msp.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(context, R.color.textPrimary)),
                    0, start.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            msp.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(context, R.color.textSecondary)),
                    start.length(), content.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            tv.setText(msp);
        } else {
            tv.setText(context.getResources().getString(R.string.ganggang));
        }
    }
}
