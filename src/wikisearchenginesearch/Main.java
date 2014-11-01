/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wikisearchenginesearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author akshay
 */
public class Main {
    private static HashMap< String , Double > Weight = new HashMap< String , Double >();
    private static Set<Integer> DocumentId = new HashSet<Integer>();
    private static Set<Integer> InternalId = new HashSet<Integer>();
    private static ArrayList<String> QueryItems = new ArrayList<String>();
    private static String [] tokens;
    private static String [] Parameters;
    private static String [] PostingList;
    private static String [] Components;
    private static String res="";
    private static String Key="";
    private static String first="";
    private static String last="";
    private static Stemmer Stem;
    private static ArrayList<String> Word = new ArrayList<String>();
    private static ArrayList<Long> Position = new ArrayList<Long>();
    private static ArrayList<Integer> Did = new ArrayList<Integer>();
    private static ArrayList<Long> Pos = new ArrayList<Long>();
    public static int upper_bound_String(ArrayList<String> arr, String key,int l)
    {
        int len = l;
        int lo = 0;
        int hi = len-1;
        int mid = (lo + hi)/2;
        while (true) {
            int cmp = (arr.get(mid)).compareTo(key);
            if (cmp == 0 || cmp < 0) {
                lo = mid+1;
                if (hi < lo)
                    return mid<len-1?mid+1:-1;
            } else {
                hi = mid-1;
                if (hi < lo)
                    return mid;
            }
            mid = (lo + hi)/2;
        }
    }
    public static int upper_bound_Integer(ArrayList<Integer> arr, Integer key,int l)
    {
        int len = l;
        int lo = 0;
        int hi = len-1;
        int mid = (lo + hi)/2;
        while (true) {
            int cmp = (arr.get(mid));
            if (cmp == key || cmp < key) {
                lo = mid+1;
                if (hi < lo)
                    return mid<len-1?mid+1:-1;
            } else {
                hi = mid-1;
                if (hi < lo)
                    return mid;
            }
            mid = (lo + hi)/2;
        }
    }
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        BufferedReader br = new BufferedReader(new FileReader("D:\\SEM8\\IR\\FinalInput.txt"));
        BufferedWriter out = new BufferedWriter(new FileWriter("D:\\SEM8\\IR\\FinalOutput.txt"));
        RandomAccessFile finalIndex = new RandomAccessFile("C:\\IRCourse_IIIT\\IRE2012\\FinalIndex23.txt","r");
        RandomAccessFile finalTitleIndex = new RandomAccessFile("C:\\IRCourse_IIIT\\IRE2012\\TitleIndex23.txt","r");
        String input="";
        String List="";
        String Id = "";
        String Query = "";
        String Info="";
        String Str="";
        int Mask=0;
        int T,C,O,I,B;
        int DocId=0;
        int Idx=0;
        int lineNumber=0;
        int weight=0;
        int totalWord=0;
        int f=0;
        int S=0;
        int P=0;
        int id=0;
        int fidx=0;
        //Load Secondary Index
        BufferedReader br1 = new BufferedReader(new FileReader("C:\\IRCourse_IIIT\\IRE2012\\SecondaryIndex23.txt"));
        Word.clear();Position.clear();
        while( (input = br1.readLine())!= null )
        {
            tokens = input.split("#");
            Word.add(tokens[0]);
            Position.add(Long.parseLong(tokens[1]));
        }
        br1.close();
        //Load Secondary Title Index
        br1 = new BufferedReader(new FileReader("C:\\IRCourse_IIIT\\IRE2012\\SecondaryTitleIndex23.txt"));
        Did.clear();Pos.clear();
        while( (input = br1.readLine())!= null )
        {
            tokens = input.split("#");
            Did.add(Integer.parseInt(tokens[0],36));
            Pos.add(Long.parseLong(tokens[1]));
        }
        br1.close();
        
        double idf = 0 ;
        double Rank=0;
        while( (input = br.readLine())!=null )
        {
            if(input.isEmpty()==Boolean.TRUE) break;
            Weight.clear();
            input = input.replaceAll("\\s+"," ");
           // System.out.println(input);
            tokens = input.split(" ");
            DocumentId.clear();
            S=0;
            QueryItems.clear();
            long time = System.currentTimeMillis();
            for(int i = 0 ; i < tokens.length ; i++ )
            {
                InternalId.clear();
                Parameters = tokens[i].split(":");
                Id = Parameters[0];
                Query = Parameters[1];
                Query = Query.toLowerCase();
                Stem = new Stemmer();
                Query=Stem.stem(Query);
                QueryItems.add(Query);
                Idx=upper_bound_String(Word, Query, Word.size());
                Idx=Math.max(0,Idx-1);
                finalIndex.seek(Position.get(Idx));
                lineNumber=0;
                long tt = System.currentTimeMillis();
                while( lineNumber < 502 )
                {
                    List = finalIndex.readLine();
                    fidx=List.indexOf('#');
                    first = List.substring(0,fidx);
                    last = List.substring(fidx+1);
                    f = first.compareToIgnoreCase(Query);
                    if(f==0)
                    {
                        //System.out.println("Hi.. "+Long.toString(System.currentTimeMillis()-tt)+ "  " +lineNumber);
                        PostingList = last.split("#");
                        idf = Math.log10( (double)10000000 / (double)PostingList.length);
                        for(int j=0 ; j < PostingList.length ; j++ )
                        {
                            Components = PostingList[j].split(":");
                            DocId = Integer.parseInt(Components[0],36);
                            totalWord = Integer.parseInt(Components[2],36);
                            weight = Integer.parseInt(Components[1]);
                            Mask = Integer.parseInt(Components[3]);
                            
                            P=0;
                            T=(Mask&(1<<4));C=(Mask&(1<<3));I=(Mask&(1<<2));O=(Mask&(1<<1));B=(Mask&(1<<0));
                            Rank=idf*((double)weight/(double)totalWord);
                            InternalId.add(DocId);
                            Rank=Rank + ( T*10 + C*5 + O*3 + I*2 + B*1  );
                            if(Id.compareTo("T")==0 && T!=0) {Rank+=10000;}
                            else if(Id.compareTo("C")==0 && C!=0) {Rank+=10000;}
                            else if(Id.compareTo("O")==0 && O!=0) {Rank+=10000;}
                            else if(Id.compareTo("I")==0 && I!=0) {Rank+=10000;}
                            else if(Id.compareTo("B")==0 && B!=0) {Rank+=10000;}
                            res="";
                            res=res.concat(Integer.toString(DocId));
                            res=res.concat("&");
                            res=res.concat(Query);
                            if(Weight.containsKey(res) == Boolean.FALSE)
                                Weight.put(res,(double)Rank);
                            else
                            {
                              double t = Weight.get(res);
                              t=t+(double)Rank;
                              Weight.put(res,t);
                            }
                        }
                        break;
                    }
                    lineNumber=lineNumber+1;
                }
                if(S==0) DocumentId.addAll(InternalId);
                else  DocumentId.retainAll(InternalId);
                S=1;
            }
            Iterator iterator = DocumentId.iterator();
            TreeMap< Double , Integer > result = new TreeMap< Double,Integer >();
            while( iterator.hasNext() )
            {
                int Ii = (Integer) iterator.next();
                double score=0.00;
                res="";
                res=res.concat(Integer.toString(Ii));
                res=res.concat("&");
                for( int i = 0 ; i < QueryItems.size() ; i++ )
                {
                    Key = res.concat(QueryItems.get(i));
                    if( Weight.containsKey(Key) == Boolean.TRUE )
                    {
                        score = score + Weight.get(Key);
                    }
                }
                result.put(score,Ii);
            }

            Set Ss = result.descendingKeySet();
            Iterator it = Ss.iterator();
            int ret=0;

            out.write("Time: "+Long.toString(System.currentTimeMillis()-time)+"ms\n");
            TreeSet<String> isPresent = new TreeSet();
            int ff=0;
            while(it.hasNext())
            {
                double score = (Double)it.next();
                int Ii = result.get(score);
                Idx=upper_bound_Integer(Did,Ii,Did.size());
                Idx=Math.max(0,Idx-1);
                finalTitleIndex.seek(Math.max(0,Pos.get(Idx)));
                lineNumber=0;
                String gerrard="";
                String tmp="";
                ff=0;
                while( lineNumber < 502 )
                {
                    Str="";
                    Str = finalTitleIndex.readLine();
                    if(Str==null) break;
                    Parameters = Str.split(":");
                    int ii = Integer.parseInt(Parameters[0],36);
                    if( ii == Ii )
                    {
                        int tt = Str.indexOf(':');
                        gerrard = Str.substring(tt+1);
                        tt = gerrard.indexOf(':');
                        gerrard = gerrard.substring(tt+1);
                        tmp=gerrard.toLowerCase();
                        if(isPresent.contains(tmp) == Boolean.FALSE)
                        {
                            out.write(Parameters[1]+ " " +gerrard+"\n");
                            isPresent.add(tmp);
                            ff=1;
                        }
                        break;
                    }
                    lineNumber=lineNumber+1;
                }
                if(ff==1)
                {
                    ret++;
                    if(ret>=10) break;
                }
            }
            for(int k=0;k<Math.max(0,10-ret);k++)
                out.write("NA\n");
            out.write("\n");
        }
        finalTitleIndex.close();
        finalIndex.close();
        out.close();
        br.close();
        return ;
    }

}
