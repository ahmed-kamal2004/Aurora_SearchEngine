package aurora.search.dev.engine.Services;

import aurora.search.dev.engine.Models.IndexedUrls;
import aurora.search.dev.engine.Repositories.IndexedUrlsRepository;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.abs;

@Service
public class IndexedUrlsService {
    @Autowired
    private IndexedUrlsRepository urlRepository;
    @Autowired
    private MongoTemplate mongoTemplate;


    public IndexedUrls getUrl(String url){
        return this.urlRepository.findByUrl(url);
    }

    public IndexedUrls create(String url, Long length){
        return this.urlRepository.insert(new IndexedUrls(url,length));
    }
    public List<IndexedUrls>getUrls(){
        return this.urlRepository.findAllByOrderByRankDesc();
    }

    public boolean connectUrls(String url1,String url2){

        // url2 points to url1
        IndexedUrls url1_obj = this.urlRepository.findByUrl(url1);
        IndexedUrls url2_obj = this.urlRepository.findByUrl(url2);

        url1_obj.getUrls_ingoing().add(url2_obj.getUrl());
        url2_obj.getUrls_outgoing().add(url1_obj.getUrl());

        this.urlRepository.save(url1_obj);
        this.urlRepository.save(url2_obj);
        return true;
    }

    public boolean rank(){
            // M = (1-d)A + dB
            List<IndexedUrls> urlList =  this.urlRepository.findAll();
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
                HashSet<String> outGoing = urlList.get(i).getUrls_outgoing();
                int prob_inverse = outGoing.size();
//                double prob = prob_inverse == 0 ? (float)1/size : (float) 1 /prob_inverse;
                double prob = (float) 1 /prob_inverse;
                System.out.println(prob);
                for(int j =0;j<size;j++){
                    if(outGoing.contains(urlList.get(j).getUrl())){
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
                System.out.println("2");
            }
            System.out.println("3");
            for(int i =0;i<size;i++){
                urlList.get(i).setRank(X.getEntry(i,0));
                this.urlRepository.save(urlList.get(i));
            }

        return true;
    }


}
