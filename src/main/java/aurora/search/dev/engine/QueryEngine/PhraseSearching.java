package aurora.search.dev.engine.QueryEngine;

import aurora.search.dev.engine.DataBase.MongoDB;
import aurora.search.dev.engine.Helper.Constants;
import org.bson.Document;
import java.io.IOException;
import java.util.*;
public class PhraseSearching {
    /** Can be used for testing */
    //List<String> allContent = new ArrayList<>();
    //List<Integer> Urls = new ArrayList<>();
    //HashSet<Integer> finalUrls = new HashSet<>();

    List<String> queryTerms;
    HashMap<Integer, ArrayList<Integer>> results = new HashMap<>();
    //HashMap<Integer, ArrayList<String>> matchingParagraphs = new HashMap<>();
    HashMap<Integer, Double> Url_Score = new HashMap<>();
    HashMap<Integer, Integer> Url_ParagraphID = new HashMap<>();
    HashSet<String> stopWords;
    String Phrase;
    QueryProcessor q;
    private final MongoDB database;
    HashSet<Document> matchingDocs;
    List<String> tokens;
    public PhraseSearching(String Phrase) throws IOException {
        database = new MongoDB(Constants.DATABASE_NAME);
        this.Phrase = Phrase;
        q = new QueryProcessor(true); // the true flag used to search for the exact word not meaning not stemmed
        tokens = List.of(Phrase.toLowerCase().split("\\s+"));
        queryTerms = q.SetQueryWordsDB(tokens);
        stopWords = QueryProcessor.getStopWords();
        String firstWord = getFirstWord(Phrase);
        if(firstWord.isEmpty())
            firstWord = Phrase;
        matchingDocs = q.processQuery(firstWord);
        getContent(firstWord);
        this.PhraseSearch(Phrase);
    }
    String getFirstWord(String query) {
        String firstNonStopWord = "";
        String[] words = query.split("\\s+");
        for (String word : words) {
            if (!word.isEmpty() && !stopWords.contains(word)) {
                firstNonStopWord = word;
                break;
            }
        }
        return firstNonStopWord;
    }

    void getContent(String firstWord) {
        HashSet<Integer> vis = new HashSet<>();
        for (Document doc : matchingDocs) {
            List<Document> pages = (List<Document>) doc.get("pages");
            for (Document page : pages) {
                int currId =(Integer) page.get("urlId");
                //Urls.add(currId); -> for testing
                ArrayList<Integer> matchingParIndex = new ArrayList<>();
                var originalWords = (List<String>) page.get("originalWords");
                var parIndexes = (List<Integer>) page.get("paragraphIndexes");
                for (int i = 0; i < originalWords.size(); i++) {
                    if (Objects.equals(originalWords.get(i), firstWord.toLowerCase())) {
                        int parIndex = parIndexes.get(i);
                        if(!vis.contains(parIndex)) {
                            vis.add(parIndex);
                            //String currParagraph = database.getParagraph(parIndex);
                            matchingParIndex.add(parIndex);
                            //allContent.add(currParagraph);
                        }
                    }
                }
                results.put(currId, matchingParIndex);
            }
        }
    }

    public void PhraseSearch(String original_query) {
        for(int urlId : results.keySet()){
            //ArrayList<String> ans = new ArrayList<>();
            for(Integer ParIndex: results.get(urlId)){
                String current_content = database.getParagraph(ParIndex);
                if (current_content.toLowerCase(Locale.ROOT).contains(original_query.toLowerCase(Locale.ROOT))) {
                    //ans.add(current_content);
                    //matchingParagraphs.put(urlId, ans);
                    //finalUrls.add(urlId);
                    Url_ParagraphID.put(urlId, ParIndex);
                    Url_Score.put(urlId, 1.0);
                }
            }
        }
    }
}

