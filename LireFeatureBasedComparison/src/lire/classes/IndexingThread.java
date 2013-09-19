package lire.classes;

import net.semanticmetadata.lire.impl.ChainedDocumentBuilder;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import net.semanticmetadata.lire.DocumentBuilderFactory;
import net.semanticmetadata.lire.impl.SiftDocumentBuilder;
import net.semanticmetadata.lire.impl.SurfDocumentBuilder;
import net.semanticmetadata.lire.indexing.parallel.ParallelIndexer;
import net.semanticmetadata.lire.utils.FileUtils;

public class IndexingThread extends Thread 
{
    String imDir, inDir;    
    Double percentage;
    int threads;
    int totalImages;    
    long prevTime, initTime;
    
    public IndexingThread(String imagesDir, String indexDir, int thr ) 
    {
        imDir = imagesDir;
        inDir = indexDir;
        threads = thr;
        percentage = new Double(-1);
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
                System.out.println("Error: No images found to index");
                return;
            }
            totalImages = images.size();
            
            initTime = System.currentTimeMillis();
            prevTime = initTime;
            ParallelIndexer pin =new ParallelIndexer(threads, inDir, imDir, true)
            {
                    @Override
                    public void addBuilders(ChainedDocumentBuilder builder) 
                    {
                        builder.addBuilder(new MetadataBuilder());
                    }
            };
            Thread t = new Thread(pin);
            t.start();
            while (!pin.hasEnded()) 
            {
                if(percentage.compareTo(pin.getPercentageDone())!=0)
                {
                    percentage = (double) pin.getPercentageDone();
                    int imageNo = (int) (percentage * totalImages);
                    System.out.println( nf.format(imageNo) + " " +  nf.format(System.currentTimeMillis() - prevTime) + "  " +  nf.format(System.currentTimeMillis() - initTime) );
                    prevTime = System.currentTimeMillis();                    
                }
            }
            try 
            {
                t.join();
            }
            catch (InterruptedException e) 
            {
                e.printStackTrace();
            }
            long timeTaken = (System.currentTimeMillis() - initTime);
            
            System.out.println("Finished indexing in " + timeTaken + " miliseconds");
        } 
        catch (IOException ex) 
        {
            System.out.println(ex.getMessage());
        }
    }

    class MetadataBuilder extends ChainedDocumentBuilder 
    {
        public MetadataBuilder() 
        {
            super();
            addBuilder(DocumentBuilderFactory.getCEDDDocumentBuilder());
            addBuilder(DocumentBuilderFactory.getFCTHDocumentBuilder());
            addBuilder(DocumentBuilderFactory.getJCDDocumentBuilder());
            addBuilder(DocumentBuilderFactory.getColorLayoutBuilder());
            addBuilder(DocumentBuilderFactory.getScalableColorBuilder());
            addBuilder(DocumentBuilderFactory.getEdgeHistogramBuilder());
            addBuilder(DocumentBuilderFactory.getAutoColorCorrelogramDocumentBuilder());
            addBuilder(DocumentBuilderFactory.getTamuraDocumentBuilder());
            addBuilder(DocumentBuilderFactory.getGaborDocumentBuilder());
            addBuilder(DocumentBuilderFactory.getColorHistogramDocumentBuilder());
            addBuilder(DocumentBuilderFactory.getJpegCoefficientHistogramDocumentBuilder());
            addBuilder(DocumentBuilderFactory.getOpponentHistogramDocumentBuilder());
            addBuilder(DocumentBuilderFactory.getJointHistogramDocumentBuilder());
            addBuilder(DocumentBuilderFactory.getLuminanceLayoutDocumentBuilder());
            addBuilder(DocumentBuilderFactory.getPHOGDocumentBuilder());
            addBuilder(new SurfDocumentBuilder());
            addBuilder(new SiftDocumentBuilder());
        }
    }
}
