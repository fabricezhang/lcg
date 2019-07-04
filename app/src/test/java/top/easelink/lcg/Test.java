package top.easelink.lcg;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Iterator;

public class Test {

    @org.junit.Test
    public void atest() {
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
