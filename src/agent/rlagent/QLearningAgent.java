package agent.rlagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import environnement.Action;
import environnement.Environnement;
import environnement.Etat;

/**
 * Renvoi 0 pour valeurs initiales de Q.
 * 
 * @author laetitiamatignon
 */
public class QLearningAgent extends RLAgent {

	/**
	 * Format de memorisation des Q valeurs : utiliser partout setQValeur car cette methode notifie la vue.
	 */
	protected HashMap<Etat, HashMap<Action, Double>> qvalues;

	/**
	 * Créé un QLearning agent.
	 * 
	 * @param alpha
	 *            valeur de alpha.
	 * @param gamma
	 *            valeur de gamma.
	 * @param environment
	 *            l'environnement actuel.
	 */
	public QLearningAgent(double alpha, double gamma, Environnement environment) {
		super(alpha, gamma, environment);
		this.qvalues = new HashMap<Etat, HashMap<Action, Double>>();
	}

	/**
	 * renvoi la (les) action(s) de plus forte(s) valeur(s) dans l'etat e (plusieurs actions sont renvoyees si valeurs identiques) renvoi liste vide si aucunes actions possibles dans l'etat (par ex. etat absorbant)
	 */
	@Override
	public List<Action> getPolitique(Etat state) {
		final List<Action> actions = new ArrayList<Action>();

		double maxValue = Double.NEGATIVE_INFINITY;
		for (Action action : this.getActionsLegales(state)) {
			final double vkResult = getQValeur(state, action);
			if (vkResult > maxValue) {
				maxValue = vkResult;

				actions.clear();
				actions.add(action);
			} else if (vkResult == maxValue) {
				actions.add(action);
			}
		}

		return actions;
	}

	@Override
	public double getValeur(Etat state) {
		return this.getActionsLegales(state).stream().mapToDouble(action -> getQValeur(state, action)).max().orElse(0);
	}

	@Override
	public double getQValeur(Etat state, Action action) {
		if (this.qvalues.get(state) == null || this.qvalues.get(state).get(action) == null) {
			return 0;
		}

		return this.qvalues.get(state).get(action);
	}

	@Override
	public void setQValeur(Etat state, Action action, double value) {
		if (this.qvalues.get(state) == null) {
			this.qvalues.put(state, new HashMap<Action, Double>());
		}

		this.qvalues.get(state).put(action, value);

		if (value < this.vmin) {
			this.vmin = value;
		}

		if (value > this.vmax) {
			this.vmax = value;
		}

		this.notifyObs();
	}

	/**
	 * mise a jour du couple etat-valeur (e,a) apres chaque interaction <etat e,action a, etatsuivant esuivant, recompense reward> la mise a jour s'effectue lorsque l'agent est notifie par l'environnement apres avoir realise une action.
	 */
	@Override
	public void endStep(Etat state, Action action, Etat nextState, double reward) {
		if (RLAgent.DISPRL) {
			System.out.println("QL mise a jour etat " + state + " action " + action + " etat' " + nextState + " r " + reward);
		}

		double computedValue = (1 - this.alpha) * getQValeur(state, action) + this.alpha * (reward + this.gamma * getValeur(nextState));
		setQValeur(state, action, computedValue);
	}

	@Override
	public Action getAction(Etat state) {
		this.actionChoisie = this.stratExplorationCourante.getAction(state);
		return this.actionChoisie;
	}

	@Override
	public void reset() {
		super.reset();
		this.qvalues.clear();

		this.episodeNb = 0;
		this.notifyObs();
	}
}