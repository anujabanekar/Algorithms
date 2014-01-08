import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

class HuffNode {

	HuffNode leftNode, rightNode;
	boolean isLeafPresent = false;
	int frequency = 0;
	int intNodepointer;
	char ch = 0;

	public HuffNode(int frequ) {
		this.isLeafPresent = false;
		this.frequency = frequ;
		this.intNodepointer = 0;
		this.ch = 0;
	}

	public HuffNode(int frequ, int index) {
		this.isLeafPresent = false;
		this.frequency = frequ;
		this.intNodepointer = index;
		this.ch = 0;
	}

	public HuffNode(char ch, int frequ, boolean isLeaf, int index) {
		this.ch = ch;
		this.frequency = frequ;
		this.isLeafPresent = isLeaf;
		this.intNodepointer = index;
	}

	public boolean isNodeLarger(HuffNode n) {
		if (this.frequency > n.frequency)
			return true;
		else if (this.frequency == n.frequency)
			return comparechar(n);
		return false;
	}

	public boolean isNodeSmaller(HuffNode n) {
		if (this.frequency < n.frequency)
			return true;
		else if (this.frequency == n.frequency)
			return comparechar(n);
		return false;
	}

	public boolean isNodeEqual(HuffNode n) {
		if (this.frequency == n.frequency)
			return true;
		return false;
	}

	private boolean comparechar(HuffNode n) {
		if (this.ch < n.ch)
			return true;
		return false;
	}

	public String getNodestrchar() {
		String returnch = "";
		if (this.isLeafPresent) {
			returnch = String.valueOf(this.ch);

			if (this.ch == '\'' || this.ch == '"' || this.ch == '<'
					|| this.ch == '>' || this.ch == '{') {
				returnch = "\\" + returnch;
			}
		}
		return returnch;
	}

	public String getNodeFreqStr() {
		return String.valueOf(this.frequency);
	}

	public String getNodeValueStr() {
		return String.valueOf(this.intNodepointer);
	}

}

class Heapify {

	int heapSize;
	private final int maxHeapSize = 1024;
	HuffNode A[];

	public Heapify() {
		this.A = new HuffNode[maxHeapSize + 1];
		this.heapSize = 0;
	}

	public void buildMinHeap(HuffNode A[]) {
		for (int i = heapSize / 2; i >= 1; i--)
			minHeapify(i);
		return;
	}

	private void minHeapify(int i) {
		int l = leftchild(i);
		int r = rightchild(i);
		int smallest = i;

		if (l <= heapSize && A[l].isNodeSmaller(A[i]))
			smallest = l;

		if (r <= heapSize && A[r].isNodeSmaller(A[smallest]))
			smallest = r;

		if (smallest != i) {
			interchangeNode(i, smallest);
			minHeapify(smallest);
		}
	}

	public HuffNode heapMinimum() {
		return A[1];
	}

	public HuffNode heapExtractMin() {
		if (heapSize < 1)
			return null;
		HuffNode min = A[1];
		A[1] = A[heapSize];
		heapSize = heapSize - 1;
		minHeapify(1);
		return min;
	}

	public void heapDecKey(int i, HuffNode key) {
		if (key.isNodeLarger(A[i]))
			return;

		int p;
		A[i] = key;

		while (i > 1 && A[p = parentNode(i)].isNodeLarger(A[i])) {
			interchangeNode(i, p);
			i = p;
		}
	}

	public void heapInsertNode(HuffNode key) {
		if (heapSize >= maxHeapSize - 1)
			return;

		heapSize = heapSize + 1;
		A[heapSize] = new HuffNode(Integer.MAX_VALUE);
		heapDecKey(heapSize, key);
	}

	public void displayHeapify() {
		for (int i = 1; i <= heapSize; i++) {
			System.out.print(", " + A[i].frequency);
		}
	}

	private int parentNode(int i) {
		return i / 2;
	}

	private int leftchild(int i) {
		return 2 * i;
	}

	private int rightchild(int i) {
		return 2 * i + 1;
	}

	private void interchangeNode(int i, int j) {
		HuffNode temp = A[i];
		A[i] = A[j];
		A[j] = temp;
		return;
	}

}

class DrawTree {

	HuffNode root;
	int noofleave;
	int noofbits;

	public Hashtable<Character, String> codeWords;
	public Hashtable<Character, Integer> frequencies;

	public DrawTree() {
		this.root = null;
		this.noofleave = 0;
		codeWords = new Hashtable<Character, String>();
		frequencies = new Hashtable<Character, Integer>();
	}

	public void addNode(char c, int freq) {
		if (frequencies.containsKey(c))
			return;
		frequencies.put(c, freq);
		noofleave++;
		return;
	}

	public void buildHuffmanTree() {

		Heapify PQ = new Heapify();

		boolean isLeafNode = true;
		int index = 0;

		for (Character c : frequencies.keySet()) {
			HuffNode n = new HuffNode(c, frequencies.get(c), isLeafNode,
					++index);
			PQ.heapInsertNode(n);
		}

		for (int i = 1; i < noofleave; i++) {
			HuffNode x = PQ.heapExtractMin();
			HuffNode y = PQ.heapExtractMin();
			HuffNode z = new HuffNode(x.frequency + y.frequency, noofleave + i);

			z.leftNode = x;
			z.rightNode = y;

			PQ.heapInsertNode(z);
		}

		this.root = PQ.heapExtractMin();
	}

	public void buildCodeWords() {
		traversing(this.root, "");
	}

	private void traversing(HuffNode n, String s) {

		if (n.isLeafPresent) {
			if (!codeWords.containsKey(n.ch)) {
				codeWords.put(n.ch, s);
			}
		}

		else {
			traversing(n.leftNode, s + "0");
			traversing(n.rightNode, s + "1");
		}
	}

	public void printCodeWords() {
		for (Character c : codeWords.keySet()) {
			System.out.println(c + " = " + codeWords.get(c));
		}
		return;
	}

	public void saveCodeWordsToFile(String fileFullPathName) {
		boolean appendMode = false;
		for (Character c : codeWords.keySet()) {
			FileHandle.write(c + " = " + codeWords.get(c) + "\n",
					fileFullPathName, appendMode);
			appendMode = true;
		}
		return;
	}

	// load the code words from a File
	public void loadFile(String fileFullPathName) {

		Hashtable<Character, String> codes = new Hashtable<Character, String>();

		List<String> strs = FileHandle.read(fileFullPathName);

		String str = "";
		String s = "";
		char c;

		for (int i = 0; i < strs.size(); i++) {

			str = strs.get(i);

			if (str == null || str.isEmpty()) {
				c = '\n';
				str = strs.get(++i);
				s = str.substring(3, str.length());
			} else {
				c = str.charAt(0);
				s = str.substring(4, str.length());
			}

			if (!codes.containsKey(c)) {
				codes.put(c, s);
			}
		}

		loadHuffmanTreeFromCodeWords(codes);

		this.codeWords = codes;
		noofleave = this.codeWords.size();

		return;
	}

	private void loadHuffmanTreeFromCodeWords(Hashtable<Character, String> codes) {

		noofleave = 0;
		root = new HuffNode(0, ++noofleave);

		for (Character c : codes.keySet()) {
			String word = codes.get(c);
			loadhufftree(c, word);
		}
	}

	private void loadhufftree(char ch, String word) {

		HuffNode curr = root;

		for (Character c : word.toCharArray()) {

			if (c == '0') {
				if (curr.leftNode == null)
					curr.leftNode = new HuffNode(0, ++noofleave);

				curr = curr.leftNode;
			}

			else if (c == '1') {
				if (curr.rightNode == null)
					curr.rightNode = new HuffNode(0, ++noofleave);

				curr = curr.rightNode;
			}

			else
				throw new RuntimeException(
						"Error: CodeWord can contain either 0 or 1.");
		}

		curr.ch = ch;
		curr.isLeafPresent = true;

		return;
	}

	public String getEncodedString(char[] chars) {

		StringBuilder encodedString = new StringBuilder();

		for (char c : chars) {
			if (codeWords.containsKey(c))
				encodedString.append(codeWords.get(c));
			else
				throw new RuntimeException("Error occured");
		}

		return encodedString.toString();
	}

	public String getDecodedString(String str) {

		StringBuilder decodedString = new StringBuilder();

		int len = str.length();
		int i = 0;
		char c;
		HuffNode curr;

		while (i < len) {

			curr = this.root;

			while (!curr.isLeafPresent && i < len) {
				c = str.charAt(i);
				if (c == '0')
					curr = curr.leftNode;
				else if (c == '1')
					curr = curr.rightNode;
				else
					throw new RuntimeException(
							"Error: Binary Bits should be present");
				i++;
			}

			if (curr.isLeafPresent)
				decodedString.append(curr.ch);
		}

		return decodedString.toString();
	}

}

class GraphHandler {

	private String leafNodeColorString = "blue";
	private String internalNodeColorString = "pink";
	private String edgeColorString = "black";
			
	private List<String> str = new ArrayList<String>();
	private DrawTree hf;
	
	public GraphHandler(DrawTree ht) {
		hf = ht;
	}
	
	public List<String> getGraphInputFile() {
		
		str = new ArrayList<String>();
		
		str.add("graph G {");
		str.add(" ");
		str.add("\tnode [width=.1, height=.1, color=\""+ internalNodeColorString + "\"]");
		str.add("\tedge [color=\""+ edgeColorString + "\"]");
		str.add(" ");
		travarseTreeAndBuildStrings(hf.root);
		str.add("}");
		
		return str;
	}
	
	private void travarseTreeAndBuildStrings(HuffNode n) {
		
		if(n.isLeafPresent) {
			str.add("\t" + n.getNodeValueStr() + " [shape=record, color=\"" + leafNodeColorString + "\", label=\" '" + n.getNodestrchar() + "' | " + n.getNodeFreqStr() + " \"]");
			str.add(" ");
		}
		
		else {
			str.add("\t" + n.getNodeValueStr() + " [label=\"" + n.getNodeFreqStr() + "\"]");
			
			str.add("\t" + n.getNodeValueStr() + " -- " + n.leftNode.getNodeValueStr() + " [label=\"0\"]");
			str.add("\t" + n.getNodeValueStr() + " -- " + n.rightNode.getNodeValueStr() + " [label=\"1\"]");
			str.add(" ");
			
			travarseTreeAndBuildStrings(n.leftNode);
			travarseTreeAndBuildStrings(n.rightNode);
		}
			
		return;
	}


}

public class MainHuffman {

	public static Hashtable<Character, Integer> charFrequencies;
	public static DrawTree huff;

	@SuppressWarnings("static-access")
	public static Options buildOptions() {

		Options options = new Options();

		Option help = new Option("help", "print program help");

		Option buildhuffman = OptionBuilder.withArgName("file").hasArg()
				.withDescription("Build the Huffman tree from the given file")
				.create("buildhuffman");

		Option huffmanvalues = OptionBuilder.withArgName("file").hasArg()
				.withDescription("Save Tree").create("huffmanvalues");

		Option encodehuffmanfile = OptionBuilder.withArgName("file").hasArg()
				.withDescription("File encoded with huffman values")
				.create("encodehuffmanfile");

		Option encodehuffmantext = OptionBuilder.withArgName("text").hasArg()
				.withDescription("Text is encrypted")
				.create("encodehuffmantext");

		Option decodehuffmanfile = OptionBuilder.withArgName("file").hasArg()
				.withDescription("The encoded file is decoded")
				.create("decodehuffmanfile");

		Option decodehuffmantext = OptionBuilder.withArgName("text").hasArg()
				.withDescription("The encrypted text is decrypted")
				.create("decodehuffmantext");

		Option output = OptionBuilder.withArgName("file").hasArg()
				.withDescription("Output the file").create("output");

		Option graphgenerator = OptionBuilder.withArgName("file").hasArg()
				.withDescription("Input file for GraphGenerator")
				.create("graphgenerator");

		options.addOption(help);
		options.addOption(buildhuffman);
		options.addOption(huffmanvalues);
		options.addOption(encodehuffmanfile);
		options.addOption(encodehuffmantext);
		options.addOption(decodehuffmanfile);
		options.addOption(decodehuffmantext);
		options.addOption(output);
		options.addOption(graphgenerator);

		return options;
	}

	public static void main(String[] args) {

		Options options = buildOptions();
		CommandLine cmd = null;

		CommandLineParser parser = new BasicParser();
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException exp) {

			System.err.println("Parsing fail: " + exp.getMessage());
			System.exit(0);
		}

		handleCommandLineOptions(cmd, options);
	}

	public static void handleCommandLineOptions(CommandLine cmd, Options options) {

		if (cmd.getOptions().length == 1) {
			if (cmd.hasOption("help")) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("huffman", options);
				System.exit(0);
			}

			System.out.println("\nInadequate commandlines ");
			return;
		}

		String buildhuffman, huffmanvalues, encodehuffmanfile, encodehuffmantext, decodehuffmanfile, decodehuffmantext, outputFile, graphgenerator;
		buildhuffman = huffmanvalues = encodehuffmanfile = encodehuffmantext = decodehuffmanfile = decodehuffmantext = outputFile = graphgenerator = null;

		if (cmd.hasOption("buildhuffman")) {
			buildhuffman = cmd.getOptionValue("buildhuffman");
		}
		if (cmd.hasOption("huffmanvalues")) {
			huffmanvalues = cmd.getOptionValue("huffmanvalues");
		}
		if (cmd.hasOption("encodehuffmanfile")) {
			encodehuffmanfile = cmd.getOptionValue("encodehuffmanfile");
		}
		if (cmd.hasOption("encodehuffmantext")) {
			encodehuffmantext = cmd.getOptionValue("encodehuffmantext");
		}
		if (cmd.hasOption("decodehuffmanfile")) {
			decodehuffmanfile = cmd.getOptionValue("decodehuffmanfile");
		}
		if (cmd.hasOption("decodehuffmantext")) {
			decodehuffmantext = cmd.getOptionValue("decodehuffmantext");
		}
		if (cmd.hasOption("output")) {
			outputFile = cmd.getOptionValue("output");
		}
		if (cmd.hasOption("graphgenerator")) {
			graphgenerator = cmd.getOptionValue("graphgenerator");
		}

		if (huffmanvalues == null || huffmanvalues.isEmpty()) {
			System.out.println("\n--tree commandline option missing.");
			return;
		}

		huff = new DrawTree();

		if (buildhuffman != null && !buildhuffman.isEmpty()) {

			countCharacterFrequncies(buildhuffman);

			computeHuffmanTree();

			huff.saveCodeWordsToFile(huffmanvalues);

		}

		else {
			String str = null;
			String s = "";
			boolean appendMode = false;

			huff.loadFile(huffmanvalues);

			if (encodehuffmanfile != null && !encodehuffmanfile.isEmpty()) {

				if (outputFile == null || outputFile.isEmpty())
					System.out
							.println("\n--output commandline option missing.");
				else {
					char[] chars = FileHandle.readchar(encodehuffmanfile);
					str = huff.getEncodedString(chars);
					FileHandle.write(str, outputFile, appendMode);
					System.out.println("\n Successful generation of file - "
							+ outputFile);
				}
				return;
			}

			else if (decodehuffmanfile != null && !decodehuffmanfile.isEmpty()) {

				if (outputFile == null || outputFile.isEmpty())
					System.out
							.println("\n--output commandline option missing.");
				else {

					s = "";
					List<String> strs = FileHandle
							.read(decodehuffmanfile);
					if (strs != null && strs.size() > 0)
						s = strs.get(0);
					str = huff.getDecodedString(s);
					FileHandle.write(str, outputFile, appendMode);

					System.out.println("\nSuccessful generation of file - "
							+ outputFile);
				}
				return;

			}

			// Encryption
			else if (encodehuffmantext != null && !encodehuffmantext.isEmpty()) {
				str = huff.getEncodedString(encodehuffmantext.toCharArray());
				System.out.println("\nEncrypted text: " + str);
			}

			// Decryption
			else if (decodehuffmantext != null && !decodehuffmantext.isEmpty()) {
				str = huff.getDecodedString(decodehuffmantext);
				System.out.println("\nDecrypted Text: " + str);
			}

			if (outputFile != null && !outputFile.isEmpty()) {
				if (str != null && !str.isEmpty()) {
					FileHandle.write(str, outputFile, appendMode);
					System.out.println("\nSuccessful generation of file - "
							+ outputFile);
				}
			}
		}

		if (graphgenerator == null || graphgenerator.isEmpty())
			graphgenerator = "GraphInput.gv";
		generateGraphInputFile(graphgenerator);
		System.out
				.println("\nSuccessful generation of file - "
						+ graphgenerator);
	}

	public static void computeHuffmanTree() {

		for (Character c : charFrequencies.keySet()) {
			huff.addNode(c, charFrequencies.get(c));
		}

		huff.buildHuffmanTree();
		huff.buildCodeWords();

		return;
	}

	public static void countCharacterFrequncies(String fileFullPathName) {

		charFrequencies = new Hashtable<Character, Integer>();

		char[] chars = FileHandle.readchar(fileFullPathName);

		for (char c : chars) {
			if (!charFrequencies.containsKey(c)) {
				charFrequencies.put(c, 1);
			} else {
				charFrequencies.put(c, charFrequencies.get(c) + 1);
			}
		}

		return;
	}

	public static void generateGraphInputFile(String outputFileFullPathName) {

		FileHandle.createFile(outputFileFullPathName);

		GraphHandler gv = new GraphHandler(huff);
		List<String> strs = gv.getGraphInputFile();

		boolean appendMode = false;

		for (String str : strs) {
			FileHandle.write(str + "\n", outputFileFullPathName,
					appendMode);
			appendMode = true;
		}

		return;
	}

}
