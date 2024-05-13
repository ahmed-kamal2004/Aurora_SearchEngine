package aurora.search.dev.engine.Models;


import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;

@Setter
@Getter
@Document(collection = "CrawledUrls")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrawledUrls {

    @Id
    private ObjectId _id;
    private String URL;
    private Long length;
    private double rank;
    private HashSet<String> OUTGOINGURLS;
}
