package cc.mallet.topics;

/**
 * Created by wlu on 26.07.16.
 */
public class HierarchicalLdaUtils {
    private HierarchicalLDA hlda;

    public HierarchicalLdaUtils(HierarchicalLDA hlda) {
        this.hlda = hlda;
    }



    public String nodesAsString() {
        StringBuffer result = new StringBuffer();
        nodeAsString(hlda.rootNode, 0, result);
        return result.toString();
    }

    public void nodeAsString(HierarchicalLDA.NCRPNode node, int indent, StringBuffer result) {
        for (int i=0; i<indent; i++) {
            result.append("  ");
        }

        result.append(node.totalTokens + "/" + node.customers + " ");
        result.append(node.getTopWords(hlda.numWordsToDisplay, false));
        result.append(System.lineSeparator());

        for (HierarchicalLDA.NCRPNode child : node.children) {
            nodeAsString(child, indent + 1, result);
        }
    }
}
