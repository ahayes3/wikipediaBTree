import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

public class SimilarityAppCtrl
{

	private int crawlAmt = 6;

	@FXML
	private Button btn;

	@FXML
	private TextField tf;

	@FXML
	private Hyperlink hpl;

	public void initialize()
	{
		String basePath = System.getProperty("user.dir");

		String[] links = new String[5];
		links[0] = "https://en.wikipedia.org/wiki/Bean";
		links[1] = "https://en.wikipedia.org/wiki/Volcano";
		links[2] = "https://en.wikipedia.org/wiki/Leishmania_donovani";
		links[3] = "https://en.wikipedia.org/wiki/Crime_in_Alaska";
		links[4] = "https://en.wikipedia.org/wiki/Mario";
		File dir = new File(basePath + File.separator + "pageCache");

		if ((dir.exists()) && (dir.isDirectory()))
		{
			ArrayList pages = new ArrayList();
			System.out.println("Found a directory at " + dir.getPath());
			basePath = dir.getPath() + File.separator;
			Path p = Paths.get(basePath);

			try
			{
				Files.walk(p).filter(x -> (!x.toString().equals("D:\\Projects\\csc365\\project1\\pageCache\\lastmodified.txt"))).forEach(y -> pages.add(y.toString()));
				System.out.println("PAGES " + pages);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			ArrayList<File> files = new ArrayList<File>(Arrays.asList(dir.listFiles()));
			files.remove(new File(basePath + "lastmodified.txt"));
			if (files.size() == (crawlAmt * 5) + 5)
			{
				for (File f : files)
				{
					updateModified(toLink(f));
				}
			} else
			{
				for (String link : links)
				{
					crawlFile(basePath + removePunc(link), crawlAmt);
				}
			}
		} else
		{
			ArrayList<String> pages = new ArrayList<>();

			System.out.print("Directory not found: Creating new directory at ");
			dir.mkdir();
			System.out.println(dir.getPath());
			basePath = dir.getPath() + File.separator;
			System.out.println(basePath + "lastmodified.txt");
			for (String i : links)
			{
				String path = basePath + removePunc(i);
				System.out.println("Creating file at " + path);
				writeToFile(path, getHTML(i));
			}
			File folder = new File(basePath);
			File[] files = folder.listFiles();
			for (File f : files)
			{
				pages.add(f.getPath());
			}
			for (Object page : pages)
			{
				crawlFile((String) page, crawlAmt);
			}
		}
	}

	private String toLink(File f)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(f.getName().substring(0, 4) + "://" + f.getName().substring(5, 7) + "." + f.getName().substring(7, 16) + "." + f.getName().substring(16, 19) + "/" + f.getName().substring(19, 23) + "/" + f.getName().substring(23));
		return sb.toString();
	}

	private String toPath(String link)
	{
		return System.getProperty("user.dir") + File.separator + "pageCache" + File.separator + removePunc(link);
	}

	private void updateModified(String p)
	{
		File dir = new File(System.getProperty("user.dir") + File.separator + "pageCache");
		File[] files;
		ArrayList<File> fileList = new ArrayList<File>();
		String c = "";
		File modifiedFile = new File(System.getProperty("user.dir") + File.separator + "pageCache" + File.separator + "lastmodified.txt");
		if (!modifiedFile.isFile())
		{
			try
			{
				modifiedFile.createNewFile();
				files = dir.listFiles();
				for (File f : files)
				{
					if (!f.getPath().equals(modifiedFile.getPath()))
						fileList.add(f);

				}
				System.out.println("LIST FILES " + fileList);
				BufferedWriter bw = new BufferedWriter(new FileWriter(modifiedFile));
				boolean first = true;
				for (File f : fileList)
				{
					if (first)
					{
						bw.append(f.getPath());
					} else
						bw.append("\n" + f.getPath());
					bw.append("\n" + f.lastModified());
				}
				bw.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		try
		{

			List<String> list = Files.readAllLines(Paths.get(modifiedFile.getPath()));
			ArrayList<String> list2 = new ArrayList<>(list);
			for (int i = 0; i < list.size(); i += 2)
			{
				if (list2.contains(p))
				{
					list2.set(list2.indexOf(p) + 1, "" + new File(p).lastModified());
				} else
				{
					list2.add(p);
					list2.add("" + new File(p).lastModified());
				}
			}
			boolean first = true;
			for (String s : list2)
			{
				if (first)
				{
					c += s;
					first = false;
				} else
					c += "\n" + s;
			}
			try
			{
				BufferedWriter bw = new BufferedWriter(new FileWriter(modifiedFile));
				bw.write(c);
				bw.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void writeToFile(String p, String c)
	{
		System.out.println("writeToFile() path: " + p);
		File f = new File(p);
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			bw.write(c);
			bw.close();
			updateModified(p);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void createHTML(String n, String c)
	{
		System.out.println("Creating " + n);
		File f = new File(System.getProperty("user.dir") + File.separator + "pageCache" + File.separator + n);
		if (!f.isFile())
		{
			try
			{
				f.createNewFile();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			bw.write(c);
			bw.close();
			updateModified(f.getPath());
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private String removePunc(String s)
	{
		String removePuncRegex = "[:\\/.-]";
		return s.replaceAll(removePuncRegex, "");
	}

	public void enterHandler(KeyEvent ke)
	{
		if (ke.getCode() == KeyCode.ENTER)
		{
			retrieve(tf.getText());
		}
	}

	public void btnHandler(ActionEvent ae)
	{
		retrieve(tf.getText());
	}

	public void openBrowser(MouseEvent me)
	{
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
		{
			try
			{
				Desktop.getDesktop().browse(new URI(hpl.getText()));
			} catch (URISyntaxException | IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void retrieve(String s)
	{
		String basepath = System.getProperty("user.dir") + File.separator + "pageCache" + File.separator;
		File dir = new File(System.getProperty("user.dir") + File.separator + "pageCache");

		ArrayList<TermHash> termHashes = new ArrayList<>();

		TermHash inputHash = new TermHash(s);
		termHashes.add(inputHash);
		TermHash totalOccurence = new TermHash("TOTAL OCCURENCE");

		//Creates hash table for the input link
		Document doc = getDoc(s);
		Element content = doc.getElementsByClass("mw-parser-output").get(0);
		Elements paragraphs = content.select("p");
		ArrayList<String> words=new ArrayList<>();
		for (Element para : paragraphs)
		{
			words.addAll(Arrays.asList(para.text().split(" ")));
		}
		fillMap(inputHash, words);


		//Adds all body content words from all documents to total occurrences
		List<File> files = new ArrayList<>(Arrays.asList(dir.listFiles()));
		files = files.stream().filter(p -> !p.getPath().equals(basepath + "lastmodified.txt")).collect(Collectors.toList());

		for (File fi : files)
		{
			TermHash tmp = new TermHash(toLink(fi));
			fillMap(tmp, getWords(fi));
			for (String str : tmp.getKeys())
			{
				if (totalOccurence.containsKey(str))
				{
					totalOccurence.get(str).occurences += tmp.get(str).occurences;
				}
				else
				{
					totalOccurence.add(str);

					totalOccurence.get(str).occurences += tmp.get(str).occurences - 1;
				}
			}
			termHashes.add(tmp);
			System.out.println("Files "+fi.getName());
		}

		//TODO COMPARISONS
		for (int i = 0; i < termHashes.size(); i++)
		{
			System.out.println("File " + i);
			for (String str : termHashes.get(i).getKeys())
			{
				termHashes.get(i).get(str).tfidf = tfidf(str, i, totalOccurence, termHashes);
				//System.out.println("TFIDF "+termHashes.get(i).get(str).item+termHashes.get(i).get(str).tfidf);
			}
		}
		//Removing the input hash from list
		termHashes.remove(0);
		TermHash closest = null;
		double mostSim = Double.MIN_VALUE;

		for (TermHash t : termHashes)
		{
			double dotproduct = 0.0;
			double magnitude1 = 0.0;
			double magnitude2 = 0.0;
			double cosineSimilarity = 0.0;
			ArrayList<String> commonWords = new ArrayList<>();
			for (String str : totalOccurence.getKeys())
			{
				if ((t.containsKey(str)) || (inputHash.containsKey(str)))
					commonWords.add(str);
			}
			//ArrayList<String> commonWords = new ArrayList<String>(totalOccurence.getKeys().stream().filter(p -> t.containsKey(p)||inputHash.containsKey(p)).collect(Collectors.toList()));
			double[] inputVec = new double[commonWords.size()];
			double[] compVec = new double[commonWords.size()];
			for (int i = 0; i < commonWords.size(); i++)
			{
				if (inputHash.get(commonWords.get(i)) != null)
					inputVec[i] = inputHash.get(commonWords.get(i)).tfidf;
				else
					inputVec[i] = 0;
				if (t.get(commonWords.get(i)) != null)
					compVec[i] = t.get(commonWords.get(i)).tfidf;
				else
					compVec[i] = 0;
			}
			for (int i = 0; i < inputVec.length; i++)
			{
				dotproduct += inputVec[i] * compVec[i];
				magnitude1 += Math.pow(inputVec[i], 2);
				magnitude2 += Math.pow(compVec[i], 2);
			}
			magnitude1 = Math.sqrt(magnitude1);
			magnitude2 = Math.sqrt(magnitude2);
			if (magnitude1 != 0.0 || magnitude2 != 0.0)
			{
				cosineSimilarity = dotproduct / (magnitude1 * magnitude2);
			} else
				cosineSimilarity = Double.MIN_VALUE;


			System.out.println(t.getLink() + " SIMILARITY: " + cosineSimilarity);
			if (cosineSimilarity > mostSim)
			{

				mostSim = cosineSimilarity;
				closest = t;
			}
		}



		hpl.setText(closest.getLink());


	}

	private double tfidf(String s, int hashIndex, TermHash totalOccurs, List<TermHash> termhashes)
	{
		double tf = tf(s, termhashes.get(hashIndex));
		double idf = idf(s, termhashes);
		return tf * idf;
	}

	private double tf(String s, TermHash h)
	{
		return ((double) h.get(s).occurences / h.totalWords());
	}

	private double idf(String s, List<TermHash> termhashes)
	{
		int total = 0, hashcontain = 0;
		for (TermHash h : termhashes)
		{
			if (h.get(s) != null)
				total += h.getOccurences(s);
			if (h.containsKey(s))
				hashcontain++;
		}
		return Math.log10(((double) termhashes.size() - 1) / (double) hashcontain);
	}

	private List<String> getWords(File f)
	{
		List<String> words = new ArrayList<>();
		Document cachedDoc = null;
		List<String> paras = new ArrayList<>();
		try
		{
			cachedDoc = Jsoup.parse(Files.readString(Paths.get(f.getPath())));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		Element body = cachedDoc.getElementsByClass("mw-parser-output").get(0);
		Elements pars = body.select("p");
		for (Element e : pars)
		{
			String str = e.text();
			words.addAll(Arrays.asList(str.split(" ")));
		}
		return words;
	}

	private void fillMap(TermHash inputHash, List<String> words)
	{
		for (String w : words)
		{
			String wo = w.toLowerCase();
			String regex = "[\\\"\\[\\]\\(\\)]";
			String st = wo.replaceAll(regex, "");
			String st2;
			if ((!st.matches("[\\/\\-\\_\\+\\=\\~\\`\\;:]")) && (!st.matches("-?[0-9]*\\.?")))
			{
				while (st.matches("\\w*\\.\\d*"))
				{
					st = st.substring(0, st.length() - 1);
				}

				if (st.matches("[A-Za-z0-9\\.]*[\\.\\,\\;:\\(\\)]"))
					st2 = st.substring(0, st.length() - 1);
				else if (st.matches("[\\[\\(]"))
					st2 = st.substring(1);
				else
					st2 = st;
				st2 = st2.replaceAll("[\\.\\,]\\d*", "");
				if (st2.matches("\\w*[\\.\\,]"))
				{
					//System.out.println("ADDING: " + st2);
					st2 = st2.substring(0, st2.length() - 2);
				}
				if (!((st2.matches("\\d")) || (st2.matches("\\d\\.\\d"))))
				{
					//System.out.println("ADDING: " + st2);
					inputHash.add(st2);
				}
			}
		}
		System.out.println("Filled "+inputHash.getLink());
	}

	private Document getDoc(String s)
	{
		return Jsoup.parse(getHTML(s));
	}

	private void createBTree(TermHash t)
	{

	}

	private String getHTML(String s)
	{
		String a = "";
		String link1 = s;
		URL url = null;
		try
		{
			url = new URL(link1);

		} catch (MalformedURLException e)
		{
			System.out.println("MALFROMED URL");
		}

		try
		{
			URLConnection urlc = url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String n;
			while ((n = br.readLine()) != null)
			{
				a += (n + System.lineSeparator());
			}
			br.close();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return a;
	}

	private ArrayList<String> crawlFile(String path, int amt)
	{
		ArrayList<String> links = new ArrayList<String>();
		String basePath = System.getProperty("user.dir") + File.separator + "pageCache" + File.separator;
		List<Element> elements;
		try
		{
			Document doc = Jsoup.parse(Files.readString(Paths.get(path)));
			Elements elems = doc.select("a[href]");
			elements = elems.stream().filter(y -> y.parents().contains(doc.getElementsByClass("mw-parser-output").get(0))).filter(p -> p.attr("href").matches("/wiki/[A-Za-z0-9_]*")).collect(Collectors.toList());//Puts all links under div mw-parser-output into elements
			boolean match = false;
			List<Element> removelist = new ArrayList();
			for (int i = 1; i < elements.size(); i++)
			{
				match = false;
				for (Element e : elements.subList(0, i))
				{
					if (elements.get(i).attr("href").equals(e.attr("href")))
						match = true;
				}
				if (match)
					removelist.add(elements.get(i));
			}
			for (Element e : removelist)
			{
				elements.remove(e);
			}
			elements = elements.stream().limit(amt).collect(Collectors.toList());
			for (Object o : elements)
			{
				Element e = (Element) o;
				links.add("https://en.wikipedia.org" + e.attr("href"));
			}

			for (String link : links)
			{
				if (Files.exists(Paths.get(basePath + removePunc(link))))
				{
					List lastModified = Files.readAllLines(Paths.get(basePath + "lastmodified.txt"));
					for (Object f : lastModified)
					{
						if (f.toString().equals(basePath + removePunc(link)))
						{
							Files.delete(Paths.get(basePath + removePunc(link)));
							createHTML(removePunc(link), getHTML(link));
						}
					}
				} else
				{
					createHTML(removePunc(link), getHTML(link));
				}
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return links;
	}
}