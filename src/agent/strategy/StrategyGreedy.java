package agent.strategy;

import java.util.List;
import java.util.Random;

import agent.rlagent.RLAgent;
import environnement.Action;
import environnement.Etat;

/**
 * Strategie qui renvoit un choix aleatoire avec proba epsilon, un choix glouton (suit la politique de l'agent) sinon
 * 
 * @author lmatignon
 */
public class StrategyGreedy extends StrategyExploration {

	/**
	 * parametre pour probabilite d'exploration
	 */
	protected double epsilon;

	private final Random rand = new Random();

	public StrategyGreedy(RLAgent agent, double epsilon) {
		super(agent);
		this.epsilon = epsilon;
	}

	@Override
	public Action getAction(Etat state) {
		final List<Action> legalActions = this.agent.getActionsLegales(state);
		if (legalActions.isEmpty()) {
			return null;
		}

		if (this.epsilon > this.rand.nextDouble()) {
			return legalActions.get(this.rand.nextInt(legalActions.size()));
		}

		final List<Action> politicActions = this.agent.getPolitique(state);
		return politicActions.get(this.rand.nextInt(politicActions.size()));
	}

	public double getEpsilon() {
		return this.epsilon;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
		System.out.println("epsilon:" + epsilon);
	}
}