package gameCore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;


public class gameCore implements Runnable {

	AtomicBoolean stillPlaying;
	gameStart game;

	public gameCore(gameStart g) {
		this.game = g;
		this.stillPlaying = game.stillPlaying;
	}

	public synchronized void run() {
		try {
			while (stillPlaying.get()) {
				if(!game.myServer.interrupted.get()) {
					sendPlayers();
					game.myServer.hasDraw.set(false);
					for (int i = 20; i > 0 && game.myServer.ttApp.get(); i--) {
						while(game.myServer.interrupted.get()) {
							Thread.sleep(1000);
							sendTiming(i);
						}
						Thread.sleep(1000);
						sendTiming(i);
	
					}
					if(!game.myServer.interrupted.get()) {
						if(checkIfFinished())
							stillPlaying.set(false);
						else
							nextPlayer();
					}
				}
			}
			System.out.println("Partita terminata !");
			System.exit(0);
		} catch (InterruptedException | IOException e) {
			System.out.println("Connessione interrotta : un utente si è disconnesso");
			System.exit(0);
		}
		/*
		 * try { Thread.sleep(9000); stillPlaying.set(false); } catch
		 * (InterruptedException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
	}

	public synchronized void nextPlayer() {
		if (game.myServer.currentPlayer + 1 == game.myServer.players.size())
			game.myServer.currentPlayer = 0;
		else
			game.myServer.currentPlayer++;
		game.myServer.ttApp.set(true);
	}
	public synchronized void sendPlayers() throws IOException {
		String nome = "";
		int c = 0;
		for (Entry<String, HashMap<ObjectInputStream, ObjectOutputStream>> a : game.myServer.players.entrySet()) {
			if (c == game.myServer.currentPlayer) {
				nome = a.getKey();
				break;
			}
			c++;
		}
		int c1 = 0;
		for (HashMap<ObjectInputStream, ObjectOutputStream> a : game.myServer.players.values()) {
			for (ObjectOutputStream b : a.values()) {
				b.writeObject(16);
				if (c1 ==game.myServer.currentPlayer)
					b.writeObject("È il tuo turno");
				else
					b.writeObject("È il turno di " + nome);
				b.writeObject(nome);
			}
			c1++;
		}
	}

	public synchronized void sendTiming(int t) throws IOException {
		for (HashMap<ObjectInputStream, ObjectOutputStream> a : game.myServer.players.values()) {
			for (ObjectOutputStream b : a.values()) {
				b.writeObject(15);
				b.writeObject(t);
			}
		}
	}
	
	public synchronized boolean checkIfFinished() {
		
		return false;
	}

}
