import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;

public class BTreeLoader
{
	String path;
	FileChannel channelIn,channelOut;
	ByteBuffer buff;
	BTreeLoader(String p)
	{
		path=p;
	}
	public void test2(BTree.Node n)
	{
		ByteBuffer b=ByteBuffer.allocateDirect(1024);
	}
	public void loadIn()
	{
		try
		{
			channelIn = FileChannel.open(Paths.get(path));
			buff = ByteBuffer.allocateDirect(1024);

		} catch(IOException e) {e.printStackTrace();}
	}
	public void loadOut()
	{
		try
		{
			channelOut=FileChannel.open(Paths.get(path));
			buff=ByteBuffer.allocateDirect(1024);
		} catch(IOException e) {e.printStackTrace();}
	}
	public BTree.Node readNode()
	{
		int read=-1;
		try
		{
			read=channelIn.read(buff);
		} catch (IOException e) {e.printStackTrace();}
		if(read!=-1)
		{
			buff.flip();
		}
		//TODO REMOVE LINE BELOW
		return null;
	}
	public void closeIn()
	{
		try
		{
			channelIn.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	public void closeOut()
	{
		try
		{
			channelOut.close();
		} catch (IOException e) {e.printStackTrace();}
	}
}
