package connessioneServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

public class keephearing implements Runnable {
	Socket s;
	ObjectInputStream inObj;
	ObjectOutputStream outObj;
	String nickname;
	Server myServer;

	public keephearing(Socket s, Server myServer) {
		try {
			this.inObj = new ObjectInputStream(s.getInputStream());
			this.outObj = new ObjectOutputStream(s.getOutputStream());
			this.s = s;
			this.myServer = myServer;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void run() {
		try {
			// System.out.println("Sto aspettando un messaggio sul Thread");
			int myBit;
			while ((myBit = (int) inObj.readObject()) != Integer.MAX_VALUE) {
				switch (myBit) {
				case 0:
					String message = (String) inObj.readObject();
					// System.out.println("Il client ha appena inviato una stringa : "+message);
					break;
				case 1:
					int message1 = (int) inObj.readObject();
					// System.out.println("Il client ha appena inviato un intero : "+message1);
					break;
				case 99:
					String nickname = (String) inObj.readObject();
					boolean matches = false;
					if (myServer.acceptConn.get()) {
						if (myServer.players.size() < 4) {
							if (!nickname.isEmpty()) {
								for (String a : myServer.players.keySet()) {
									if (a.equals(nickname.trim()))
										matches = true;
								}
								if (matches) {
									outObj.writeObject(99);
									outObj.writeObject(0);
								} else {
									HashMap<ObjectInputStream, ObjectOutputStream> o = new HashMap<ObjectInputStream, ObjectOutputStream>();
									o.put(inObj, outObj);
									myServer.players.put(nickname, o);
									// new newConnections().showCurrentPlayers();

									this.nickname = nickname;
									outObj.writeObject(99);
									outObj.writeObject(1);
									outObj.writeObject(nickname);
									System.out.println("L'utente " + nickname + " si è appena registrato");
								}
							} else {
								outObj.writeObject(99);
								outObj.writeObject(0);
							}
						} else {
							outObj.writeObject(99);
							outObj.writeObject(3);
						}
					} else {
						outObj.writeObject(99);
						outObj.writeObject(2);
					}
					break;
				case 20:
					int idCard = (int) inObj.readObject();
					if (currentPName() == trovaCarta(idCard, 0) && myServer.ttApp.get()) {
						// System.out.println("Mi è arrivata la carta con id " + idCard + " da " +
						// trovaCarta(idCard, 0));
						System.out.println(myServer.lastCard.toString());
						// System.out.println(comparaCarta(myServer.playerCards.get(trovaCarta(idCard,
						// 0)).get(idCard))
						// + " con id " + idCard);
						if (comparaCarta(myServer.playerCards.get(trovaCarta(idCard, 0)).get(idCard)) > 0) { // VERIFICA
																												// TRA
																												// CARTA
																												// LANCIATA
																												// E
																												// ULTIMA
																												// CARTA
																												// USATA

							updateLastCard(idCard);
							// System.out.println("La carta è dello stesso colore");

						} else { // COLORI DELLE CARTE DIVERSI
							// System.out.println("Colori diversi");
							writeToCurrentP(22);
						}

					}
					break;
				case 27:
					String name = (String) inObj.readObject();
					if (name.compareTo(currentPName()) == 0) {
						if (!myServer.hasDraw.get()) {
							writeToCurrentP(27);
							writeToCurrentP(1);
							HashMap<Integer, HashMap<String, String>> tempCard = new HashMap<Integer, HashMap<String, String>>();
							HashMap<String, String> cartaPescata = myServer.mazzo.pescaCarta();
							tempCard.put(myServer.idCard, cartaPescata);
							myServer.playerCards.get(currentPName()).put(myServer.idCard, cartaPescata);
							myServer.idCard++;
							writeToCurrentP(tempCard);
							int[] coord = myServer.mazzo.coordinateMazzo(cartaPescata);
							writeToCurrentP(coord);
							myServer.hasDraw.set(true);
						} else {
							myServer.players.get(name).values().iterator().next().writeObject(27);
							myServer.players.get(name).values().iterator().next().writeObject(2);
						}
					} else {
						myServer.players.get(name).values().iterator().next().writeObject(27);
						myServer.players.get(name).values().iterator().next().writeObject(0);
					}
					break;
				case 42:
					String color = (String) inObj.readObject();
					String nameOfPlayer = (String) inObj.readObject();
					System.out.println("Ho ricevuto il color " + color);
					System.out.println("name vale " + nameOfPlayer + " currentName vale " + currentPName());
					if (nameOfPlayer.compareTo(currentPName()) == 0) {
						System.out.println(
								"Mi hanno inviato " + color + " \nlast card vale " + myServer.lastCard.toString());
						System.out.println("Prima lastCard vale " + myServer.lastCard.toString());
						myServer.lastCard.values().iterator().next().put("color", color);
						System.out.println("Ora lastCard vale " + myServer.lastCard.toString());
						myServer.interrupted.set(false);
						writeAllNoCurrent(100);
						writeAllNoCurrent(currentPName() + " ha cambiato colore in " + color);
					}
					break;
				case 56:
					String pName = (String) inObj.readObject();
					if (pName.compareTo(currentPName()) == 0) {
						myServer.ttApp.set(false);
					}
					break;
				default:
					break;
				}
			}
			/*
			 * while((message=(String) inObj.readObject())!= null)
			 * System.out.println("Il client ha scritto "+message);
			 */
		} catch (IOException e) {
			System.err.println("La connessione è stata interrotta");
			if (myServer.players.size() == 0)
				System.exit(0);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void writeToClient(String a) {
		try {
			outObj.writeObject(0);
			outObj.writeObject(a);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void writeToClient(int a) {
		try {
			outObj.writeObject(1);
			outObj.writeObject(a);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized String trovaCarta(int idCard, int cosaVuoi) {
		for (Entry<String, HashMap<Integer, HashMap<String, String>>> a : myServer.playerCards.entrySet()) {
			for (Entry<Integer, HashMap<String, String>> b : a.getValue().entrySet()) {
				if (b.getKey() == idCard)
					switch (cosaVuoi) {
					case 0:
						return a.getKey();
					case 1:
						return b.getValue().values().iterator().next();
					case 2:
						return b.getValue().get("value");
					}
			}
		}
		return null;
	}

	public synchronized HashMap<String, String> trovaCartaHashMap(int idCard) {

		return myServer.playerCards.get(trovaCarta(idCard, 0)).get(idCard);

		/*
		 * HashMap<Integer, HashMap<String, String>> carta =
		 * trovaCartaCompleteHashMap(idCard); HashMap<String, String> temp = new
		 * HashMap<String,String>(); for(HashMap<String,String> a : carta.values()) {
		 * for(String c : a.keySet()) { for(String d : a.values()) temp.put(c, d); } }
		 * return temp;
		 */
	}

	public synchronized HashMap<Integer, HashMap<String, String>> trovaCartaCompleteHashMap(int idCard) {
		/*
		 * for (Entry<Integer, HashMap<String, String>> a :
		 * myServer.playerCards.values().iterator().next().entrySet()) { if
		 * (a.getKey().intValue() == idCard) { HashMap<Integer, HashMap<String,String>>
		 * temp = new HashMap<Integer, HashMap<String,String>>();
		 * temp.put(a.getKey().intValue(), a.getValue()); return temp; } }
		 */
		HashMap<Integer, HashMap<String, String>> a = myServer.playerCards.get(currentPName());
		HashMap<Integer, HashMap<String, String>> temp = new HashMap<Integer, HashMap<String, String>>();
		temp.put(idCard, myServer.playerCards.get(currentPName()).get(idCard));
		return temp;
	}

	public synchronized String currentPName() {
		int a = 0;
		for (String b : myServer.players.keySet()) {
			if (a == myServer.currentPlayer)
				return b;
			a++;
		}
		return null;
	}

	public synchronized void writeToCurrentP(int m) {
		try {
			int a = 0;
			for (HashMap<ObjectInputStream, ObjectOutputStream> b : myServer.players.values()) {
				if (a == myServer.currentPlayer)
					b.values().iterator().next().writeObject(m);
				a++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void writeToCurrentP(int[] m) {
		try {
			int a = 0;
			for (HashMap<ObjectInputStream, ObjectOutputStream> b : myServer.players.values()) {
				if (a == myServer.currentPlayer)
					b.values().iterator().next().writeObject(m);
				a++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void writeToCurrentP(HashMap<Integer, HashMap<String, String>> card) {
		try {
			int a = 0;
			for (HashMap<ObjectInputStream, ObjectOutputStream> b : myServer.players.values()) {
				if (a == myServer.currentPlayer)
					b.values().iterator().next().writeObject(card);
				a++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void writeToEveryone(int m) {
		try {
			for (HashMap<ObjectInputStream, ObjectOutputStream> a : myServer.players.values()) {
				for (ObjectOutputStream b : a.values()) {
					b.writeObject(m);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void writeToEveryone(HashMap<String, String> m) {
		try {
			for (HashMap<ObjectInputStream, ObjectOutputStream> a : myServer.players.values()) {
				for (ObjectOutputStream b : a.values()) {
					b.writeObject(m);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void writeToEveryoneComplete(HashMap<Integer, HashMap<String, String>> m) {
		try {
			for (HashMap<ObjectInputStream, ObjectOutputStream> a : myServer.players.values()) {
				for (ObjectOutputStream b : a.values()) {
					b.writeObject(m);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void writeToEveryone(int[] m) {
		try {
			for (HashMap<ObjectInputStream, ObjectOutputStream> a : myServer.players.values()) {
				for (ObjectOutputStream b : a.values()) {
					b.writeObject(m);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void updateLastCard(int idCard) {
		System.out.println();
		writeToEveryone(9);
		writeToEveryoneComplete(trovaCartaCompleteHashMap(idCard));
		writeToEveryone(coordinateMazzo(trovaCartaHashMap(idCard)));
		writeToCurrentP(8);
		writeToCurrentP(idCard);

		myServer.lastCard.clear();

		myServer.lastCard = trovaCartaCompleteHashMap(idCard);

		if (isSpecialCard(myServer.playerCards.get(currentPName()).get(idCard))) {
			writeToCurrentP(42);
			myServer.interrupted.set(true);
		}

		int specialC = isPlusCard(myServer.playerCards.get(currentPName()).get(idCard));
		HashMap<Integer,HashMap<String,String>> tempCard;
		
		switch (specialC) {
		case -1:
			for (int i = 0; i < 2; i++) {
				writeNextP(27);
				writeNextP(1);
				tempCard = null;
				tempCard = pescaProssimaCarta();
				writeNextP(tempCard);
				writeNextP(myServer.mazzo.coordinateMazzo(tempCard.values().iterator().next()));
			}
			writeNextP(100);
			writeNextP("Quell'infame di " + currentPName() + " ti ha fatto pescare 2 carte");
			break;
		case 1:
			for (int i = 0; i < 4; i++) {
				writeNextP(27);
				writeNextP(1);
				tempCard = null;
				tempCard = pescaProssimaCarta();
				writeNextP(tempCard);
				writeNextP(myServer.mazzo.coordinateMazzo(tempCard.values().iterator().next()));
			}
			writeNextP(100);
			writeNextP("Quell'infame di " + currentPName() + " ti ha fatto pescare 4 carte");
			break;
		}

		myServer.playerCards.get(currentPName()).remove(idCard);

		myServer.ttApp.set(false);

	}

	public synchronized int[] coordinateMazzo(HashMap<String, String> carta) {
		int row = 0, col = 0;
		if (carta.get("color") == "") {
			row = carta.get("value") == "Jolly" ? 0 : 4;
			col = 13;
		} else {
			switch (carta.get("color")) {
			case "red":
				row = 0;
				break;
			case "yellow":
				row = 1;
				break;
			case "green":
				row = 2;
				break;
			case "blue":
				row = 3;
				break;
			}
			if (carta.get("value") != "Pesca" && carta.get("value") != "Inverti" && carta.get("value") != "Stop") {
				col = Integer.parseInt(carta.get("value"));
			} else {
				switch (carta.get("value")) {
				case "Pesca":
					col = 12;
					break;
				case "Inverti":
					col = 11;
					break;
				case "Stop":
					col = 10;
					break;
				}
			}
		}
		return new int[] { row, col };
	}

	public synchronized int comparaCarta(HashMap<String, String> a) {
		HashMap<String, String> tempLastCard = myServer.lastCard.values().iterator().next();
		String[] b = new String[] { "color", "value" };
		int equalities = 0;
		if (isSpecialCard(a)) {
			equalities++;
			return equalities;
		} else {
			for (String c : b) {
				if (tempLastCard.get(c).compareTo(a.get(c)) == 0)
					equalities++;
			}
		}
		return equalities;
	}

	public synchronized void removeNulls() {
		for (HashMap<Integer, HashMap<String, String>> a : myServer.playerCards.values()) {
			a.remove(0);
		}
	}

	public synchronized boolean isSpecialCard(HashMap<String, String> a) {
		if (a.get("color").compareTo("") == 0)
			return true;
		return false;
	}

	public synchronized void writeAllNoCurrent(int message) {
		for (Entry<String, HashMap<ObjectInputStream, ObjectOutputStream>> a : myServer.players.entrySet()) {
			if (a.getKey().compareTo(currentPName()) != 0) {
				try {
					for (ObjectOutputStream b : a.getValue().values()) {
						b.writeObject(message);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized void writeAllNoCurrent(String message) {
		for (Entry<String, HashMap<ObjectInputStream, ObjectOutputStream>> a : myServer.players.entrySet()) {
			if (a.getKey().compareTo(currentPName()) != 0) {
				try {
					for (ObjectOutputStream b : a.getValue().values()) {
						b.writeObject(message);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized void writeNextP(int message) {
		String nextPlayersName = nextPName();
		for (Entry<String, HashMap<ObjectInputStream, ObjectOutputStream>> a : myServer.players.entrySet()) {
			if (a.getKey().compareTo(nextPlayersName) == 0) {
				try {
					a.getValue().values().iterator().next().writeObject(message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized void writeNextP(HashMap<Integer, HashMap<String, String>> message) {
		String nextPlayersName = nextPName();
		for (Entry<String, HashMap<ObjectInputStream, ObjectOutputStream>> a : myServer.players.entrySet()) {
			if (a.getKey().compareTo(nextPlayersName) == 0) {
				try {
					a.getValue().values().iterator().next().writeObject(message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized void writeNextP(String message) {
		String nextPlayersName = nextPName();
		for (Entry<String, HashMap<ObjectInputStream, ObjectOutputStream>> a : myServer.players.entrySet()) {
			if (a.getKey().compareTo(nextPlayersName) == 0) {
				try {
					a.getValue().values().iterator().next().writeObject(message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized void writeNextP(int[] message) {
		String nextPlayersName = nextPName();
		for (Entry<String, HashMap<ObjectInputStream, ObjectOutputStream>> a : myServer.players.entrySet()) {
			if (a.getKey().compareTo(nextPlayersName) == 0) {
				try {
					a.getValue().values().iterator().next().writeObject(message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public synchronized int isPlusCard(HashMap<String, String> a) {
		if (a.get("value") == "Pesca")
			return -1;
		else if (a.get("value") == "Pesca+")
			return 1;
		return 0;
	}

	public synchronized String nextPName() {
		int nextPID;
		if (myServer.currentPlayer + 1 == myServer.players.size())
			nextPID = 0;
		else
			nextPID = myServer.currentPlayer + 1;
		String nome = "";
		int c = 0;
		for (Entry<String, HashMap<ObjectInputStream, ObjectOutputStream>> a : myServer.players.entrySet()) {
			if (c == nextPID) {
				nome = a.getKey();
				break;
			}
			c++;
		}
		return nome;
	}

	public synchronized HashMap<Integer, HashMap<String, String>> pescaProssimaCarta() {
		HashMap<Integer, HashMap<String, String>> tempCard = new HashMap<Integer, HashMap<String, String>>();
		HashMap<String, String> cartaPescata = myServer.mazzo.pescaCarta();
		tempCard.put(myServer.idCard, cartaPescata);
		myServer.playerCards.get(nextPName()).put(myServer.idCard, cartaPescata);
		myServer.idCard++;
		return tempCard;
	}
}
