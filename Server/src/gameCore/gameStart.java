package gameCore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import connessioneServer.Server;
import mazzoServer.creaMazzo;

public class gameStart extends Thread {
	
	Server myServer;
	AtomicBoolean stillPlaying=new AtomicBoolean(true);
	
	
	public gameStart(Server myServer){
		this.myServer=myServer;
		for(String nomePlayer:myServer.players.keySet()) {
			HashMap<Integer,HashMap<String,String>> tempMap=new HashMap<Integer,HashMap<String,String>>() ;
			HashMap<String,String> tempMap_2=new HashMap<String,String>();
			tempMap_2.put(null,null);
			tempMap.put(0, tempMap_2);
			myServer.playerCards.put(nomePlayer, tempMap);
		}
		
	}
	public synchronized void run() {

		myServer.mazzo=new creaMazzo();
		myServer.mazzo.generaMazzo();
		startGame(myServer.mazzo);
		Thread core= new Thread(new gameCore(this));
		core.start();
			/*try {
				while(true) {
					Thread.sleep(1000);
					if(stillPlaying.get())
						System.out.println("Stll playing");
					else
						System.out.println("Not playing");
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
		//int[] coord=mazzo.coordinateMazzo(cartaPescata);
	}
	public synchronized void startGame(creaMazzo mazzo) {
		try {
			HashMap<String,String> cartaPescata_1=mazzo.pescaCarta();
			myServer.lastCard.clear();
			myServer.lastCard.put(myServer.idCard, cartaPescata_1);
			myServer.idCard++;
			generaBaseElem();
			for(int i=0;i<8;i++) {
				for(Map.Entry<String,HashMap<ObjectInputStream,ObjectOutputStream>> a:myServer.players.entrySet()) {
					for(ObjectOutputStream nowOutObj:a.getValue().values()) {
						if(i==0) {
							HashMap<Integer,HashMap<String,String>> temp_1=new HashMap<Integer,HashMap<String,String>>();
							temp_1.put(myServer.idCard, cartaPescata_1);
							System.out.println("Prima carta : "+cartaPescata_1.get("value")+" "+cartaPescata_1.get("color"));
							int[] coord_1=mazzo.coordinateMazzo(cartaPescata_1);
							nowOutObj.writeObject(9);
							nowOutObj.writeObject(temp_1);
							nowOutObj.writeObject(coord_1);
						}
						else {
							HashMap<Integer,HashMap<String,String>> temp=new HashMap<Integer,HashMap<String,String>>();
							HashMap<String,String> cartaPescata=mazzo.pescaCarta();
							temp.put(myServer.idCard, cartaPescata);
							System.out.println("Ho pescato : "+cartaPescata.get("value")+" "+cartaPescata.get("color"));
							int[] coord=mazzo.coordinateMazzo(cartaPescata);
							nowOutObj.writeObject(10);
							nowOutObj.writeObject(temp);
							nowOutObj.writeObject(coord);
							myServer.playerCards.get(a.getKey()).put(myServer.idCard, cartaPescata);
							myServer.idCard++;
						}
					}
				}
			}
			String [] playersName=new String[myServer.players.size()];
			int c=0;
			for(String a:myServer.players.keySet()) {
				playersName[c]=a;
				c++;
			}
			for(HashMap<ObjectInputStream,ObjectOutputStream> a:myServer.players.values()) {
				for(ObjectOutputStream nowOutObj:a.values()) {
					nowOutObj.writeObject(50);
					nowOutObj.writeObject(playersName);
				}
			}
			System.out.println("Player cards to string "+myServer.playerCards.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized void generaBaseElem() throws IOException {
		int c=0;
		Object names [] = new Object[myServer.players.size()];
		for(Entry<String,HashMap<ObjectInputStream,ObjectOutputStream>> a : myServer.players.entrySet()) {
			for(ObjectOutputStream b : a.getValue().values()) {
				names[c]=(a.getKey());
				c++;
			}
		}
		for(Entry<String,HashMap<ObjectInputStream,ObjectOutputStream>> a : myServer.players.entrySet()) {
			for(ObjectOutputStream b : a.getValue().values()) {
				b.writeObject(0);
				b.writeObject(names);
			}
		}
	}
}
