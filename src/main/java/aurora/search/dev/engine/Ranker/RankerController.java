package aurora.search.dev.engine.Ranker;

import aurora.search.dev.engine.Models.InvertedFile;
import aurora.search.dev.engine.Models.Url;
import aurora.search.dev.engine.Services.InvertedFileService;
import aurora.search.dev.engine.Services.UrlService;
import aurora.search.dev.engine.Utilities.DescKeyComparator;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("ranker/")
public class RankerController {

    @Autowired
    private InvertedFileService invertedFileService;
    @Autowired
    private UrlService urlService;

    private final double TD_UPPER = 0.5; // Constant number
    private final double RANK_COFF = 1;
    private final double RELEVANCE_COFF = 1;

    // MAIN
    @GetMapping("calc/")
    public ResponseEntity<Boolean> calc() { // content relevance is done here.
        this.invertedFileService.CalculateTF();
        this.invertedFileService.CalculateIDF();
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }


    // MAIN
    @GetMapping("rank")
    public ResponseEntity<Boolean> rank() {
        return new ResponseEntity<Boolean>(this.urlService.rank(), HttpStatus.OK);
    }


    //MAIN   /// UNDER TESTING
    @GetMapping("search")
    public ResponseEntity<Map<Double, Pair<String, List<String>>>> search(@RequestBody Map<String, String> payload) {


        // Return every Url with its words available in it

        // algorithm
        // 1 - for every page calc TF * IDF for each word in the query
        // 2 - for every page we sum the values of TF * IDF = sum(TF * IDF)
        // 3 - we repeat this algorithm for every page
        // 4 - we rank them based on the ranker for example

        try {
            String query = payload.get("query");
            String[] words = query.split("\\s+");

            Map<Double, Pair<String, List<String>>> returnUrlList = new TreeMap<>(new DescKeyComparator()); // Desc order retirval from high to low rank
            Map<String, List<String>> urlRelevanceMap = new HashMap<>();
            Map<String, Double> urlRelevanceCalcMap = new HashMap<>();

            for (String word : words) {
                Optional<InvertedFile> optionalWordFile = this.invertedFileService.getInvertedFile(word);
                if (optionalWordFile.isPresent()) {
                    InvertedFile wordFile = optionalWordFile.get();
                    HashSet<Url> wordUrls = wordFile.getUrls();
                    Map<String, Double> wordTds = wordFile.getTfs();
                    for (Url wordUrl : wordUrls) {
                        if (wordTds.get(wordUrl.getUrl()) > this.TD_UPPER)
                            continue;
                        if (!urlRelevanceMap.containsKey(wordUrl.getUrl())) {
                            urlRelevanceMap.put(wordUrl.getUrl(), new ArrayList<>());
                            urlRelevanceCalcMap.put(wordUrl.getUrl(), 0.0);
                        }
                        urlRelevanceMap.get(wordUrl.getUrl()).add(word);
                        urlRelevanceCalcMap.put(wordUrl.getUrl(),
                                urlRelevanceCalcMap.get(wordUrl.getUrl()) + wordFile.getIdf() * wordTds.get(wordUrl.getUrl()));
                    }

                }
            }
            for(String urlString : urlRelevanceMap.keySet()){
                double score = this.RANK_COFF * this.urlService.getUrlById(urlString).get().getRank()
                        + this.RELEVANCE_COFF * urlRelevanceCalcMap.get(urlString);
                returnUrlList.put(score, new Pair<>(urlString, urlRelevanceMap.get(urlString)));
            }
            return new ResponseEntity<>(returnUrlList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("create-url")
    public ResponseEntity<Url> create_url(@RequestBody Map<String, String> payload) {
        String url = payload.get("url");
        Long length = Long.parseLong(payload.get("length"));
        return new ResponseEntity<Url>(this.urlService.create(url, length), HttpStatus.OK);
    }

    @PostMapping("create-invertedfile")
    public ResponseEntity<InvertedFile> create_file(@RequestBody Map<String, String> paylaod) {
        return new ResponseEntity<InvertedFile>(this.invertedFileService.create_or_update(paylaod.get("word"),
                paylaod.get("url"), Long.parseLong(paylaod.get("length")), Long.parseLong(paylaod.get("place"))), HttpStatus.OK);
    }

    @PutMapping("connect-url")
    public ResponseEntity<Boolean> connectUrls(@RequestBody Map<String, String> payload) {
        return new ResponseEntity<>(this.urlService.connectUrls(payload.get("url1"), payload.get("url2")), HttpStatus.OK);
    }

    @GetMapping("urls")
    public ResponseEntity<List<Url>> getUrls() {
        return new ResponseEntity<>(this.urlService.getUrls(), HttpStatus.OK);
    }


}
