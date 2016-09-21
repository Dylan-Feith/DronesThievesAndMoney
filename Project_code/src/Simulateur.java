import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import fr.emse.simulator.Simulator;
import fr.emse.simulator.gui.MapFrame;

/** @author Charlotte Duruisseau, Dylan Feith */
 
 

public class Simulateur extends Simulator {

	private World world;
	private ArrayList<Intruder> arrested;
	private ArrayList<Intruder> escaped;
	private MapFrame frame;
	
	/**
	 *  Constructeur 
	 *  @ param file : le fichier texte à lire
	 *  
	 */
	Simulateur(File file)  {
		super(file);
		frame = new MapFrame(world);
		arrested = new ArrayList<Intruder>();
		escaped = new ArrayList<Intruder>();
	}

	public boolean isOver() {
		// S'il n'y a plus d'intruder sur la carte on s'arrete.
		if (world.getSizeIntruderList() == 0)
			return true;
		
		return false;
	}

	public void loadMap(File arg0) {

		world = new World(arg0.getName());
	}
	
	public World getWorld() {
		return this.world;
	}
	
	public void setArrested(Intruder i){
		this.arrested.add(i);
	}
	
	public void setEscaped(Intruder i){
		this.escaped.add(i);
	}

	/**
	 * joue un tour
	 */
	public void runOneStep() {

		// On place notre liste de MobileEntity dans une variable locale TheList car on y a acces uniquement en ecriture
		ArrayList<MobileEntity> theList = this.world.getMobileEntityList();
		
		// selection aleatoire de l'ordre de jeu
		Collections.shuffle(theList);
		
		for (MobileEntity e : theList) {

			// si l'entite n'est ni arrete ni echappe
			if (!arrested.contains(e) && !escaped.contains(e)) {
				
				// c'est un intru, on verifie que nextTurn est nul pour jouer
				if (!(e instanceof Intruder && ((Intruder) e).getnextTurn()>0)){
					e.decideMoveAct(this);
				}
				
				// sinon on décrémente nextTurn
				else {
					((Intruder) e).setnextTurn(((Intruder) e).getnextTurn()-1);
				}
					
			}
			
			frame.repaint(100);
		}
	}
	
	

	public static void main(String[] args) {
		

		Simulateur simulation = new Simulateur(new File("test.txt"));
		simulation.run();
		System.out.println("Le jeu s'est bien terminé");
		
	}

}
