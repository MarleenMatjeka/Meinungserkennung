import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.Executors;

public class Mainclass {
	public static ArrayList<Tweet> tweetlist;
	public static HashMap<String, Integer> map;
	public static NGramBuilder otto;
	public static Evaluation eva;
	
	public static void main(String[] args) {
		tweetlist = new ArrayList<>();
		otto = new NGramBuilder();
		Evaluation eva = new Evaluation();
		map = new HashMap<>();

		
		// N=9 Mapping erzeugen, Daten erzeugen, parameter finden, trainieren, testen und evaluieren:
		// Achtung Dateipfade sind hardcodiert
		createMapping(9);
		
		tweetlist = new ArrayList<>();
		createTargetSeperatedInputFiles(tweetlist, new int[] {9}, "train.csv", "n9", "train");
		tweetlist = new ArrayList<>();
		createTargetSeperatedInputFiles(tweetlist, new int[] {9}, "test.csv", "n9", "test");
	
		testSVMAll(9);		// muss manuell ausgewertet werden
		
		// n, target, svmtype, kerneltype, c
		train(9,"Abortion", 0,0,500);
		train(9,"Atheism", 0,0,500);
		train(9,"Climate", 0,2,2000);
		train(9,"Clinton", 0,0,500);
		train(9,"Feminist", 0,0,500);
	
		eva.evaluate(9);
		// Ergebnisse in FinaleN9Statistiken.txt
	
	}

	
	/*
	 * Mapping für trainings und testdaten für ngramme der Größe 1 bis 3
	 */
	public static void createMapping(int n) {
		int mappingvalue = 1;
		
		try(FileWriter fw = new FileWriter(new File("C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/Statistiken/Mapping.txt"))){
		
			
		readTweets("train.csv");				// tweets einlesen
		System.out.println("train.csv einlesen: "+checkTweetlist(tweetlist));
		for(Tweet t : tweetlist) {				// für jeden tweet
			for(int i = 1; i<=n; i++) {			// werden ngramme erzeugt von n=1 bis n=9
				ArrayList<String> ngrams = otto.buildNgram(t.getContent(), i);		//ngramme für diesen tweet erzeugen
				for(String s : ngrams) {		// jedes ngramm
					if(!map.containsKey(s)) {	// 
						map.put(s, mappingvalue);	// kommt in die hashmap wenn es noch nicht vorhanden ist 
						fw.write(s+ "  "+mappingvalue+"\n");
						mappingvalue++;
					}
				}
			}
		}
		
		
		
		readTweets("test.csv");				// tweets einlesen
		System.out.println("test.csv einlesen: "+checkTweetlist(tweetlist));
		for(Tweet t : tweetlist) {				// für jeden tweet
			for(int i = 1; i<=n; i++) {			// werden ngramme erzeugt von n=1 bis n=9
				ArrayList<String> ngrams = otto.buildNgram(t.getContent(), i);		//ngramme für diesen tweet erzeugen
				for(String s : ngrams) {		// jedes ngramm
					if(!map.containsKey(s)) {	// 
						map.put(s, mappingvalue);	// kommt in die hashmap wenn es noch nicht vorhanden ist 
						fw.write(s+ "  "+mappingvalue+"\n");
						mappingvalue++;
					}
				}
			}
		}
		
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	 * Ohne Separierung der Targets
	 */
	public static void createInputFile2(ArrayList<Tweet> tweetlist, int n, String inputfile, String outputfile) {
		System.out.println("Einlesen der Tweets: " + readTweets(inputfile));
		try(FileWriter fw = new FileWriter(new File("C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/windows/"
						+ outputfile))){
			for(Tweet t: tweetlist) {
				ArrayList<String> ngrams = otto.buildNgram(t.getContent(), n);
	
				switch (t.getStance()) {
				case "FAVOR":
					fw.write("1 ");
					break;
				case "AGAINST":
					fw.write("2 ");
					break;
				case "NONE":
					fw.write("3 ");
					break;
				}
				
			
				// ngramme nachgucken und indizes sortieren
				ArrayList<Integer> indizes = new ArrayList<>();
				for(String s : ngrams) {
					int index = lookup(s);		// passende Zahl für das Ngramm holen
					if(!indizes.contains(index))		//für den fall das zwei ngramme auf die gleiche zahl gemappt wurden, wird der index nicht doppelt eingefügt
					indizes.add(index);
				}
				Collections.sort(indizes);
				
				for(int i : indizes) {
					fw.write(i+":1.0 ");
				}
				fw.write(System.lineSeparator());
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Datei die mono-, bi- und trigramme enthält
	 * Ohne Separierung der Targets
	 */
	public static void createInputFile3(ArrayList<Tweet> tweetlist, String inputfile, String outputfile) {
		System.out.println("Einlesen der Tweets: " + readTweets(inputfile));
		try(FileWriter fw = new FileWriter(new File("C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/windows/"
						+ outputfile))){
			for(Tweet t: tweetlist) {
				ArrayList<String> ngrams1 = otto.buildNgram(t.getContent(), 1);
				ArrayList<String> ngrams2 = otto.buildNgram(t.getContent(), 2);
				ArrayList<String> ngrams3 = otto.buildNgram(t.getContent(), 3);
	
				switch (t.getStance()) {
				case "FAVOR":
					fw.write("1 ");
					break;
				case "AGAINST":
					fw.write("2 ");
					break;
				case "NONE":
					fw.write("3 ");
					break;
				}
				
			
				// ngramme nachgucken und indizes sortieren
				ArrayList<Integer> indizes = new ArrayList<>();
				for(String s : ngrams1) {
					int index = lookup(s);		// passende Zahl für das Ngramm holen
					if(!indizes.contains(index))		//für den fall das zwei ngramme auf die gleiche zahl gemappt wurden, wird der index nicht doppelt eingefügt
					indizes.add(index);
				}
				for(String s : ngrams2) {
					int index = lookup(s);		// passende Zahl für das Ngramm holen
					if(!indizes.contains(index))		//für den fall das zwei ngramme auf die gleiche zahl gemappt wurden, wird der index nicht doppelt eingefügt
					indizes.add(index);
				}
				for(String s : ngrams3) {
					int index = lookup(s);		// passende Zahl für das Ngramm holen
					if(!indizes.contains(index))		//für den fall das zwei ngramme auf die gleiche zahl gemappt wurden, wird der index nicht doppelt eingefügt
					indizes.add(index);
				}
				Collections.sort(indizes);
				
				for(int i : indizes) {
					fw.write(i+":1.0 ");
				}
				fw.write(System.lineSeparator());
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	
	
	/*
	 * erzeugt 5 Inputdateien für die SVM, für jedes Thema eine 
	 * bekommt die tweetlist, das n für die n-Gramme, Datei der Trainingsdaten im Ordner
	 * C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/stancedataset/
	 * StanceDataset/ Dateiname, die die erzeugten inputfiles haben sollen
	 * Dateiendung, die die erzeugten Inputfiles haben sollen. entweder train oder
	 * test ohne Punkt angeben
	 */
	public static void createTargetSeperatedInputFiles(ArrayList<Tweet> tweetlist, int[] ns, String inputfile,
		String outputfileName, String outputfileEnding) {

		System.out.println("Einlesen der Tweets: " + readTweets(inputfile)); // Tweets aus der Datei extrahieren

		File fileClinton = new File(
				"C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/windows/"
						+ outputfileName + "Clinton." + outputfileEnding);
		File fileClimate = new File(
				"C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/windows/"
						+ outputfileName + "Climate." + outputfileEnding);
		File fileFeminist = new File(
				"C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/windows/"
						+ outputfileName + "Feminist." + outputfileEnding);
		File fileAbortion = new File(
				"C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/windows/"
						+ outputfileName + "Abortion." + outputfileEnding);
		File fileAtheism = new File(
				"C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/windows/"
						+ outputfileName + "Atheism." + outputfileEnding);
		try (FileWriter fwClinton = new FileWriter(fileClinton);
				FileWriter fwClimate = new FileWriter(fileClimate);
				FileWriter fwFeminist = new FileWriter(fileFeminist);
				FileWriter fwAbortion = new FileWriter(fileAbortion);
				FileWriter fwAtheism = new FileWriter(fileAtheism);) {
			for (Tweet t : tweetlist) { // für jeden Tweet aus der liste
				switch (t.getTarget()) { // geprüft um welches target es sich handelt und danach wird der richtige
											// writer ausgewählt
				case "Atheism":
					writeItem(fwAtheism, t.getStance(), t.getContent(), ns);
					break;
				case "Climate Change is a Real Concern":
					writeItem(fwClimate, t.getStance(), t.getContent(), ns);
					break;
				case "Feminist Movement":
					writeItem(fwFeminist, t.getStance(), t.getContent() ,ns);
					break;
				case "Hillary Clinton":
					writeItem(fwClinton, t.getStance(), t.getContent(), ns);
					break;
				case "Legalization of Abortion":
					writeItem(fwAbortion, t.getStance(), t.getContent(), ns);
					break;
				}
			}
			fwClinton.close();
			fwClimate.close();
			fwFeminist.close();
			fwAbortion.close();
			fwAtheism.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * testet für alle seperaten Targetfiles
	 */
	public static void testSVMAll(int n) {
		testSVM("n"+n+"Abortion.train");
		testSVM("n"+n+"Atheism.train");
		testSVM("n"+n+"Climate.train");
		testSVM("n"+n+"Clinton.train");
		testSVM("n"+n+"Feminist.train");
	}

	/* 
	 * wird von tetsSVMAll() benutzt
	 * Parameter für einzelne dateien testen
	 */
	public static void testSVM(String file) {
		String command = "";
		
		String result = ""; // Für die Kommandooutputs
		int svmType = 0;
		int kernelType = 0;	
		int c = 0;
		
		try (FileWriter writer = new FileWriter(new File(
				"C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/Statistiken/AuswertungParameterTesten"+file+".txt"))) {
			while (svmType < 2) { // für svmtype 0 und 1
				while(kernelType < 4) {
					while (c <= 2000) {
						command = "cd C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/windows && svm-train.exe -s "
								+ svmType + " -c "+c+" -h 0 -t " + kernelType + " -v 10 "+file;

						// beide kommandos ausführen und in die Ergebnisdatei schreiben
						result = executeCommand(command);
						writer.write("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
						writer.write(command);
						writer.write("\n");
						int index = result.indexOf("Cross");
						if(index<0)
							index = 0;
						writer.write(result.substring(index));
						writer.write("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

						c = c +500;
						System.out.println("c geändert");
					}
					kernelType++;
					c = 0;
					System.out.println("kerneltype geändert");
				}	
				svmType++;
				kernelType = 0;
				c = 0;
				System.out.println("svmtype geändert");
				
			}
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*
	 * trainiert alle Dateien für die 5 Targets für das gegebene n
	 */
	public static void trainAll(int n, int svmType, int kernelType, int c) {

		train(n,"Abortion", svmType, kernelType, c);
		train(n,"Atheism", svmType, kernelType, c);
		train(n,"Climate", svmType, kernelType, c);
		train(n,"Clinton", svmType, kernelType, c);
		train(n,"Feminist", svmType, kernelType, c);
		
	}
	
	/*
	 * wird von trainAll benutzt
	 * trainiert und testet eine Datei 
	 */
	public static void train(int n , String target, int svmType, int kernelType, int c ) {
		String command1 = "";
		String command2 = "";

		command1 = "cd C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/windows && svm-train.exe -s "+svmType+" -c "+c+" -t "+kernelType+" n"+n+target+".train";
		command2 = "cd C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/windows && svm-predict.exe n"+n+target+".test n"+n+target+".train.model n"+n+target+".out";
		try {
			System.out.println(executeCommand(command1));
			System.out.println(executeCommand(command2));
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*
	 * 
	 * Kommandozeile nutzen: Eingabe Befehl, gibt Konsolenausgabe zurück
	 * Befehle mit && verbinden
	 * Befehl zb.: "cd C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/windows && svm-predict.exe n3.test n3.train.model n3.out"
	 */
	public static String executeCommand(String kommando) throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder();
		builder.command("cmd.exe", "/c", kommando);

		Process process;
		process = builder.start();

		BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		String result = "";
		while (true) {
			line = r.readLine();
			if (line == null) {
				break;
			}
			result = result + line + "\n";
		}
		return result;
	}
	
	

	
	// Hilfsfunktionen:
	
	/*
	 * wird von createMapping() und createInputfile() und createTargetSeperatedInputfiles() benutzt
	 * Liest die CSV Datei ein und füllt die tweetlist mit den einzelnen Tweets 
	 * gibt true zurück, wenn erfolgreich
	 */
	public static boolean readTweets(String filename) {
		String rawTweet = "";
		// int nummer = 0; // für testzwecke
		try ( // Trainingsdaten einlesen
				BufferedReader reader = new BufferedReader(new FileReader(
						"C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/stancedataset/StanceDataset/"
								+ filename));) {
			while (reader.ready()) {
				rawTweet = reader.readLine(); // Datei zeilenweise auslesen, eine Zeile = ein Tweet mit allen Metadaten
				// System.out.println("Tweet "+nummer+": "+tweet); // für testzwecke
				// ++nummer;
				Tweet tweet = stringToTweet(rawTweet); // die Zeile in ein Tweetobjekt umwandeln
				tweetlist.add(tweet);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Fehler datei nicht gefunden");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("Fehler beim einlesen der dATEI");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Fehler beim konvertieren von String zu Tweet");
		}
		tweetlist.remove(0); // entfernt den ersten tweet da die erste zeile der Datei keinen tweet enthält
		return checkTweetlist(tweetlist);
	}
		
	/*
	 * wird von readTweets() benutzt
	 * Prüft, ob alle Tweets in der Tweetliste vollständig sind
	 */
	public static boolean checkTweetlist(ArrayList<Tweet> tweetlist) {
		for (int i = 0; i < tweetlist.size(); i++) {
			Tweet t = tweetlist.get(i);
			if (t.getContent().isEmpty() || t.getTarget().isEmpty() || t.getStance().isEmpty()
					|| t.getOpinion().isEmpty() || t.getSentiment().isEmpty()) {
				// falls mind. ein Attribut nicht gefüllt ist
				System.out.println("mindestens ein Tweet hat nicht alle Werte \nTweet Nr: " + i);
				return false;
			}
		}
		// falls alle Tweets den Test bestanden haben
		return true;
	}
	
	/* 
	 * Wird von readTweets() benutzt
	 * Wandelt die CSV datei in die einzelnen Tweets um 
	 * Aufbau CSV Datei: Tweet,Target,Stance,Opinion Towards,Sentiment Wenn
	 * Tweet oder Opinion Towards ein Komma enthält, so sind sie in "" gesetzt
	 */
	public static Tweet stringToTweet(String raw) {
		String content = new String();
		String target = new String();
		String stance = new String();
		String opinion = new String();
		String sentiment = new String();
		int pointer = 0;
		int pointer2 = 0;

		// content
		if (raw.charAt(0) == '"') {
			pointer = 1;
			while (true) { // zur Erkennung von escape ""
				pointer2 = raw.indexOf('"', pointer);
				if (raw.charAt(pointer2 + 1) == '"') { // falls der nachfolgende char auch ein " ist
					pointer = pointer2 + 2; // überspringe die beiden ""
				} else {
					break;
				}
			}
			content = raw.substring(1, pointer2); // Inhalt ohne die "" substring(startIndex, endIndex) startIndex
													// inklusiv, endIndex exklusiv
			pointer = pointer2 + 1;
			; // pointer aufs erste Komma setzen
		} else {
			pointer = raw.indexOf(','); // finde das erste Vorkommen eines Kommas
			content = raw.substring(0, pointer);
		}

		// target
		pointer2 = raw.indexOf(',', pointer + 1); // zweites Komma finden. Nach dem ersten Komma anfangen zu suchen
		target = raw.substring(pointer + 1, pointer2);

		// stance
		pointer = raw.indexOf(',', pointer2 + 1); // Such nach dem zweiten Komma und finde das dritte
		stance = raw.substring(pointer2 + 1, pointer);

		// opinion towards
		if (raw.charAt(pointer + 1) == '"') {
			pointer2 = raw.indexOf('"', pointer + 2);
			opinion = raw.substring(pointer + 2, pointer2);
			pointer2++; // pointer2 aufs Komma setzen
		} else {
			pointer2 = raw.indexOf(',', pointer + 1);
			opinion = raw.substring(pointer + 1, pointer2);
		}

		// sentiment
		sentiment = raw.substring(pointer2 + 1);

		return new Tweet(content, target, stance, opinion, sentiment);
	}
		
	/*
	 * wird von createTargetSeperatedInputfiles() benutzt
	 * macht aus den ngrammen eines tweets die entsprechende Zeile im Inputfile
	 */
	public static void writeItem(FileWriter fw, String stance, String tweetcontent, int[] ns) throws IOException {
		switch (stance) {
		case "FAVOR":
			fw.write("1 ");
			break;
		case "AGAINST":
			fw.write("2 ");
			break;
		case "NONE":
			fw.write("3 ");
			break;
		}
		
		ArrayList<Integer> indizes = new ArrayList<Integer>();	// sammelt die zahlenwerte aller vorkommenden ngramme (features)
		for(int i = 0; i<ns.length; i++) {	// für jedes n 
			ArrayList<String> ngrams = otto.buildNgram(tweetcontent, ns[i]);
			for(String s : ngrams) {
				int index = lookup(s);		// passende Zahl für das Ngramm holen
				if(!indizes.contains(index))		//für den fall das zwei ngramme auf die gleiche zahl gemappt wurden, wird der index nicht doppelt eingefügt
				indizes.add(index);
			}
		}
		Collections.sort(indizes);
		
		for(int i : indizes) {
			fw.write(i+":1.0 ");
		}
		fw.write(System.lineSeparator());
	}

	/*
	 * wird von createInputfile() und createTargetSeperatedInputfiles() benutzt
	 * gibt den Value für den key zurück
	 */
	public static int lookup(String key) {
    	if (map.containsKey(key)) 
        {
            return map.get(key);  
        }
    	System.out.println("Warnung, NGramm nicht im Mapping enthalten");
    	return 0;
    }

	/*
	 * für testzwecke
	 * gibt ein zweidimensionales String array auf der Konsole aus
	 */
	public static void printArray(String[][] array) {
		for(int i = 0; i < array.length;i++) {
			 System.out.println(); 
			 System.out.print("Zeile " + i + ": ");
			 for(int j = 0; j<array[i].length ; j++) {
				 System.out.print(array[i][j]); System.out.print("  "); 
			} 
		} 
	}

	/*
	 * für testzwecke
	 * gibt den längsten und den kürzesten tweet aus dem file auf der konsole aus 
	 */
	public static void printMinMaxTweets(String file) {
		int max = 0;
		int min = 10000;
		System.out.println("Tweets einlesen: "+readTweets(file));
		for(Tweet t : tweetlist) {
			ArrayList<String> list = otto.findWordsString(t.getContent());
			int count = list.size();
			if(count > max)
				max = count;
			else if(count < min)
				min = count;
		}
		System.out.println("längster Tweet: "+max+" kürzester Tweet: "+min);
	}
}
