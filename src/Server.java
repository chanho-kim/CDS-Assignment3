import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicIntegerArray;


public class Server {
	
	static AtomicIntegerArray books;
	
	public static void main(String args[]){
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));
		    for(String line; (line = br.readLine()) != null;) {
		    	//Read the input
		        String[] command = line.split(" ");
		        
		        //How many books?
		        books = new AtomicIntegerArray(Integer.valueOf(command[0]));
		        
		        //Initialize all the books to -1
		        for(int i=0; i < books.length(); i+=1){
		        	books.set(i, -1);
		        }
		        
		        //Which ports for UDP and TCP?
		        Thread u = new Thread(new UDP(Integer.valueOf(command[1])));
		        Thread t = new Thread(new TCP(Integer.valueOf(command[2])));
		        
		        u.start();
		        t.start();      
		    }
			br.close();		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}

class UDP implements Runnable{
	
	DatagramSocket socket;
	DatagramPacket Rpacket;	//Receive Packet
	DatagramPacket Spacket;	//Send Packet
	
	UDP(int port){
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			System.out.println("Socket initialization failed.");
			e.printStackTrace();
		}
	}
			
	public void run(){
		while(true){
			try {
				//Initialization
				String sendData = "";
				byte[] receiveData = new byte[1024];
				
				//Establish connection
				Rpacket = new DatagramPacket(receiveData, receiveData.length);
				socket.receive(Rpacket);
				InetAddress IPAddress = Rpacket.getAddress();
				int Rport = Rpacket.getPort();
				
				//Receive the data
				String in = new String(Rpacket.getData()).trim();
				String[] command = in.split(" ");
				
				//Get the client index & book index
				int client = Integer.parseInt(command[0].replaceAll("[\\D]", ""));
				int bookIndex = Integer.parseInt(command[1].replaceAll("[\\D]", ""));
				
				//Reserve or return?
				if(command[2].equals("reserve")){
					//if the book is not reserved yet, mark it with client's ID
					if(Server.books.compareAndSet(bookIndex-1, -1, client)){
						sendData = command[0] + " b" + bookIndex;
					}
					else{
						sendData = "fail " + command[0] + " b" + bookIndex ;
					}
				}
				
				else if(command[2].equals("return")){
					//if the book is marked with client's ID, free it
					if(Server.books.compareAndSet(bookIndex-1, client, -1)){
						sendData = "free " + command[0] + " b" + bookIndex;
					}
					else{
						sendData = "fail " + command[0] + " b" + bookIndex;
					}
				}
				
				//Send the result back
				Spacket = new DatagramPacket(sendData.getBytes(), sendData.getBytes().length, IPAddress, Rport);
				socket.send(Spacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

class TCP implements Runnable {

	ServerSocket socket;
	
	TCP(int port){
		try {
		
			socket = new ServerSocket(port);

		} catch (IOException e) {
			System.out.println("Socket initialization failed.");
			e.printStackTrace();
		}
	}
	
	public void run(){
		while(true){
			try {
				//Initialization
				String sendData = "";
				
				//Establish connection
				Socket s = socket.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				DataOutputStream out = new DataOutputStream(s.getOutputStream());
				
				//Receive the data
				String inString = in.readLine();
				String[] command = inString.split(" ");
				
				//Get the client index & book index
				int client = Integer.parseInt(command[0].replaceAll("[\\D]", ""));
				int bookIndex = Integer.parseInt(command[1].replaceAll("[\\D]", ""));
				
				//Reserve or return?
				if(command[2].equals("reserve")){
					//if the book is not reserved yet, mark it with client's ID					
					if(Server.books.compareAndSet(bookIndex-1, -1, client)){
						sendData = command[0] + " b" + bookIndex + '\n';
					}
					else{
						sendData = "fail " + command[0] + " b" + bookIndex + '\n';
					}
				}
				
				else if(command[2].equals("return")){
					//if the book is marked with client's ID, free it					
					if(Server.books.compareAndSet(bookIndex-1, client, -1)){
						sendData = "free " + command[0] + " b" + bookIndex + '\n';
					}
					else{
						sendData = "fail " + command[0] + " b" + bookIndex + '\n';
					}
				}
				
				//Send the result back
				out.writeBytes(sendData);					
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
