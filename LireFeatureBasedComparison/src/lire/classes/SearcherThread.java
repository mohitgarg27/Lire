/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lire.classes;

import java.io.IOException;
import java.text.NumberFormat;
import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;
import net.semanticmetadata.lire.impl.ChainedDocumentBuilder;
import net.semanticmetadata.lire.impl.VisualWordsImageSearcher;
import net.semanticmetadata.lire.indexing.parallel.ParallelIndexer;
import net.semanticmetadata.lire.utils.FileUtils;

/**
 *
 * @author mita
 */
public class SearcherThread 
{
    String imDir, inDir, grndTruthFile;    

    int totalImages;    
    long prevTime, initTime;
    
    enum feat
    
    public SearcherThread(String imageDir, String indexDir, String groundTruth ) 
    {
        imDir = imageDir;
        inDir = indexDir;
        grndTruthFile = groundTruth;
    }
        
    public void run() 
    {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(4);
        try 
        {                     
            java.util.ArrayList<java.lang.String> images = FileUtils.getAllImages(new java.io.File(imDir), true);
            if (images == null) 
            {
                System.out.println("Error: No images found to search");
                return;
            }
            totalImages = images.size();
            
            initTime = System.currentTimeMillis();
            prevTime = initTime;
            
            for(String imLoc : images)
            {
                
            }
        } 
        catch (IOException ex) 
        {
            System.out.println(ex.getMessage());
        }
    }
    
    private ImageSearcher getSearcher() {
        int numResults = 50;
        try {
            numResults = Integer.parseInt(textfieldNumSearchResults.getText());
        } catch (Exception e) {
            // nothing to do ...
        }
        ImageSearcher searcher = ImageSearcherFactory.createColorLayoutImageSearcher(numResults);
        if (selectboxDocumentBuilder.getSelectedIndex() == 1) {
            searcher = ImageSearcherFactory.createScalableColorImageSearcher(numResults);
        } else if (selectboxDocumentBuilder.getSelectedIndex() == 2) {
            searcher = ImageSearcherFactory.createEdgeHistogramImageSearcher(numResults);
        } else if (selectboxDocumentBuilder.getSelectedIndex() == 3) {
            searcher = ImageSearcherFactory.createAutoColorCorrelogramImageSearcher(numResults);
        } else if (selectboxDocumentBuilder.getSelectedIndex() == 4) { // CEDD
            searcher = ImageSearcherFactory.createCEDDImageSearcher(numResults);
        } else if (selectboxDocumentBuilder.getSelectedIndex() == 5) { // FCTH
            searcher = ImageSearcherFactory.createFCTHImageSearcher(numResults);
        } else if (selectboxDocumentBuilder.getSelectedIndex() == 6) { // JCD
            searcher = ImageSearcherFactory.createJCDImageSearcher(numResults);
        } else if (selectboxDocumentBuilder.getSelectedIndex() == 7) { // SimpleColorHistogram
            searcher = ImageSearcherFactory.createColorHistogramImageSearcher(numResults);
        } else if (selectboxDocumentBuilder.getSelectedIndex() == 8) {
            searcher = ImageSearcherFactory.createTamuraImageSearcher(numResults);
        } else if (selectboxDocumentBuilder.getSelectedIndex() == 9) {
            searcher = ImageSearcherFactory.createGaborImageSearcher(numResults);
        } else if (selectboxDocumentBuilder.getSelectedIndex() == 10) {
            searcher = ImageSearcherFactory.createJpegCoefficientHistogramImageSearcher(numResults);
        } else if (selectboxDocumentBuilder.getSelectedIndex() == 11) {
            searcher = new VisualWordsImageSearcher(numResults, DocumentBuilder.FIELD_NAME_SURF_VISUAL_WORDS);
        } else if (selectboxDocumentBuilder.getSelectedIndex() == 12) {
            searcher = ImageSearcherFactory.createJointHistogramImageSearcher(numResults);
        } else if (selectboxDocumentBuilder.getSelectedIndex() == 13) {
            searcher = ImageSearcherFactory.createOpponentHistogramSearcher(numResults);
        } else if (selectboxDocumentBuilder.getSelectedIndex() == 14) {
            searcher = ImageSearcherFactory.createLuminanceLayoutImageSearcher(numResults);
        } else if (selectboxDocumentBuilder.getSelectedIndex() >= 15) {
            searcher = ImageSearcherFactory.createPHOGImageSearcher(numResults);
        }
        return searcher;
    }
    
}
