package ruleset.eval;

import player.Evaluator;

// TODO escribir specs

public class EvaluatorFactory {

	public EvaluatorFactory() {}
	
	public Evaluator createEvaluator(int type) {
		if (type == 1)
			return new Evaluator1();
		else if (type == 2)
			return new Evaluator2();
		else 
			return null;
	}
	
}
