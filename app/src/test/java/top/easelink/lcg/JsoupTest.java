package top.easelink.lcg;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Iterator;

public class JsoupTest {

    private static String getLabelText(Elements paramElements, String paramString) {
        Element element = paramElements.select(paramString).first();
        if (element != null) {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("firstEle = ");
            localStringBuilder.append(element.text());
            System.out.println(localStringBuilder.toString());
            return element.text();
        }
        return "";
    }

    private static String getAttrText(Elements paramElements, String paramString1, String attr) {
        Element element = paramElements.select(paramString1).first();
        if (element != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("firstEle = ");
            sb.append(element.attr(attr));
            System.out.println(paramElements.toString());
            return element.text();
        }
        return "";
    }

    @org.junit.Test
    public void atest() {
        try {

            Document document = Jsoup.connect("https://www.52pojie.cn/forum.php?mod=guide&view=digest").get();
            Elements elements = document.select("div#threadlist");
            int i = 0;
            Iterator iterator = elements.iterator();
            while (iterator.hasNext()) {
                Object localObject3 = iterator.next();
                Elements localElements = ((Element) localObject3).select("td.by");
                Elements innerElements = ((Element) localObject3).select("th.common");
                localObject3 = ((Element) localObject3).select("td.num");
                getLabelText(elements, ".xst");
                getLabelText(localElements, "a[c]");
                getLabelText(localElements, "em");
                getLabelText(elements, "a.xi2");
                getLabelText(elements, "em");
                getLabelText(localElements, "a[target]");
                getAttrText(elements, "a.xst", "href");
                i++;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("count = ");
            sb.append(i);
            System.out.println(sb.toString());
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }

    @org.junit.Test
    public void TestThread() {
        try {
            Object localObject1 = Jsoup.connect("https://www.52pojie.cn/thread-943148-1-3.html").get().select("table").select("td.t_f");
            int i = 0;
            Iterator localIterator = ((Elements) localObject1).iterator();
            while (localIterator.hasNext()) {
                Element element = (Element) localIterator.next();
                StringBuilder localStringBuilder = new StringBuilder();
                localStringBuilder.append("html =");
                localStringBuilder.append((element).text());
                System.out.println(localStringBuilder.toString());
                i++;
            }
            localObject1 = new StringBuilder();
            ((StringBuilder) localObject1).append("count = ");
            ((StringBuilder) localObject1).append(i);
            System.out.println((localObject1).toString());
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }
}
