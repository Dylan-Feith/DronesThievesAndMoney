import java.util.ArrayList;
import java.util.Collections;

import fr.emse.simulator.astar.AStarPathFinder;
import fr.emse.simulator.astar.EuclideanDistanceHeuristic;
import fr.emse.simulator.astar.PreferEmptyCellsLocalCost;
import fr.emse.simulator.world.Cell;
import fr.emse.simulator.world.Occupant;
import fr.emse.simulator.world.Robber;

/** @author Charlotte Duruisseau, Dylan Feith */

//mettre un constructeur + modifier parse

public class Intruder extends MobileEntity implements Robber {
	

	private int stolenMoney;
	
	// nextTurn indique le nombre de tour qu'il faut encore attendre avant que l'intruder ne joue à nouveau.
	// si nextTurn==0, il peut jouer
	// si nextTurn==1, il ne pourra pas jouer au prochain tour mais au suivant
	private int nextTurn;
	
	 /**
	  *  Constructeur
	  */
	Intruder(int identifiant, WorldCell cellule){
		id=identifiant;
		cell=cellule;
		stolenMoney=0;
		nextTurn=0;
	}
	
	public int getnextTurn(){
		return this.nextTurn;
	}
	public void setnextTurn(int val){
		nextTurn=val;
	}

	/**
	 * @param s : le simulateur
	 * @return la liste des cases à suivre pour atteindre son but
	 * @see MobileEntity#decide(Simulateur)
	 */
	public ArrayList<Cell> decide(Simulateur s) {
		
		// création de toIgnore
		ArrayList<Class<? extends Occupant>> toIgnore = new ArrayList<Class<? extends Occupant>>();
		toIgnore.add(Drone.class);
		toIgnore.add(Intruder.class);
				
		AStarPathFinder solver = new AStarPathFinder(
			s.getWorld(),
			new EuclideanDistanceHeuristic(),
			new PreferEmptyCellsLocalCost(1, 3),
			toIgnore);
				
		ArrayList<Cell> minpath = new ArrayList<Cell>();
		
		// cas 1 : l'intruder recherche de l'argent
		if (s.getWorld().getSizeMoneyList() > 0 && this.stolenMoney<2) {	
			
			// on veut initialiser minpath de manière non nul 
			//On essaye avec le premier élément de la liste money
			minpath =  solver.getShortestPath(this.cell, s.getWorld().getMoney(0).cell);
			
			// Si celui ci est nul, on cherche le premier chemin non nul vers une source d'argent
			int i=1;
			while(minpath == null && i<s.getWorld().getSizeMoneyList()){
				minpath =  solver.getShortestPath(this.cell, s.getWorld().getMoney(i).cell);
				i++;
			}
			
			// si ils sont tous nul on renvoit null 
			if (minpath==null)
				return null;
			
			// sinon on s'assure qu'il n'y a pas de chemin plus court en parcourant les derniers éléments de la liste
			ArrayList<Cell> newpath = new ArrayList<Cell>();
			for(int k=i; k<s.getWorld().getSizeMoneyList(); k++){
				newpath =  solver.getShortestPath(this.cell, s.getWorld().getMoney(k).cell);
									
				// on cherche le plus court qui n'est pas nul
				if (newpath!=null)
					if (newpath.size() < minpath.size())
						minpath=newpath;	
			}
		}
		
		
		//cas 2 : l'intruder a soit son quotat d'or 
		//		  soit il n'y a plus d'argent sur la carte 
		// 		  Il cherche alors une sortie
		
		else {
			
			// on veut initialiser minpath de manière non nul 
			//On essaye avec le premier élément de la liste porteDeSortie
			minpath =  solver.getShortestPath(this.cell, s.getWorld().porteDeSortie.get(0));
			
			// Si celui ci est nul, on cherche le premier chemin non nul vers une sortie
			int i=1;
			while(minpath == null && i<s.getWorld().porteDeSortie.size()){
				minpath =  solver.getShortestPath(this.cell, s.getWorld().porteDeSortie.get(i));
				i++;
			}
			
			// si ils sont tous nul on renvoit null 
			if (minpath==null)
				return null;
			
			// sinon on s'assure qu'il n'y a pas de chemin plus court en parcourant les derniers éléments de la liste
			ArrayList<Cell> newpath = new ArrayList<Cell>();			
			for(int k=i; k<s.getWorld().porteDeSortie.size(); k++){
				newpath =  solver.getShortestPath(this.cell, s.getWorld().porteDeSortie.get(k));
									
				// on cherche le plus court qui n'est pas nul
				if (newpath!=null)
					if (newpath.size() < minpath.size())
						minpath=newpath;
					
			}						

		}
		// Dans tous les cas, on retourne le chemin le plus court			
		return minpath;
				
	}		
		

	/**
	 * @param s : le simulateur
	 * @see MobileEntity#act(Simulateur)
	 * vole de l'argent dans une case adjacente
	 */
	public void act(Simulateur s) {
		
		// On vérifie qu'il n'est pas plein car sinon il est possible que pendant qu'il se dirige vers une sortie, il trouve de l'argent
		if (this.stolenMoney != 2) {
			World w=s.getWorld();
		
			int row= this.cell.getRow();
			int col= this.cell.getCol();
		
			// On stocke dans voisins la liste des cellules des voisins de l'intru
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
		
			// On regarde si il y a de l'argent parmi les cellules voisines et on agit  
			// On cherche l'incide i qui correspond à la position du premier paquet d'argent dans la liste des voisins
			int i=0;
			while (i<voisins.size() && !(voisins.get(i).getOccupant() instanceof Money)){
				i++;
			}
			
			// si i = voisins.size(), c'est qu'il n'y avait pas d'argent dans la liste voisins.
			if (i < voisins.size()) {
					w.removeMoney((Money) voisins.get(i).getOccupant());
					voisins.get(i).setOccupant(null);
					this.stolenMoney++;
			}

		}
		// Si on est dans act, c'est que on est dans un tour ou il peut jouer (ie nextTurn==0)
		// on incrémente donc nextTurn a sa bonne valeur
		this.nextTurn=this.stolenMoney;
	}
	
}
