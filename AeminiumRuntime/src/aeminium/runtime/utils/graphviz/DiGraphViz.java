package aeminium.runtime.utils.graphviz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class DiGraphViz extends GraphViz {
	protected final String name;
	protected final StringBuilder nodes;
	protected final StringBuilder connections;
	protected final String EOL = System.getProperty("line.separator");
	protected final int ranksep;
	protected final RankDir rankdir;
	
	public DiGraphViz(String name, int ranksep, RankDir rankdir) {
		this.name    = name;
		this.ranksep = ranksep;
		this.rankdir = rankdir;
		nodes        = new StringBuilder();
		connections  = new StringBuilder();
	}
	
	public String getName() {
		return name;
	}
	
	public void addNode(int id, String label) {
		addNode(id, label, DEFAULT_SHAPE, DEFAULT_COLOR);
	}
	
	public void addNode(int id,
					    String label,
					    Shape shape,
					    Color color) {
		nodes.append(String.format("    %12d [label=\"%s\", shape=\"%s\", color=\"%s\"]"+EOL, id, label, shape.name().toLowerCase(), color.name().toLowerCase()));
	}
	
	public void addConnection(int from, int to) {
				
	}
	
	public void addConnection(int from,
							  int to,
							  LineStyle lineStyle,
							  Color color) {
		connections.append(String.format("    %12d -> %12d [style=\"%s\", color=\"%s\"]"+EOL, from, to, lineStyle.name().toLowerCase(), color.name().toLowerCase()));
	}
	
	public boolean dump(File file) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			dump(fos);
			fos.close();
			return true;
		} catch (IOException e) {
		}
		return false;
	}
	
	public boolean dump(OutputStream os ) {
		try {
			dumpOutputStream(os);
			return true;
		} catch (IOException e) {
		}
		return false;
	}
	
	protected void dumpOutputStream(OutputStream os) throws IOException {
		os.write(String.format("digraph %s {" + EOL, name).getBytes());
		os.write(String.format("    rankdir=%s" + EOL, rankdir.name()).getBytes());
		os.write(String.format("    ranksep=%d" + EOL, ranksep).getBytes());
		os.write(nodes.toString().getBytes());
		os.write(connections.toString().getBytes());
		os.write("}".getBytes());
	}
}