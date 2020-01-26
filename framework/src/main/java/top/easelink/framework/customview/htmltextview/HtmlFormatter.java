/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.easelink.framework.customview.htmltextview;

import android.content.Context;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

public class HtmlFormatter {

    private HtmlFormatter() {
    }

    public static Spanned formatHtml(@NonNull HtmlFormatterBuilder builder) {
        return formatHtml(
                builder.getHtml(),
                builder.getImageGetter(),
                builder.getClickableSpecialSpan(),
                builder.getDrawTableLinkSpan(),
                builder.getIndent(),
                builder.isRemoveTrailingWhiteSpace(),
                null,
                null,
                null);
    }

    public static Spanned formatHtml(
            @Nullable String html,
            ImageGetter imageGetter,
            ClickableSpecialSpan clickableSpecialSpan,
            DrawTableLinkSpan drawTableLinkSpan,
            float indent,
            boolean removeTrailingWhiteSpace,
            @Nullable Context context,
            @Nullable OnImgTagClickListener onImgTagClickListener,
            @Nullable OnLinkTagClickListener onLinkTagClickListener) {
        final HtmlTagHandler htmlTagHandler = new HtmlTagHandler();
        htmlTagHandler.setClickableSpecialSpan(clickableSpecialSpan);
        htmlTagHandler.setDrawTableLinkSpan(drawTableLinkSpan);
        htmlTagHandler.setListIndentPx(indent);

        html = htmlTagHandler.overrideTags(html);

        Spanned formattedHtml;
        if (removeTrailingWhiteSpace) {
            formattedHtml = removeHtmlBottomPadding(Html.fromHtml(html, imageGetter, htmlTagHandler));
        } else {
            formattedHtml = Html.fromHtml(html, imageGetter, htmlTagHandler);
        }
        if (formattedHtml instanceof SpannableStringBuilder) {
            if (onImgTagClickListener != null) {
                ImageSpan[] spans = formattedHtml.getSpans(0, formattedHtml.length(), ImageSpan.class);
                for (ImageSpan span : spans) {
                    String url = span.getSource();
                    int pStart = formattedHtml.getSpanStart(span);
                    int pEnd = formattedHtml.getSpanEnd(span);
                    ImageClickSpan imageClickSpan = new ImageClickSpan(context, url, pStart);
                    imageClickSpan.setListener(onImgTagClickListener);
                    ((SpannableStringBuilder) formattedHtml).setSpan(imageClickSpan, pStart, pEnd, SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            if (onLinkTagClickListener != null) {
                URLSpan[] urlSpans = formattedHtml.getSpans(0, formattedHtml.length(), URLSpan.class);
                for (URLSpan span : urlSpans) {
                    String url = span.getURL();
                    int pStart = formattedHtml.getSpanStart(span);
                    int pEnd = formattedHtml.getSpanEnd(span);
                    ((SpannableStringBuilder) formattedHtml).removeSpan(span);
                    LinkClickSpan linkClickSpan = new LinkClickSpan(context, url);
                    linkClickSpan.setListener(onLinkTagClickListener);
                    ((SpannableStringBuilder) formattedHtml).setSpan(linkClickSpan, pStart, pEnd, Spanned.SPAN_POINT_POINT);
                }
            }
        }
        return formattedHtml;
    }

    /**
     * Html.fromHtml sometimes adds extra space at the bottom.
     * This methods removes this space again.
     * See https://github.com/SufficientlySecure/html-textview/issues/19
     */
    @Nullable
    private static Spanned removeHtmlBottomPadding(@Nullable Spanned text) {
        if (text == null) {
            return null;
        }

        while (text.length() > 0 && text.charAt(text.length() - 1) == '\n') {
            text = (Spanned) text.subSequence(0, text.length() - 1);
        }
        return text;
    }
}
