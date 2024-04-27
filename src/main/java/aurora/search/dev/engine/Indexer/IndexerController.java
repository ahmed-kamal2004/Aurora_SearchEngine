package aurora.search.dev.engine.Indexer;

import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IndexerController {
    public static void main(String[] args) throws IOException {
        PreIndexer preIndexer = new PreIndexer();
        // dummy url -> to be changed with url foreach in crawledUrls collection
        //////////////////////////////////////////////////////////
        org.jsoup.nodes.Document doc = Jsoup.connect("https://colah.github.io/posts/2015-08-Understanding-LSTMs/").get();
        org.jsoup.nodes.Document pageDoc = Jsoup.parse(doc.html());
        Elements allPageElems = pageDoc.getAllElements();
        String paragraph = "";
        String elemntName = "", elementText, cleanElemText;
        for (Element element : allPageElems) {

            elemntName = element.nodeName();
            if (elemntName.equals("a")) {
                continue; // skip links
            }
            elementText = element.ownText();
            if (elementText.isEmpty()) {
                continue; // skip empty text
            }
            paragraph = paragraph + elementText + " ";
        }
        //////////////////////////////////////////////

        //System.out.println(paragraph);
        String cleanedPage = preIndexer.cleanPage(paragraph);
        List<String> tokenizedWords = preIndexer.tokenizeIt(cleanedPage);
        //System.out.println(tk);
        List<String> nonStopWords = preIndexer.removeStopWords(tokenizedWords);
        //System.out.println(tk);
        List<String> stemWords = preIndexer.StemIt(nonStopWords);
        //System.out.println(stemWords);
    }
}
