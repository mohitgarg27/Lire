/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lire.mains;

import lire.classes.IndexingThread;
import lire.classes.SearcherThread;

/**
 *
 * @author mohit
 */
public class Searcher 
{
    public static void main(String[] args)
    {
        if(args.length!=4)
        {
            printHelp();
        }
        else
        {
            SearcherThread t = new SearcherThread(args[1], args[2], args[1].concat("/ids.txt"), Integer.parseInt(args[3]));
            t.start();
        }
    }
    
    private static void printHelp() 
    {
        System.out.println("Usage:\n" +
                "java jar [Indexer/Searcher] [Params For Indexer]" +
                "\n\n[Params for Indexer]\n" +
                "images-directory               ... The directory the images are found in. It's traversed recursively.\n" +
                "index                          ... The directory of the index. Will be appended or created if not existing.\n" +
                "number of threads(optional)    ... The number of threads used for extracting features, e.g. # of CPU cores." +
                "\n\n[Params for Searcher]\n" +
                "images-directory               ... The directory the images are found in. It's traversed recursively.\n" +
                "index                          ... The directory of the index for search .\n" +
                "groundTruth                    ... The file containing matching expectations.");
    }
    
    
}
