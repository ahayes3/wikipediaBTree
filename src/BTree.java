import java.util.ArrayList;
import java.util.Arrays;

public class BTree
{
	public Node root;
	static class Node
	{
		private static final int MAX_SIZE = 5;
		private long[] keys;
		private int[] freqs;
		private int keysLength;
		private Node parent;
		private Node[] children;

		public Node()
		{
			keysLength=0;
			keys=new long[MAX_SIZE];
			Arrays.fill(keys,-1);
			children=new Node[MAX_SIZE+1];
			freqs=new int[MAX_SIZE];
			Arrays.fill(freqs,-1);
			parent=null;
		}
		public Node(Node p)
		{
			keysLength=0;
			keys=new long[MAX_SIZE];
			Arrays.fill(keys,-1);
			children=new Node[MAX_SIZE+1];
			Arrays.fill(children,null);
			freqs=new int[MAX_SIZE];
			Arrays.fill(freqs,-1);
			parent=p;
		}
		public Node(Node p,long[] l,int[] f)
		{
			parent=p;
			keys=new long[MAX_SIZE];
			for(int i=0;i<keys.length;i++)
			{
				if(i<l.length)
				{
					keys[i] = l[i];
					keysLength++;
				}
				else
					keys[i]=-1;
			}
			children=new Node[MAX_SIZE+1];
			Arrays.fill(children,null);
			freqs=new int[MAX_SIZE];
			for(int i:freqs)
			{
				if(i<f.length)
					freqs[i]=f[i];
				else
					freqs[i] =-1;
			}

		}
		public boolean isLeaf()
		{
			for(Node n:children)
			{
				if(n!=null)
					return false;
			}
			return true;
		}
		public static int getMaxSize()
		{
			return MAX_SIZE;
		}
		public Node add(long l)
		{
			Node root=null;
			if((isLeaf())&&(keysLength<MAX_SIZE-1))
			{
				addKey(l);
				root=this.getRoot();
			}
			else if((isLeaf())&&(keysLength==MAX_SIZE-1))
			{
				addKey(l);
				root=this.promote();
				root=root.getRoot();
			}
			else
			{
				findChildFor(l).add(l);
				root=this.getRoot();
			}
		return root;
		}
		public Node findChildFor(long l)
		{
			if(l<keys[0])
				return children[0];
			else if(l>keys[keysLength-1])
				return children[keysLength]; //Childrenlength is keysLength +1 so this is keyslength +1 -1
			else
			{
				for(int i=0;i<keysLength-1;i++)
				{
					if((l>keys[i])&&(l<keys[i+1]))
						return children[i+1];
				}
			}
			return null;
		}
		public Node getRoot()
		{
			if(parent==null)
				return this;
			else
				parent.getRoot();
			return null;
		}
		private Node promote() //Returns Node term was promoted to
		{
			int centerIndex;
			Node left,right;
			long[] subLeft;
			int[] freqLeft;
			long[] subRight;
			int[] freqRight;
			if(MAX_SIZE%2==0)
			{
				centerIndex = (MAX_SIZE-1) / 2;
				subLeft=new long[(MAX_SIZE-1)/2];
				freqLeft=new int[(MAX_SIZE-1)/2];
				subRight=new long[(MAX_SIZE)/2];
				freqRight=new int[(MAX_SIZE)/2];
			}
			else
			{
				centerIndex = (MAX_SIZE - 1) / 2;
				subLeft=new long[(MAX_SIZE-1)/2];
				freqLeft=new int[(MAX_SIZE-1)/2];
				subRight=new long[(MAX_SIZE-1)/2];
				freqRight=new int[(MAX_SIZE-1)/2];
			}
			if(parent==null)
			{
				parent=new Node();
				parent.addKey(keys[centerIndex]);
			}
			else
			{
				for(Node n:parent.children)
				{
					if(n==this)
						n=null;
				}
				parent.addKey(keys[centerIndex],freqs[centerIndex]);
			}
			for(int i=0;i<centerIndex;i++)
			{
				subLeft[i]=keys[i];
				freqLeft[i]=freqs[i];
			}
			for(int i=centerIndex+1;i<keysLength;i++)
			{
				subRight[i-(centerIndex+1)]=keys[i];
				freqRight[i-(centerIndex+1)]=freqs[i];
			}
			left=new Node(parent,subLeft,freqLeft);
			right=new Node(parent,subRight,freqRight);
			for(int i=0;i<parent.keysLength+1;i++)
			{
				if(parent.children[i]==null);
				{
					parent.children[i]=left;
					parent.children[i+1]=right;
					break;
				}
			}
			parent.sortAll();
			if(parent.keysLength==MAX_SIZE)
				parent.promote();
			return parent;
		}
		private boolean isFull()
		{
			if(this.length()==MAX_SIZE)
				return true;
			return false;
		}
		public void addKey(long l)
		{
			boolean found=false;
			for(int i=0;i<keys.length;i++)
			{
				if(keys[i]==l)
				{
					freqs[i]++;
					found=true;
				}
			}
			if(!found)
			{
				for(int i=0;i<keys.length;i++)
				{
					if(keys[i]==-1)
					{
						keys[i] = l;
						freqs[i]=1;
						keysLength++;
						sortAll();
						break;
					}
				}
			}
		}
		public void addKey(long l,int f)
		{
				for(int i=0;i<keys.length;i++)
				{
					if(keys[i]==-1)
					{
						keys[i] = l;
						freqs[i]=f;
						keysLength++;
						sortAll();
					}
				}
		}
		private void sortAll()
		{
			sortKeys();
			sortChildren();
		}
		private void sortKeys()
		{
			boolean sorted=false;
			long tmp;
			int tmpI;
			while(!sorted)
			{
				sorted=true;
				for(int i=0;i<keysLength-1;i++)
				{
					if(keys[i]>keys[i+1])
					{
						sorted=false;
						tmp=keys[i];
						keys[i]=keys[i+1];
						keys[i+1]=tmp;

						tmpI=freqs[i];
						freqs[i]=freqs[i+1];
						freqs[i+1]=tmpI;
					}
				}
			}
		}
		private void sortChildren()
		{
			boolean empty=true;
			for(Node n:children)
			{
				if(n!=null)
					empty=false;
			}
			if(!empty)
			{
				boolean sorted = false;
				Node tmp;
				while (!sorted)
				{
					sorted = true;
					for (int i = 0; i < keysLength; i++) //Childrenlength +1 -1
					{
						if (children[i].keys[0] > children[i + 1].keys[0])
						{
							sorted = false;
							tmp = children[i];
							children[i] = children[i + 1];
							children[i + 1] = tmp;
						}
					}
				}
			}
		}
		public int length()
		{
			return keysLength;
		}
		public ArrayList<Long> inOrder(ArrayList<Long> list)
		{
			if(isLeaf())
			{
				for(long l:keys)
				{
					if(l!=-1)
						list.add(l);
				}
			}
			else
			{
				int i=0;
				for(Node c:children)
				{
					if(c!=null)
					{
						c.inOrder(list);
						if (i < keysLength)
							list.add(keys[i]);
						i++;
					}
				}
			}
			return list;
		}
	}
	public BTree()
	{
		root=null;
	}
	public void add(String s)
	{
		long hash=bloomHash(s);
		if(root==null)
		{
			root=new Node(null);
			root.addKey(hash);
		}
		else
		{
			root = root.add(hash);
		}
	}
	public static long bloomHash(String s)
	{
		long hash=0;
		int hash1=s.hashCode();
		int hash2=0;
		for(int i=0;i<s.length();i++)
		{
			hash2+=(int) s.charAt(i);
		}
		hash2=hash2^(hash2>>5);
		hash+=hash1;
		hash=hash<<32;
		hash+=hash2;
		return hash;
	}
	public boolean contains(String s)
	{
		long hash = bloomHash(s);
		Node n=root;
		while(n!=null)
		{
			for (long l:n.keys)
			{
				if(l==hash)
					return true;
			}
			n=n.findChildFor(hash);
		}
		return false;
	}
	public ArrayList<Long> inOrder()
	{
		ArrayList<Long> output=new ArrayList<>();
		root.inOrder(output);
		return output;
	}
}
