
package chatsocket;

import java.io.*;
import java.net.*;
import java.util.*;

public class server {
  static int nr_conex = 0;
  static Vector < DataOutputStream > sockete = new Vector < DataOutputStream > ();
  static Vector < String > useri = new Vector < String > ();
  static Vector < Integer > inChat = new Vector < Integer >();
    
  public static void main(String[] args) throws IOException{
        
    ServerSocket ss = null;
    Socket cs = null ;
    boolean is_listening = true;
        
    //Pornesc serverul :
    Scanner in = new Scanner(System.in);
    
    
      System.out.print("Introduceti portul: ");
      ss = new ServerSocket( in.nextInt() );
        
      System.out.println("Server ON! ");
    
     
      while (is_listening){
        cs = ss.accept();
        DataOutputStream el = new DataOutputStream(cs.getOutputStream());
        System.out.println("User nou! ");
        nr_conex++;
        sockete.add(el);
        new Conexiune(cs,nr_conex,sockete);
      }
    
   
   
    }
}

class Conexiune extends Thread{
  String user_name;
  int identitate;
  Socket user = null;
  DataInputStream in = null;
  DataOutputStream out = null;
  Vector<DataOutputStream> sockete;
    
  public Conexiune(Socket cs, int nr, Vector<DataOutputStream> sockete) throws IOException {
        
    user = cs;
    identitate = nr-1;
    in = new DataInputStream(user.getInputStream());
    out = new DataOutputStream(user.getOutputStream());
    this.sockete = sockete;
    server.inChat.add(1);
        
    //STABILIM USERNAME-UL :
    in.readUTF();
    int ok = 1; //nu a ales username
    while (ok==1){
     out.writeUTF("Alegeti un username: ");
      String s = in.readUTF();
      if (s.equals("\n")==false){
        if (verificaNume(s)==true){
          user_name = s;
          ok = 0;
          server.useri.addElement(s);
        }else 
            out.writeUTF("Eroare: Username existent. ");
       }
    }
    System.out.println(user_name+ " s-a conectat!");
    start();
  }
    
    
    
    
  public void run(){
    try{
      while (true){
        
        String message = in.readUTF();
        if (message.equals("\n")==false) {  
               
          if (message.equals("LIST")) trimiteLista();
          else if (message.equals("LOG OUT")) deconecteaza();
         
          else if (message.equals("SCHIMBA USERNAME ")) user_name = schimbaUserName(user_name);
          else if ( message.equals("BCAST")) {
            String mesaj = in.readUTF();
            String msg = user_name+" : " + mesaj;
            trimiteMesajTuturor(msg);
          } else if ( message.startsWith("SEND TO")){
            
             String whom = message.substring(message.lastIndexOf(" ")+1);
             System.out.println(whom);
             if (verificaNume(whom)==true) out.writeUTF("Eroare: Nu exista utilizatorul!");
             else{
                String what = in.readUTF();
                trimiteMesajParticular(whom, what);
              }
             }
          else out.writeUTF("Nu exista comanda!");
        }
      }
    }
    catch (IOException e){}
   
      
  }
     
  synchronized public boolean verificaNume(String nume){
    for (int i = 0; i<server.useri.size();i++)
      if ( (server.inChat.elementAt(i)==1) &&  (nume.equals(server.useri.elementAt(i)))) return false;
    return true;
   }
  
  synchronized public void trimiteLista() throws IOException{
    out.writeUTF("Userii online sunt: ");
    for (int i = 0; i<server.useri.size(); i++)
      if (server.inChat.elementAt(i)==1) 
        out.writeUTF(server.useri.elementAt(i));
  }
  
   public void deconecteaza() throws IOException{
    out.writeUTF("La revedere!");
    for (int i = 0; i< server.useri.size(); i++){
      if (user_name.equals(server.useri.elementAt(i))&& server.inChat.elementAt(i)==1)
        server.inChat.setElementAt(0,i);
     }
    System.out.println("Utilizatorul "+ user_name + " s-a deconectat");
    in.close();
    out.close();
    user.close();
   
  }
  
  synchronized public String schimbaUserName(String nume_vechi) throws IOException{
    String nm = nume_vechi;
    int ok = 1; //nu a ales username
    while (ok==1){
      out.writeUTF("Alegeti un username: ");
      String s = in.readUTF();
      if (verificaNume(s) == true){
         user_name = s;
         ok = 0;
         //setam  numele:
          for (int i = 0; i< server.useri.size(); i++)
            if (nm.equals(server.useri.elementAt(i))&& server.inChat.elementAt(i)==1)
              server.useri.setElementAt(s,i);
       }else out.writeUTF("Username existent."); 
  }
 System.out.println(nm+" a devenit "+ user_name);
 trimiteMesajTuturor("Userul " + nm + "a devenit"+ user_name);
 return user_name;
  }
  
  synchronized public void trimiteMesajTuturor(String mesaj) throws IOException{
    if (mesaj.length() > 255) out.writeUTF("Eroare: Mesaj prea lung!");
    else {
      System.out.println(mesaj);
      for (int i = 0; i<server.useri.size(); i++)
        if (server.inChat.elementAt(i)==1)
          sockete.get(i).writeUTF(mesaj); 
    }
  }
  
  synchronized public void trimiteMesajParticular(String whom, String what) throws IOException{
    if (what.length()>255) out.writeUTF("Eroare: Mesaj prea lung!");
    else{
      for (int i =0; i< server.useri.size();i++)
        if (server.inChat.elementAt(i)==1 && whom.equals(server.useri.elementAt(i))==true)
          sockete.get(i).writeUTF(user_name + " : " + what);
     }
  }
  
  
}