/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lire.classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.imageio.ImageIO;
import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;
import net.semanticmetadata.lire.impl.VisualWordsImageSearcher;
import net.semanticmetadata.lire.utils.FileUtils;
import org.apache.commons.collections4.map.MultiValueMap;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author mohit
 */
public class SearcherThread extends Thread
{
    String imDir, inDir, grndTruthFile;    

    int totalImages;    
    long prevTime, initTime;        
    
    MultiValueMap grndtruthMap;
    HashMap<String, String> grndtruthMapReverse;
    
    int numResults = 40;
    
    Feature feature;

    enum Feature
    {
        AutoColorCorrelogram,
        CEDD,
        ColorLayout,
        EdgeHistogram,
        FCTH,
        //Feature, // Sift
        Gabor,
        JCD,
        JointHistogram,
        JpegCoefficientHistogram,  
        LuminanceLayout,
        //MSER,
        OpponentHistogram,
        PHOG,
        ScalableColor,
        SimpleColorHistogram,
        //SurfFeature,
        Tamura
    }
    
    public SearcherThread()
    {
        feature = Feature.CEDD;
    }
    public SearcherThread(String imageDir, String indexDir, String groundTruth, int numRes ) 
    {
        imDir = imageDir;
        inDir = indexDir;
        grndTruthFile = groundTruth;
        numResults = numRes;
        feature = Feature.AutoColorCorrelogram;        
        grndtruthMap = new MultiValueMap<>();
        grndtruthMapReverse = new HashMap<>();
        loadGroundTruth();
    }
        
    public void run() 
    {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(4);
        
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
        df.setMinimumFractionDigits(4);
        df.setMinimumFractionDigits(4);
        
        ImageSearcher imSearcher = null;
        
        try 
        {                     
            java.util.ArrayList<java.lang.String> images = FileUtils.getAllImages(new java.io.File(imDir), true);
            if (images == null) 
            {
                System.out.println("Error: No images found to search");
                return;
            }
            totalImages = images.size();
            Collections.shuffle(images);
            
            initTime = System.currentTimeMillis();
            prevTime = initTime;
            IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(inDir)));
            for(Feature foo : Feature.values())
            {
                double avgPrecision = 0;
                double avgRecall = 0;
                long avgQueryTime = 0;
                imSearcher = getSearcher(foo.name());
                
                int imCount = 0;
                for(String imLoc : images)
                {
                    ImageSearchHits hits = imSearcher.search(ImageIO.read(new FileInputStream(imLoc)), reader);
                    avgQueryTime += System.currentTimeMillis() - prevTime;
                    
                    ArrayList<String> results = new ArrayList<String>();
                    if(hits!=null)
                        for(int i=0; i<hits.length(); i++)
                        {
                            results.add(hits.doc(i).getField(DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue());
                        }
                    
                    avgPrecision += calculatePrecision(imLoc, results);
                    avgRecall += calculateRecall(imLoc, results);

                    prevTime = System.currentTimeMillis();     
                    //System.out.println(imLoc+ " " + avgPrecision + " " + avgRecall + " " + imCount );
                    imCount++;
                    if(imCount>numResults)
                        break;
                }
                avgQueryTime /=images.size();
                avgPrecision /= images.size();
                avgRecall /= images.size();
                System.out.printf("%18s %4d %20d %12s %12s \n", foo.name(), numResults, avgQueryTime, df.format(avgPrecision), df.format(avgRecall)  );
            }            
        } 
        catch (IOException ex) 
        {
            System.out.println(ex.getMessage());
        }
    }
    
    public static void main(String[] args)
    {
        SearcherThread s = new SearcherThread();
        for(Feature foo : s.feature.values())
            {
                System.out.println(foo.name());
            }
    }
    
    public void loadGroundTruth()
    {
        BufferedReader br = null;
        try 
        {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(grndTruthFile));
            while ((sCurrentLine = br.readLine()) != null) 
            {
                String[] splits = sCurrentLine.split("\t");
                //System.out.println(sCurrentLine);
                if(splits.length==2)
                {
                    grndtruthMap.put(splits[1], splits[0]);  
                    grndtruthMapReverse.put(splits[0], splits[1]);
                    //System.out.println(splits[0] +" "+ splits[1]);
                }
            }
        } catch (IOException e) 
        {
            e.printStackTrace();
        }
        finally 
        {
            try 
            {
                if (br != null)br.close();
            } 
            catch (IOException ex) 
            {
                ex.printStackTrace();
            }
        } 
    }
    
    public void get(String key)
    {
        Object o = grndtruthMap.get(key);
        
    }
    
    private ImageSearcher getSearcher(String name) 
    {   
        ImageSearcher searcher = null;
        
        if(name.equals(Feature.AutoColorCorrelogram.name()))
            searcher = ImageSearcherFactory.createAutoColorCorrelogramImageSearcher(numResults);
        if(name.equals(Feature.CEDD.name()))
            searcher = ImageSearcherFactory.createCEDDImageSearcher(numResults);
        if(name.equals(Feature.ColorLayout.name()))
            searcher = ImageSearcherFactory.createColorLayoutImageSearcher(numResults);    
        if(name.equals(Feature.EdgeHistogram.name()))
            searcher = ImageSearcherFactory.createEdgeHistogramImageSearcher(numResults);    
        if(name.equals(Feature.FCTH.name()))
            searcher = ImageSearcherFactory.createFCTHImageSearcher(numResults);
        //if(name.equals(Feature.Feature.name()))
        //    searcher = new VisualWordsImageSearcher(numResults, DocumentBuilder.FIELD_NAME_SIFT_VISUAL_WORDS);    
        if(name.equals(Feature.Gabor.name()))
            searcher = ImageSearcherFactory.createGaborImageSearcher(numResults);    
        if(name.equals(Feature.JCD.name()))
            searcher = ImageSearcherFactory.createJCDImageSearcher(numResults);    
        if(name.equals(Feature.JointHistogram.name()))
            searcher = ImageSearcherFactory.createJointHistogramImageSearcher(numResults);    
        if(name.equals(Feature.JpegCoefficientHistogram.name()))
            searcher = ImageSearcherFactory.createJpegCoefficientHistogramImageSearcher(numResults);    
        if(name.equals(Feature.LuminanceLayout.name()))
            searcher = ImageSearcherFactory.createLuminanceLayoutImageSearcher(numResults);    
//        if(name.equals(Feature.MSER.name()))
//            searcher = new VisualWordsImageSearcher(numResults, DocumentBuilder.FIELD_NAME_MSER_LOCAL_FEATURE_HISTOGRAM_VISUAL_WORDS);
        if(name.equals(Feature.OpponentHistogram.name()))
            searcher = ImageSearcherFactory.createOpponentHistogramSearcher(numResults);    
        if(name.equals(Feature.PHOG.name()))
            searcher = ImageSearcherFactory.createPHOGImageSearcher(numResults);    
        if(name.equals(Feature.ScalableColor.name()))
            searcher = ImageSearcherFactory.createScalableColorImageSearcher(numResults);    
        if(name.equals(Feature.SimpleColorHistogram.name()))
            searcher = ImageSearcherFactory.createColorHistogramImageSearcher(numResults);    
        //if(name.equals(Feature.SurfFeature.name()))
          //  searcher = new VisualWordsImageSearcher(numResults, DocumentBuilder.FIELD_NAME_SURF_VISUAL_WORDS);    
        if(name.equals(Feature.Tamura.name()))
            searcher = ImageSearcherFactory.createTamuraImageSearcher(numResults);
        
        return searcher;
    }
    
    private double calculatePrecision(String imLoc, ArrayList<String> results) 
    {
        double precision = 0;
        String fileName = getImageName(imLoc);
        String groupId = grndtruthMapReverse.get(fileName).toString();
        
        int retrievedImages = numResults;
        int relevantImages = grndtruthMap.getCollection(groupId).size();
        int relevantInRetrieved = 0;
        
        for(int i=0; i<results.size(); i++)
        {
            String resIm = getImageName(results.get(i));
            if( grndtruthMapReverse.get(resIm).equals(groupId) )
                relevantInRetrieved++;
        }
        //ArrayList<String> relevant = (ArrayList<String>) grndtruthMap.getCollection(groupId);
        precision = (double)relevantInRetrieved/retrievedImages;
        
        return precision;
    }

    private String getImageName(String path)
    {
        String[] splits = path.split("/");
        String fileName = splits[splits.length-1];
        splits = fileName.split("\\.");        
        return splits[0];        
    }
    
    private double calculateRecall(String imLoc, ArrayList<String> results) 
    {
        double recall = 0;
        String fileName = getImageName(imLoc);
        String groupId = grndtruthMapReverse.get(fileName).toString();
        
        int retrievedImages = numResults;
        int relevantImages = grndtruthMap.getCollection(groupId).size();
        int relevantInRetrieved = 0;
        
        for(int i=0; i<results.size(); i++)
        {
            String resIm = getImageName(results.get(i));
            if( grndtruthMapReverse.get(resIm).equals(groupId) )
                relevantInRetrieved++;
        }
        //ArrayList<String> relevant = (ArrayList<String>) grndtruthMap.getCollection(groupId);

        recall = (double)relevantInRetrieved/relevantImages;
        
        return recall;        
    }
}
