import fr.emse.simulator.world.SimulationMap;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/** @author Charlotte Duruisseau, Dylan Feith */

public class World implements SimulationMap {

	private int nbrowWorld;
	private int nbcolWorld;
	private WorldCell[][] tabcells; 						// le tableau de cellules
	private ArrayList<MobileEntity> mobileEntitylist; 		// comprend tous les drones et les intrus melanges pris dans l'ordre initial de la carte
	private ArrayList<Intruder> intruderList;				// comprend tous les intrus
	private ArrayList<Money> moneyList;						// comprend tous les pieces
	public ArrayList<WorldCell> porteDeSortie;				// comprend toutes les sorties possibles

	/** 
	 * Constructeur 
	 * lit egalement le fichier texte
	 */
	World(String file) {

		mobileEntitylist = new ArrayList<MobileEntity>();
		intruderList = new ArrayList<Intruder>();
		moneyList = new ArrayList<Money>();
		porteDeSortie = new ArrayList<WorldCell>();
		
		int nbTotalMobileEntity = 1;
		Scanner fichier = new Scanner("");
		Scanner line1 = new Scanner("");
		Scanner lineFichier = new Scanner("");
		
		try {
			fichier = new Scanner(new File(file));
			int i = 0;
			int j = 0;

			if (fichier.hasNextLine()) {
				// line1 est fermé dans le finally.
				line1 = new Scanner(fichier.nextLine());
				this.nbrowWorld = Integer.parseInt(line1.next());
				this.nbcolWorld = Integer.parseInt(line1.next());
			}
			
			// on cree le tableau de cellules
			this.tabcells = new WorldCell[this.nbrowWorld][this.nbcolWorld];
			
			// on l'initialise
			for (int k = 0; k < nbrowWorld; k++)
				for (int l = 0; l < nbcolWorld; l++)
					tabcells[k][l] = new WorldCell(k, l);
			
			while (fichier.hasNextLine() && i<nbrowWorld) {
					
				lineFichier = new Scanner(fichier.nextLine());
				j = 0;

				while (lineFichier.hasNext() && j<nbcolWorld) {
					String symbol = lineFichier.next();
					
					// En fonction du symbole rencontre, on cree les entites correspondantes et on les integre dans le monde.
					switch (symbol.charAt(0)) {
					
					case '#':
						tabcells[i][j].setOccupant(new Barrier());
						break;

					case 'I':
						Intruder intruder = new Intruder(nbTotalMobileEntity,
								tabcells[i][j]);
						tabcells[i][j].setOccupant(intruder);
						mobileEntitylist.add(intruder);
						intruderList.add(intruder);
						nbTotalMobileEntity++;
						break;

					case 'D':
						Drone drone = new Drone(nbTotalMobileEntity, tabcells[i][j]);
						tabcells[i][j].setOccupant(drone);
						mobileEntitylist.add(drone);
						nbTotalMobileEntity++;
						break;

					case '_':
						tabcells[i][j].setOccupant(null);
						
						// si la cellule est sur le bord, c'est une porte de sortie
						if (i==0 || i==nbrowWorld-1 || j==0 || j == nbcolWorld -1)
							porteDeSortie.add(tabcells[i][j]);	
						break;

					case '$':
						Money gold = new Money(tabcells[i][j]);
						tabcells[i][j].setOccupant(gold);
						moneyList.add(gold);
						break;
						
					}
					j++;
				}
				i++;
			}
			// si le monde n'a pas le meme nombre de lignes et de colonnes que ceux indiques au debut du fichier, on lance une exception
			if (i!=nbrowWorld || j!= nbcolWorld || fichier.hasNextLine() || lineFichier.hasNext() ){
				throw new CreationWorldException();
			}

		}
		
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//On gere le cas ou les indications sur la taille du monde ne sont pas valides
		catch (NumberFormatException e){
			System.out.println("veuillez saisir des entiers dans le fichier texte pour la taille du monde.");
			System.exit(0);
		}
	
		catch (CreationWorldException e){
			System.exit(0);
		}
		// finalement et dans tous les cas, on ferme le flux avec le fichier texte
		finally {
			line1.close();
			lineFichier.close();
			fichier.close();
		}
	}
	
	//concerne le monde
	public int getNbCols() {
		return this.nbcolWorld;
	}
	public int getNbRows() {
		return this.nbrowWorld;
	}
	
	//concerne les cellules
	public WorldCell get(int arg0, int arg1) {
		return this.tabcells[arg0][arg1];
	}
	
	//concerne la liste de MobileEntity
	public ArrayList<MobileEntity> getMobileEntityList() {
		return this.mobileEntitylist;
	}
	
	//concerne la liste d'Intruder
	public int getSizeIntruderList(){
		return this.intruderList.size();
	}
	public Intruder getIntruder(int i){
		return this.intruderList.get(i);
	}
	public void removeIntruder(Intruder i){
		this.intruderList.remove(i);
	}
	
	//concerne la liste de Money
	public int getSizeMoneyList(){
		return this.moneyList.size();
	}
	public Money getMoney(int i) {
		return this.moneyList.get(i);
	}
	public void removeMoney(Money m){
		this.moneyList.remove(m);
	}
	
	

}

	