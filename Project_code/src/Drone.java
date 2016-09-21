import java.util.ArrayList;
import java.util.Collections;

import fr.emse.simulator.astar.AStarPathFinder;
import fr.emse.simulator.astar.EuclideanDistanceHeuristic;
import fr.emse.simulator.astar.PreferEmptyCellsLocalCost;
import fr.emse.simulator.world.Cell;
import fr.emse.simulator.world.Occupant;
import fr.emse.simulator.world.Robot;
/** @author Charlotte Duruisseau, Dylan Feith */

//mettre un constructeur + modifier parse

public class Drone extends MobileEntity implements Robot  {
	
	/**
	 *  Constructeur 1 
	 */
	Drone(WorldCell cellule){
		this.cell=cellule;
	}
	
	/**
	 *  Constructeur 2
	 */
	
	Drone(int i,WorldCell cellule ){
		this.cell= cellule;
		this.id= i;
	}
	

	
	/**
	 * @param s : le simulateur
	 * @return : la liste des cases à suivre pour atteindre son but
	 * @see MobileEntity#decide(Simulateur)
	 */
			
	public ArrayList<Cell> decide(Simulateur s) {
		// Dans le cas où le dernier intruder s'est échappé ou fait attrapé et qu'il reste encore des drones à jouer,...
		//On évite ainsi des problème de nullpointer
		if (s.getWorld().getSizeIntruderList()==0){
			return null;
		}
		
		// création de toignore		
		ArrayList<Class<? extends Occupant>> toIgnore = new ArrayList<Class<? extends Occupant>>();
		toIgnore.add(Drone.class);
		
		AStarPathFinder solver = new AStarPathFinder(
				s.getWorld(),
				new EuclideanDistanceHeuristic(),
				new PreferEmptyCellsLocalCost(1, 3),
				toIgnore);
				
		
		ArrayList<Cell> minpath = new ArrayList<Cell>();
			
		// on veut initialiser minpath de manière non nul 
		//On essaye avec le premier élément de la liste money
		minpath =  solver.getShortestPath(this.cell, s.getWorld().getIntruder(0).cell);
					
		// Si celui ci est nul, on cherche le premier chemin non nul vers une source d'argent
		int i=1;
					
		while(minpath == null && i<s.getWorld().getSizeIntruderList()){
			minpath =  solver.getShortestPath(this.cell, s.getWorld().getIntruder(i).cell);
			i++;
		}
					
		// si ils sont tous nul on renvoit null 
		if (minpath==null)
			return null;
		
		// sinon on s'assure qu'il n'y a pas de chemin plus court en parcourant les derniers éléments de la liste
		ArrayList<Cell> newpath = new ArrayList<Cell>();
		for(int k=i; k<s.getWorld().getSizeIntruderList(); k++){
			newpath =  solver.getShortestPath(this.cell, s.getWorld().getIntruder(k).cell);
								
			// on cherche le plus court qui n'est pas nul
			if (newpath!=null)
				if (newpath.size() < minpath.size())
					minpath=newpath;	
		}
		return minpath;
	}
	
	/**
	 * @param s : le simulateur
	 *  attrape un drone si il y en a un dans les cases voisines
	 *  @see MobileEntity#act(Simulateur)
	 */
	public void act(Simulateur s) {
		
		World w=s.getWorld();
		
		int row = this.cell.getRow();
		int col = this.cell.getCol();
		
		// On stocke dans voisins la liste des cellules des voisins du drone
		ArrayList<WorldCell> voisins = new ArrayList<WorldCell>();
		for ( int j=col-1; j>=0 && j < w.getNbCols() && j<=col+1 ;j++) {
			for ( int i=row-1; i>=0 && i < w.getNbRows() && i<=row+1 ;i++){
				if(!(i == row && j== col)){
					voisins.add(w.get(i,j));
				}
			}
		}
		
		// On les mélange. Ainsi on ne privilégie pas de direction d'action
		Collections.shuffle(voisins);
		
		// On cherche l'incide i qui correspond à la position du premier intruder dans la liste des voisins
		int i=0;
		while (i<voisins.size() && !(voisins.get(i).getOccupant() instanceof Intruder)){
			i++;
		}
		
		// si i = voisins.size(), c'est qu'il n'y avait pas d'intruder dans la liste voisins.
		if (i < voisins.size()) {
				s.setArrested((Intruder) voisins.get(i).getOccupant());
				((Intruder) voisins.get(i).getOccupant()).cell=null;
				s.getWorld().removeIntruder((Intruder) voisins.get(i).getOccupant());
				voisins.get(i).setOccupant(null);
		}		
	}
		
}


