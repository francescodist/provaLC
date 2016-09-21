package rubrica;

import java.util.*;
import java.sql.*;

//CLASSE E METODI PER I CONTATTI DELLA RUBRICA
public class Anagrafica implements Comparable<Anagrafica>{
	private long id;
	private String nome;
	private String cognome;
	private String citta;
	private String indirizzo;
	private List<Telefono> telefoni = new ArrayList<Telefono>();
	
	//COSTRUTTORE DA RIGA DI COMANDO
	public Anagrafica() {
		Scanner scan = new Scanner(System.in);
		System.out.println("Nome: ");
		nome = scan.nextLine();
		System.out.println("Cognome: ");
		cognome = scan.nextLine();
		System.out.println("Città: ");
		citta = scan.nextLine();
		System.out.println("Indirizzo: ");
		indirizzo = scan.nextLine();
		//L'UTENTE PUO' SCEGLIERE SE INSERIRE UNO O PIU' NUMERI ALLA CREAZIONE DEL CONTATTO
		boolean inserireNumero = true;
		int scelta;
		System.out.println("\n\nINSERIMENTO NUMERI TELEFONICI\n\n");
		while(inserireNumero) {
			telefoni.add(new Telefono());
			System.out.println("Inserire ulteriori numeri? 1.Si Altro.No\n"
					+ "SCELTA: ");
			try{
				scelta = scan.nextInt();
			}
			catch(Exception ex){
				scelta = 0;
			}
			scan.nextLine();
			if(scelta != 1) {
				inserireNumero = false;
			}
		}
	}
	
	//COSTRUTTORE CON PARAMETRI (DA DB)
	public Anagrafica(long id, String nome, String cognome, String citta, String indirizzo) {
		this.id = id;
		this.nome = nome;
		this.cognome = cognome;
		this.citta = citta;
		this.indirizzo = indirizzo;
	}
	
	//COMPARATOR PER ANAGRAFICA
	public int compareTo(Anagrafica a) {
        return (nome+cognome).compareTo(a.nome + a.cognome);
    }
	
	//STAMPA LE INFO DEL CONTATTO SENZA I NUMERI
	public void stampaSoloInfo() {
		
		System.out.println(nome+" "+cognome+"");
		System.out.println(citta+" - "+indirizzo+"\n");
	}
	
	//STAMPA SOLO I NUMERI DEL CONTATTO
	public void stampaElencoTelefoni() {
		int i= 0;
		for(Telefono telefono : telefoni) {
			System.out.print(++i + ". ");
			telefono.stampa();
		}
	}
	
	//STAMPA TUTTE LE INFO DEL CONTATTO
	public void stampa(){
		stampaSoloInfo();
		stampaElencoTelefoni();
		System.out.println("\n\n\n");
	}
	
	//AGGIUNGE UN NUMERO DI TELEFONO ALLA LISTA DEL CONTATTO
	public void aggiungiTelefono(Telefono telefono) {
		telefoni.add(telefono);
	}
	
	//MODIFICA LE INFO DEL CONTATTO PRIMA DI UPDATE SU DB
	public void aggiorna(){
		String nome, cognome, citta, indirizzo;
		Scanner scan = new Scanner(System.in);
		System.out.println("Premere solo INVIO per lasciare un campo invariato");
		System.out.println("Vecchio Nome: " + this.nome);
		System.out.println("Nuovo Nome: ");
		nome = scan.nextLine();
		if(!(nome.equals(""))) {
			this.nome = nome;
		}
		
		System.out.println("Vecchio Cognome: " + this.cognome);
		System.out.println("Nuovo Cognome: ");
		cognome = scan.nextLine();
		if(!(cognome.equals(""))) {
			this.cognome = cognome;
		}
		
		
		System.out.println("Vecchia Città: " + this.citta);
		System.out.println("Nuova Città: ");
		citta = scan.nextLine();
		if(!(citta.equals(""))) {
			this.citta = citta;
		}
		
		
		System.out.println("Vecchio Indirizzo: " + this.indirizzo);
		System.out.println("Nuovo Indirizzo: ");
		indirizzo = scan.nextLine();
		if(!(indirizzo.equals(""))) {
			this.indirizzo = indirizzo;
		}
		
	}
	
	//RIMUOVE UN CONTATTO (E TUTTI I NUMERI CORRISPONDENTI) DAL DB
	public void rimuovi(Statement stmt){
		try{
			if(stmt.executeUpdate("DELETE FROM anagrafica "
					+ "WHERE ID_ANAGRAFICA = " + id) >= 1 
					&&
					stmt.executeUpdate("DELETE FROM numeri "
					+ "WHERE ID_ANAGRAFICA = " + id) >= 1) {
						System.out.println("Contatto eliminato correttamente!");
					}
					else{
						System.out.println("Eliminazione contatto non riuscita!");
					}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	//RIMUOVE UN NUMERO DI TELEFONO DALLA LISTA DEL CONTATTO (E DAL DB)
	public void rimuoviTelefono(int i, Statement stmt){
		try{
			Telefono telefono = telefoni.get(i);
		
		
			try{
				if(stmt.executeUpdate("DELETE FROM numeri "
						+ "WHERE ID_ANAGRAFICA = " + id
						+ " AND ID_NUMERO = " + telefono.getID()) >= 1){
					telefoni.remove(i);
					//SE IL CONTATTO NON HA PIU' NUMERI VIENE ELIMINATO DALLA RUBRICA
					if(telefoni.size() == 0) {
						stmt.executeUpdate("DELETE FROM anagrafica "
								+ "WHERE ID_ANAGRAFICA = " + id);
					}
					System.out.println("Numero eliminato correttamente!");
				}
				else {
					System.out.println("Impossibile eliminare numero selezionato!");
				}
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		catch(Exception ex){
			System.out.println("Scelta non consentita!");
			return;
		}
	}
	
	//INSERISCE UN NUOVO CONTATTO (CON ALMENO UN NUMERO) NEL DB
	public void inserisciDB(Statement stmt){
		ResultSet rs;
		try{
			stmt.executeUpdate("INSERT INTO anagrafica (NOME, COGNOME, CITTA, INDIRIZZO)"
				+"VALUES('"+nome+"','"+cognome+"','"+citta+"','"+indirizzo+"');", Statement.RETURN_GENERATED_KEYS);
			rs = stmt.getGeneratedKeys();
			rs.next();
			//ID CONTATTO NECESSARIO PER INSERIMENTO NUMERI NEL DB
			id = rs.getLong(1);
			for(Telefono telefono : telefoni){
				stmt.executeUpdate("INSERT INTO numeri (ID_ANAGRAFICA, NUMERO, TIPO)"
						+"VALUES('"+id+"','"+telefono.getNumero()+"','"+telefono.getTipo()+"');", Statement.RETURN_GENERATED_KEYS);
				rs = stmt.getGeneratedKeys();
				rs.next();
				//ID TELEFONO NECESSARIO PER FUTURE OPERAZIONI SUI NUMERI DI TELEFONO
				telefono.setID(rs.getLong(1));
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	//AGGIORNA CONTATTO SU DB
	public void updateDB(Statement stmt){
		try{
			stmt.executeUpdate("UPDATE anagrafica "
					+ "SET NOME = '"+ nome +"', COGNOME = '" + cognome + "', CITTA = '"+ citta +"', INDIRIZZO = '"+ indirizzo
					+ "' WHERE ID_ANAGRAFICA = " + id);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	//METODI GET NECESSARI
	public long getID(){
		return id;
	}
	
	public String getNome(){
		return nome;
	}
	
	public String getCognome(){
		return cognome;
	}
	
	public String getCitta(){
		return citta;
	}
	
	public String getIndirizzo(){
		return indirizzo;
	}
	
	public Telefono getTelefono(int i){
		return telefoni.get(i);
	}
	
	
}
