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
			BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));
			
			String[] init = br.readLine().split(" ");
			customerID = Integer.valueOf(init[0]);
			InetAddress IPAddress = InetAddress.getByName(init[1]);
			
			for(String line; (line = br.readLine()) == null;){
				String[] command = line.split(" ");
				
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
					int port = Integer.parseInt(command[2]);
					
					byte[] receiveData = new byte[1024];
					
					if(command[3].equals("U")){
						DatagramSocket Dsocket = new DatagramSocket();
						String s = customerID + " " + command[0] + " " + command[1];
						DatagramPacket DSpacket = new DatagramPacket(s.getBytes(), s.getBytes().length, IPAddress, port);
						Dsocket.send(DSpacket);
						DatagramPacket DRpacket = new DatagramPacket(receiveData, receiveData.length);
						Dsocket.receive(DRpacket);
						System.out.println(DRpacket.getData());
						Dsocket.close();
					}
					else if(command[3].equals("T")){
						Socket socket = new Socket(IPAddress, port);
						DataOutputStream out = new DataOutputStream(socket.getOutputStream());
						BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						out.writeBytes(customerID + " " + command[0] + " " + command[1]);
						System.out.println(in.readLine());
						socket.close();
					}
				}	
			br.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
