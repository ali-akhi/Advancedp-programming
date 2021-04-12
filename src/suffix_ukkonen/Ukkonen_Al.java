/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package suffix_ukkonen;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author ali
 */
public class Ukkonen_Al {

    public static boolean DETAILED = true;
    public static char UNIQUE = '$';
    private String phrase;
    private Node root;
    private int leafEnd;
    private Node internalNode;
    private Node activeNode;
    private int activeEdge;
    private int activeLength;
    private int remainingSuffixCount;

    public Ukkonen_Al() {
        root = new Node(-1);
        leafEnd = -1;
        activeNode = root;
        activeEdge = -1;
        activeLength = 0;
        remainingSuffixCount = 0;
    }

    public void process(String str) {
        if (str.indexOf(UNIQUE) != str.length() - 1) {
            throw new IllegalArgumentException("String must end in the unique character $");
        }
        phrase = str;
        for (int i = 0; i < phrase.length(); i++) {
            if (DETAILED) {
                System.out.println("----------Step " + (i + 1) + "-------------");
            }
            processIndex(i);
            if (DETAILED) {
                printTree();
            }
        }
        if (!DETAILED) {
            printTree();
        }
    }
    private void processIndex(int index) {
        if (DETAILED) {
            System.out.println("Rule One/Trick Three");
        }
        leafEnd = index;
        remainingSuffixCount++;
        internalNode = null;
        while (remainingSuffixCount > 0) {
            if (activeLength == 0) {
                if (DETAILED) {
                    System.out.println("ALZ");
                }
                activeEdge = index;
                if (DETAILED) {
                    printActivePoint();
                }
            }

            if (!activeNode.edgeMap.containsKey(phrase.charAt(activeEdge))) {
                if (DETAILED) {
                    System.out.println("Rule Two: new leaf");
                }
                activeNode.edgeMap.put(phrase.charAt(activeEdge), new Node(index));

                if (internalNode != null) {
                    if (DETAILED) {
                        System.out.println("Set up suffixLink from last internal node to activeNode");
                    }
                    internalNode.suffixLink = activeNode;
                    internalNode = null;
                }

            } else {

                Node next = activeNode.edgeMap.get(phrase.charAt(activeEdge));
                if (DETAILED) {
                    System.out.println("Trick One: Skip/Count");
                }
                if (walkedDown(next)) {
                    continue;
                }
                if (phrase.charAt(next.start + activeLength) == phrase.charAt(index)) {
                    if (DETAILED) {
                        System.out.println("Rule Three");
                    }
                    if (internalNode != null && activeNode != root) {
                        internalNode.suffixLink = activeNode;
                        internalNode = null;
                    }
                    if (DETAILED) {
                        System.out.println("ER3");
                    }
                    activeLength++;
                    if (DETAILED) {
                        printActivePoint();
                    }
                    if (DETAILED) {
                        System.out.println("Trick Two");
                    }
                    break;
                }
                if (DETAILED) {
                    System.out.println("Rule Two: Split Node");
                }
                int splitEnd = next.start + activeLength - 1;
                Node split = new Node(next.start, splitEnd);
                activeNode.edgeMap.put(phrase.charAt(activeEdge), split);
                split.edgeMap.put(phrase.charAt(index), new Node(index));
                next.start += activeLength;
                split.edgeMap.put(phrase.charAt(next.start), next);

                if (internalNode != null) {
                    if (DETAILED) {
                        System.out.println("Set up suffixLink from last internal node to this newly created one");
                    }
                    internalNode.suffixLink = split;
                }
                internalNode = split;
            }

            remainingSuffixCount--;
            if (activeNode == root && activeLength > 0) {
                if (DETAILED) {
                    System.out.println("ER2C1");
                }
                activeLength--;
                activeEdge = index - remainingSuffixCount + 1;
                if (DETAILED) {
                    printActivePoint();
                }
            } else if (activeNode != root) {
                if (DETAILED) {
                    System.out.println("ER2C2");
                }
                activeNode = activeNode.suffixLink;
                if (DETAILED) {
                    printActivePoint();
                }
            }
        }
    }
    private boolean walkedDown(Node n) {
        if (activeLength >= edgeLength(n)) {
            if (DETAILED) {
                System.out.println("WD");
            }
            activeNode = n;
            activeEdge += edgeLength(n);
            activeLength -= edgeLength(n);
            if (DETAILED) {
                printActivePoint();
            }
            return true;
        }

        return false;
    }

    private int edgeLength(Node n) {
        return n.end != -1 ? n.end - n.start + 1 : leafEnd - n.start + 1;
    }
    public void dfsSetAndPrint(Node n, int labelHeight) {
        if (n == null) {
            return;
        }
        if (n.start != -1) {
            System.out.print(n);
        }
        boolean isLeaf = true;
        for (Entry<Character, Node> entry : n.edgeMap.entrySet()) {
            Node node = entry.getValue();
            if (isLeaf && n.start != -1) {
                System.out.println(" " + n.suffixIndex);
            }
            isLeaf = false;
            dfsSetAndPrint(node, labelHeight + edgeLength(node));
        }
        if (isLeaf) {
            n.suffixIndex = phrase.length() - labelHeight;
            System.out.println(" " + n.suffixIndex);
        }
    }
      class Node {
    int start;
    int end;
    Map<Character, Node> edgeMap;
    Node suffixLink;
    int suffixIndex;

    Node(int start) {
      this.start = start;
      this.end = -1;
      edgeMap = new HashMap<>();
      suffixLink = root;
      suffixIndex = -1;
    }
    Node(int start, int end) {
      this.start = start;
      this.end = end;
      edgeMap = new HashMap<>();
      suffixLink = root;
      suffixIndex = -1;
    }
    @Override
    public String toString() {
      return end == -1 ? phrase.substring(start, leafEnd+1) : phrase.substring(start, end+1);
    }
  }
  private void printActivePoint() {
    StringBuilder sb = new StringBuilder();
    sb.append("ActivePoint is (");
    if (activeNode.start == -1) {
      sb.append("root, ");
    } else {
      sb.append(phrase.substring(activeNode.start, activeNode.start+1));
      sb.append(", ");
    }
    sb.append(phrase.substring(activeEdge, activeEdge+1));
    sb.append(", ");
    sb.append(activeLength).append(")");
    System.out.println(sb.toString());
  }
  public void printTree() {
    dfsSetAndPrint(root, 0);
    if (DETAILED) {
      printActivePoint();
    }
  }
}
