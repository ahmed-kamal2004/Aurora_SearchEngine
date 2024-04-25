package aurora.search.dev.engine.Services;

import aurora.search.dev.engine.Models.InvertedFile;
import aurora.search.dev.engine.Models.Url;
import aurora.search.dev.engine.Repositories.InvertedFileRepository;
import aurora.search.dev.engine.Repositories.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InvertedFileService {
    @Autowired
    private InvertedFileRepository invertedFileRepository;
    @Autowired
    private UrlRepository urlRepository;
    @Autowired
    private MongoTemplate mongoTemplate;


    public InvertedFile create_or_update(String word , String url , Long length, Long place)
    {
        Optional<Url> findurl = this.urlRepository.findById(url);
        Url ll;
        if(!findurl.isPresent())
        {
            ll = new Url(url,length);
            this.urlRepository.save(ll);
        }
        else{
            ll = findurl.get();
        }
        Optional<InvertedFile> file = this.invertedFileRepository.findById(word);
        if(!file.isPresent()){
            // create the inverted file if not found
            this.invertedFileRepository.save(new InvertedFile(word,new HashMap<String,Float>(0),new HashMap<String ,List<Long>>(0),0.0f,new HashSet<>(0)));
        }
        InvertedFile _file = this.invertedFileRepository.findById(word).get();
        if(!_file.getUrls().contains(url)){
            _file.getUrls().add(ll);
            _file.getTfs().put( url,0.0f);
        }
        _file.getPlace().computeIfAbsent(url, k -> new ArrayList<Long>(0));
        _file.getPlace().get(url).add(place);
        this.invertedFileRepository.save(_file);
        return _file;
    }


    public Boolean CalculateTF(){
        // TF : # of specific word in x/ # of all words in x
        for(InvertedFile file : this.invertedFileRepository.findAll()){
            for(Url i : file.getUrls()){
                long counter = (long) file.getPlace().get(i.getUrl()).size();
                float Tf = (float) counter / i.getLength();
                file.getTfs().put(i.getUrl(),Tf); // saves automatic
            }
            this.invertedFileRepository.save(file);
        }
        return true;
    }
    public boolean CalculateIDF(){
        // IDF : # of X /# of X where m appears
        for(InvertedFile file : this.invertedFileRepository.findAll()){
            file.setIdf((float) (this.urlRepository.findAll().size() / file.getUrls().size()));
            this.invertedFileRepository.save(file);
        }
        return true;
    }

}
