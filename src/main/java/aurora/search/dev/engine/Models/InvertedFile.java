package aurora.search.dev.engine.Models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Document(collection = "invertedfile")
@AllArgsConstructor
@NoArgsConstructor
public class InvertedFile {
    @Id
    private String word;
    private Map<String, Double> tfs;
    private Map<String,List<Long>> place;
    private Float idf;
    @DocumentReference
    private HashSet<Url> urls;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Map<String, Double> getTfs() {
        return tfs;
    }

    public void setTfs(Map<String, Double> tfs) {
        this.tfs = tfs;
    }

    public Map<String, List<Long>> getPlace() {
        return place;
    }

    public void setPlace(Map<String, List<Long>> place) {
        this.place = place;
    }

    public Float getIdf() {
        return idf;
    }

    public HashSet<Url> getUrls() {
        return urls;
    }

    public void setUrls(HashSet<Url> urls) {
        this.urls = urls;
    }

    public void setIdf(Float idf) {
        this.idf = idf;
    }


}
