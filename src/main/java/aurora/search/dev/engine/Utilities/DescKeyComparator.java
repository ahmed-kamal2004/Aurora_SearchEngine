package aurora.search.dev.engine.Utilities;

import java.util.Comparator;

public class DescKeyComparator implements Comparator<Double> {
    @Override
    public int compare(Double k1,Double k2){
        return k2.compareTo(k1);
    }
}
