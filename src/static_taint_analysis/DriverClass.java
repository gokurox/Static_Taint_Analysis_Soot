package static_taint_analysis;

import soot.Pack;
import soot.PackManager;
import soot.SootClass;
import soot.SootResolver;
import soot.Transform;
import soot.options.Options;

/**
 * @author Gursimran Singh
 * @rollno 2014041
 */

public class DriverClass {
	final private static String[] TESTFILES = {"TestFiles.TC0",
											   "TestFiles.TC1",
											   "TestFiles.TC2",
											   "TestFiles.TC3",
											   "TestFiles.TC4",
											   "TestFiles.TC5",
											   "TestFiles.TC6"}; 
	
	public static void main(String[] args) {
		/* 
		 * Set Command line Options
		 */
		Options.v().setPhaseOption ("jb", "use-original-names:true");
		Options.v().set_output_format(Options.output_format_jimple);
		
		Pack jtp = PackManager.v().getPack ("jtp");
		jtp.add (new Transform("jtp.instrumenter", new AnalysisWrapper()));
		
		SootResolver.v().resolveClass("java.lang.CloneNotSupportedException", SootClass.SIGNATURES);
		
		if (args.length > 0) {
			soot.Main.main (args);
		}
		else if (TESTFILES.length > 0) {
			soot.Main.main (TESTFILES);
		}
		else {
			System.err.println ("No input arguments provided ...");
			System.err.println ("Use either:");
			System.err.println ("\t" + "1. command line args");
			System.err.println ("\t" + "2. static field TESTFILES");
		}
	}
}
