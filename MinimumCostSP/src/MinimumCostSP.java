import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

class graph {

	int graphnodes, graphedges, visit, mincost; // number of nodes that are
												// currently visited
	TreeMap<Integer, treenode> nodes; // list of all nodes in the graph
	TreeMap<Integer, value> edlist; // list of all edges in the graph
	TreeMap<Integer, value> mincostlist; // list of all edges in the MCST of the
											// graph

	public graph() {

		this.nodes = new TreeMap<Integer, treenode>();
		this.edlist = new TreeMap<Integer, value>();
		this.mincostlist = new TreeMap<Integer, value>();
	}

	public void nodesaddition(int n) {

		while (!nodes.containsKey(n)) {
			graphnodes++;
			nodes.put(n, new treenode(n));
		}

	}

	public void addedge(int u, int v, int c, int i) {

		treenode x = nodes.get(u);
		treenode y = nodes.get(v);
		x.addedge(new value(u, v, c, i));
		y.addedge(new value(v, u, c, i));
		edlist.put(i, new value(u, v, c, i));
		graphedges++;

	}

	public void output(String gname) {

		System.out.println("\n");
		String str = gname + "Graph:";

		for (int i = 0; i <= str.length(); i++)
			System.out.print("=");
		System.out.println();

		System.out.println(str);
		for (int i = 0; i <= str.length(); i++)
			System.out.print("=");
		System.out.println();

		System.out.println();

		System.out.println(graphnodes + " nodes, " + graphedges + " edges");
		edgesprint(this.edlist);
	}

	public void edgesprint(TreeMap<Integer, value> eList) {

		for (int i : eList.keySet()) {
			value e = eList.get(i);
			System.out.println(e.uid + ". (" + e.a + ", " + e.b + ") - weight "
					+ e.cost);
		}
	}

	public boolean graphconnected() {

		this.visit = 0;
		treenode r = null;

		for (int i : nodes.keySet()) {
			r = nodes.get(i);
			break;
		}

		if (r != null) {
			visiting(false);
			traversing(r);
		}

		return (this.visit == this.graphnodes);
	}

	public boolean minspanconnect() {

		this.visit = 0;
		value e = null;
		treenode u, v;

		visiting(false);

		for (int i : mincostlist.keySet()) {
			e = mincostlist.get(i);

			u = nodes.get(e.a);
			v = nodes.get(e.b);

			if (!u.visited) {
				this.visit++;
				u.visited = true;
			}

			if (!v.visited) {
				this.visit++;
				v.visited = true;
			}
		}

		return (this.visit == this.graphnodes);
	}

	public void traversing(treenode n) {

		this.visit++;
		n.visited = true;

		for (int i : n.edges.keySet()) {

			value e = n.edges.get(i);
			treenode v = this.nodes.get(e.b);

			if (!v.visited)
				traversing(v);
		}

	}

	public void visiting(boolean v) {
		for (int i : nodes.keySet()) {
			nodes.get(i).visited = v;
		}
		return;
	}

	public graph subgraph(List<Integer> indices) {

		graph g = new graph();
		for (int i : indices) {
			g.nodesaddition(i);
		}

		for (value e : this.edlist.values()) {
			if (g.nodes.containsKey(e.a) && g.nodes.containsKey(e.b))
				g.addedge(e.a, e.b, e.cost, e.uid);
		}

		g.mincost = 0;
		for (value e : this.mincostlist.values()) {
			if (g.nodes.containsKey(e.a) && g.nodes.containsKey(e.b)) {
				g.mincost += e.cost;
				if (!g.mincostlist.containsKey(e.uid))
					g.mincostlist.put(e.uid, e);
			}
		}

		if (!g.graphconnected()) {
			System.out.println("\nSubgraph is not connected.");
			return null;
		}

		if (!g.minspanconnect()) {
			System.out.println("\nMCST in the Subgraph is not connected.");
			return null;
		}

		return g;
	}

	private List<value> sortededges(TreeMap<Integer, value> edList) {

		value e;
		List<value> currentedges = new ArrayList<value>();

		for (int i : edList.keySet()) {
			e = edList.get(i);
			currentedges.add(e);
		}
		Collections.sort(currentedges);

		return currentedges;
	}

	public void MCST() {

		treenode u, v;

		for (int i : nodes.keySet()) {
			u = nodes.get(i);
			set(u);
		}

		List<value> currentedges = sortededges(this.edlist);

		this.mincost = 0;

		for (value ed : currentedges) {
			u = nodes.get(ed.a);
			v = nodes.get(ed.b);

			if (findSet(u) != findSet(v)) {
				combinedset(u, v);

				this.mincost += ed.cost;

				if (!mincostlist.containsKey(ed.uid)) {
					mincostlist.put(ed.uid, ed);
				}
			}
		}

		return;
	}

	public void outputmincost(String mName) {

		System.out.println("MCST:");
		System.out.println();
		System.out.println("Total cost - " + this.mincost);
		System.out.println();
		edgesprint(mincostlist);
	}

	public void set(treenode x) {
		x.parent = x;
		x.rank = 0;
	}

	public void combinedset(treenode x, treenode y) {
		link(findSet(x), findSet(y));
	}

	public void link(treenode x, treenode y) {
		if (x.rank > y.rank)
			y.parent = x;
		else {
			x.parent = y;
			if (x.rank == y.rank)
				y.rank++;
		}
	}

	public treenode findSet(treenode x) {
		if (x.parent != x)
			x.parent = findSet(x.parent);
		return x.parent;
	}

	public List<String> gettingthegraph() {

		List<String> graphStrings = new ArrayList<String>();
		graphStrings.add("nodes: " + graphnodes);
		graphStrings.add("edges: " + graphedges);

		for (int i : edlist.keySet()) {
			value e = edlist.get(i);
			graphStrings.add("(" + e.a + ", " + e.b + ") - " + e.cost);
		}

		return graphStrings;
	}
}

class value implements Comparable<value> {

	public int uid, a, b, cost;

	public value(int n, int m, int w, int i) {
		uid = i;
		a = n;
		b = m;
		cost = w;
	}

	@Override
	public int compareTo(value v) {
		return this.cost - v.cost;
	}

}

class treenode {
	boolean visited;
	int distance, noe, rank, uid;
	TreeMap<Integer, value> edges;

	public treenode parent; // parent of this node

	public treenode(int id) {
		this.uid = id;
		this.edges = new TreeMap<Integer, value>();
	}

	public void addedge(value e) {
		while (!edges.containsKey(e.uid))
			edges.put(e.uid, e);

	}

}

public class MinimumCostSP {

	@SuppressWarnings("static-access")
	public static Options buildOptions() {

		Options options = new Options();

		Option graph = OptionBuilder.withArgName("file").hasArg()
				.withDescription("graph from file").create("graph");

		Option sub = OptionBuilder.withArgName("file").hasArg()
				.withDescription("subgraph from file").create("sub");

		Option random = OptionBuilder.withArgName("n").hasArg()
				.withDescription(" Random Graph with n nodes").create("random");

		Option out = OptionBuilder.withArgName("file").hasArg()
				.withDescription("Write the output").create("out");

		options.addOption(graph);
		options.addOption(sub);
		options.addOption(random);
		options.addOption(out);
		return options;
	}

	public static void main(String[] args) {

		Options options = buildOptions();
		CommandLine cmd = null;

		CommandLineParser parser = new BasicParser();
		try {
			cmd = parser.parse(options, args);
		} catch (Exception e) {

			System.out.println("error" + e);
		}
		try {
			handling(cmd, options);
		} catch (Exception e) {
		}
		return;
	}


/*	
public static void updateEdgeCost(int ind, int c, graph g, graph subg) {
		
		DrawTree e = null;
		boolean modified = true;
		
		if(!g.edgeList.containsKey(ind)) {
			System.out.println("\nInvalid edge index - " +ind);
			modified = false;
		}
		
		else {
			e = g.edgeList.get(ind);
			
			if(c < 0) {
				System.out.println("\nCost must be positive.");
				modified = false;
			}
			if( !g.mcstEdgeList.containsKey(ind) ) {
				System.out.println("\nEdge: " + e.ind + ". (" + e.x + ", " + e.y + ") - is not an edge of the mcst.");
				modified = false;
			}	
			if( !subg.nodeList.containsKey(e.x) || !subg.nodeList.containsKey(e.y) ) {
				System.out.println("\nEdge: " + e.ind + ". (" + e.x + ", " + e.y + ") - is not inside the sub-graph.");
				modified = false;
			}
		}
		
		if(!modified) {
			System.out.println("\nNo modification is required in the partial MCST.");
			return;
		}

		
		if(c == e.c) {
			System.out.println("\nThe new cost " + c + " is equal to the previous cost of Edge: " + e.ind + ". (" + e.x + ", " + e.y + ") - " + e.c + ".");
			modified = false;
		}

		else if(c < e.c) {
			System.out.println("\nThe new cost " + c + " is less than the previous cost of Edge: " + e.ind + ". (" + e.x + ", " + e.y + ") - " + e.c + ".");
			System.out.println("\nNew cost of the partial MCST is: " + (subg.mcstCost-e.c+c) );
			modified = false;
		}
		
		else {
			
			Edge modifiedEdge = subg.isModificationRequired(e, c);
			
			if(modifiedEdge == null) {
				System.out.println("\nNew cost of the partial MCST is: " + (subg.mcstCost-e.c+c) );
				modified = false;
			}
			
			else {
				System.out.println("\nThe partial MCST needs to be modified.");
				subg.printMCST("Modified-Partial-");
				
				System.out.println("\nThe cost of Edge: " + e.ind + ". (" + e.x + ", " + e.y + ") was updated to " + c + " from " + e.c + ".");
				System.out.println("Edge: " + e.ind + ". (" + e.x + ", " + e.y + ") - weight " + c + " is removed.");
				System.out.println("Edge: " + modifiedEdge.ind + ". (" + modifiedEdge.x + ", " + modifiedEdge.y + ") - weight " + modifiedEdge.c + " is added.");
				modified = true;
			}
		}
		
		if(!modified) {
			System.out.println("\nNo modification is required in the partial MCST.");
			return;
		}
		
		return;
	}
*/

	public static void handling(CommandLine cmd, Options options) {
		String random = "10";
		String output = "ran.txt";
		String graph = null;
		String sub = "sub.txt";
		graph g, subg;

		if (cmd.hasOption("graph")) {
			graph = cmd.getOptionValue("graph");
			System.out.println("\nGraph loaded from file: " + graph);
			g = generategraph(graph);

			g.output("Original");
			g.MCST();
			g.outputmincost("");
//			List<Integer> indices = subnodes(sub);
//			subg = g.subgraph(indices);
//			subg.output("Sub-");
//			subg.outputmincost("Partial-");

		}
		if (cmd.hasOption("sub")) {
			sub = cmd.getOptionValue("sub");
			graph = "MinCSP_input.txt";
			g = generategraph(graph);
			g.MCST();
			List<Integer> indices = subnodes(sub);
			subg = g.subgraph(indices);
			subg.output("Sub-");
			subg.outputmincost("Partial-");

		}
		if (cmd.hasOption("random")) {
			random = cmd.getOptionValue("random");
		}
		if (cmd.hasOption("out")) {
			output = cmd.getOptionValue("out");
		}

		if (cmd.hasOption("random")) {
			int n = Integer.parseInt(random);
			graph g1 = randomg(n);
			save(g1, output);
			System.out.println("\nRandom graph with " + n
					+ " nodes are generated to file - " + output);
		}

		return;
	}

	public static graph randomg(int n) {

		final double p = 0.6;
		final int maxCost = 300;

		graph g = null;

		do {
			g = randomgraph(n, p, maxCost);
		} while (!g.graphconnected());

		return g;
	}

	public static graph randomgraph(int n, double p, int maxCost) {

		graph g = new graph();

		int i, j, c;
		int edgeIndex = 0;
		treenode u, v;

		for (i = 1; i <= n; i++)
			g.nodesaddition(i);

		Random randP = new Random();
		Random ranC = new Random();

		for (i = 1; i <= n; i++) {

			u = g.nodes.get(i);

			for (j = i + 1; j <= n; j++) {

				if (p >= randP.nextDouble()) {

					c = ranC.nextInt(maxCost) + 1;
					v = g.nodes.get(j);

					edgeIndex++;
					g.addedge(u.uid, v.uid, c, edgeIndex);
				}
			}
		}

		return g;
	}

	public static graph generategraph(String fileFullPathName) {

		graph g = new graph();

		List<String> strs = FileHandle.read(fileFullPathName);

		String str1, str2;

		str1 = strs.get(0);

		str2 = strs.get(1);

		int n, ne;

		n = ne = 0;

		n = Integer.parseInt(str1.substring(7, str1.length()));
		ne = Integer.parseInt(str2.substring(7, str2.length()));

		int i, j;
		int u, v, c;
		String str = null;
		String[] vals;
		for (i = 1; i <= n; i++)
			g.nodesaddition(i);

		for (i = 2, j = 0; i < strs.size() && j < ne; i++, j++) {

			str = strs.get(i);

			vals = str.split("-");

			if (vals.length != 2)
				break;

			c = Integer.parseInt(vals[1].trim());

			vals = vals[0].trim().split(",");

			if (vals.length != 2)
				break;

			vals[0] = vals[0].trim();
			vals[1] = vals[1].trim();

			u = Integer.parseInt(vals[0].substring(1));
			v = Integer.parseInt(vals[1].substring(0, vals[1].length() - 1));

			g.addedge(u, v, c, j + 1);
		}

		return g;
	}

	public static List<Integer> subnodes(String fname) {

		List<Integer> indices = new ArrayList<Integer>();
		List<String> strs = FileHandle.read(fname);

		for (String str : strs) {
			int nodeInd = Integer.parseInt(str.trim());
			indices.add(nodeInd);

		}

		return indices;
	}

	// save the graph to file
	public static void save(graph g, String fname) {

		boolean append = false;
		List<String> strs = g.gettingthegraph();
		for (String str : strs) {
			FileHandle.write(str + "\n", fname, append);
			append = true;
		}

		return;
	}

}
