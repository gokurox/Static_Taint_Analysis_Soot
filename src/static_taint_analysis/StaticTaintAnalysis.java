package static_taint_analysis;

import java.util.ArrayList;
import java.util.Iterator;

import soot.Body;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.ReturnStmt;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;

/**
 * @author Gursimran Singh
 * @rollno 2014041
 */

public class StaticTaintAnalysis extends ForwardFlowAnalysis<Unit, FlowSet> {

	final private boolean VERBOSE = false;
	final private boolean INCLUDE_TEMP = true;
	final private Body methodBody;
	
	public StaticTaintAnalysis(UnitGraph graph) {
		super(graph);
		
		methodBody = graph.getBody();
		doAnalysis();
	}

	@SuppressWarnings("unused")
	@Override
	protected void flowThrough(FlowSet in, Unit unit, FlowSet out) {
		// Copy inFlow to outFlow
		in.copy (out);
		
		if (VERBOSE) {
			System.out.println ("Unit: " + unit);
			System.out.println ("InFlow: " + in);
		}
		
		// For assignment Statements
		if (unit instanceof AssignStmt) {
			Iterator<ValueBox> defBoxesIt, useBoxesIt;
			defBoxesIt = unit.getDefBoxes().iterator();
			useBoxesIt = unit.getUseBoxes().iterator();
			
			while (defBoxesIt.hasNext()) {
				ValueBox defbox = defBoxesIt.next();
				
				if (VERBOSE)
					System.out.println ("Removing: " + defbox.getValue());
				out.remove(defbox.getValue().toString());

				boolean tainted = false;
				while (useBoxesIt.hasNext()) {
					ValueBox usebox = useBoxesIt.next();
					Value value = usebox.getValue();
					if (value instanceof Local) {
						if (in.contains(value.toString())) {
							if (VERBOSE)
								System.out.println ("Tainted Var: " + value);
							tainted = true;
						}
					}
				}
				
				if (tainted) {
					if (VERBOSE)
						System.out.println ("Adding: " + defbox.getValue());
					out.add (defbox.getValue().toString());
				}
			}
		}
		
		if (VERBOSE) {
			System.out.println ("outFlow: " + out);
			System.out.println ();
		}
		
		if (unit instanceof ReturnStmt) {
			System.out.println ("RETURN SINK:");
			System.out.println ("UnitStatement: " + unit.toString());
			
			System.out.print ("Tainted Variables being returned: ");
			Iterator<ValueBox> useIt = unit.getUseBoxes().iterator();
			ArrayList<String> localTainted = new ArrayList<>();
			while (useIt.hasNext()) {
				Value usebox = useIt.next().getValue();
				if (usebox instanceof Local) {
					if (!INCLUDE_TEMP && usebox.toString().charAt(0) == '$') {
						continue;
					}
					
					if (out.contains(usebox.toString())) {
						localTainted.add (usebox.toString());
					}
				}
			}
			System.out.println (localTainted);
			
			System.out.println ("Potentially Tainted Variables at this point:");
			System.out.println (out);
			System.out.println ();
		}
		
		if (unit instanceof InvokeStmt) {
			if (unit.toString().contains("java.io.PrintStream")) {
				System.out.println ("PRINT SINK:");
				System.out.println ("UnitStatement: " + unit.toString());
				
				System.out.print ("Tainted Variables being printed: ");
				Iterator<ValueBox> useIt = unit.getUseBoxes().iterator();
				ArrayList<String> localTainted = new ArrayList<>();
				while (useIt.hasNext()) {
					Value usebox = useIt.next().getValue();
					if (usebox instanceof Local) {
						if (!INCLUDE_TEMP && usebox.toString().charAt(0) == '$') {
							continue;
						}
						
						if (out.contains(usebox.toString())) {
							localTainted.add (usebox.toString());
						}
					}
				}
				System.out.println (localTainted);
				
				System.out.println ("Potentially Tainted Variables at this point:");
				System.out.println (out);
				System.out.println ();
			}
		}
	}

	@Override
	protected void copy(FlowSet src, FlowSet dest) {
		src.copy (dest);
	}

	@Override
	protected FlowSet entryInitialFlow() {
		ArraySparseSet entry = new ArraySparseSet();
		
		int paraCount = methodBody.getMethod().getParameterCount();
		for (int i=0; i<paraCount; i++) {
			entry.add (methodBody.getParameterLocal(i).toString());
		}
		
		return entry;
	}

	@Override
	protected void merge(FlowSet in1, FlowSet in2, FlowSet out) {
		in1.union (in2, out);
	}

	@Override
	protected FlowSet newInitialFlow() {
		return new ArraySparseSet();
	}
}
