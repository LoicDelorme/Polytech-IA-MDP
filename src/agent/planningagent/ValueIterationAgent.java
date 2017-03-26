package agent.planningagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import environnement.Action;
import environnement.Action2D;
import environnement.Etat;
import environnement.MDP;

/**
 * Cet agent met a jour sa fonction de valeur avec value iteration et choisit ses actions selon la politique calculee.
 *
 * @author DELORME Loïc & PIAT Grégoire
 * @since 1.0.0
 */
public class ValueIterationAgent extends PlanningValueAgent {

	/**
	 * Discount facteur.
	 */
	protected double gamma;

	/**
	 * Fonction de valeur des etats.
	 */
	protected final HashMap<Etat, Double> V;

	/**
	 * Créer un agent met a jour sa fonction de valeur avec value iteration et choisit ses actions selon la politique calculee.
	 * 
	 * @param gamma
	 *            La valeur gamma.
	 * @param mdp
	 *            Le MDP.
	 */
	public ValueIterationAgent(double gamma, MDP mdp) {
		super(mdp);

		this.gamma = gamma;
		this.V = new HashMap<Etat, Double>();

		this.mdp.getEtatsAccessibles().stream().forEach(e -> this.V.put(e, 0.0));
		this.notifyObs();
	}

	/**
	 * Créer un agent met a jour sa fonction de valeur avec value iteration et choisit ses actions selon la politique calculee.
	 * 
	 * @param mdp
	 *            Le MDP.
	 */
	public ValueIterationAgent(MDP mdp) {
		this(0.9, mdp);
	}

	/**
	 * Mise a jour de V: effectue UNE iteration de value iteration (calcule V_k(s) en fonction de V_{k-1}(s')) et notifie ses observateurs. Ce n'est pas la version inplace (qui utilise nouvelle valeur de V pour mettre a jour ...)
	 */
	@Override
	public void updateV() {
		// delta est utilise pour detecter la convergence de l'algorithme
		// lorsque l'on planifie jusqu'a convergence, on arrete les iterations lorsque
		// delta < epsilon
		this.delta = 0.0;

		for (Etat currentState : this.mdp.getEtatsAccessibles()) {
			if (!this.mdp.estAbsorbant(currentState)) {
				double maxValue = Double.NEGATIVE_INFINITY;
				for (Action currentAction : this.mdp.getActionsPossibles(currentState)) {
					final double vkResult = vk(currentState, currentAction);
					maxValue = vkResult > maxValue ? vkResult : maxValue;
				}

				this.delta = Math.max(this.delta, Math.abs(this.V.get(currentState) - maxValue));
				this.V.put(currentState, maxValue);
			}
		}

		this.notifyObs();
	}

	/**
	 * Compute the Vk(s) value.
	 * 
	 * @param state
	 *            The current state.
	 * @param action
	 *            The current action.
	 * @return The Vk(s) value.
	 */
	private double vk(Etat state, Action action) {
		double res = 0.0;
		try {
			final Map<Etat, Double> probas = this.mdp.getEtatTransitionProba(state, action);
			for (Map.Entry<Etat, Double> pairs : probas.entrySet()) {
				res += pairs.getValue() * (this.mdp.getRecompense(state, action, pairs.getKey()) + this.gamma * this.V.get(pairs.getKey()).doubleValue());
			}
		} catch (Exception e) {
			// Nothing.
		}

		return res;
	}

	/**
	 * renvoi l'action executee par l'agent dans l'etat e Si aucune actions possibles, renvoi Action2D.NONE
	 */
	@Override
	public Action getAction(Etat e) {
		final List<Action> actions = this.getPolitique(e);
		return actions.isEmpty() ? Action2D.NONE : actions.get(this.rand.nextInt(actions.size()));
	}

	@Override
	public double getValeur(Etat e) {
		return this.V.get(e);
	}

	/**
	 * renvoi la (les) action(s) de plus forte(s) valeur(s) dans etat (plusieurs actions sont renvoyees si valeurs identiques, liste vide si aucune action n'est possible)
	 */
	@Override
	public List<Action> getPolitique(Etat e) {
		final List<Action> actions = new ArrayList<Action>();

		double maxValue = Double.NEGATIVE_INFINITY;
		for (Action currentAction : this.mdp.getActionsPossibles(e)) {
			final double vkResult = vk(e, currentAction);
			if (vkResult > maxValue) {
				maxValue = vkResult;

				actions.clear();
				actions.add(currentAction);
			} else if (vkResult == maxValue) {
				actions.add(currentAction);
			}
		}

		return actions;
	}

	@Override
	public void reset() {
		super.reset();

		this.V.clear();
		this.mdp.getEtatsAccessibles().stream().forEach(e -> this.V.put(e, 0.0));
		this.notifyObs();
	}

	/**
	 * Obtenir la fonction de valeur des états.
	 * 
	 * @return La fonction de valeur des états.
	 */
	public HashMap<Etat, Double> getV() {
		return this.V;
	}

	/**
	 * Obtenir la valeur gamma.
	 * 
	 * @return La valeur du gamma.
	 */
	public double getGamma() {
		return this.gamma;
	}

	@Override
	public void setGamma(double g) {
		this.gamma = g;
		System.out.println("new gamma value: " + this.gamma);
	}
}