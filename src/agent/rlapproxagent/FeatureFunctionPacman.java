package agent.rlapproxagent;

import pacman.elements.ActionPacman;
import pacman.elements.StateAgentPacman;
import pacman.elements.StateGamePacman;
import pacman.environnementRL.EnvironnementPacmanMDPClassic;
import environnement.Action;
import environnement.Etat;
/**
 * Vecteur de fonctions caracteristiques pour jeu de pacman: 4 fonctions phi_i(s,a)
 * <li> phi_0 est le biais
 * <li> phi_1 est  nb de fantomes adjacent à s', a un pas de pacman resulting position (nb de fantomes qui peuvent atteindre en un pas s')
 * <li> phi_2 est s'il y a un food/dot dans pacman resulting position et pas de risque d'etre atteint par fantome (phi_2 = 1 si (food et phi_1==0))
 * # if there is no danger of ghosts then add the food feature
 * <li> phi_3 est distance au plus proche food/dot depuis pacman resulting position
 *  # dist/mapsize : make the distance a number less than one otherwise the update will diverge wildly
 *  
 * @author laetitiamatignon
 *
 */
public class FeatureFunctionPacman implements FeatureFunction{
	private double[] vfeatures ;
	
	private static int NBACTIONS = 4;//5 avec NONE possible pour pacman, 4 sinon 
	//--> doit etre coherent avec EnvironnementPacmanRL::getActionsPossibles


	public FeatureFunctionPacman() {

	}

	@Override
	public int getFeatureNb() {
		return 4;
	}

	@Override
	public double[] getFeatures(Etat e, Action a) {
		vfeatures = new double[4];
		StateGamePacman stategamepacman ;
		//EnvironnementPacmanMDPClassic envipacmanmdp = (EnvironnementPacmanMDPClassic) e;

		//calcule pacman resulting position a partir de Etat e
		if (e instanceof StateGamePacman){
			stategamepacman = (StateGamePacman)e;
		}
		else{
			System.out.println("erreur dans FeatureFunctionPacman::getFeatures n'est pas un StateGamePacman");
			return vfeatures;
		}
		//si fait ca ici, simu la modif du maze donc on pert l'info que pacman se retrouve sur dot (closestDot=0) si action le met sur dot !
		//StateGamePacman pacmangame_next = //stategamepacman.nextStatePacman(new ActionPacman(a.ordinal()));
		//StateAgentPacman pacmanstate_next = pacmangame_next.getPacmanState(0);

		StateAgentPacman pacmanstate_next= stategamepacman.movePacmanSimu(0, new ActionPacman(a.ordinal()));
		 
		//phi_0: biais
		vfeatures[0] = 1.0;

		// phi_1 est  nb de fantomes adjacent à s'+nb de fantome sur s' !
		// a un pas de pacman resulting position (nb de fantomes qui peuvent atteindre en un pas s')
		//--> si se retrouve sur ghost, renvoie 2 !
		vfeatures[1] = 0.0;
		int[] positionvoisine;
		for (int i=0; i<NBACTIONS; i++){//calcule position voisine de resulting position, en simulant les 4 actions depuis resulting position
			positionvoisine = stategamepacman.getNextPosition(new ActionPacman(i), pacmanstate_next);
			if (stategamepacman.isGhost(positionvoisine[0], positionvoisine[1])){
				vfeatures[1] ++;
			}
		}
		//if (vfeatures[1]==2)
		//	vfeatures[1]=1;
		if (stategamepacman.isGhost(pacmanstate_next.getX(), pacmanstate_next.getY())){
			vfeatures[1] ++;
		}
		
		
		//phi_2 est s'il y a un food/dot dans pacman resulting position et pas de risque d'etre atteint par fantome dans pacman resulting position
		if (vfeatures[1] == 0.0 && stategamepacman.getMaze().isFood(pacmanstate_next.getX(), pacmanstate_next.getY()))
		{
			vfeatures[2] = 1.0;
		}
		else {
			vfeatures[2] = 0.0;
		}
		
		// phi_3 est distance au plus proche food/dot depuis pacman resulting position
		//  # dist/mapsize : make the distance a number less than one otherwise the update will diverge wildly
		int dist = stategamepacman.getClosestDot(pacmanstate_next);
	
		
		vfeatures[3] = dist / (double)((stategamepacman.getMaze().getSizeX()*stategamepacman.getMaze().getSizeY()));
		
		//pas indispensable
		for (int i=0;i<vfeatures.length;i++)
			vfeatures[i] =vfeatures[i]/10; 
		
		/*System.out.print("featurevector: state :"+e+" action = "+a+" f = {");
		for (int i=0; i<vfeatures.length; i++)
			System.out.print(vfeatures[i]+" ");
		System.out.print("} dist "+dist+" size "+stategamepacman.getMaze().getSizeX());
		System.out.println(" ");*/
		
		return vfeatures;
	}

	public void reset() {
		vfeatures = new double[4];
		
	}

}
