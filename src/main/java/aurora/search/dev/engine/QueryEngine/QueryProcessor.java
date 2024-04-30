package aurora.search.dev.engine.QueryEngine;

import aurora.search.dev.engine.DataBase.MongoDB;
import aurora.search.dev.engine.Helper.Constants;
import ca.rmen.porterstemmer.PorterStemmer;
import com.mongodb.client.MongoCollection;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class QueryProcessor {
    private final PorterStemmer porterStemmer;
    private final MongoDB database;

    private final HashSet<String> StopWords;

    String[] tokens;
    private final HashSet<Document> matchingDocs=new HashSet<>();
    private final List<String> queryTerms = new ArrayList<>();
    private final List<String> queryTermsMeaning = new ArrayList<>();
    HashSet<String>Searched=new HashSet<>();
    List<String> QueryWords=new ArrayList<String>();
    public QueryProcessor() {

        porterStemmer=new PorterStemmer();
        database=new MongoDB(Constants.DATABASE_NAME);
        StopWords=getStopWords();
    }
    private void preprocessQuery(String query) throws IOException {
        HashSet<String>Meanings;
        tokens = query.toLowerCase().split("\\s+");
        SetQueryWordsDB();
        for (String token : tokens) {
            if (!StopWords.contains(token) ) {
                queryTerms.add(token);
                Meanings = getMeaning(token);
                queryTermsMeaning.addAll(Meanings);
            }
        }
    }
    boolean findExactWord (String exactWord)
    {
        boolean found =false;
        MongoCollection<Document> collection = database.getWordsCollection();

        String stemmedWord = porterStemmer.stemWord(exactWord);
        Document query1 = new Document("word", stemmedWord);
        Document query2 = new Document("word", exactWord);

        Document cursor =  collection.find(query1).first();
        if(cursor == null) cursor = collection.find(query2).first();
        if (cursor !=null) {
            List<Document> pages = (List<Document>) cursor.get("pages");
            for (Document page : pages) {
                var originalWords = (List<?>) page.get("originalWords");
                if (originalWords.contains(exactWord)) {
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    public void SetQueryWordsDB()
    {
        for (String token:tokens)
        {
            if (!StopWords.contains(token)&&findExactWord(token))
            {
                QueryWords.add(token);
            }
        }
    }

    private void searchIndex(List<String> queryTerms) {

        for (String term : queryTerms) {
            String stemmedToken = porterStemmer.stemWord(term);
            if(!Searched.contains(stemmedToken))
            {
                Searched.add(stemmedToken);
                for (Document doc : database.getWordsCollection().find(new Document("word", stemmedToken))) {
                    matchingDocs.add(doc);
                }
            }
        }
    }
    public HashSet<Document> processQuery(String query) throws IOException {
        preprocessQuery(query);
        searchIndex(queryTerms);
        searchIndex(queryTermsMeaning);
        return matchingDocs;
    }

    public HashSet<Integer> extractUrlsId( ){
        HashSet<Integer> urlsID = new HashSet<>();
        for (Document matchingDoc : matchingDocs) {
            List<Document> pages = (List<Document>) matchingDoc.get("pages");
            for (Document page : pages) {
                urlsID.add(((Integer) page.get("urlId")));
            }
        }
        return urlsID;
    }
    public void showOutputs(HashSet<Integer> urlsID ){
        MongoCollection<Document> indexedUrlsCollection = database.getIndexedUrlsCollection();

        for (Integer id : urlsID) {
            Document query = new Document("_id", id);
            Document doc = indexedUrlsCollection.find(query).first();
            if (doc != null){
                System.out.println("URL : " + doc.get("url") );
                System.out.println("Title : " + doc.get("title") );
                System.out.println("Description : " + doc.get("description") );
            }
        }
    }
    public static HashSet<String>  getStopWords() {
        HashSet<String> stopWordsSet=new HashSet<String>();
        try {
            File stopFile = new File("StopWords.txt").getAbsoluteFile();
            Scanner reader = new Scanner(stopFile);
            while (reader.hasNextLine()) {
                String word = reader.nextLine();
                stopWordsSet.add(word);
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("An exception occurred while reading the stop words!");
            e.printStackTrace();
        }
        return stopWordsSet;
    }
    public static HashSet<String> getMeaning(String word) throws IOException {
        HashSet<String> synonyms = new HashSet<String >();
        String path = "C:\\dict";
        IDictionary dict = new Dictionary(new File(path));
        dict.open();
        IIndexWord idxWord = dict.getIndexWord(word, POS.NOUN);
        if (idxWord != null) {
            for (IWordID wordID : idxWord.getWordIDs()) {
                IWord iword = dict.getWord(wordID);
                ISynset synset = iword.getSynset();
                for (IWord w : synset.getWords()) {
                    synonyms.add(w.getLemma());
                }
            }
        }
        dict.close();
        return synonyms;
    }



    public static void main(String[] args) throws IOException {
        String testQuery = "reasons for football art learning";
        QueryProcessor q=new QueryProcessor();
        HashSet<Document> womenFootball = q.processQuery(testQuery);
        womenFootball.forEach(x-> System.out.println(x.toString()));
        HashSet<Integer> urlId = q.extractUrlsId();
        q.showOutputs(urlId);
    }
}