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
									System.out.println("L'utente " + nickname + " si � appena registrato");
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
						// System.out.println("Mi � arrivata la carta con id " + idCard + " da " +
						// trovaCarta(idCard, 0));
						System.out.println(myServer.lastCard.toString());
						//System.out.println(comparaCarta(myServer.playerCards.get(trovaCarta(idCard, 0)).get(idCard))
						//		+ " con id " + idCard);
						if (comparaCarta(myServer.playerCards.get(trovaCarta(idCard, 0)).get(idCard)) > 0) { // VERIFICA
																												// TRA
																												// CARTA
																												// LANCIATA
																												// E
																												// ULTIMA
																												// CARTA
																												// USATA

							updateLastCard(idCard);
							//System.out.println("La carta � dello stesso colore");

						} else { // COLORI DELLE CARTE DIVERSI
							//System.out.println("Colori diversi");
							writeToCurrentP(22);
						}

					}
					break;
				case 27:
					String name = (String) inObj.readObject();
					if(!myServer.hasDraw.get()) {
						if(name.compareTo(currentPName())==0) {
							writeToCurrentP(27);
							writeToCurrentP(1);
							HashMap<Integer,HashMap<String,String>> tempCard = new HashMap<Integer,HashMap<String,String>>();
							HashMap<String,String> cartaPescata=myServer.mazzo.pescaCarta();
							tempCard.put(myServer.idCard, cartaPescata);
							myServer.playerCards.get(currentPName()).put(myServer.idCard, cartaPescata);
							myServer.idCard++;
							writeToCurrentP(tempCard);
							int [] coord = myServer.mazzo.coordinateMazzo(cartaPescata);
							writeToCurrentP(coord);
							//myServer.ttApp.set(false);
							myServer.hasDraw.set(true);
						}
						else {
							myServer.players.get(name).values().iterator().next().writeObject(27);
							myServer.players.get(name).values().iterator().next().writeObject(0);
						}
					}
					else {
						myServer.players.get(name).values().iterator().next().writeObject(27);
						myServer.players.get(name).values().iterator().next().writeObject(2);
					}
					break;
				case 42:
					String color = (String) inObj.readObject();
					if(this.nickname.compareTo(currentPName())==0) {
						System.out.println("Mi hanno inviato "+color+" \nlast card vale "+myServer.lastCard.toString());
						myServer.specialColor.set(color);
						myServer.myTimer.notify();
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
			System.err.println("La connessione � stata interrotta");
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
	
	public synchronized void writeToCurrentP(int [] m) {
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
	
	public synchronized void writeToCurrentP(HashMap<Integer,HashMap<String,String>> card) {
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
		
		if(isSpecialCard(myServer.playerCards.get(currentPName()).get(idCard))) {
			writeToCurrentP(42);
			try {
				System.out.println("Hai usato una carta speciale");
				synchronized(myServer.myTimer) {
					myServer.myTimer.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		myServer.playerCards.get(currentPName()).remove(idCard);

		myServer.ttApp.set(false);
		
		myServer.myTimer.interrupt();
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
		if(isSpecialCard(a)) {
			equalities++;
			return equalities;
		}
		if(isSpecialCard(tempLastCard)) {
			if (tempLastCard.get("color").compareTo(myServer.specialColor.get())==0) {
				equalities++;
				myServer.specialColor.set("");
				return equalities;
			}
		}
		else {
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
	public synchronized boolean isSpecialCard(HashMap<String,String> a) {
		if(a.get("color").compareTo("")==0)
			return true;
		return false;
	}
}
