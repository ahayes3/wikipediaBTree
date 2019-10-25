import javax.swing.text.html.parser.Parser;
import java.util.ArrayList;

public class TermHash
{
	private static final double RESIZE_THRESHOLD = .75;
	private int count;
	private String link;

	static class Node
	{
		String item;
		int occurences;
		double tfidf;
		Node next;
		int index;

		Node(String key,int i)
		{
			next = null;
			this.item = key;
			occurences = 1;
			tfidf = -1;
			index=i;
		}
	}

	Node[] table;

	public boolean containsKey(String key)
	{
		int hash = key.hashCode();
		int index = (int) Math.abs(Math.pow(hash,2)%(table.length-1));
		if(table[index]!=null)
		{
			for(Node n= table[index];n!=null;n=n.next)
			{
				if(n.item.equals(key))
					return true;
			}
		}
		return false;
	}

	public void add(String key)
	{
		int hash = key.hashCode();
		int index = (int) Math.abs(Math.pow(hash,2)%(table.length-1));

		if (this.containsKey(key))
			get(key).occurences++;
		else if (!this.containsKey(key))
		{
			if(table[index]!=null)
			{
				Node last=table[index];
				while(last.next!=null)
					last=last.next;
				last.next=new Node(key,index);
			}
			else
				table[index]=new Node(key,index);
		}
		if ((double) (++count / table.length) > RESIZE_THRESHOLD)
			resize();


	}
	public int indexOf(String key)
	{
		return (int) Math.abs(Math.pow(key.hashCode(),2)%(table.length-1));
	}
	public Node get(String key)
	{
		int index=(int) Math.abs(Math.pow(key.hashCode(),2)%(table.length-1));
		if(table[index]!=null)
		{
			for(Node n=table[index];n!=null;n=n.next)
			{
				if(n.item.equals(key))
					return n;
			}
		}
		return null;
//		for(Node n:table)
//		{
//			for (Node check = n; check != null; check = check.next)
//			{
//				if (check.item.equals(key))
//					return check;
//			}
//		}
//		return null;
	}

	public int getOccurences(String key)
	{
		return get(key).occurences;
	}

	private void transfer(Node no, Node[] t)
	{
		ArrayList<Node> l=new ArrayList<>();
		for(Node node=no;node!=null;node=node.next)
		{
			l.add(node);
		}
		for(Node node:l)
		{
			node.next=null;
			int hash=node.item.hashCode(); //2147483657
			int index=(int) Math.abs(Math.pow(hash,2)%(t.length-1));
			if(t[index]==null)
				t[index]=node;
			else
			{
				Node tmp=t[index];
				while(tmp.next!=null)
				{
					tmp = tmp.next;
				}
				tmp.next=node;
			}
		}
	}

	private void resize()
	{
		System.out.println("RESIZE");
		Node[] newTable = new Node[(table.length) * 2];

		for (int i = 0; i < table.length; i++)
		{
			if (table[i] != null)
			{
				transfer(table[i], newTable);
			}
		}

		table = newTable;
	}

	public ArrayList<String> getKeys()
	{
		ArrayList<String> keys = new ArrayList<>();
		for (Node n : table)
		{

			for(Node check=n;check!=null;check=check.next)
			{
				keys.add(check.item);
			}
		}
		return keys;
	}

//	public ArrayList<Integer> getAllOccurs()
//	{
//		ArrayList<Integer> occurs = new ArrayList<>();
//		for (Node n : table)
//		{
//			if (n != null)
//				occurs.add(n.occurences);
//		}
//		return occurs;
//	}

	public int totalWords()
	{
		int total = 0;
		for (Node n : table)
		{
			for(Node check=n;check!=null;check=check.next)
			{
				total+=check.occurences;
			}
		}
		return total;
	}

	public String getLink()
	{
		return link;
	}

	public TermHash(String l)
	{
		link = l;
		table = new Node[500];
		count = 0;
	}
}
