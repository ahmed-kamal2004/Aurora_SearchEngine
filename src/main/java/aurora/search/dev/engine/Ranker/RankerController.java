package aurora.search.dev.engine.Ranker;

import aurora.search.dev.engine.Models.InvertedFile;
import aurora.search.dev.engine.Models.Url;
import aurora.search.dev.engine.Services.InvertedFileService;
import aurora.search.dev.engine.Services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("ranker/")
public class RankerController {

    @Autowired
    private InvertedFileService invertedFileService;
    @Autowired
    private UrlService urlService;

    // MAIN
    @GetMapping("calc/")
    public ResponseEntity<Boolean> calc(){ // content relevance is done here.
        this.invertedFileService.CalculateTF();
        this.invertedFileService.CalculateIDF();
        return new ResponseEntity<Boolean>(true,HttpStatus.OK);
    }


    // MAIN
    @GetMapping("rank")
    public ResponseEntity<Boolean> rank(){
        return new ResponseEntity<Boolean>(this.urlService.rank(),HttpStatus.OK);
    }


    //MAIN
    @GetMapping("search/")
    public ResponseEntity<List<String>> search(@RequestBody Map<String,String> payload){

        // algorithm
        // 1 - for every page calc TF * IDF for each word in the query
        // 2 - for every page we sum the values of TF * IDF = sum(TF * IDF)
        // 3 - we repeat this algorithm for every page
        // 4 - we rank them based on the ranker for example
        String query = payload.get("query");
        List<String> ll = new ArrayList<>();
        return new ResponseEntity<List<String>>(ll,HttpStatus.OK);
    }

    @PostMapping("create-url")
    public ResponseEntity<Url>create_url(@RequestBody Map<String,String> payload){
        String url = payload.get("url");
        Long length = Long.parseLong(payload.get("length"));
        return new ResponseEntity<Url>(this.urlService.create(url,length),HttpStatus.OK);
    }

    @PostMapping("create-invertedfile")
    public ResponseEntity<InvertedFile>create_file(@RequestBody Map<String,String> paylaod){
        return new ResponseEntity<InvertedFile>(this.invertedFileService.create_or_update(paylaod.get("word"),
                paylaod.get("url"),Long.parseLong(paylaod.get("length")),Long.parseLong(paylaod.get("place"))),HttpStatus.OK);
    }

    @PutMapping("connect-url")
    public ResponseEntity<Boolean>connectUrls(@RequestBody Map<String,String>payload){
        return new ResponseEntity<>(this.urlService.connectUrls(payload.get("url1"),payload.get("url2")),HttpStatus.OK);
    }

    @GetMapping("urls")
    public ResponseEntity<List<Url>>getUrls(){
        return new ResponseEntity<>(this.urlService.getUrls(),HttpStatus.OK);
    }


}
