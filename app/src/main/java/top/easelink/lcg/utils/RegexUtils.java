package top.easelink.lcg.utils;

import android.text.TextUtils;
import androidx.annotation.Nullable;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author : junzhang
 * date   : 2019-07-12 19:24
 * desc   :
 */
public class RegexUtils {

    @Nullable
    public static ArrayList<String> extractInfoFrom(String content, String patternStr) {
        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(patternStr)) {
            return null;
        }
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(content);
        ArrayList<String> urls = new ArrayList<>();
        while (matcher.find()) {
            urls.add(matcher.group());
        }
        for(String s: urls) {
            Timber.d(s);
        }
        return urls;
    }


}
