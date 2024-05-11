package aurora.search.dev.engine.Indexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ca.rmen.porterstemmer.PorterStemmer;
public class PreIndexer {
    public  List<String> ReadFile()  {
        List<String> StopWords = new ArrayList<>();
        File file = new File("Aurora_SearchEngine\\StopWords.txt").getAbsoluteFile();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine();
                StopWords.add(word);
            }
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return StopWords;
    }
    public  List<String> removeStopWords(List<String> Words)  {
        List<String> StopWords = ReadFile();
        Words.removeAll(StopWords);
        Words.removeIf(item -> item == null || item.isEmpty());
        return Words;
    }
    public String cleanPage(String paragraphToClean){
        // Removing anything except alphabet characters
        paragraphToClean = paragraphToClean.replaceAll("<style([\\s\\S]+?)</style>", "");
        paragraphToClean = paragraphToClean.replaceAll("<script([\\s\\S]+?)</script>", "");
        paragraphToClean = paragraphToClean.replaceAll("<meta[^>]*>", "");
        paragraphToClean = paragraphToClean.replaceAll("<link[^>]*>", "");
        paragraphToClean = paragraphToClean.replaceAll("[^a-zA-Z]", " ");

        return paragraphToClean;
    }
    public List<String> tokenizeIt(String sentence){
        sentence = sentence.toLowerCase();
        List<String> words =new Vector<>();
        Pattern pattern = Pattern.compile("\\w+");
        Matcher match = pattern.matcher(sentence);
        while (match.find()) {
            words.add(match.group());
        }
        return words;
    }

    public List<String> StemIt(List<String> words) {
        PorterStemmer porterStemmer = new PorterStemmer();
        List<String> stemWords = new ArrayList<>();

        for (String word : words) {
            String stem = porterStemmer.stemWord(word);
            stemWords.add(stem);
        }
        return stemWords;
    }
}
