import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Evaluation {
	
	/*
	 * für ein gegebenes n die Statistiken berechnen
	 * dazu gehört:
	 * F-Score für jedes Target
	 * F-microT = Durchschnitt über alle Werte der Targets zusammen
	 * F-macroT = Durchschnitt der F-Werte der einzelnen Targets
	 * Benötigt die Dateien <nTarget>.test <nTarget>.out für jedes Thema. Diese können vorher mit createTargetSeperatedInputFiles erzeugt und dann trainiert werden
	 */
	public void evaluate(int n) {
		double fmicroT = 0;
		double fmacroT = 0;
		double fscoreAbortion = 0;
		double fscoreAtheism = 0;
		double fscoreClimate = 0;
		double fscoreFeminist = 0;
		double fscoreClinton = 0;
		
		// zur Berechnung von F-macroT
		// erstelle die Statistiken für jedes target und hole den f wert 
		try {
			fscoreAbortion = createFscoreForTarget("n"+n+"Abortion.test", "n"+n+"Abortion.out", "n"+n+"AbortionStatistics.txt");
			fscoreAtheism = createFscoreForTarget("n"+n+"Atheism.test", "n"+n+"Atheism.out", "n"+n+"AtheismStatistics.txt");
			fscoreClimate = createFscoreForTarget("n"+n+"Climate.test", "n"+n+"Climate.out", "n"+n+"ClimateStatistics.txt");
			fscoreFeminist = createFscoreForTarget("n"+n+"Feminist.test", "n"+n+"Feminist.out", "n"+n+"FeministStatistics.txt");
			fscoreClinton = createFscoreForTarget("n"+n+"Clinton.test", "n"+n+"Clinton.out", "n"+n+"ClintonStatistics.txt");	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fmacroT = (fscoreAbortion+fscoreAtheism+fscoreClimate+fscoreClinton+fscoreFeminist)/5;
		
		//f-microT berechnen:
		// eine große Testdatei erstellen
		try {		
		concatFiles("n"+n+"Abortion.test", "n"+n+"Atheism.test","n"+n+ "AA.test", n);
		concatFiles("n"+n+"Climate.test", "n"+n+"Clinton.test","n"+n+ "CC.test", n);
		concatFiles("n"+n+"AA.test", "n"+n+"CC.test","n"+n+ "AC.test", n);
		concatFiles("n"+n+"AC.test", "n"+n+"Feminist.test","n"+n+ "FmicroT.test", n);
		
		concatFiles("n"+n+"Abortion.out", "n"+n+"Atheism.out","n"+n+ "AA.out", n);
		concatFiles("n"+n+"Climate.out", "n"+n+"Clinton.out","n"+n+ "CC.out", n);
		concatFiles("n"+n+"AA.out", "n"+n+"CC.out","n"+n+ "AC.out", n);
		concatFiles("n"+n+"AC.out", "n"+n+"Feminist.out","n"+n+ "FmicroT.out", n);
			
			fmicroT = createFscoreForTarget("n"+n+ "FmicroT.test", "n"+n+ "FmicroT.out", "n"+n+"FmicroTStatistics.txt");
			
			
			// tempfiles wieder löschen
			
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		try(FileWriter fw = new FileWriter(new File("C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/Statistiken/n"+n+"Statistics.txt"))){
			DecimalFormat df = new DecimalFormat("###.###");
			fw.write("Statistiken für N="+n+
					"\nF-microT: "+ df.format(fmicroT)+
					"\nF-macroT: "+df.format(fmacroT)+
					"\nF-Score Abortion: "+df.format(fscoreAbortion)+
					"\nF-Score Atheism: "+df.format(fscoreAtheism)+
					"\nF-Score Climate: "+df.format(fscoreClimate)+
					"\nF-Score Feminist: "+df.format(fscoreFeminist)+
					"\nF-Score Clinton: "+df.format(fscoreClinton));
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		try {
			concatFilesStatistiken(new String[] {"n"+n+"Statistics.txt","n"+n+"FmicroTStatistics.txt","n"+n+"AbortionStatistics.txt","n"+n+"AtheismStatistics.txt","n"+n+"ClimateStatistics.txt","n"+n+"FeministStatistics.txt","n"+n+"ClintonStatistics.txt"}, "FinaleN"+n+"Statistiken.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*
	 * Wird von evaluate() benutzt
	 * berechnet die Statistiken und den F-Wert für ein Target (eine KOmbination aus Test und Out file)
	 * übergeben werden muss der name mit Dateiendung des .test files, der name mit Dateiendung des out. files und der Name mit Dateiendung der Evaluierungsdatei, die erstellt werden soll
	 * erstellt wird dann eine Evaluierungsdatei mit den Statistiken
	 * F-score berechnen, dieser wird auch zurückgegeben
	 * FAVOR = 1
	 * AGAINST = 2
	 * NONE = 3
	 */
	public double createFscoreForTarget(String testfile, String outputfile, String evaluationfile)throws IOException {
		String lineTest = "";
		String lineOut = "";
		double fscore = 0;
				
		double richtigKlassifiziert = 0;
		double gesamtanzahl = 0;
		
		double korrektAlsFAVORklassifiziert = 0;	
		double alsFAVORklassifiziertGesamt = 0;	// anzahl der als FAVOR klassifizierten Items
		double FAVORitems = 0;						// anzahl aller Items die FAVOR sind
		
		double korrektAlsAGAINSTklassifiziert = 0;
		double alsAGAINSTklassifiziertGesamt = 0;	// anzahl der als AGAINST klassifizierten Items
		double AGAINSTitems = 0;					// anzahl aller items die AGAINST sind
		
		try(BufferedReader readerTest = new BufferedReader(new FileReader("C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/windows/"+testfile));
				BufferedReader readerOut = new BufferedReader(new FileReader("C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/windows/"+outputfile));
				FileWriter fw = new FileWriter(new File("C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/Statistiken/"+evaluationfile))){ 
			while(readerTest.ready() && readerOut.ready()) {		//Werte vergleichen und zählen
				lineTest = readerTest.readLine();
				lineOut = readerOut.readLine();
				gesamtanzahl++;
				
				if(lineTest.charAt(0) == '1')
					FAVORitems++;
				if(lineTest.charAt(0) == '2')
					AGAINSTitems++;
				if(lineOut.charAt(0) == '1')
					alsFAVORklassifiziertGesamt++;
				if(lineOut.charAt(0) == '2')
					alsAGAINSTklassifiziertGesamt++;
				
				if(lineTest.charAt(0)==lineOut.charAt(0)) {
					if(lineTest.charAt(0) == '1') {
						korrektAlsFAVORklassifiziert++;
						richtigKlassifiziert++;
					}
					if(lineTest.charAt(0) == '2') {
						korrektAlsAGAINSTklassifiziert++;
						richtigKlassifiziert++;
					}	
					if(lineTest.charAt(0) == '3') {
						richtigKlassifiziert++;
					}
				}	
			}
			
			
		// Werte berechnen:
		double precisionFAVOR = 0;	
		if(alsFAVORklassifiziertGesamt != 0) 
			precisionFAVOR = korrektAlsFAVORklassifiziert / alsFAVORklassifiziertGesamt;
		
		double recallFAVOR = 0;
		if(FAVORitems != 0)
				recallFAVOR = korrektAlsFAVORklassifiziert  / FAVORitems;
		
		double FscoreFAVOR = 0;
		if((precisionFAVOR+recallFAVOR) != 0)
			FscoreFAVOR = 2*((precisionFAVOR*recallFAVOR)/(precisionFAVOR+recallFAVOR));
		
		
		double precisionAGAINST = 0;
		if(alsAGAINSTklassifiziertGesamt != 0)
			precisionAGAINST = korrektAlsAGAINSTklassifiziert / alsAGAINSTklassifiziertGesamt;
		
		double recallAGAINST = 0;
		if(AGAINSTitems != 0)
			recallAGAINST = korrektAlsAGAINSTklassifiziert / AGAINSTitems;
		
		double FscoreAGAINST = 0;
		if((precisionAGAINST+recallAGAINST) != 0)
			FscoreAGAINST = 2*((precisionAGAINST*recallAGAINST)/(precisionAGAINST+recallAGAINST));	
		
		//the overall F-Score:
		fscore = (FscoreFAVOR+FscoreAGAINST)/2;
		
		DecimalFormat df = new DecimalFormat("###.###");
		fw.write("Statistik für "+outputfile+
				"\nAnzahl aller Tweets: "+(int)gesamtanzahl+
				"\ndavon korrekt klassifiziert: "+(int)richtigKlassifiziert+
				"\nAnzahl FAVOR Items: "+(int)FAVORitems+
				"\ndavon korrekt als FAVOR klassifiziert: "+(int)korrektAlsFAVORklassifiziert+ 
				"\ninsgesamt als FAVOR klassifiziert: "+(int)alsFAVORklassifiziertGesamt+
				"\nAnzahl AGAINST Items: "+(int)AGAINSTitems+
				"\ndavon korrekt als AGAINST klassifiziert: "+(int)korrektAlsAGAINSTklassifiziert+ 
				"\ninsgesamt als AGAINST klassifiziert: "+(int)alsAGAINSTklassifiziertGesamt+
				"\nF-Score: "+df.format(fscore));	
				
		readerTest.close();
		readerOut.close();
		fw.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
		return fscore;
	}
	
	/*
	 * Wird von evaluate() benutzt
	 * Für StatistikenPfad 
	 * mit Leerzeilen für die Lesbarkeit
	 * liste muss mind. 2 files enthalten
	 */
	public void concatFilesStatistiken(String[] files, String resultFile) throws IOException {
		FileWriter writer = new FileWriter(new File("C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/Statistiken/"+resultFile));
		
		for(int i = 1; i<files.length; i++) {
			String file1 = files [i-1];
			String file2 = files [i];
		
		
		BufferedReader reader1 = new BufferedReader(new FileReader("C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/Statistiken/"+file1));
		BufferedReader reader2 = new BufferedReader(new FileReader("C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/Statistiken/"+file2));
		String line = "";
		while(reader1.ready()) {
			line = reader1.readLine();
			writer.write(line);
			writer.write("\n");
		}
		writer.write("\n");
		while(reader2.ready()) {
			line = reader2.readLine();
			writer.write(line);
			writer.write("\n");
		}
		
		writer.write("\n\n");
		reader1.close();
		reader2.close();
		files [i] = resultFile;
		}
		
		writer.close();
	}
	
	/*
	 * Wird von evaluate() benutzt
	 * Konkateniert alle Dateien aus files, an dem angegebenen Pfad pathFiles
	 * Ergebnisdatei landet in C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/Statistiken/
	 */
	public void concatFiles(String[] files, String pathFiles,  String resultFile) throws IOException {
		FileWriter writer = new FileWriter(new File("C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/windows/"+resultFile));
		System.out.println("writer erzeugt");
		for(int i = 1; i<files.length; i++) {
			String file1 = files [i-1];
			String file2 = files [i];
			System.out.println("i="+i);
		
		BufferedReader reader1 = new BufferedReader(new FileReader("C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/windows/"+file1));
		BufferedReader reader2 = new BufferedReader(new FileReader("C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/windows/"+file2));
		String line = "";
		System.out.println("reader erzeugt");
		while(reader1.ready()) {
			line = reader1.readLine();
			writer.write(line);
			writer.write("\n");
			System.out.println("line geschrieben für i= "+i+"line: "+line);
		}
		System.out.println("erste while schleife: file 1 wurde geschrieben");
		while(reader2.ready()) {
			line = reader2.readLine();
			writer.write(line);
			writer.write("\n");
		}
		System.out.println("zweite wihlie schleife : file 2 wurde geschireben");
		reader1.close();
		reader2.close();
		files [i] = resultFile;
		
		}
		
		writer.close();
	}
	
	public void concatFiles(String file1, String file2, String resultFile, int n) throws IOException {
		FileWriter writer = new FileWriter(new File("C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/windows/"+resultFile));
		BufferedReader reader1 = new BufferedReader(new FileReader("C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/windows/"+file1));
		BufferedReader reader2 = new BufferedReader(new FileReader("C:/Users/Marleen/Documents/Semester7/Seminar/Implementierung/libsvm-3.22/libsvm-3.22/windows/"+file2));
		String line = "";
		while(reader1.ready()) {
			line = reader1.readLine();
			writer.write(line);
			writer.write("\n");
		}
		while(reader2.ready()) {
			line = reader2.readLine();
			writer.write(line);
			writer.write("\n");
		}
		
		writer.close();
		reader1.close();
		reader2.close();
	}
}
