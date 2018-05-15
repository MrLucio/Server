package connessioneServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import mazzoServer.creaMazzo;

public class Server {
	
	ServerSocket server=null;
	public Socket socket=null;
	
	int porta=2000;
	
	public int currentPlayer=0;
	public Map<String,HashMap<ObjectInputStream,ObjectOutputStream>> players=new HashMap<String,HashMap<ObjectInputStream,ObjectOutputStream>>();
	AtomicBoolean acceptConn=new AtomicBoolean();
	public AtomicBoolean ttApp=new AtomicBoolean(true);
	public AtomicBoolean hasDraw=new AtomicBoolean(false);
	public AtomicBoolean interrupted = new AtomicBoolean(false);
	public AtomicBoolean saltaProssimoTurno = new AtomicBoolean(false);
	public AtomicBoolean invertiPlayers = new AtomicBoolean(false);
	public HashMap<String,Boolean> uno = new HashMap<String,Boolean>();
	public String hasToDraw=null;
	Thread myNewConnections;
	public creaMazzo mazzo;
	public int idCard = 1;
	
	public Map<String,HashMap<Integer,HashMap<String,String>>> playerCards = new HashMap<String,HashMap<Integer,HashMap<String,String>>>();
	public Map<Integer,HashMap<String,String>> lastCard= new HashMap<Integer,HashMap<String,String>>();
	
	static Thread t1;
	
	public Socket attendi() {
		
		try {
			server=new ServerSocket(porta);
			Thread myNewConnections= new Thread(new newConnections(server,this));
			myNewConnections.start();
			this.myNewConnections=myNewConnections;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return socket;
		
	}
	public synchronized static void main(String args[]) throws IOException {
		Server s=new Server();
		s.socket=s.attendi();
		Scanner t=new Scanner(System.in);
		String m;
		s.acceptConn.set(true);
		while(true) {
			m=t.nextLine();
			if(m.equals("/start"))
				if(s.players.size()>1) {
					t1 =new gameCore.gameStart(s);
					t1.start();
					s.acceptConn.set(false);
				}
				else
					System.out.println("Numero di giocatori insufficiente");
		}
	}
}
