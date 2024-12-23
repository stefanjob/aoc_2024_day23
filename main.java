
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class main {

    public static void main(String[] args) {
        
        boolean full = true;
        Scanner scanner = null;

        ArrayList<String> cons = new ArrayList<>();
        
        try {
            if (full) {
                scanner = new Scanner(new File("input_full.txt"));
            } else {
                scanner = new Scanner(new File("input_test.txt"));
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
 
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.println(line);
            cons.add(line);
        }

        // Parse the input into a graph
        Map<String, Set<String>> graph = new HashMap<>();

        for (String connection : cons) {
            String[] parts = connection.split("-");
            String node1 = parts[0];
            String node2 = parts[1];
            graph.putIfAbsent(node1, new HashSet<>());
            graph.putIfAbsent(node2, new HashSet<>());
            graph.get(node1).add(node2);
            graph.get(node2).add(node1);
        }

        // Find all triangles
        Set<Set<String>> triangles = new HashSet<>();
        for (String node : graph.keySet()) {
            for (String neighbor1 : graph.get(node)) {
                for (String neighbor2 : graph.get(node)) {
                    if (!neighbor1.equals(neighbor2) && graph.get(neighbor1).contains(neighbor2)) {
                        // Found a triangle
                        Set<String> triangle = new TreeSet<>(Arrays.asList(node, neighbor1, neighbor2));
                        triangles.add(triangle);
                    }
                }
            }
        }

        // Filter triangles to include only those with at least one 't'-starting node
        int count = 0;
        for (Set<String> triangle : triangles) {
            boolean containsTNode = triangle.stream().anyMatch(node -> node.startsWith("t"));
            if (containsTNode) {
                count++;
                System.out.println(triangle);
            }
        }
        System.out.println("Number of t triangles: " + count);

        /* Find the largest clique - brute force - runs forever...
        Set<String> largestClique = new HashSet<>();
        for (String node : graph.keySet()) {
            Set<String> clique = new HashSet<>();
            findClique(graph, node, clique, largestClique);
        }
        

        // Output the result
        System.out.println("Largest clique size: " + largestClique.size());
        System.out.println("Largest clique: " + largestClique);
        */

        // Find the largest clique using Bron-Kerbosch algorithm
        Set<String> largestClique = findLargestClique(graph);
        System.out.println("Largest clique size: " + largestClique.size());
        System.out.println("Largest clique: " + largestClique);

        Object compis[] = largestClique.toArray();
        Arrays.sort(compis);
        String password = "";
        for (Object o : compis) {  
            password = password.concat((String)o);
            password = password.concat(",");
        }
        System.out.println("Password: " + password.substring(0,password.length()-1));
    }

    public static Set<String> findLargestClique(Map<String, Set<String>> graph) {
        Set<String> largestClique = new HashSet<>();
        bronKerbosch(new HashSet<>(), new HashSet<>(graph.keySet()), new HashSet<>(), graph, largestClique);
        return largestClique;
    }

    private static void bronKerbosch(Set<String> R, Set<String> P, Set<String> X, Map<String, Set<String>> graph, Set<String> largestClique) {
        if (P.isEmpty() && X.isEmpty()) {
            // R is a maximal clique
            if (R.size() > largestClique.size()) {
                largestClique.clear();
                largestClique.addAll(R);
            }
            return;
        }

        // Choose a pivot to minimize branching
        String pivot = choosePivot(P, X, graph);
        Set<String> nonNeighbors = new HashSet<>(P);
        nonNeighbors.removeAll(graph.getOrDefault(pivot, Collections.emptySet()));

        // Explore non-neighbors of the pivot
        for (String node : nonNeighbors) {
            Set<String> newR = new HashSet<>(R);
            newR.add(node);

            Set<String> newP = new HashSet<>(P);
            newP.retainAll(graph.getOrDefault(node, Collections.emptySet()));

            Set<String> newX = new HashSet<>(X);
            newX.retainAll(graph.getOrDefault(node, Collections.emptySet()));

            bronKerbosch(newR, newP, newX, graph, largestClique);

            P.remove(node);
            X.add(node);
        }
    }

    private static String choosePivot(Set<String> P, Set<String> X, Map<String, Set<String>> graph) {
        String pivot = null;
        int maxDegree = -1;
        Set<String> union = new HashSet<>(P);
        union.addAll(X);

        for (String node : union) {
            int degree = graph.getOrDefault(node, Collections.emptySet()).size();
            if (degree > maxDegree) {
                maxDegree = degree;
                pivot = node;
            }
        }

        return pivot;
    }

    private static void findClique(Map<String, Set<String>> graph, String node, Set<String> clique, Set<String> largestClique) {
        // Add the current node to the clique
        clique.add(node);

        // Check if the current clique is valid
        if (isClique(graph, clique)) {
            // Update the largest clique if necessary
            if (clique.size() > largestClique.size()) {
                largestClique.clear();
                largestClique.addAll(clique);
            }

            // Explore neighbors for further clique expansion
            for (String neighbor : graph.get(node)) {
                if (!clique.contains(neighbor)) {
                    findClique(graph, neighbor, clique, largestClique);
                }
            }
        }

        // Backtrack
        clique.remove(node);
    }

    private static boolean isClique(Map<String, Set<String>> graph, Set<String> clique) {
        for (String node1 : clique) {
            for (String node2 : clique) {
                if (!node1.equals(node2) && !graph.get(node1).contains(node2)) {
                    return false;
                }
            }
        }
        return true;
    }
}