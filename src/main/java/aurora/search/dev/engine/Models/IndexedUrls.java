package aurora.search.dev.engine.Models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;

@Document(collection = "url")
@AllArgsConstructor
@NoArgsConstructor
public class IndexedUrls {

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Id
    private ObjectId _id;
    private String title;

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    private String description;
    private String url;
    private Long length;
    private double rank;
    private HashSet<String> urls_outgoing;
    private HashSet<String> urls_ingoing;

    public ObjectId get_id() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public Long getLength() {
        return length;
    }

    public double getRank() {
        return rank;
    }

    public HashSet<String> getUrls_outgoing() {
        return urls_outgoing;
    }

    public HashSet<String> getUrls_ingoing() {
        return urls_ingoing;
    }

    public IndexedUrls(String url, Long length) {
        this.url = url;
        this.length = length;
        this.urls_outgoing = new HashSet<>(0);
        this.urls_ingoing = new HashSet<>(0);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }

    public void setUrls_outgoing(HashSet<String> urls_outgoing) {
        this.urls_outgoing = urls_outgoing;
    }

    public void setUrls_ingoing(HashSet<String> urls_ingoing) {
        this.urls_ingoing = urls_ingoing;
    }

}
