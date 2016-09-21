import fr.emse.simulator.world.Coin;

/** @author Charlotte Duruisseau, Dylan Feith */

//mettre un constructeur + modifier parse

public class Money extends Entity implements Coin {
	/**
	 * constructeur
	 */
	Money(WorldCell cellule){
		this.cell=cellule;
	}

}
