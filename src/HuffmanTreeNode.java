public class HuffmanTreeNode implements Comparable<HuffmanTreeNode> {
    String str;
    int amount;
    HuffmanTreeNode left;
    HuffmanTreeNode right;

    public HuffmanTreeNode(String str, int amount) {
        this.str = str;
        this.amount = amount;
    }

    public HuffmanTreeNode(HuffmanTreeNode l, HuffmanTreeNode r) {

        str=l.getStr()+r.getStr();
        amount=l.getAmount()+(r.getAmount());
        left=l;
        right=r;
    }

    public String getStr() {
        return str;
    }

    public int getAmount() {
        return amount;
    }

    public void addAmount() {
        amount++;
    }

    @Override
    public int compareTo(HuffmanTreeNode node) {
        int answ= this.amount - node.getAmount();
        if (answ==0){
            answ= (((int)this.getStr().charAt(0)) - ((int)node.getStr().charAt(0)));
        }
        return (answ);
    }

    @Override
    public String toString() {
        return
                str + " " + amount;
    }
}
