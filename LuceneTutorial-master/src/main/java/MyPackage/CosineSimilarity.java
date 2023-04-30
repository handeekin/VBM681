package MyPackage;

import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;

public class CosineSimilarity extends SimilarityBase {

  
	@Override
	protected double score(BasicStats stats, double freq, double docLen) {
		double tf = 1 + (Math.log(freq) / Math.log(2));
        double idf = Math.log((stats.getNumberOfDocuments() + 1) / stats.getDocFreq()) / Math.log(2);
        float dotProduct = (float) (tf * idf);
        return dotProduct;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}

