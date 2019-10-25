import java.util.ArrayList;
import java.util.HashMap;

public class Testing
{
	public static void main(String [] args)
	{
		BTree.Node n=new BTree.Node();
		n.add(BTree.bloomHash("abc"));
		BTreeLoader bl=new BTreeLoader("NO PATH");
		bl.testReadWrite(n);
	}
}
