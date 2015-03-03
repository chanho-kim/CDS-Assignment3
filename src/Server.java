import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicIntegerArray;


public class Server {
	
	static AtomicIntegerArray books;
	
	public static void main2(String args[]){
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));
		    for(String line; (line = br.readLine()) != null;) {
		        String[] command = line.split(" ");
		        books = new AtomicIntegerArray(Integer.valueOf(command[0]));
		        
		        //Initialize all the books to -1
		        for(int i=0; i < books.length(); i+=1){
		        	books.set(i, -1);
		        }
		        
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
				String sendData = "";
				byte[] receiveData = new byte[1024];
				Rpacket = new DatagramPacket(receiveData, receiveData.length);
				System.out.println("UDP Listening on port " + socket.getLocalPort());
				socket.receive(Rpacket);
				System.out.println("Received: " + Rpacket.toString());
				InetAddress IPAddress = Rpacket.getAddress();
				int Rport = Rpacket.getPort();
				String in = new String(Rpacket.getData());
				String[] command = in.split(" ");
				//Get the client index & book index
				int client = Integer.parseInt(command[0].replaceAll("[\\D]", ""));
				int bookIndex = Integer.parseInt(command[1].replaceAll("[\\D]", ""));
				if(command[2].equals("reserve")){
					if(Server.books.compareAndSet(bookIndex, -1, client)){
						sendData = command[0] + " b" + bookIndex;
					}
					else{
						sendData = "fail " + command[0] + " b" + bookIndex;
					}
				}
				else if(command[2].equals("return")){
					if(Server.books.compareAndSet(bookIndex, client, -1)){
						sendData = "free " + command[0] + " b" + bookIndex;
					}
					else{
						sendData = "fail " + command[0] + " b" + bookIndex;
					}
				}
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
				String sendData = "";
				System.out.println("TCP Listening on port " + socket.getLocalPort());
				Socket s = socket.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				DataOutputStream out = new DataOutputStream(s.getOutputStream());
				String inString = in.readLine();
				System.out.println("Received: " + inString);
				String[] command = inString.split(" ");
				//Get the client index & book index
				int client = Integer.parseInt(command[0].replaceAll("[\\D]", ""));
				int bookIndex = Integer.parseInt(command[1].replaceAll("[\\D]", ""));
				if(command[2].equals("reserve")){
					if(Server.books.compareAndSet(bookIndex, -1, client)){
						sendData = command[0] + " b" + bookIndex;
					}
					else{
						sendData = "fail " + command[0] + " b" + bookIndex;
					}
				}
				else if(command[2].equals("return")){
					if(Server.books.compareAndSet(bookIndex, client, -1)){
						sendData = "free " + command[0] + " b" + bookIndex;
					}
					else{
						sendData = "fail " + command[0] + " b" + bookIndex;
					}
				}
				out.write(sendData.getBytes());					
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
