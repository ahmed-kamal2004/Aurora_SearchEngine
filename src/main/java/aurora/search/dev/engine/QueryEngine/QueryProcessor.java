package aurora.search.dev.engine.QueryEngine;

import aurora.search.dev.engine.DataBase.MongoDB;
import aurora.search.dev.engine.Helper.Constants;
import ca.rmen.porterstemmer.PorterStemmer;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class QueryProcessor {
    static boolean exact; /** for Phrase searching to get the exact word not meaning not stemmed **/
    private final PorterStemmer porterStemmer;
    private final MongoDB database;
    private final HashSet<String> StopWords;
    private final HashSet<Document> matchingDocs=new HashSet<>();
    private final List<String> queryTermsMeaning = new ArrayList<>();
    List<String> queryTerms = new ArrayList<>();
    HashMap<Integer, Double> Url_Score = new HashMap<>();
    HashMap<Integer, Integer> Url_ParagraphID = new HashMap<>();
    HashMap<Integer, String> Url_Paragraph = new HashMap<>();
    HashSet<Integer> Url_FoundPar = new HashSet<>();
    HashSet<String>Searched=new HashSet<>();
    List<String> tokens;
    public QueryProcessor(boolean exactFlag) {
        exact = exactFlag; // for Phrase searching to get the exact word not meaning not stemmed
        porterStemmer=new PorterStemmer();
        database=new MongoDB(Constants.DATABASE_NAME);
        StopWords=getStopWords();
    }
    private void preprocessQuery(String query) throws IOException {
        HashSet<String>Meanings;
        tokens = List.of(query.toLowerCase().split("\\s+"));
        queryTerms = SetQueryWordsDB(tokens);
        for (String token : queryTerms) {
            Meanings = getMeaning(token);
            queryTermsMeaning.addAll(Meanings);
        }
    }
    public List<String> SetQueryWordsDB(List<String> tokens)
    {
        List<String> allWords = new ArrayList<>();
        for (String token:tokens)
        {
            if (!StopWords.contains(token))
            {
                allWords.add(token);
            }
        }
        return allWords;
    }

    private void searchIndex(List<String> queryTerms, boolean originalWord) {

        for (String term : queryTerms) {
            String stemmedToken = porterStemmer.stemWord(term);
            if(!Searched.contains(stemmedToken))
            {
                Searched.add(stemmedToken);
                for (Document doc : database.getWordsCollection().find(new Document("word", stemmedToken))) {
                    matchingDocs.add(doc);
                    Double Idf = (Double) doc.get("IDF");
                    if(!originalWord) Idf*=0.8; // to decrease the score of pages gotten by meanings
                    for(Document page : (List<Document>) doc.get("pages")){
                        Double TF = (Double) page.get("TF");
                        Integer urlId = (Integer) page.get("urlId");

                        if(Url_Score.containsKey(urlId)){
                            Double lastScore = Url_Score.get(urlId);
                            Url_Score.put(urlId, lastScore+Idf*TF);
                        }
                        else {
                            Url_Score.put(urlId, Idf*TF);
                        }

                        /** FOR CORRESPONDING PARAGRAPHS **/
                        List<String> originalWords = (List<String>) page.get("originalWords");
                        int index = originalWords.indexOf(term);
                        List<Integer> paragraphIndexes = (List<Integer>) page.get("paragraphIndexes");
                        if(index!=-1&& originalWord){
                            int idx = paragraphIndexes.get(index);
                            Url_Paragraph.put(urlId, database.getParagraph(idx));
                            Url_FoundPar.add(urlId);
                            Url_ParagraphID.put(urlId, idx);
                        }

                        if(!Url_FoundPar.contains(urlId)){
                            int idx = paragraphIndexes.get(0);
                            Url_ParagraphID.put(urlId, idx);
                            String description = database.getParagraph(idx);
                            Url_Paragraph.put(urlId, description);
                        }
                    }
                }
            }
        }
    }
    public HashSet<Document> processQuery(String query) throws IOException {
        preprocessQuery(query);
        searchIndex(queryTerms, true);
        if(!exact)
            searchIndex(queryTermsMeaning, false);
        return matchingDocs;
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

    /** FOR TESTING **/
    /*
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
                Logging.printColored(STR."URL : \{doc.get("url")}", Color.BLUE);
                Logging.printColored(STR."Title: \{doc.get("title")}", Color.YELLOW);
                Logging.printColored(STR."Description: \{doc.get("description")}", Color.YELLOW);
            }
        }
    }
   */


}