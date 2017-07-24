
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

/**

 Reads a directory of XML content and writes out the abstract

 FIXME Needs major refactoring.
 Not threadsafe.

 */


public class AbstractParser {
    static long foundMatches = 0;

    static final int MAX_FILES = 500000;

    public static void main(final String[] args) {

        // System.out.println(filter("mit is great at using technology"));

        final String inputFolder = "/Users/osmandin/Downloads/eecs";
        final String output = "/Users/osmandin/code/eecs-abstracts"; // where abstracts are written

        System.out.println("Input directory:" + inputFolder);

        final File dir = new File(inputFolder);

        long fileCount = 1; // TODO, used to set a limit

        FileFilter XMLFileFilter = new FileFilter() {
            public boolean accept(File pathname) {
                String ext = FilenameUtils.getExtension(pathname.toString());
                return ext.equals("xml");
            }
        };

        FileFilter folders = new FileFilter() {
            public boolean accept(File pathname) {
                return !FilenameUtils.getExtension(pathname.toString()).equals(".DS_Store");
            }
        };

        for (final File thesis_folder : dir.listFiles(folders)) {

            final File[] xmlFiles = thesis_folder.listFiles(XMLFileFilter);

            if (xmlFiles == null) {
                continue;
            }

            for (final File f: xmlFiles) {
                runAnalysis(f, new File(output + "/" + fileCount + "/" + "abstract.txt"));
                fileCount++;
            }

            if (fileCount > MAX_FILES) {
                break;
            }
        }

        System.out.println("Number of documents with abstracts:" + foundMatches);
    }


    private static void runAnalysis(final File f, final File output) {
        try {
            final DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            final Document doc = dBuilder.parse(f);
            final NodeList nl = doc.getDocumentElement().getChildNodes();

            for (int k = 0; k < nl.getLength(); k++) {
                final String abstracText = printTags(nl.item(k));

                // now write text to file

                // create the directory:  //TODO this could run into linux filesystem limits

                FileUtils.writeStringToFile(output, abstracText, "UTF-8", true); // TODO

            }
        } catch (Exception e) {
            e.printStackTrace(); //TODO
        }
    }

    private static String printTags(final Node nodes) {

        final StringBuffer abstractText = new StringBuffer();

        // TODO use xpath

        if (nodes.hasChildNodes() || nodes.getNodeType() != 3) {
            //System.out.println(nodes.getNodeName());

            if (nodes.getNodeName().equals("mods:abstract")) {
                foundMatches++;
                return nodes.getTextContent();
            }

            final NodeList nl = nodes.getChildNodes();
            for (int j = 0; j < nl.getLength(); j++) {
                abstractText.append(printTags(nl.item(j)));

            }
        }

        return filter(abstractText.toString());
    }

    static String[] searchList = {"new", "thesis", "using", "high", "high", "mit",
    "based", "likely", "large", "small", "true", "false", "used",
    "final", "rate", "end", "result", "algorithm", "algorithms", "order",
    "general", "solution", "set", "study", "university",
    "make", "different", "allows", "problem", "use", "method", "start", "end",
    "time", "approximately", "measure", "results", "end", "effort", "abstract",
    "analysis", "create", "created", "devise", "techniques", "various", "new", "novel", "related", "relevant",
    "obtained", "created", "beginning", "start", "stop", "test", "valid", "work", "applied"}; //TODO need more sophisticated approach

    static List<String> stopWords = Arrays.asList(searchList);

    //TODO need a better filtering mechanism as XML is read

    private static String filter(final String abstractText) {

        final StringBuffer modifiedText = new StringBuffer();

        final String[] splited = abstractText.split("\\s+");

        for (final String s : splited) {
            if (!stopWords.contains(s)) {
                modifiedText.append(s + " "); // TODO
            }
        }

        return modifiedText.toString();
    }

}
