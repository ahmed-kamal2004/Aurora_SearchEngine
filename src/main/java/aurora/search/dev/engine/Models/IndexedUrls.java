package aurora.search.dev.engine.Models;


import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;

@Setter
@Getter
@Document(collection = "IndexedUrls")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndexedUrls {

    @Id
    private Integer _id;
    private String title;

    private String description;
    private String url;
    private Long length;
    private double rank;
    private HashSet<String> urls_outgoing;
    public IndexedUrls(String url, Long length) {
        this.url = url;
        this.length = length;
        this.urls_outgoing = new HashSet<>(0);
    }

}
