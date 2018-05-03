package mazzoServer;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;


public class creaMazzo {
	private Map<Integer,HashMap<String,String>> m; 
	public void generaMazzo() {
				
				//Object mazzo= new Object[109][5];
				Map<Integer,HashMap<String,String>> mazzo=new HashMap<Integer,HashMap<String,String>>();

				
				int contatoreMazzo=0;
				/*for(HashMap<String,String> myMap:listaCarte.values()) {
					for(Entry<String,String> pair: myMap.entrySet()) {
						if(pair.getKey()=="much") {
							System.out.println(pair.getKey()+" "+pair.getValue());
							for(int i=0;i<Integer.parseInt(pair.getValue());i++) {
								if(Boolean.parseBoolean(myMap.get("colored")))
								mazzo.put(contatoreMazzo, myMap);
								System.out.println("i: "+i+" contatoreMazzo: "+contatoreMazzo);
								contatoreMazzo++;
							}
						}
					}
				}*/
				HashMap<String,String> temp=new HashMap<String,String>();
				String [] colori= {"red","yellow","green","blue"};
				String [] eightColored= {"Pesca","Inverti","Stop"};
				String [] notColored= {"Jolly","Pesca+"};
				Random rn=new Random();
				int id=0;
				for(int i=0;i<19;i++) {
					for(String coloreNow:colori) {
						temp.put("color", coloreNow);
						temp.put("value",Integer.toString(rn.nextInt(9)));
						temp.put("id",Integer.toString(id));
						mazzo.put(contatoreMazzo, temp);
						contatoreMazzo++;
						temp=new HashMap<String,String>();
						id++;
					}
				}
				for(int i=0;i<8;i++) {
					for(String coloredNow:eightColored) {
						temp.put("color", colori[rn.nextInt(colori.length)]);
						temp.put("value",coloredNow);
						temp.put("id",Integer.toString(id));
						mazzo.put(contatoreMazzo, temp);
						contatoreMazzo++;
						temp=new HashMap<String,String>();
						id++;
					}
				}
				for(int i=0;i<4;i++) {
					for(String notColoredNow:notColored) {
						temp.put("color","");
						temp.put("value",notColoredNow);
						mazzo.put(contatoreMazzo, temp);
						temp.put("id",Integer.toString(id));
						contatoreMazzo++;
						temp=new HashMap<String,String>();
						id++;
					}
				}
				this.m=mazzo;
				randomMazzo();

		
	}
	public void randomMazzo(){
		System.out.println("\n\n");
		Random rn=new Random();
		HashMap<String,String> temp=new HashMap<String,String>();
		
		int myCurrentRand=0,anotherRand=0;
		for(int i=0;i<this.m.size();i++) {
			for(int j=0;j<2500;j++) {
				myCurrentRand=rn.nextInt(this.m.size());
				do {
					anotherRand=rn.nextInt(this.m.size());
				}
				while(anotherRand==myCurrentRand);
				temp=this.m.get(myCurrentRand);
				this.m.replace(myCurrentRand, this.m.get(anotherRand));
				this.m.replace(anotherRand, temp);
				temp=new HashMap<String,String>();
			}
		}
		//printMazzo();
	}
	public HashMap<String,String> pescaCarta() {
		
		 Map<Integer,HashMap<String,String>> mazzo=new HashMap<Integer,HashMap<String,String>>();
		 HashMap<String,String> myCard=this.m.get(0);
		 for(int i=0;i<this.m.size();i++) {
			 mazzo.put(i-1, this.m.get(i));
		 }
		 this.m=mazzo;
		 this.m.values().removeIf(Objects::isNull);
		 return myCard;
	}
	public static int[] coordinateMazzo(HashMap<String,String> carta){
		int row=0,col=0;
		if(carta.get("color")=="") {
			row=carta.get("value")=="Jolly"?0:4;
			col=13;
		}
		else {
			switch(carta.get("color")) {
				case "red":
					row=0;
					break;
				case "yellow":
					row=1;
					break;
				case "green":
					row=2;
					break;
				case "blue":
					row=3;
					break;
			}
			if(carta.get("value")!="Pesca" && carta.get("value")!="Inverti" && carta.get("value")!="Stop") {
				col=Integer.parseInt(carta.get("value"));
			}
			else {
				switch(carta.get("value")) {
					case "Pesca":
						col=12;
						break;
					case "Inverti":
						col=11;
						break;
					case "Stop":
						col=10;
						break;
				}
			}
		}
		return new int[] {row,col};
	}
	public void printMazzo() {
		/*for(HashMap<String,String> myMap:this.m.values()) {
			for(Entry<String,String> pair: myMap.entrySet()) {
				System.out.println(pair.getKey()+" "+pair.getValue());
			}
			System.out.print("----\n");
		}*/
		System.out.println(this.m.size());
		 for(int i=0;i<this.m.size();i++) {
			 System.out.println(this.m.get(i)!=null?this.m.get(i):"");
		 }
		System.out.println("\n\n\n\n\n");
	}
}
