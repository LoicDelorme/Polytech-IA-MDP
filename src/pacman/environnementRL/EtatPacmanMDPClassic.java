package pacman.environnementRL;

import java.util.ArrayList;
import java.util.List;

import environnement.Etat;
import pacman.elements.MazePacman;
import pacman.elements.StateAgentPacman;
import pacman.elements.StateGamePacman;

/**
 * Classe pour définir un etat du MDP pour l'environnement pacman avec QLearning tabulaire.
 */
public class EtatPacmanMDPClassic implements Etat {

	/**
	 * Liste de tous les pacmans.
	 */
	private List<Position2D> pacmans;

	/**
	 * Liste de tous les fantomes.
	 */
	private List<Position2D> ghosts;

	/**
	 * Liste de toutes les pièces.
	 */
	private List<Position2D> dots;

	public EtatPacmanMDPClassic(StateGamePacman stateGamePacman) {
		this.pacmans = new ArrayList<Position2D>();
		this.ghosts = new ArrayList<Position2D>();
		this.dots = new ArrayList<Position2D>();

		fillPacmansList(stateGamePacman);
		fillGhostsList(stateGamePacman);
		fillDotsList(stateGamePacman);
	}

	private void fillPacmansList(StateGamePacman stateGamePacman) {
		StateAgentPacman agentState = null;
		for (int offset = 0; offset < stateGamePacman.getNumberOfPacmans(); offset++) {
			agentState = stateGamePacman.getPacmanState(offset);
			this.pacmans.add(new Position2D(agentState.getX(), agentState.getY()));
		}
	}

	private void fillGhostsList(StateGamePacman stateGamePacman) {
		StateAgentPacman agentState = null;
		for (int offset = 0; offset < stateGamePacman.getNumberOfGhosts(); offset++) {
			agentState = stateGamePacman.getGhostState(offset);
			this.ghosts.add(new Position2D(agentState.getX(), agentState.getY()));
		}
	}

	private void fillDotsList(StateGamePacman stateGamePacman) {
		final MazePacman maze = stateGamePacman.getMaze();
		for (int x = 0; x < maze.getSizeX(); x++) {
			for (int y = 0; y < maze.getSizeY(); y++) {
				if (maze.isFood(x, y)) {
					this.dots.add(new Position2D(x, y));
				}
			}
		}
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException cnse) {
			cnse.printStackTrace(System.err);
			return null;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.dots == null) ? 0 : this.dots.hashCode());
		result = prime * result + ((this.ghosts == null) ? 0 : this.ghosts.hashCode());
		result = prime * result + ((this.pacmans == null) ? 0 : this.pacmans.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		EtatPacmanMDPClassic other = (EtatPacmanMDPClassic) obj;
		if (this.dots == null) {
			if (other.dots != null)
				return false;
		} else if (!this.dots.equals(other.dots))
			return false;
		if (this.ghosts == null) {
			if (other.ghosts != null)
				return false;
		} else if (!this.ghosts.equals(other.ghosts))
			return false;
		if (this.pacmans == null) {
			if (other.pacmans != null)
				return false;
		} else if (!this.pacmans.equals(other.pacmans))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "";
	}
}