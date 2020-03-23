package com.damao.Transaction;

import com.damao.Utils.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class MerkleTree {
    TreeNode root = null;
    private String merkleRoot;
    private List<TreeNode> treeList;
    private List<Transaction> transactions;

    public MerkleTree(List<Transaction> transactions) {
        this.buildTree(transactions);
    }

    /**
     * 层次遍历，从下往上构建二叉树
     * @param transactions 交易集合
     */
    private void buildTree(List<Transaction> transactions) {
        treeList = new LinkedList<TreeNode>();
        this.transactions = new LinkedList<Transaction>();
        this.transactions.addAll(transactions);
        for (Transaction transaction : transactions) {
            String trans = Encryption.getSHA256(transaction.toString());
            TreeNode node = new TreeNode(trans);
            treeList.add(node);
        }
        while (!treeList.isEmpty()) {
            int length = treeList.size();
            if (length == 1) {
                root = treeList.remove(0);
                break;
            }
            if (length % 2 != 0) {
                int lastIndex = treeList.size() - 1;
                TreeNode lastTrans = treeList.get(lastIndex);
                treeList.add(lastTrans);
                ++length;
            }
            for (int i = 0; i < length; i += 2) {                /*左右子结点合并取哈希*/
                String data = Encryption.getSHA256(treeList.remove(i).toString() + treeList.remove(i + 1).toString());
                treeList.add(new TreeNode(data));
            }
        }
        this.merkleRoot = Encryption.getSHA256(root.toString());
    }

    /**
     * 比特币核心验证功能，验证一个交易是否存在
     * @param index 这个区块中的交易的索引
     * @return 验证路径
     */
    public List<String> getVerifyPath(int index) {
        Transaction transaction = transactions.get(index);
        String trans = Encryption.getSHA256(transaction.toString());
        Stack<TreeNode> stack = postOrderPath(root, trans);
        List<String> path = new ArrayList<String>();
        TreeNode thisChild = stack.pop();
        while (!stack.isEmpty()) {
            TreeNode parent = stack.pop();
            path.add(getAnotherChild(parent, thisChild).toString());
            thisChild = parent;
        }
        return path;
    }

    /**
     * 返回根结点到data == trans结点的路径
     * @param root 根结点
     * @param trans 加密的交易内容
     * @return 根结点到data == trans结点的路径
     */
    public Stack<TreeNode> postOrderPath(TreeNode root, String trans) {
        Stack<TreeNode> stack = new Stack<TreeNode>();
        TreeNode p = root;
        TreeNode r = null;
        while (p != null || !stack.isEmpty()) {
            if (p != null) {
                stack.push(p);
                p = p.left;
            } else {
                p = stack.peek();
                if (p.right != null && p.right != r) {
                    p = p.right;
                    stack.push(p);
                    p = p.left;
                } else {
                    p = stack.pop();
                    if (p.trans == trans) {
                        stack.push(p);
                        return stack;
                    }
                    r = p;
                    p = null;
                }
            }
        }
        return null;
    }

    /**
     * 给定一颗双亲结点和一个孩子节点，返回他的另一个孩子结点
     * @param root 双亲结点
     * @param thisChild 其中的一个孩子结点
     * @return 另一个孩子结点
     */
    public TreeNode getAnotherChild(TreeNode root, TreeNode thisChild) {
        if (root.left == thisChild) return root.right;
        else return root.left;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

    /* Merkle Tree结点 */
    class TreeNode {
        String trans;
        TreeNode left;
        TreeNode right;

        public TreeNode(String trans) {
            this.trans = trans;
            this.left = null;
            this.right = null;
        }

        @Override
        public String toString() {
            return "TreeNode{trans='" + trans + '\'' + ", left=" + left + ", right=" + right + '}';
        }
    }

    @Override
    public String toString() {
        return "com.damao.Transaction.MerkleTree{root=" + root + ", merkleRoot='" + merkleRoot + '\'' + ", treeList=" + treeList + ", transactions=" + transactions + '}';
    }
}
