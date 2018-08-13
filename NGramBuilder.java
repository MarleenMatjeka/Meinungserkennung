import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;

/*
 *  K�mmert sich um die Zerst�ckelung der Tweets in die entsprechenden N-Gramme
 */
public class NGramBuilder {
	
	/*
	 * erzeugt aus einem gegebenen String und einem gegebenen n, 
	 * eine ArrayListe mit den ngrammen
	 */
	public ArrayList<String> buildNgram(String tweetcontent, int n){
		ArrayList<String> liste = new ArrayList<>();
		String[][] ngrams = buildNGramString(tweetcontent, n);		// benutzt die funktionierende methode
		for (int i = 0; i < ngrams.length; i++) {
			String value = "";
				for (int j = 0; j < ngrams[i].length; j++) { // f�r die i-te zeile jede zelle (spalte) anschauen
					value = value + ngrams[i][j]; 
					value = value +"_";								
				}
			liste.add(value);
		}
		return liste;
	}
	
	
	/*
	 * Wird von buildNgram() benutzt 
	 * Erzeugt aus dem �bergebenen String und n NGramme und speichert diese in einem zweidimensionalen StringArray
	 * (�berbleibsel aus �lterer Programmierung bevor das Mapping der NGramme korrigiert wurde)
	 */
	public String[][] buildNGramString(String tweetcontent, int n){
		if(n<1||n>200) {		// Fehlerfall
			System.out.println("Fehler: N darf zwischen 1 und 200 liegen. Es wird ein leeres Array zur�ckgegeben");
			return new String[0][0]; 
		}
		
		int nGramCounter = 0;
		
		tweetcontent = removePunctuation(tweetcontent);		// alle satzzeichen entfernen
		ArrayList<String> words = findWordsString(tweetcontent);	// Tweet wird in seine einzelnen W�rter aufgeteilt
		words = removeHashtags(words);
		int ngrammAnzahl = 1;								// falls der Inhalt weniger worte enth�lt als n lang ist
		String nGramList[][];
		if(words.size()>n) {
			ngrammAnzahl = words.size()-n+1;				// Anzahl der NGramme die aus dem Tweet gebildet werden k�nnen
			nGramList = new String [ngrammAnzahl][n];		// Tabelle in richtiger Gr��e erstellen
		}else {
			nGramList = new String [ngrammAnzahl][words.size()];
		}
		
		
		
		if(ngrammAnzahl==1) {								// falls inhalt kleiner als n, gibt es nur ein ngramm
			for(int i = 0; i< words.size(); i++) {
				nGramList[0][i] = words.get(i);
			}
		}else {
			while(nGramCounter<ngrammAnzahl) {					// f�r jedes zu erstellende NGramm	
				for(int i = 0; i < n; i++) {					// von 0 bis n f�r die Spalten der Matrix
					String word = words.get(nGramCounter+i);		// passendes wort finden
					nGramList[nGramCounter][i] = word;			// wort an die passende stelle eintragen
				}
				nGramCounter++;									// nach erstellung des nGramms den counter erh�hen
			}
		}
		return nGramList;
	}
		
	/* wird von buildNGramString() benutzt
	 * Entfernt alle Satzzeichen aus dem Tweet (au�er #, @ und ')
	 */
	public String removePunctuation(String tweetcontent){
		try {
		tweetcontent = tweetcontent.replaceAll("[\\p{P}&&[^#]&&[^@]&&[^']]", "");}
		catch(PatternSyntaxException e) {
			e.printStackTrace();
			System.out.println("regular expression syntax invalid");
		}
		return tweetcontent;
	}
		
	/* wird von buildNGramString() benutzt
	 *  macht aus einem Satz eine Liste der einzelnen W�rter
	 *  W�rter werden klein geschrieben
	 *  achtet auch auf mehrfach aufeinander folgende Leerzeichen 
	 */
	public ArrayList<String> findWordsString(String line) {
		ArrayList<String> words = new ArrayList<String>();
		String word = "";
		for(int i = 0; i < line.length(); i++) {
			if(line.charAt(i) != ' ') {						// falls der aktuelle char kein leerzeichen ist
				word = word.concat(""+line.charAt(i)); 		// wird er zum wort hinzugef�gt	
				if(i == line.length()-1)					// falls es das letzte zeichen ist
					words.add(word);
			}else if(!word.equals("")){						// Leerzeichen gefunden & wort ist nicht leer
				word = word.toLowerCase();					// nur klein geschriebene W�rter
				words.add(word);							// wort ins array schreiben
				word = "";									// Wort wieder zur�cksetzen
			}	
		}
		
		return words;
	}
		
	/* wird von buildNGramString() benutzt
	 * entfernt Hashtags und User 
	 */
	public ArrayList<String> removeHashtags(ArrayList<String> words){
		ArrayList<String> newWords = new ArrayList<>();
		for(String word : words) {
			if(!(word.startsWith("#")||word.startsWith("@"))) {
				newWords.add(word);
			}
		}

		return newWords;
	}
}
