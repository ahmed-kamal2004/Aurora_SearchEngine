package aurora.search.dev.engine.Models;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.HashSet;

@Document(collection = "url")
@AllArgsConstructor
@NoArgsConstructor
public class Url {

    @Id
    private String url;

    public Url(String url, Long length) {
        this.url = url;
        this.length = length;
        this.urls_outgoing = new HashSet<>(0);
        this.urls_ingoing = new HashSet<>(0);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public double getRank() {
        return rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }

    public HashSet<String> getUrls_outgoing() {
        return urls_outgoing;
    }

    public void setUrls_outgoing(HashSet<String> urls_outgoing) {
        this.urls_outgoing = urls_outgoing;
    }

    public HashSet<String> getUrls_ingoing() {
        return urls_ingoing;
    }

    public void setUrls_ingoing(HashSet<String> urls_ingoing) {
        this.urls_ingoing = urls_ingoing;
    }

    private Long length;
    private double rank;
    private HashSet<String> urls_outgoing;
    private HashSet<String> urls_ingoing;
}
