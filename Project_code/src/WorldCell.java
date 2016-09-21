import fr.emse.simulator.world.Cell;
import fr.emse.simulator.world.Occupant;

/** @author Charlotte Duruisseau, Dylan Feith */

public class WorldCell implements Cell {
	
	private int nbrow;
	private int nbcol;
	private Occupant occupant;
	
	// mettre un constructeur + modifier parse
	
	/** 
	 * Constructeur
	 */	
	WorldCell(int i, int j){
		this.nbrow=i;
		this.nbcol=j;		
	}

	public int getCol() {
		return nbcol;
	}
	
	public int getRow() {
		return nbrow;
	}

	public Occupant getOccupant() {
		return occupant;
	}
	
	public void setOccupant (Occupant element){
		occupant=element;
	}
	
	public boolean isEmpty() {
		if (occupant == null)
			return true;
		
		else 
			return false;
	}

}
