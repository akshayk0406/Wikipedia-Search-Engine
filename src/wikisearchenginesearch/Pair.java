/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wikisearchenginesearch;

/**
 *
 * @author akshay
 */
public class Pair implements Comparable<Pair>
{
     int docId;
     String Term;
     public Pair()
     {
         docId=0;
         Term="";
     }
     public Pair(int i,String p)
     {
         docId=i;
         Term=p;
     }
     public int getDocId()
     {
         return docId;
     }
     public String getWeight()
     {
         return Term;
     }
     public void setDocId(int i)
     {
         docId = i;
     }
     public void setTerm(String i)
     {
         Term=i;
     }

    public int compareTo(Pair o)
    {
        if( docId > o.getDocId() ) return 1;
        return -1;
    }
}
