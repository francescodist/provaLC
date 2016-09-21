package rubrica;

import java.util.*;
import java.sql.*;

//CLASSE CON MAIN E LOGICA DEL PROGRAMMA
public class Driver {

	public static void main(String[] args) {
		
		try{
			//STABILISCO CONNESSIONE AL SERVER
			Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/rubrica", "root", "1234");
			Statement stmt = conn.createStatement();
			ResultSet rs;
			Anagrafica anagrafica;
			Telefono telefono;
			List<Anagrafica> rubrica = new ArrayList<Anagrafica>();			
			int scelta;
			while(true){
				System.out.println("RUBRICA\n\n"
						+ "1 - Nuovo Contatto\n" 
						+ "2 - Modifica Contatto\n"
						+ "3 - Ricerca Contatto\n"
						+ "4 - Stampa Rubrica\n"
						+ "Altra Scelta - ESCI\n\n"
						+ "SCELTA: ");
				Scanner scan = new Scanner(System.in);
				try{
					scelta = scan.nextInt();
				}
				catch(Exception ex){
					scelta = 0;
				}
				scan.nextLine();
				switch(scelta){
				case 1: System.out.println("\n\nAGGIUNGI NUOVO CONTATTO\n\n");
						anagrafica = new Anagrafica();
						anagrafica.inserisciDB(stmt);
						break;
				case 2: System.out.println("\n\nSCEGLIERE CONTATTO DA MODIFICARE\n\n");
						rs = getAnagraficaFromDB(stmt);
						
						
						//STAMPO TUTTI I RECORD NEL RESULTSET E CREO UNA LISTA PER LA SCELTA DELL'UTENTE
						for(int i = 1; rs.next(); i++){
							anagrafica = new Anagrafica(rs.getLong("ID_ANAGRAFICA"),
									rs.getString("NOME"),
									rs.getString("COGNOME"),
									rs.getString("CITTA"),
									rs.getString("INDIRIZZO"));
							System.out.println(i+".\n");
							anagrafica.stampaSoloInfo();
							rubrica.add(anagrafica);
						}
						System.out.println("SCELTA: ");
						try{
							anagrafica = rubrica.get(scan.nextInt() - 1);
						}
						catch(Exception ex){
							System.out.println("Scelta non consentita!");
							scan.nextLine();
							break;
						}
						scan.nextLine();
						rs = getAnagraficaFromDB(stmt, anagrafica.getID());
						
						if(!(rs.next())) {
							System.out.println("ID non presente nell'anagrafica!\n");
							break;
						}
						
						
						//CREO UN OGGETTO ANAGRAFICA A PARTIRE DALLA SCELTA DELL'UTENTE
						anagrafica = new Anagrafica(
								rs.getLong("ID_ANAGRAFICA"),
								rs.getString("NOME"),
								rs.getString("COGNOME"),
								rs.getString("CITTA"),
								rs.getString("INDIRIZZO"));
						
						
						do{
							anagrafica.aggiungiTelefono(new Telefono(
									rs.getLong("ID_NUMERO"),
									rs.getString("NUMERO"),
									rs.getString("TIPO")));
						}while(rs.next());
						
						System.out.println("\nSCEGLIERE UN'OPZIONE\n");
						System.out.println("1 - Modifica Contatto");
						System.out.println("2 - Elimina Contatto");
						System.out.println("Altro - ESCI");
						try{
							scelta = scan.nextInt();
						}
						catch(Exception ex){
							scelta = 0;
						}
						scan.nextLine();
						switch(scelta) {
						case 1: System.out.println("SCEGLIERE UN'OPZIONE\n\n"
								+ "1 - Modifica Dati Contatto\n"
								+ "2 - Modifica Numero Esistente\n"
								+ "3 - Aggiungi Nuovo Numero al Contatto\n"
								+ "4 - Elimina Numero dal Contatto\n"
								+ "Altro - ESCI");
								try{
									scelta = scan.nextInt();
								}
								catch(Exception ex){
									scelta = 0;
								}
								scan.nextLine();
								switch(scelta) {
								case 1: anagrafica.aggiorna();
										anagrafica.updateDB(stmt);
										break;
								case 2: System.out.println("SCEGLI IL NUMERO DA MODIFICARE");
										anagrafica.stampaElencoTelefoni();
										System.out.println("\nSCELTA: ");
										try{
											telefono = anagrafica.getTelefono(scan.nextInt() - 1);
										}
										catch(Exception ex){
											System.out.println("Scelta non consentita!");
											scan.nextLine();
											break;
										}
										scan.nextLine();
										telefono.aggiorna();
										telefono.updateDB(stmt);
										
										System.out.println("Premere INVIO per continuare...");
										scan.nextLine();
										break;
								case 3: System.out.println("\n\nINSERIMENTO NUMERO TELEFONICO\n\n");
										telefono = new Telefono();
										telefono.inserisciDB(stmt);
										
										break;
								case 4:	System.out.println("SCEGLI NUMERO DA ELIMINARE");
										
										anagrafica.stampaElencoTelefoni();
										System.out.println("\nSCELTA: ");
										try{
											anagrafica.rimuoviTelefono(scan.nextInt() - 1, stmt);
										}
										catch(Exception ex){
											System.out.println("Scelta non consentita!");
										}
										scan.nextLine();
										System.out.println("Premi INVIO per continuare...");
										scan.nextLine();
										break;
										
								default:break;
								}
								break;
						case 2: anagrafica.rimuovi(stmt);
								System.out.println("Premi INVIO per continuare...");
								scan.nextLine();
								break;
						default:break;
						}
						break;
						
				case 3: rubrica = cercaInRubrica(stmt);
						
						System.out.println("RISULTATO RICERCA:\n");
						for(Anagrafica a : rubrica){
							a.stampa();
						}
						System.out.println("Premi INVIO per continuare...");
						scan.nextLine();
						rubrica.clear();
						break;
				case 4: rs = getRubricaFromDB(stmt);
						rubrica = creaRubrica(rs);
						Collections.sort(rubrica);
						for(Anagrafica a : rubrica){
							a.stampa();
						}
						rubrica.clear();
						break;
				default: System.exit(0);
				}
				
				
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	//CARICA TUTTA LA TABELLA ANAGRAFICA DAL DB
	public static ResultSet getAnagraficaFromDB(Statement stmt){
		try{
			return stmt.executeQuery("SELECT * FROM anagrafica");
		}
		catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	
	//CARICA LA TABELLA ANAGRAFICA RELATIVA AD UN UNICO ID CON RELATIVI NUMERI DI TELEFONO
	public static ResultSet getAnagraficaFromDB(Statement stmt, long id){
		try{
			return stmt.executeQuery("SELECT anagrafica.ID_ANAGRAFICA, NOME,COGNOME,CITTA,INDIRIZZO,ID_NUMERO,NUMERO,TIPO "
					+ "FROM anagrafica "
					+ "join numeri "
					+ "ON anagrafica.ID_ANAGRAFICA = numeri.ID_ANAGRAFICA "
					+ "WHERE anagrafica.ID_ANAGRAFICA = " + id);
		}
		catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	//OTTIENE NUMERO VIA ID DAL DB
	public static ResultSet getNumeroFromDB(Statement stmt, long id){
		try{
			return stmt.executeQuery("SELECT ID_NUMERO, NUMERO, TIPO "
					+ "FROM numeri "
					+ "WHERE ID_ANAGRAFICA = " + id);
		}
		catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	//OTTIENE TUTTI I CONTATTI CON RELATIVI NUMERI
	public static ResultSet getRubricaFromDB(Statement stmt){
		try{
			return stmt.executeQuery("SELECT anagrafica.ID_ANAGRAFICA, NOME,COGNOME,CITTA,INDIRIZZO,ID_NUMERO,NUMERO,TIPO "
					+ "FROM anagrafica "
					+ "join numeri "
					+ "ON anagrafica.ID_ANAGRAFICA = numeri.ID_ANAGRAFICA ");
		}
		catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	//RICERCA SUL DB A PARTIRE DA UNA STRINGA IN RELAZIONE A NOME+COGNOME O COGNOME+NOME
	public static ResultSet ricercaFromDB(Statement stmt, String ricerca){
		try{
			return stmt.executeQuery("SELECT * "
					+ "FROM (SELECT anagrafica.ID_ANAGRAFICA, NOME, COGNOME, CITTA, INDIRIZZO, ID_NUMERO, NUMERO, TIPO "
							+ "FROM anagrafica "
							+ "JOIN numeri "
							+ "ON anagrafica.ID_ANAGRAFICA = numeri.ID_ANAGRAFICA) AS rubrica "
					+ "WHERE (CONCAT(NOME,' ',COGNOME) LIKE '%"+ ricerca +"%') "
					+ "OR (CONCAT(COGNOME, ' ',NOME) LIKE '%"+ ricerca +"%')");
		}
		catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	//RICHIEDE E FORMATTA CORRETTAMENTE LA STRINGA PER POI CHIAMARE LA QUERY PER LA RICERCA SU DB
	public static List<Anagrafica> cercaInRubrica(Statement stmt){
		String ricerca = "";
		ResultSet rs;
		
		Scanner scan = new Scanner(System.in);
		System.out.println("Inserire stringa da ricercare:");
		ricerca = scan.nextLine();
		//TRIM E RIMOZIONE SPAZI SUPERFLUI
		ricerca = ricerca.trim().replaceAll(" +", " ");
		rs = ricercaFromDB(stmt, ricerca);
		return creaRubrica(rs);
	}
	
	//RITORNA UNA LISTA DI CONTATTI CON RELATIVI NUMERI
	public static List<Anagrafica> creaRubrica(ResultSet rs){
		List<Anagrafica> rubrica = new ArrayList<Anagrafica>();
		Anagrafica anagrafica;
		Telefono telefono;
		try{
			
			while(rs.next()){
					anagrafica = new Anagrafica(
							rs.getLong("ID_ANAGRAFICA"),
							rs.getString("NOME"),
							rs.getString("COGNOME"),
							rs.getString("CITTA"),
							rs.getString("INDIRIZZO"));
					do{
						telefono = new Telefono(
								rs.getLong("ID_NUMERO"),
								rs.getString("NUMERO"),
								rs.getString("TIPO"));
								anagrafica.aggiungiTelefono(telefono);
					}while(rs.next() && rs.getLong("ID_ANAGRAFICA") == anagrafica.getID());
					rubrica.add(anagrafica);
					rs.previous();
			}
			return rubrica;
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	
}
