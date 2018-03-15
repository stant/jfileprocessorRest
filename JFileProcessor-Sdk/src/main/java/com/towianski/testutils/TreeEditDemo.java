/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.testutils;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.util.EventObject;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * @see http://stackoverflow.com/a/12651990/230513
 * @see http://stackoverflow.com/a/11639595/230513
 * @see http://stackoverflow.com/a/11113648/230513
 */
public class TreeEditDemo extends JPanel {

    private JTree tree;
    private DefaultMutableTreeNode root;
    private DefaultTreeCellEditor editor;
    private JLabel label = new JLabel(" ", JLabel.CENTER);

    public TreeEditDemo() {
        super(new BorderLayout());
        root = new DefaultMutableTreeNode("Nodes");
        root.add(new DefaultMutableTreeNode(new Resource("one")));
        root.add(new DefaultMutableTreeNode(new Resource("two")));
        root.add(new DefaultMutableTreeNode(new Resource("three")));
        final DefaultTreeModel treeModel = new DefaultTreeModel(root);
        tree = new JTree(treeModel);
        tree.setEditable(true);
        editor = new MyTreeCellEditor(tree,
            (DefaultTreeCellRenderer) tree.getCellRenderer());
        tree.setCellEditor(editor);
        tree.getInputMap().put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "startEditing");
        this.add(new JScrollPane(tree));
        this.add(label, BorderLayout.SOUTH);
        tree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = e.getNewLeadSelectionPath();
                if (path != null) {
                    DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode) path.getLastPathComponent();
                    if (node.isLeaf()) {
                        Resource user = (Resource) node.getUserObject();
                        label.setText(user.toString());
                    } else {
                        label.setText(" ");
                    }
                }
            }
        });
        editor.addCellEditorListener(new CellEditorListener() {

            @Override
            public void editingStopped(ChangeEvent e) {
                label.setText(editor.getCellEditorValue().toString());
            }

            @Override
            public void editingCanceled(ChangeEvent e) {
            }
        });
    }

    private static class MyTreeCellEditor extends DefaultTreeCellEditor {

        public MyTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
            super(tree, renderer);
        }

        @Override
        public Object getCellEditorValue() {
            String value = (String) super.getCellEditorValue();
            return new Resource(value);
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            return super.isCellEditable(e)
                && ((TreeNode) lastPath.getLastPathComponent()).isLeaf();
        }
    }

    private static class Resource {

        String name;

        public Resource(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    private void display() {
        JFrame f = new JFrame("TreeEditorDemo");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(this);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new TreeEditDemo().display();
            }
        });
    }
}