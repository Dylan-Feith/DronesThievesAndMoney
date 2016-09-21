import java.util.ArrayList;

import fr.emse.simulator.world.Cell;

/** @author Charlotte Duruisseau, Dylan Feith */

public abstract class MobileEntity extends Entity {
	protected int id;
	
	
	public abstract ArrayList<Cell> decide(Simulateur s);
	public abstract void act(Simulateur s);
	
	/**
	 * Permet de se déplacer
	 * @param s : le simulateur
	 * @param passage : le trajet calculé par decide(s)
	 * @return true si la mobileEntity peut agir apres avoir bouge et false sinon
	 * Le seul cas où il ne peut agir apres avoir bouge est lorsqu'un intruder vient de s'echappe
	 */	
	public boolean move(Simulateur s,ArrayList<Cell> passage) {
		if (  passage==null ){
			return true;
		}

		else {
			
			int row = passage.get(1).getRow();
			int col = passage.get(1).getCol();
			
			WorldCell destination = s.getWorld().get(row,col);
			
			// si la prochaine case a aller est une porte de Sortie, l'intruder s'echappe et ne pourra donc pas agir 
			// Remarque : un drone ne peut jamais avoir comme destination une porte de sortie 
			if (s.getWorld().porteDeSortie.contains(destination)) {
				s.setEscaped((Intruder)this);
				this.cell.setOccupant(null);
				this.cell=null;
				s.getWorld().removeIntruder((Intruder)this);
				return false;
			}
			else {
				
				// Si la destination n'est pas vide, on se déplace
				if (destination.isEmpty()){
					this.cell.setOccupant(null);
					destination.setOccupant(this);
					this.cell= destination;
					return true;
				}
				else return true;
				
			}
			
		}
	}
	
	/**
	 * Permet de choisir la prochaine cellule
	 * Permet de s'y déplacer
	 * Permet d'agir si possible
	 * 
	 * @param s : le simulateur
	 */
	
	public void decideMoveAct(Simulateur s) {
		
		ArrayList<Cell> passage =new ArrayList<Cell>();	
		passage = this.decide(s);
		boolean bool = this.move(s,passage);
		
		if(bool){
			this.act(s);
		}
		
	}

}
