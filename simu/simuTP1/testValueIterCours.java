package simuTP1;

import javax.swing.SwingUtilities;

import vueGridworld.VueGridworldValue;
import agent.ValueAgent;
import agent.planningagent.ValueIterationAgent;
import environnement.gridworld.GridworldEnvironnement;
import environnement.gridworld.GridworldMDP;

public class testValueIterCours {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		  SwingUtilities.invokeLater(new Runnable(){
				public void run(){
		
					String[][] grid = {{" ","#"},
							{"S","-1"},
							{"1"," "}};
				//	GridworldMDP gmdp = new GridworldMDP(grid);
					
					
					GridworldMDP gmdp = GridworldMDP.getBookGrid();
					GridworldEnvironnement.setDISP(true);//affichage transitions
					
					GridworldEnvironnement g = new GridworldEnvironnement(gmdp);
					
					ValueIterationAgent a = new ValueIterationAgent(gmdp);	
					ValueAgent.DISPEPISODE = true;
					VueGridworldValue vue = new VueGridworldValue(g,a);
					
									
					vue.setVisible(true);
				}
			});

	}
}
