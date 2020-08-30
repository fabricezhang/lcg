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
package top.easelink.framework.customview.htmltextview

import android.text.Html

class HtmlFormatterBuilder {

    var html: String? = null
        private set
    var imageGetter: Html.ImageGetter? = null
        private set
    var clickableSpecialSpan: ClickablePreCodeSpan? = null
        private set
    var drawPreCodeSpan: DrawPreCodeSpan? = null
        private set
    var indent = 24.0f
        private set
    var isRemoveTrailingWhiteSpace = true
        private set

    fun setHtml(html: String?): HtmlFormatterBuilder {
        this.html = html
        return this
    }

    fun setImageGetter(imageGetter: Html.ImageGetter?): HtmlFormatterBuilder {
        this.imageGetter = imageGetter
        return this
    }

    fun setClickableSpecialSpan(clickableSpecialSpan: ClickablePreCodeSpan?): HtmlFormatterBuilder {
        this.clickableSpecialSpan = clickableSpecialSpan
        return this
    }

    fun setDrawPreCodeSpan(drawPreCodeSpan: DrawPreCodeSpan?): HtmlFormatterBuilder {
        this.drawPreCodeSpan = drawPreCodeSpan
        return this
    }

    fun setIndent(indent: Float): HtmlFormatterBuilder {
        this.indent = indent
        return this
    }

    fun setRemoveTrailingWhiteSpace(removeTrailingWhiteSpace: Boolean): HtmlFormatterBuilder {
        isRemoveTrailingWhiteSpace = removeTrailingWhiteSpace
        return this
    }
}