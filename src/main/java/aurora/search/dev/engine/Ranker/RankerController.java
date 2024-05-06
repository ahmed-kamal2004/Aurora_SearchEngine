package aurora.search.dev.engine.Ranker;


import aurora.search.dev.engine.QueryEngine.QueryController;
import aurora.search.dev.engine.Services.IndexedUrlsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("ranker/")
public class RankerController {
    public record Output(
            String url,
            String title,
            String paragraph,
            List<String>words
    ){}

    @Autowired
    private IndexedUrlsService urlService;

    private final double TD_UPPER = 0.5; // Constant number
    private final double RANK_COFF = 1;
    private final double RELEVANCE_COFF = 1;



    // MAIN
    @GetMapping("rank")
    public ResponseEntity<Boolean> rank() {
        return new ResponseEntity<Boolean>(this.urlService.rank(), HttpStatus.OK);
    }


    //MAIN   /// UNDER TESTING
    @GetMapping("search")
    public ResponseEntity<Map<Double, Output>> search(@RequestBody Map<String, String> payload) {
        try {
            String query = payload.get("query");

            QueryController q = new QueryController();
            List<QueryController.allNeeded> listOfRecords = q.getQueryResults(query);
            List<String>words = q.getQueryWords();

            Map<Double, Output>results = new HashMap<>();
            for(QueryController.allNeeded item : listOfRecords){
                Output newOutput = new Output(item.url(),item.title(),item.paragraph(),words);
                Double keyValue = this.RANK_COFF*this.urlService.getUrl(item.url()).getRank() + this.RELEVANCE_COFF * item.Idf_TF();
                results.put(keyValue,newOutput);
            }
            return new ResponseEntity<>(results,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
