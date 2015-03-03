import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicIntegerArray;


public class Server {
	
	static AtomicIntegerArray books;
	static DatagramSocket UDPSocket;
	static ServerSocket TCPSocket;
	
	public static void main(String args[]){
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));
		    for(String line; (line = br.readLine()) != null;) {
		        String[] command = line.split(" ");
		        books = new AtomicIntegerArray(Integer.valueOf(command[0]));
		        UDPSocket = new DatagramSocket(Integer.valueOf(command[1]));
		        TCPSocket = new ServerSocket(Integer.valueOf(command[2]));
		        
		        while(true){
		        	
		        	
		        	
		        }
		        
		    }
			br.close();		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class UDP implements Runnable{
		public void run(){
			
		}
	}
	
	private class TCP implements Runnable {
		public void run(){
			
		}
	}
	
}
