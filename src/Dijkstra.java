import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;


/**
 * An Adjacency Matrix File reader for un-weighted graphs using the BFS method and Dijkstra algorithm.
 *
 * @author Ankit Ahlwat
 * @version 2023
 */
public class Dijkstra {
    private String[] stringArray;
    private ArrayList<DijkstraNode> unVisited;

    private HashMap<Integer, DijkstraNode> dijkstraNodeHashMap;

    /**
     * Instantiates a new Dijkstra object.
     */
    public Dijkstra() {
    }

    /**
     * Read strings from filepath.
     *
     * @param filePath the file path
     */
    public void readStrings(final String filePath) {
        try {
            String stringFromFile = Files.readString(Path.of(filePath));
            makeAdjacencyList(stringFromFile);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Could Not Find File, Please Try Again");
        }
    }

    private void makeAdjacencyList(String stringFromFile) {
        stringFromFile = stringFromFile.replace("(", "");
        stringFromFile = stringFromFile.replace(")", "");
        stringFromFile = stringFromFile.replace(" ", "");
        stringFromFile = stringFromFile.replace(",", "");
        stringArray = stringFromFile.split("\n");
    }

    /**
     * Find the shortest paths using the BFS method.
     *
     * @param startingNode the starting node
     * @param endingNode   the ending node
     * @throws ArrayIndexOutOfBoundsException if startingNode or endingNode not in range.
     */
    public void findShortestPathsBFS(final int startingNode, final int endingNode) {
        if (endingNode >= stringArray.length || startingNode < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        unVisited = new ArrayList<>();
        dijkstraNodeHashMap = new HashMap<>();
        makeNodeList();
        dijkstraNodeHashMap.get(startingNode).setLengthFromStart(0);
        Deque<DijkstraNode> dijkstraNodeDeque = new LinkedList<>();
        dijkstraNodeDeque.add(dijkstraNodeHashMap.get(startingNode));
        while (!dijkstraNodeDeque.isEmpty()) {
            DijkstraNode currentNode = dijkstraNodeDeque.peek();
            int currentNodePosition = currentNode.getInt();
            for (int i = 0; i < stringArray.length; i++) {
                if (i == currentNodePosition) {
                    continue;
                }
                if (stringArray[currentNodePosition].charAt(i) == '1'
                        && unVisited.contains(dijkstraNodeHashMap.get(i))) {
                    DijkstraNode nextNode = dijkstraNodeHashMap.get(i);
                    if (!dijkstraNodeDeque.contains(nextNode)) {
                        dijkstraNodeDeque.add(nextNode);
                    }
                    updateShortestPathList(currentNode, nextNode);
                }
            }
            unVisited.remove(dijkstraNodeDeque.poll());
        }
        checkIfPathExists(dijkstraNodeHashMap.get(endingNode));
    }

    private void makeNodeList() {
        for (int i = 0; i < stringArray.length; i++) {
            DijkstraNode currentNode = new DijkstraNode(i);
            unVisited.add(currentNode);
            dijkstraNodeHashMap.put(i, currentNode);
        }
    }

    private void updateShortestPathList(final DijkstraNode currentNode, final DijkstraNode nextNode) {
        int length = 1 + currentNode.getLengthFromStart();
        if (nextNode.getLengthFromStart() == null || length == nextNode.getLengthFromStart()) {
            nextNode.setLengthFromStart(length);
            nextNode.getPreviousList().add(currentNode);
        } else if (length < nextNode.getLengthFromStart()) {
            nextNode.getPreviousList().clear();
            nextNode.getPreviousList().add(currentNode);
        }
    }

    private void checkIfPathExists(final DijkstraNode endingNode) {
        if (endingNode.getPreviousList().size() != 0) {
            connectNodes(endingNode);
        } else {
            System.out.println("No path exists");
        }
    }

    private void connectNodes(final DijkstraNode currentNode) {
        if (currentNode.getPreviousList().size() == 0) {
            printPaths(currentNode);
        } else {
            for (DijkstraNode prevNode : currentNode.getPreviousList()) {
                prevNode.getNextList().add(0, currentNode);
                connectNodes(prevNode);
            }
        }
    }

    private void printPaths(DijkstraNode node) {
        while (node.getNextList().size() != 0) {
            System.out.printf("%s -> ", node);
            node = node.getNextList().get(0);
        }
        System.out.println(node);
    }
}
