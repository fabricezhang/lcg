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

import android.text.Html.ImageGetter;

import androidx.annotation.Nullable;

public class HtmlFormatterBuilder {

    private String html;
    private ImageGetter imageGetter;
    private ClickableSpecialSpan clickableSpecialSpan;
    private DrawPreCodeSpan drawPreCodeSpan;
    private float indent = 24.0f;
    private boolean removeTrailingWhiteSpace = true;

    public String getHtml() {
        return html;
    }

    public ImageGetter getImageGetter() {
        return imageGetter;
    }

    public ClickableSpecialSpan getClickableSpecialSpan() {
        return clickableSpecialSpan;
    }

    public DrawPreCodeSpan getDrawPreCodeSpan() {
        return drawPreCodeSpan;
    }

    public float getIndent() {
        return indent;
    }

    public boolean isRemoveTrailingWhiteSpace() {
        return removeTrailingWhiteSpace;
    }

    public HtmlFormatterBuilder setHtml(@Nullable final String html) {
        this.html = html;
        return this;
    }

    public HtmlFormatterBuilder setImageGetter(@Nullable final ImageGetter imageGetter) {
        this.imageGetter = imageGetter;
        return this;
    }

    public HtmlFormatterBuilder setClickableSpecialSpan(@Nullable final ClickableSpecialSpan clickableSpecialSpan) {
        this.clickableSpecialSpan = clickableSpecialSpan;
        return this;
    }

    public HtmlFormatterBuilder setDrawPreCodeSpan(@Nullable final DrawPreCodeSpan drawPreCodeSpan) {
        this.drawPreCodeSpan = drawPreCodeSpan;
        return this;
    }

    public HtmlFormatterBuilder setIndent(final float indent) {
        this.indent = indent;
        return this;
    }

    public HtmlFormatterBuilder setRemoveTrailingWhiteSpace(final boolean removeTrailingWhiteSpace) {
        this.removeTrailingWhiteSpace = removeTrailingWhiteSpace;
        return this;
    }
}