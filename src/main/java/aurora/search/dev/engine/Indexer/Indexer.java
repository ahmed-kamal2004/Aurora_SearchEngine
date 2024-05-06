package aurora.search.dev.engine.Indexer;

import aurora.search.dev.engine.DataBase.MongoDB;
import aurora.search.dev.engine.Helper.Constants;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Indexer {
    static int elemTextIndex = -1;
    MongoDB DBController;
    PreIndexer preIndexer;
    private HashMap<String, Document> indexedUrls; // used to keep track the visited urls then add them to db
    private ConcurrentHashMap<String, List<Document>> indexedWords; // used to store each word and the documents that it was appeared in
    public Indexer(MongoDB DB) {
        DBController = DB;
        preIndexer = new PreIndexer();
        synchronized (this) {
            elemTextIndex = DBController.getIndexedParagraphsCount() - 1;
        }
    }

    public void updateWordsCollection() throws InterruptedException {
        class UpdateWordsCollection implements Runnable {

            List<Thread> threads = new ArrayList<Thread>();
            List<List<String>> keys = new ArrayList<List<String>>();
            public UpdateWordsCollection() throws InterruptedException {
                System.out.printf("Unique words: %d\n",indexedWords.keySet().size());
                for (int i = 0; i <  Constants.NUM_OF_THREADS; i++) {
                    keys.add(new ArrayList<String>());
                }
                for (int i = 0; i <  Constants.NUM_OF_THREADS; i++) {
                    Thread thread = new Thread(this);
                    String I = Integer.toString(i, 10);
                    thread.setName(I);
                    threads.add(thread);
                }

                int cnt = 0;
                for (String word : indexedWords.keySet()) {
                    int idx = cnt %  Constants.NUM_OF_THREADS;
                    keys.get(idx).add(word);
                    cnt++;
                }

                for (Thread thread_ : threads) {
                    thread_.start();
                }

                for (Thread thread_ : threads) {
                    thread_.join();
                }
            }
            public void run() {
                try {
                    Thread t = Thread.currentThread();
                    String name = t.getName();
                    int idx = Integer.parseInt(name);
                    for (String word : keys.get(idx)) {
                        DBController.addIndexedWord(word, indexedWords.get(word));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        UpdateWordsCollection uDB = new UpdateWordsCollection();

    }
    public void updateUrlsCollection(String url, Document urlDoc){
        DBController.addIndexedUrl(url , urlDoc);
    }


    public void updateParagraphsCollection(String paragraph, Integer paragraphId) {
        DBController.addIndexedParagraph(paragraph, paragraphId);
    }

    public List<Document> processParagraph(String paragraph, String elemName) {
        List<Document> tempAllWords = new ArrayList<>();
        elemTextIndex++;
        int localElemTextIndex = elemTextIndex;
        updateParagraphsCollection(paragraph, localElemTextIndex);
        String cleanElemText = preIndexer.cleanPage(paragraph);
        List<String> elemWords = preIndexer.tokenizeIt(cleanElemText);
        List<String> elemNoStopWords = preIndexer.removeStopWords(elemWords);
        List<String> finalElemWords = preIndexer.StemIt(elemNoStopWords);
        for (int i = 0; i < finalElemWords.size(); i++) {
            String finalElemWord = finalElemWords.get(i);
            String originalWord = elemNoStopWords.get(i);
            Document elemNameDoc = new Document().append("elemName", elemName);
            Document elemTextDoc = new Document()
                    .append("elemText", paragraph)
                    .append("elemTextIndex", localElemTextIndex);
            Document wordDoc = new Document()
                    .append("word", finalElemWord)
                    .append("originalWord", originalWord)
                    .append("wordIndex", i);
            Document allWordDoc = new Document()
                    .append("elemNameDoc", elemNameDoc)
                    .append("elemTextDoc", elemTextDoc)
                    .append("wordDoc", wordDoc);
            tempAllWords.add(allWordDoc);
        }
        return tempAllWords;
    }

    public void startIndexer(String url, Integer _id) throws InterruptedException, IOException {
        if (DBController.isUrlIndexed(url)) {
            System.out.println("Page already indexed");
            return;
        }
        indexedWords = new ConcurrentHashMap<String, List<Document>>();
        indexedUrls = new HashMap<String, Document>();
        org.jsoup.nodes.Document pageDocument = Jsoup.connect(url).get();
        String title = pageDocument.title();
        Element metaDescription = pageDocument.select("meta[name=description]").first();
        String description = "";
        if (metaDescription != null) {
            description = metaDescription.attr("content");
        } else {
            for (Element paragraph : pageDocument.select("p")) {
                if (paragraph != null) {
                    if (paragraph.text().length() >= Constants.DESCRIPTION_LENGTH){
                        description = paragraph.text();
                        break;
                    }
                }
            }
        }

        Elements allHtmlElements = pageDocument.getAllElements();
        List<Document> allWords = new ArrayList<>();
        int wordIndex;
        String paragraph = "";
        String elemName = "", elemText;
        for (Element htmlElement : allHtmlElements) {
            elemName = htmlElement.nodeName();
            if ("a".equals(elemName)) {
                continue;
            }
            elemText = htmlElement.ownText();
            if(elemText.isEmpty()){
                continue;
            }
            paragraph = paragraph + elemText + " ";
            if (paragraph.length() >= Constants.PARAGRAPH_LENGTH) {
                allWords.addAll(processParagraph(paragraph, elemName));
                paragraph = "";
            }
        }
        if (!paragraph.isEmpty()) { // handling the remaining paragraph(with less than PARAGRAPH_LENGTH) if any
            allWords.addAll(processParagraph(paragraph, elemName));
        }

        int totalWords = allWords.size();
        HashMap<String, Document> wordDocMap = new HashMap<String, Document>();
        for (Document allWordDoc : allWords) {
            String tagType = allWordDoc.get("elemNameDoc", Document.class).getString("elemName");
            Integer paragraphIndex = allWordDoc.get("elemTextDoc", Document.class).getInteger("elemTextIndex");
            String word = allWordDoc.get("wordDoc", Document.class).getString("word");
            String originalWord = allWordDoc.get("wordDoc", Document.class).getString("originalWord");
            wordIndex = allWordDoc.get("wordDoc", Document.class).getInteger("wordIndex");

            List<String> originalWordsArr = new ArrayList<String>();
            List<String> tagTypesArr = new ArrayList<String>();
            List<Integer> paragraphIndexesArr = new ArrayList<Integer>();
            List<Integer> wordIndexesArr = new ArrayList<Integer>();
            if (wordDocMap.containsKey(word)) {
                originalWordsArr = wordDocMap.get(word).getList("originalWordsArr", String.class);
                tagTypesArr = wordDocMap.get(word).getList("tagTypesArr", String.class);
                paragraphIndexesArr = wordDocMap.get(word).getList("paragraphIndexesArr", Integer.class);
                wordIndexesArr = wordDocMap.get(word).getList("wordIndexesArr", Integer.class);
            }

            originalWordsArr.add(originalWord);
            tagTypesArr.add(tagType);
            paragraphIndexesArr.add(paragraphIndex);
            wordIndexesArr.add(wordIndex);
            Document doc = new Document();
            doc.append("originalWordsArr", originalWordsArr)
                    .append("tagTypesArr", tagTypesArr)
                    .append("paragraphIndexesArr", paragraphIndexesArr)
                    .append("wordIndexesArr", wordIndexesArr);
            if (wordDocMap.containsKey(word)) {
                doc.append("TF", wordDocMap.get(word).getInteger("TF") + 1);
            } else {
                doc.append("TF", 1);
            }
            wordDocMap.put(word, doc);
        }

        for (String word : wordDocMap.keySet()) {
            double TF = wordDocMap.get(word).getInteger("TF") / (double) totalWords; // Normalized TF
            List<String> originalWords = wordDocMap.get(word).getList("originalWordsArr", String.class);
            List<String> tagTypes = wordDocMap.get(word).getList("tagTypesArr", String.class);
            List<Integer> paragraphIndexes = wordDocMap.get(word).getList("paragraphIndexesArr", Integer.class);
            List<Integer> wordIndexes = wordDocMap.get(word).getList("wordIndexesArr", Integer.class);

            Document doc = new Document();
            doc.append("urlId", _id)
                    .append("originalWords", originalWords)
                    .append("TF", TF)
                    .append("tagTypes", tagTypes)
                    .append("paragraphIndexes", paragraphIndexes)
                    .append("wordIndexes", wordIndexes);

            if (TF < 0.5) { // Handling spam and dummy redundancy
                if (indexedWords.containsKey(word)) {
                    indexedWords.get(word).add(doc);
                }
                else {
                    List<Document> docArray = new ArrayList<Document>();
                    docArray.add(doc);
                    indexedWords.put(word, docArray);
                }
            }
        }
        Document urlDocument = new Document("_id", _id)
                .append("url", url)
                .append("title", title)
                .append("description", description);
        indexedUrls.put(url, urlDocument);
        updateUrlsCollection(url, urlDocument);
        updateWordsCollection();
    }

}