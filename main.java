
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class main {

    public static void main(String[] args) {
        
        boolean full = false;
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

        // Find the largest connected component
        Set<String> visited = new HashSet<>();
        int largestSize = 0;
        Set<String> largestComponent = new HashSet<>();

        for (String node : graph.keySet()) {
            if (!visited.contains(node)) {
                Set<String> component = new HashSet<>();
                findComponent(graph, node, visited, component);
                if (component.size() > largestSize) {
                    largestSize = component.size();
                    largestComponent = component;
                }
            }
        }

        // Output the result
        System.out.println("Largest connected component size: " + largestSize);
        System.out.println("Largest connected component: " + largestComponent);
        System.out.println("Number of t triangles: " + count);
    }

    private static void findComponent(Map<String, Set<String>> graph, String node, Set<String> visited, Set<String> component) {
        if (visited.contains(node)) {
            return;
        }
        visited.add(node);
        component.add(node);
        for (String neighbor : graph.get(node)) {
            findComponent(graph, neighbor, visited, component);
        }
    }
}