import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;
import org.jsoup.Connection;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
      Document doc =Jsoup.connect("https://wikipedia.org/wiki/DMOZ")
              .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
              .header("Accept-Language", "*")
              .get();

      ArrayList<Element> elements = doc.select("a[href]");
      for (Element element : elements) {
          System.out.println(element.attr("href"));
      }

    }
}
