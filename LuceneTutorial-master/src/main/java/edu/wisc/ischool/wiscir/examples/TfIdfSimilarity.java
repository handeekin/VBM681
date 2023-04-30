package edu.wisc.ischool.wiscir.examples;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

public class TfIdfSimilarity {

    private IndexSearcher searcher;
    private ClassicSimilarity similarity;
    private Analyzer analyzer;

    public TfIdfSimilarity(String indexDir) throws IOException {
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        IndexReader reader = DirectoryReader.open(dir);
        searcher = new IndexSearcher(reader);
        similarity = new ClassicSimilarity();
        searcher.setSimilarity(similarity);
        analyzer = new StandardAnalyzer();
    }

    public ScoreDoc[] search(String queryString, int numOfResults) throws IOException {
        Query query = new TermQuery(new Term("content", queryString));
        return searcher.search(query, numOfResults).scoreDocs;
    }

    public float getTfIdfSimilarity(int docId, String queryString) throws IOException {
        Query query = new TermQuery(new Term("content", queryString));
        Document doc = searcher.doc(docId);
        Terms terms = searcher.getIndexReader().getTermVector(docId, "content");
        if (terms == null) return 0.0f;
        TermsEnum termsEnum = terms.iterator();
        BytesRef termBytes = new BytesRef(queryString);
        if (!termsEnum.seekExact(termBytes)) return 0.0f;
        float tf = termsEnum.totalTermFreq();
        float idf = similarity.idf(searcher.collectionStatistics("content").docCount(), searcher.collectionStatistics("content").sumTotalTermFreq());
        float[] norms = searcher.getIndexReader().getNorms("content").getFloats();
        float norm = (norms != null) ? norms[docId] : 1.0f;
        return tf * idf * norm;
    }

}
