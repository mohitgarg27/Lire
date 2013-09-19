/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lire.mains;

import lire.classes.IndexingThread;

/**
 *
 * @author mohit
 */
public class Indexer 
{
    public static void main(String[] args)
    {
        System.out.println("Args=" + args.length);
        if(args.length!=4)
        {
            printHelp();
        }
        else
        {
            IndexingThread t = new IndexingThread(args[1], args[2], Integer.parseInt(args[3]));
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
                "image-directory                ... The directory the images are found in. It's traversed recursively.\n" +
                "index                          ... The directory of the index for search .\n" +
                "groundTruth                    ... The file containing matching expectations.");
    }
}
