/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package suffix_ukkonen;

/**
 *
 * @author ali
 */
public class Suffix_Ukkonen {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Ukkonen_Al suffixTree = new Ukkonen_Al();
        suffixTree.process("abca$");
        // suffixTree.process("cacao$")
    }

}
