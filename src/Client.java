import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;


public class Client {
	
	static int customerID;
	
	
	public static void main(String args[]){
		try {
			//Read the input
			BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));
			
			String[] init = br.readLine().split(" ");
			
			//Customer ID?
			customerID = Integer.valueOf(init[0]);
			
			//IP Address?
			InetAddress IPAddress = InetAddress.getByName(init[1]);
			
			//While there are lines to read:
			for(String line; (line = br.readLine()) != null;){
				String[] command = line.split(" ");
				
				//If the command is to sleep,
				if(command[0].equals("sleep")){
					try {
						Thread.sleep(Integer.parseInt(command[1]));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				else{
					//Retrieve port number
					int port = Integer.parseInt(command[2]);
					
					//Initialize receive buffer
					byte[] receiveData = new byte[1024];
					
					//If the connection is UDP
					if(command[3].equals("U")){
						DatagramSocket Dsocket = new DatagramSocket();
						
						//Prepare the package & establish connection
						String s = customerID + " " + command[0] + " " + command[1];
						DatagramPacket DSpacket = new DatagramPacket(s.getBytes(), s.getBytes().length, IPAddress, port);
						
						//Send the package
						Dsocket.send(DSpacket);
						
						//Receive the answer
						DatagramPacket DRpacket = new DatagramPacket(receiveData, receiveData.length);
						Dsocket.receive(DRpacket);
						s = new String(DRpacket.getData()).trim();
						System.out.println(s);
						
						//Close the connection
						Dsocket.close();
					}
					
					//If the connection is TCP
					else if(command[3].equals("T")){
						
						//Establish connection
						Socket socket = new Socket(IPAddress, port);
						
						//Prepare the out / in 
						DataOutputStream out = new DataOutputStream(socket.getOutputStream());
						BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						
						//Send the data
						String s = customerID + " " + command[0] + " " + command[1] + '\n';
						out.writeBytes(s);
						
						//Receive the answer
						System.out.println(in.readLine());
						
						//Close the connection
						socket.close();
					}
				}	
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
