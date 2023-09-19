// --== CS400 File Header Information ==--
// Name: Ahmad Hajj
// Email: ahajj@wisc.edu
// Group and Team: <your group name: two letters, and team color>
// Group TA: <name of your group's ta>
// Lecturer: Peyman Morteza
// Notes to Grader: <optional extra notes>

import java.util.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This class extends the BaseGraph data structure with additional methods for
 * computing the total cost and list of node data along the shortest path
 * connecting a provided starting to ending nodes.  This class makes use of
 * Dijkstra's shortest path algorithm.
 */
public class DijkstraGraph<NodeType, EdgeType extends Number>
        extends BaseGraph<NodeType,EdgeType>
        implements GraphADT<NodeType, EdgeType> {

    /**
     * While searching for the shortest path between two nodes, a SearchNode
     * contains data about one specific path between the start node and another
     * node in the graph.  The final node in this path is stored in it's node
     * field.  The total cost of this path is stored in its cost field.  And the
     * predecessor SearchNode within this path is referened by the predecessor
     * field (this field is null within the SearchNode containing the starting
     * node in it's node field).
     *
     * SearchNodes are Comparable and are sorted by cost so that the lowest cost
     * SearchNode has the highest priority within a java.util.PriorityQueue.
     */
    protected class SearchNode implements Comparable<SearchNode> {
        public Node node;
        public double cost;
        public SearchNode predecessor;
        public SearchNode(Node node, double cost, SearchNode predecessor) {
            this.node = node;
            this.cost = cost;
            this.predecessor = predecessor;
        }
        public int compareTo(SearchNode other) {
            if( cost > other.cost ) return +1;
            if( cost < other.cost ) return -1;
            return 0;
        }
    }

    /**
     * This helper method creates a network of SearchNodes while computing the
     * shortest path between the provided start and end locations.  The
     * SearchNode that is returned by this method is represents the end of the
     * shortest path that is found: it's cost is the cost of that shortest path,
     * and the nodes linked together through predecessor references represent
     * all of the nodes along that shortest path (ordered from end to start).
     *
     * @param start the data item in the starting node for the path
     * @param end the data item in the destination node for the path
     * @return SearchNode for the final end node within the shortest path
     * @throws NoSuchElementException when no path from start to end is found
     *         or when either start or end data do not correspond to a graph node
     */
    protected SearchNode computeShortestPath(NodeType start, NodeType end) {
        if (!nodes.containsKey(start) || !nodes.containsKey(end)) {
            throw new NoSuchElementException("Start or end node not found in graph");
        }

        Hashtable<NodeType, SearchNode> visited = new Hashtable<>();

        PriorityQueue<SearchNode> toVisit = new PriorityQueue<>();

        toVisit.add(new SearchNode(nodes.get(start), 0, null));

        while(!toVisit.isEmpty()) {
            SearchNode current = toVisit.poll();

            if(visited.containsKey(current.node.data)) continue;

            visited.put(current.node.data, current);

            if(current.node.data.equals(end)) return current;

            for(Edge edge : current.node.edgesLeaving) {
                double cost = current.cost + edge.data.doubleValue();

                toVisit.add(new SearchNode(edge.successor, cost, current));
            }
        }

        throw new NoSuchElementException("No path from " + start.toString() + " to " + end.toString());
    }



    /**
     * Returns the list of data values from nodes along the shortest path
     * from the node with the provided start value through the node with the
     * provided end value.  This list of data values starts with the start
     * value, ends with the end value, and contains intermediary values in the
     * order they are encountered while traversing this shorteset path.  This
     * method uses Dijkstra's shortest path algorithm to find this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end the data item in the destination node for the path
     * @return list of data item from node along this shortest path
     */
    public List<NodeType> shortestPathData(NodeType start, NodeType end) {
        SearchNode path;

        try {
            path = computeShortestPath(start, end);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("No path from " + start.toString() + " to " + end.toString());
        }

        LinkedList<NodeType> pathData = new LinkedList<>();

        while(path != null) {
            pathData.addFirst(path.node.data);
            path = path.predecessor;
        }

        return pathData;
    }


    /**
     * Returns the cost of the path (sum over edge weights) of the shortest
     * path freom the node containing the start data to the node containing the
     * end data.  This method uses Dijkstra's shortest path algorithm to find
     * this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end the data item in the destination node for the path
     * @return the cost of the shortest path between these nodes
     */
    public double shortestPathCost(NodeType start, NodeType end) {
        SearchNode path = computeShortestPath(start, end);

        return path.cost;
    }

    // TODO: implement 3+ tests in step 8.
    @Test
    public void testShortestPathData() {
        DijkstraGraph<String, Integer> graph = new DijkstraGraph<>();

        graph.insertNode("1");
        graph.insertNode("2");
        graph.insertNode("3");

        graph.insertEdge("1", "2", 1);
        graph.insertEdge("2", "3", 1);
        graph.insertEdge("1", "3", 3);

        List<String> shortestPath = graph.shortestPathData("1", "3");

        List<String> expectedPath = Arrays.asList("1", "2", "3");

        assertEquals(expectedPath, shortestPath);
    }


    @Test
    public void testShortestPathCost() {
        DijkstraGraph<String, Integer> graph = new DijkstraGraph<>();

        graph.insertNode("1");
        graph.insertNode("2");
        graph.insertNode("3");

        graph.insertEdge("1", "2", 1);
        graph.insertEdge("2", "3", 1);
        graph.insertEdge("1", "3", 3);

        double shortestCost = graph.shortestPathCost("1", "3");

        double expectedCost = 2.0;

        assertEquals(expectedCost, shortestCost, 0.01);
    }


    @Test
    public void testNoPath() {
        DijkstraGraph<Integer, Integer> graph = new DijkstraGraph<>();

        graph.insertEdge(1, 2, 1);
        graph.insertEdge(2, 3, 1);

        graph.insertNode(4);

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            graph.shortestPathData(1, 4);
        });

        String expectedMessage = "No path from 1 to 4";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }





}