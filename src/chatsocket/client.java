package chatsocket;

import java.net.*;
import java.util.*;
import java.io.*;

public class client {

    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(System.in);
        Socket cs = null;
        try{
          System.out.println("Introduceti adresa server-ului si portul: ");
           cs = new Socket( in.next(), in.nextInt() );
        }
        catch (IOException e){
          System.out.println("Server sau port gresit! Introduceti din nou!");
        }
        
        
        DataOutputStream out = new DataOutputStream(cs.getOutputStream());
        final DataInputStream is = new DataInputStream(cs.getInputStream());
        String st = "";
        
        
        Thread T = new Thread (new Runnable(){
          
            public void run() {
             
                while (true){
                    String s ="";
                   try{
                        s = is.readUTF();
                        System.out.println(s);
                   }catch (IOException e) {}
                    }
                }
            }
        );
        T.start();
        while(true){
            st = in.nextLine();
            out.writeUTF(st);
        }
        
        

    }
 }
