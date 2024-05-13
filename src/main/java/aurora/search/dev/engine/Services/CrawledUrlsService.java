package aurora.search.dev.engine.Services;

import aurora.search.dev.engine.Models.CrawledUrls;
import aurora.search.dev.engine.Repositories.CrawledUrlsRepository;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

import static java.lang.Math.abs;

@Service
public class CrawledUrlsService {
    @Autowired
    private CrawledUrlsRepository crawledUrlsRepository;


    public CrawledUrls getUrl(String url){
        return this.crawledUrlsRepository.findByURL(url);
    }

    public List<CrawledUrls>getUrls(){
        return this.crawledUrlsRepository.findAllByOrderByRankDesc();
    }

    public boolean connectUrls(String url1,String url2){

        // url2 points to url1
        CrawledUrls url1_obj = this.crawledUrlsRepository.findByURL(url1);
        CrawledUrls url2_obj = this.crawledUrlsRepository.findByURL(url2);

        url2_obj.getOUTGOINGURLS().add(url1_obj.getURL());

        this.crawledUrlsRepository.save(url1_obj);
        this.crawledUrlsRepository.save(url2_obj);
        return true;
    }

    public boolean rank(){
            // M = (1-d)A + dB
            List<CrawledUrls> urlList =  this.crawledUrlsRepository.findAll();
            int size = urlList.size();
            RealMatrix A = new Array2DRowRealMatrix(size,size);
            RealMatrix B = new Array2DRowRealMatrix(size,size);
            RealMatrix X = new Array2DRowRealMatrix(size,1);
            float dumping_factor = 0.15f;

            for(int i = 0;i<size;i++){
                X.setEntry(i,0,(double)1/size );
                for(int j = 0;j<size;j++){
                    A.setEntry(i,j,0.0);
                    B.setEntry(i,j,dumping_factor/size);
                }
            }

            for(int i = 0;i<size;i++){
                HashSet<String> outGoing = urlList.get(i).getOUTGOINGURLS();
                int prob_inverse = outGoing.size();
                double prob = prob_inverse == 0 ? (float)1/size : (float) 1 /prob_inverse;
//                double prob = (float) 1 /prob_inverse;
                for(int j =0;j<size;j++){
                    if(outGoing.contains(urlList.get(j).getURL())){
//                        A.setEntry(i,j,prob);
                        A.setEntry(i,j,prob * (1- dumping_factor));
                    }
                }
            }

            A = A.add(B); // (1-d)A + dB

            RealMatrix X_past = new Array2DRowRealMatrix(size,1);
            X_past = X.copy();
            X_past = X_past.scalarAdd(100.0);

            while(abs(X_past.getNorm() - X.getNorm()) > 0.001f){
                X_past = X.copy();
                X = A.transpose().multiply(X_past);
            }
            for(int i =0;i<size;i++){
                urlList.get(i).setRank(X.getEntry(i,0));
                this.crawledUrlsRepository.save(urlList.get(i));
            }

        return true;
    }


}
