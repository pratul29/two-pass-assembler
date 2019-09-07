import java.util.*;
import java.io.*;
public class ass
{
    public static int pcf=0;
    static String operation[]={"NEG","AND","XOR","OR","LSR","LSL","MOV","ADD","SUB","HLT","JMP"};
    static String code[]={"000001","000010","000011","000100","000101","001010","001101","001111","010000","010001"};
    static String register[]={"R0","R1","R2","R3","R4","R5","R6","R7"};
    static String regop[]={"000","001","010","011","100","101","110","111"};
    public static void main()throws IOException
    {
        FileReader o1=new FileReader("file1.txt");
        BufferedReader o2=new BufferedReader(o1);
        String line;
        node start=null;
        int pc=-2;
        while((line=o2.readLine())!=null)
        {
            StringTokenizer st=new StringTokenizer(line); 
            int n=st.countTokens();
            if(n==3)/**DC DS without lable*/
            {
                String first=st.nextToken();
                String second=st.nextToken();
                String third=st.nextToken();
                int cpc=pc;
                int push=0;
                if(second.equalsIgnoreCase("DC"))
                {pc+=2;push++;}
                else if(second.equalsIgnoreCase("DS"))
                {pc=pc+Integer.parseInt(third)*2;push++;}
                if(push !=0)
                {
                    node pointer=new node();
                    if(start==null){
                        start=pointer;
                    }
                    else{
                        node temp=start;
                        while(temp.next!=null)
                        {
                            temp=temp.next; 
                        }
                        temp.next=pointer;
                    }
                    pointer.variable=first;
                    pointer.value=third;
                    pointer.address=Integer.toString(cpc+2);
                }
                else if(third.charAt(0)!='*' && third.charAt(0)!='R')
                    pc=pc+4;
                else
                {pc+=2;}
            }
            else if(n==4)/**lable*/
            {
                String first=st.nextToken();
                String second=st.nextToken();
                String third=st.nextToken();
                String fourth=st.nextToken();
                int cpc=pc;
                if(fourth.charAt(0)!='*' && fourth.charAt(0)!='R')
                    pc=pc+4;
                else
                {pc+=2;}
                node pointer=new node();
                if(start==null){
                    start=pointer;
                }
                else{
                    node temp=start;
                    while(temp.next!=null)
                    {
                        temp=temp.next; 
                    }
                    temp.next=pointer;
                }
                pointer.variable=first.substring(0,first.length()-1);
                pointer.address=Integer.toString(cpc+2);
            }
            else if(n==1)/**HLT*/
            {pc+=2;}
            else if(n==2)/**JMP*/
            {
                pc=pc+4;
            }
        }
        pcf=pc;
        FileReader o4=new FileReader("file1.txt");
        BufferedReader o3=new BufferedReader(o4);
        String arr[]=new String[pc/2+1];
        for(int i=0;i<arr.length;i++) 
        {arr[i]="";}
        int k=0;
        while((line=o3.readLine())!=null)
        {
            StringTokenizer st=new StringTokenizer(line); 
            int n=st.countTokens();
            if(n==3 || n==4)
            {
                if(n==4) {st.nextToken( );}
                String first=st.nextToken();
                String second=st.nextToken();
                String third=st.nextToken();
                if(!(second.equalsIgnoreCase("DC") || second.equalsIgnoreCase("DS")))/**not DC DS*/
                {
                    /** first */
                    String op1="";
                    for(int i=0;i<11;i++)
                    {
                        if(operation[i].equalsIgnoreCase(first)){
                            op1+=code[i];
                            break;
                        }
                    }
                    arr[k]+=op1;/** second */
                    String op2="";
                    if(second.charAt(0)=='*')
                    {
                        op2+="01";
                        second=second.substring(1);
                    }
                    else op2+="00";
                    for(int i=0;i<8;i++)
                    {
                        if(register[i].equalsIgnoreCase(second))
                        {
                            op2=regop[i]+op2;
                            break;
                        }
                    }
                    arr[k]+=op2;/** third */
                    String op3="";
                    if(third.charAt(0)=='R' || third.charAt(0)=='*')
                    {
                        if(third.charAt(0)=='*')
                        {
                            op3+="01";
                            third=third.substring(1);
                        }
                        else op3+="00";
                        for(int i=0;i<8;i++)
                        {
                            if(register[i].equalsIgnoreCase(third))
                            {
                                op3=regop[i]+op3;
                                break;
                            }
                        }
                        arr[k]+=op3;
                        k++;
                    }
                    else if(third.charAt(0)=='#')
                    {
                        op3="00010";
                        arr[k]+=op3;
                        k++;
                        arr[k]=hex2binary(third.substring(1));
                        k++;
                    }
                    else
                    {
                        op3="00010";
                        arr[k]+=op3;
                        node temp=start;
                        int a=0;
                        k++;
                        if(third.charAt(0)=='&')
                        {
                            a=1;
                            third=third.substring(1);
                        }
                        String val="";
                        while(!(temp.variable).equals(third))
                        {temp=temp.next;}
                        if(a==1)
                        {
                            val=temp.address;
                            arr[k]=decimal2binary(val);
                            k++;
                        }
                        else if(a==0)   
                        {
                            val=temp.value;
                            arr[k]=hex2binary(val);
                            k++;
                        }
                    }
                }
                else if(second.equalsIgnoreCase("DC")){arr[k]=hex2binary(third);k++;}
                else if(second.equalsIgnoreCase("DS")){int times=Integer.parseInt(third);
                    for(int i=0;i<times;i++)
                        arr[k++]="0000000000000000";
                }
            }
            if(n==1){arr[k]="0010100000000000";k++;}
            if(n==2)
            {
                arr[k]="0011110000000000";
                k++;
                node temp=start;
                st.nextToken();
                String lable=st.nextToken();
                while(!(temp.variable).equals(lable))
                {temp=temp.next;}
                String lol=temp.address;
                arr[k]=decimal2binary(lol);
                k++;
            }
        }
        for(int i=0;i<arr.length;i++)
            System.out.println(arr[i]);
        o1.close();
    }
    static String hex2binary(String str)
    {
        int len=4-str.length();
        int i;
        String s="";
        //System.out.println(str.length());
        //System.out.println(len);
        for(i=1;i<=len;++i)
        {
            s+="0000";
        }
        len=str.length();
        for(i=0;i<len;++i)
        {
            char ch=str.charAt(i);
            if(ch=='0')s+="0000";
            else if(ch=='1')s+="0001";
            else if(ch=='2')s+="0010";
            else if(ch=='3')s+="0011";
            else if(ch=='4')s+="0100";
            else if(ch=='5')s+="0101";
            else if(ch=='6')s+="0110";
            else if(ch=='7')s+="0111";
            else if(ch=='8')s+="1000";
            else if(ch=='9')s+="1001";
            else if(ch=='A' || ch=='a')s+="1010";
            else if(ch=='B' || ch=='b')s+="1011";
            else if(ch=='C' || ch=='c')s+="1100";
            else if(ch=='D' || ch=='d')s+="1101";
            else if(ch=='E' || ch=='e')s+="1110";
            else if(ch=='F' || ch=='f')s+="1111";
        }
        return s;
    }
    static String decimal2binary(String str)
    {
        String s="";
        if(s.equals("0")) s="0000000000000000";
        else
        {
            int n=Integer.parseInt(str);
            while(n!=0)
            {
                s=n%2+s;
                n=n/2;
            }
            int l=16-s.length();
            for(int i=0;i<l;i++) s="0"+s;
        }
        return s;
    }
}