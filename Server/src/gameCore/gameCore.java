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
				if (!game.myServer.interrupted.get()) {
					checkIfFinished();
					sendPlayers();
					sendPlayersInfo();
					someOne();
					game.myServer.hasDraw.set(false);
					for (int i = 20; i > 0 && game.myServer.ttApp.get(); i--) {
						while (game.myServer.interrupted.get()) {
							Thread.sleep(1000);
							sendTiming(i);
						}
						Thread.sleep(1000);
						sendTiming(i);

					}
					if (!game.myServer.interrupted.get()) {
						if (checkIfFinished())
							stillPlaying.set(false);
						else {
							if (game.myServer.saltaProssimoTurno.get()) {
								nextPlayer();
								game.myServer.saltaProssimoTurno.set(false);
							}
							nextPlayer();
						}
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
		 * (InterruptedException e) { e.printStackTrace(); }
		 */
	}

	public synchronized void nextPlayer() {
		// System.err.println("inverti vale " + game.myServer.invertiPlayers.get() + " e
		// currentP vale "
		// + game.myServer.currentPlayer);
		if (game.myServer.invertiPlayers.get()) {
			if (game.myServer.currentPlayer == 0)
				game.myServer.currentPlayer = game.myServer.players.size() - 1;
			else
				game.myServer.currentPlayer--;
		} else {
			if (game.myServer.currentPlayer + 1 == game.myServer.players.size())
				game.myServer.currentPlayer = 0;
			else
				game.myServer.currentPlayer++;
		}
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
				if (c1 == game.myServer.currentPlayer)
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

	public synchronized void someOne() {
		for (String name : game.myServer.players.keySet()) {
			if (validCards(name) == 1) {
				if (currentPName().compareTo(name) == 0) {
					if (game.myServer.uno.get(name).booleanValue()) {
						mandaCarta();
						mandaCarta();
						writeToCurrentP(100);
						writeToCurrentP("Non hai detto uno !");
						game.myServer.uno.replace(name, false);
					} else
						game.myServer.uno.replace(name, true);
				} else {
					if (game.myServer.uno.get(name).booleanValue()) {
						mandaCarta(name);
						mandaCarta(name);
						writeToPlayer(name, 100);
						writeToPlayer(name, "Non hai detto uno !");
						game.myServer.uno.replace(name, false);
					}
				}
			}
		}
	}

	public synchronized void sendPlayersInfo() {
		String[] infos;
		int c;
		for (Entry<String, HashMap<ObjectInputStream, ObjectOutputStream>> a : game.myServer.players.entrySet()) {
			infos = new String[game.myServer.players.size() - 1];
			c = 0;
			for (Entry<String, HashMap<ObjectInputStream, ObjectOutputStream>> b : game.myServer.players.entrySet()) {
				if (a.getKey().compareTo(b.getKey()) != 0) {
					infos[c] = "<html><div style='text-align: center;font-size:1.2em;color:blue;font-weight: bold;text-transform: capitalize;'><i>"
							+ b.getKey() + "</i></div><br>" + validCards(b.getKey()) + " carte</html>";
					c++;
				}
			}
			for (Entry<ObjectInputStream, ObjectOutputStream> b : a.getValue().entrySet()) {
				try {
					b.getValue().writeObject(55);
					b.getValue().writeObject(infos);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public synchronized int validCards(String name) {
		int c = 0;
		for (HashMap<String, String> a : game.myServer.playerCards.get(name).values()) {
			if (a.get("value") != null) {
				c++;
			}
		}
		return c;
	}

	public synchronized boolean checkIfFinished() {
		int playersInGame=0;
		for(String name : game.myServer.players.keySet()) {
			if(validCards(name)>=1)
				playersInGame++;
		}
		if(playersInGame==1)
			return true;
		else
			return false;
	}

	public synchronized String currentPName() {
		int a = 0;
		for (String b : game.myServer.players.keySet()) {
			if (a == game.myServer.currentPlayer)
				return b;
			a++;
		}
		return null;
	}

	public synchronized void mandaCarta() {
		writeToCurrentP(27);
		writeToCurrentP(1);
		HashMap<Integer, HashMap<String, String>> tempCard = new HashMap<Integer, HashMap<String, String>>();
		HashMap<String, String> cartaPescata = game.myServer.mazzo.pescaCarta();
		tempCard.put(game.myServer.idCard, cartaPescata);
		game.myServer.playerCards.get(currentPName()).put(game.myServer.idCard, cartaPescata);
		game.myServer.idCard++;
		writeToCurrentP(tempCard);
		int[] coord = game.myServer.mazzo.coordinateMazzo(cartaPescata);
		writeToCurrentP(coord);
		game.myServer.hasDraw.set(true);
	}

	public synchronized void mandaCarta(String playerName) {
		writeToPlayer(playerName, 27);
		writeToPlayer(playerName, 1);
		HashMap<Integer, HashMap<String, String>> tempCard = new HashMap<Integer, HashMap<String, String>>();
		HashMap<String, String> cartaPescata = game.myServer.mazzo.pescaCarta();
		tempCard.put(game.myServer.idCard, cartaPescata);
		game.myServer.playerCards.get(playerName).put(game.myServer.idCard, cartaPescata);
		game.myServer.idCard++;
		writeToPlayer(playerName, tempCard);
		int[] coord = game.myServer.mazzo.coordinateMazzo(cartaPescata);
		writeToPlayer(playerName, coord);
		game.myServer.hasDraw.set(true);
	}

	public synchronized void writeToEveryone(int message) {
		for (Entry<String, HashMap<ObjectInputStream, ObjectOutputStream>> a : game.myServer.players.entrySet()) {
			try {
				for (ObjectOutputStream b : a.getValue().values()) {
					b.writeObject(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void writeToEveryone(String message) {
		for (Entry<String, HashMap<ObjectInputStream, ObjectOutputStream>> a : game.myServer.players.entrySet()) {
			try {
				for (ObjectOutputStream b : a.getValue().values()) {
					b.writeObject(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void writeToPlayer(String name, String message) {
		for (ObjectOutputStream a : game.myServer.players.get(name).values()) {
			try {
				a.writeObject(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void writeToPlayer(String name, int[] message) {
		for (ObjectOutputStream a : game.myServer.players.get(name).values()) {
			try {
				a.writeObject(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void writeToPlayer(String name, HashMap<Integer, HashMap<String, String>> message) {
		for (ObjectOutputStream a : game.myServer.players.get(name).values()) {
			try {
				a.writeObject(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void writeToPlayer(String name, int message) {
		for (ObjectOutputStream a : game.myServer.players.get(name).values()) {
			try {
				a.writeObject(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void writeToCurrentP(int m) {
		try {
			int a = 0;
			for (HashMap<ObjectInputStream, ObjectOutputStream> b : game.myServer.players.values()) {
				if (a == game.myServer.currentPlayer)
					b.values().iterator().next().writeObject(m);
				a++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void writeToCurrentP(HashMap<Integer, HashMap<String, String>> message) {
		try {
			int a = 0;
			for (HashMap<ObjectInputStream, ObjectOutputStream> b : game.myServer.players.values()) {
				if (a == game.myServer.currentPlayer)
					b.values().iterator().next().writeObject(message);
				a++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void writeToCurrentP(int[] message) {
		try {
			int a = 0;
			for (HashMap<ObjectInputStream, ObjectOutputStream> b : game.myServer.players.values()) {
				if (a == game.myServer.currentPlayer)
					b.values().iterator().next().writeObject(message);
				a++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void writeToCurrentP(String message) {
		try {
			int a = 0;
			for (HashMap<ObjectInputStream, ObjectOutputStream> b : game.myServer.players.values()) {
				if (a == game.myServer.currentPlayer)
					b.values().iterator().next().writeObject(message);
				a++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
