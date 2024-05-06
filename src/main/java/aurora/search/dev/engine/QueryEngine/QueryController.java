package aurora.search.dev.engine.QueryEngine;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class QueryController {
    private static List<String> allWords;
    private static HashMap<Integer, Double> Url_Score;
    private static HashMap<Integer, Integer> Url_ParagraphID;


    //Those three functions to be used from ranker and interface
    public HashMap<Integer, Double> getURLScores(){ return Url_Score;}
    public HashMap<Integer, Integer> getURLParagraphsID(){ return Url_ParagraphID;}
    public List<String> getQueryWords(){ return allWords;}

    public static void main(String[] args) throws IOException {
        //TODO: search by image and voice
        String testQuery = "python programing for machine learning";
        if(testQuery.isEmpty())
            System.out.println("PLEASE, enter a String");
        else
        {
            if (testQuery.charAt(0)=='\"'&&testQuery.charAt(testQuery.length()-1)=='\"') {
                /*
                 *TODO: preprocess phrases here to handle boolean operation
                 */
                testQuery = testQuery.replaceAll("^\"|\"$", "");// remove quotation
                PhraseSearching p=new PhraseSearching(testQuery);
                allWords = p.queryTerms;
                Url_Score = p.Url_Score;
                Url_ParagraphID = p.Url_ParagraphID;
            }
            else {
                QueryProcessor q=new QueryProcessor(false);
                q.processQuery(testQuery);
                allWords = q.queryTerms;
                Url_Score = q.Url_Score;
                Url_ParagraphID = q.Url_ParagraphID;
            }
        }
        System.out.println(allWords);
        System.out.println(Url_ParagraphID);
        System.out.println(Url_Score);
    }
}
