package static_taint_analysis;

import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.SootMethod;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;

/**
 * @author Gursimran Singh
 * @rollno 2014041
 */

public class AnalysisWrapper extends BodyTransformer {

	@Override
	protected void internalTransform(Body arg0, String arg1, @SuppressWarnings("rawtypes") Map arg2) {
		SootMethod sootMethod = arg0.getMethod();
		UnitGraph controlFlowGraph = new BriefUnitGraph (sootMethod.getActiveBody());
		
		// Perform Analysis
		new StaticTaintAnalysis (controlFlowGraph);
	}
}
