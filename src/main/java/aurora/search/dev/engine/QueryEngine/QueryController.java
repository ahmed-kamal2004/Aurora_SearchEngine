package aurora.search.dev.engine.QueryEngine;

import aurora.search.dev.engine.DataBase.MongoDB;
import aurora.search.dev.engine.Helper.Constants;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueryController {
    private static List<String> allWords;
    private static HashMap<Integer, Double> Url_Score;
    private static HashMap<Integer, Integer> Url_ParagraphID;

    public record allNeeded(
            String url,
            Double Idf_TF,
            String title,
            String paragraph
    ) {}
    public List<allNeeded> getQueryResults(String query) throws IOException {
        startEngine(query);
        MongoDB db = new MongoDB(Constants.DATABASE_NAME);
        List<allNeeded> results = new ArrayList<>();
        for (int id : Url_Score.keySet()) {
            Double Idf_TF = Url_Score.get(id);
            Document UrlDocument = db.getUrlDocument(id);
            String url = (String) UrlDocument.get("url");
            String title = (String) UrlDocument.get("title");
            int parID = Url_ParagraphID.get(id);
            String paragraph = db.getParagraph(parID);
            allNeeded curr = new allNeeded(url, Idf_TF, title, paragraph);
            results.add(curr);
        }
        return results;
    }
    public void startEngine(String query) throws IOException {
        if (query.isEmpty()) System.out.println("PLEASE, Enter a String");
        else {
            if (query.charAt(0) == '\"' && query.charAt(query.length() - 1) == '\"') {
                query = query.replaceAll("^\"|\"$", "");// remove quotation
                PhraseSearching p = new PhraseSearching(query);
                allWords = p.queryTerms;
                Url_Score = p.Url_Score;
                Url_ParagraphID = p.Url_ParagraphID;
            } else {
                QueryProcessor q = new QueryProcessor(false);
                q.processQuery(query);
                allWords = q.queryTerms;
                Url_Score = q.Url_Score;
                Url_ParagraphID = q.Url_ParagraphID;
            }
        }
        /*System.out.println(allWords);
        System.out.println(Url_ParagraphID);
        System.out.println(Url_Score);*/
    }

    //Those three functions to be used from ranker and interface
    public HashMap<Integer, Double> getURLScores(){ return Url_Score;}
    public HashMap<Integer, Integer> getURLParagraphsID(){ return Url_ParagraphID;}
    public List<String> getQueryWords(){ return allWords;}

    public static void main(String[] args) throws IOException {
        // for test
        /*String query = "machine learning routine";
        QueryController queryCont = new QueryController();
        List<allNeeded> finalRecords = queryCont.getQueryResults(query);
        List<String> queryWords = queryCont.getQueryWords();
        System.out.println(finalRecords);
        System.out.println(queryWords);*/
    }
}
