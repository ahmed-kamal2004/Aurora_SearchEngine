package aurora.search.dev.engine.Services;

import aurora.search.dev.engine.Models.Url;
import aurora.search.dev.engine.Repositories.InvertedFileRepository;
import aurora.search.dev.engine.Repositories.UrlRepository;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.jblas.DoubleMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

@Service
public class UrlService {
    @Autowired
    private InvertedFileRepository invertedFileRepository;
    @Autowired
    private UrlRepository urlRepository;
    @Autowired
    private MongoTemplate mongoTemplate;


    public Url create(String url,Long length){
        return this.urlRepository.insert(new Url(url,length));
    }
    public List<Url>getUrls(){
        return this.urlRepository.findAllByOrderByRankDesc();
    }

    public boolean connectUrls(String url1,String url2){

        // url2 points to url1
        Url url1_obj = this.urlRepository.findById(url1).get();
        Url url2_obj = this.urlRepository.findById(url2).get();

        url1_obj.getUrls_ingoing().add(url2_obj.getUrl());
        url2_obj.getUrls_outgoing().add(url1_obj.getUrl());

        this.urlRepository.save(url1_obj);
        this.urlRepository.save(url2_obj);
        return true;
    }

    public boolean rank(){
            // M = (1-d)A + dB
            List<Url> urlList =  this.urlRepository.findAll();
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
