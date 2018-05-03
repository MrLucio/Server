package connessioneServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class newConnections implements Runnable {
	ServerSocket s;
	Server myServer;
	Thread [] pippo=new Thread[4];
	int count=0;
	public newConnections(ServerSocket s,Server myServer) {
		this.s=s;
		this.myServer=myServer;
	}
	public synchronized void run() {
		while(true) {
			try {
				Socket socket= s.accept();
				if(myServer.acceptConn.get()) {
					System.out.println("Client connesso");
					pippo[count]=new Thread(new keephearing(socket,myServer));
					pippo[count].start();
					count++;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/*public void showCurrentPlayers() {
		for(HashMap a:Server.players.values()) {
			System.out.println("- "+a);
		}
	};*/
}