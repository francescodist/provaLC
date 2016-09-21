package rubrica;

import java.util.*;
import java.sql.*;
//CLASSE E METODI PER I NUMERI DI TELEFONO
public class Telefono {
	private long id;
	private String numero;
	private String tipo;
	
	//COSTRUTTORE DA RIGA DI COMANDO
	public Telefono(){
		Scanner scan = new Scanner(System.in);
		System.out.println("Numero: ");
		numero = scan.nextLine();
		System.out.println("Tipo: ");
		tipo = scan.nextLine();
	}
	
	//COSTRUTTORE CON PARAMETRI (DA DB)
	public Telefono(long id, String numero, String tipo){
		this.id = id;
		this.numero = numero;
		this.tipo = tipo;
	}
	
	//STAMPA IL NUMERO SU UNA RIGA
	public void stampa() {
		System.out.println(numero +"\t"+ tipo);

	}
	
	//MODIFICA VECCHIO OGGETTO TELEFONO PRIMA DI UPDATE SU DB
	public void aggiorna(){
		String numero, tipo;
		Scanner scan = new Scanner(System.in);
		System.out.println("Premere solo INVIO per lasciare un campo invariato");
		System.out.println("Vecchio Numero: " + this.numero);
		System.out.println("Nuovo Numero: ");
		numero = scan.nextLine();
		if(!(numero.equals(""))) {
			this.numero = numero;
		}
		
		System.out.println("Vecchio Tipo: " + this.tipo);
		System.out.println("Nuovo Tipo: ");
		tipo = scan.nextLine();
		if(!(tipo.equals(""))) {
			this.tipo = tipo;
		}
	}
	
	//INSERIMENTO NUOVO NUMERO DB
	public void inserisciDB(Statement stmt){
		try{
			stmt.executeUpdate("INSERT INTO numeri (ID_ANAGRAFICA, NUMERO, TIPO) "
					+"VALUES('"+id+"','"+numero+"','"+tipo+"');");
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	//MODIFICA NUMERO PRESENTE SU DB
	public void updateDB(Statement stmt){
		try{
			if(stmt.executeUpdate("UPDATE numeri"
					+ " SET NUMERO='"+ numero +"', TIPO='"+tipo
					+ "' WHERE ID_NUMERO = " + id) == 1){
				System.out.println("Numero modificato con successo!");
			}
			else {
				System.out.println("Impossibile modificare numero!");
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	//METODI GET E SET NECESSARI
	public void setID(long id){
		this.id = id;
	}
	
	public long getID(){
		return id;
	}
	
	public String getNumero(){
		return numero;
	}
	
	public String getTipo(){
		return tipo;
	}
}
