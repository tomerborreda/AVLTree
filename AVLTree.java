/**
 *
 * AVLTree
 *
 * An implementation of a AVL Tree with
 * distinct integer keys and info
 *
 */

public class AVLTree {


	private IAVLNode rootNode = new AVLNode(); // The node which is the root of the tree

	private IAVLNode minNode = null; //The node which contains the minimum key in the tree.
	private IAVLNode maxNode = null; //The node which contains the maximum key in the tree.

	/*
	 * 
	 * No constructor. Use the default constructor.
	 * 
	 */

	/*    Variables for keysToArray recursion function   */
	private static int[] keysArr;
	private static int keysArrIndex;
	/*    Variables for infoToArray recursion function   */
	private static String[] infoArr;
	private static int infoArrIndex;

	/*/***************************************************/


	/**
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty
	 *
	 * Complexity: O(1)
	 */
	public boolean empty() {
		/*
		 *  Check whether the root node is virtual or not.
		 *  If the root is virtual then the tree is empty.
		 *  Otherwise, there is a real node in the tree.
		 */
		return !this.rootNode.isRealNode();
	}

	/**
	 * public String search(int k)
	 *
	 * returns the info of an item with key k if it exists in the tree
	 * otherwise, returns null
	 * 
	 * Complexity: O(h)=O(log(n))
	 */
	public String search(int k)
	{
		if (empty() == true) {return null;} //If the tree is empty then k is not in the tree.
		IAVLNode y = treePosition(this.rootNode, k); //Get the last node on the path to k.
		if (y.getKey() != k) {
			return null;
		}
		else {
			return y.getValue();
		}
	}


	/**
	 * public int insert(int k, String i)
	 *
	 * inserts an item with key k and info i to the AVL tree.
	 * the tree must remain valid (keep its invariants).
	 * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
	 * returns -1 if an item with key k already exists in the tree.
	 * 
	 * Complexity: O(h)=O(log(n))
	 */
	public int insert(int k, String i) {
		if(k < 1) { //Insert k only if k is a natural number
			return 0;
		}
		IAVLNode newNode = new AVLNode(k, i, null); //Create new node (this node is a leaf with no parent)
		if(this.empty()) { //Empty Tree
			this.rootNode = newNode;
			this.maxNode = rootNode;
			this.minNode = rootNode;
			return 0;
		}

		if(treeInsert(this.rootNode, newNode)) { //Inserted successfully.
			//Check if the new node has a maximal or minimal key in the tree and update accordingly.
			if(newNode.getKey() > maxNode.getKey())
				maxNode = newNode;
			else if(newNode.getKey() < minNode.getKey())
				minNode = newNode;
			//Rotate and update size, height and sum if necessary.
			return rotateAndUpdateAfterInsert(newNode);
		}
		else //found the inserted key, nothing happened.
			return -1;
	}

	/**
	 * public int delete(int k)
	 *
	 * deletes an item with key k from the binary tree, if it is there;
	 * the tree must remain valid (keep its invariants).
	 * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
	 * returns -1 if an item with key k was not found in the tree.
	 * 
	 * Complexity: O(h)=O(log(n))
	 */
	public int delete(int k)
	{
		if(k < 1) { //If k is not a natural number then it could not have been inserted.
			return -1;
		}
		if(this.empty()) { //Empty Tree, k was not found.
			return -1;
		} else if(isLeaf(rootNode) && rootNode.getKey()==k) { //Deleting a "root tree" returns a tree with only a virtual node.
			rootNode = new AVLNode();
			minNode = null;
			maxNode = null;
			return 0;
		}
		IAVLNode node = treePosition(rootNode, k); //Get the last node on the path to k.
		if(node.getKey() == k) { //k was found
			//Check if the node is a maximal or minimal key in the tree and update accordingly.
			if(k == maxNode.getKey())
				maxNode = predecessor(node);
			else if(k == minNode.getKey())
				minNode = successor(node);

			IAVLNode parent = bstDelete(node); //Delete the node as in a Binary Search Tree.

			//Rotate and update size, height and sum if necessary.
			return rotateAndUpdateAfterDelete(parent); 
		}
		else //k was not found
			return -1;
	}

	/**
	 * public String min()
	 *
	 * Returns the info of the item with the smallest key in the tree,
	 * or null if the tree is empty
	 * 
	 * Complexity: O(1)
	 */
	public String min()
	{
		if(!empty())
			return this.minNode.getValue();
		else
			return null;
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree,
	 * or null if the tree is empty
	 * 
	 * Complexity: O(1)
	 */
	public String max()
	{
		if(!empty())
			return this.maxNode.getValue();
		else
			return null;
	}


	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree,
	 * or an empty array if the tree is empty.
	 * 
	 * Complexity: O(n)
	 */
	public int[] keysToArray()
	{
		if (empty() == true) {return new int[] {};}
		keysArr = new int[size()];
		keysArrIndex = 0;

		/*
		 * This method is an envelope function for inOrderKeys recursion function.
		 * inOrderKeys changes keysArr to a sorted array of all keys in the tree.
		 */
		inOrderKeys(this.getRoot());

		return keysArr.clone();
	}

	/**
	 * public String[] infoToArray()
	 *
	 * Returns an array which contains all info in the tree,
	 * sorted by their respective keys,
	 * or an empty array if the tree is empty.
	 * 
	 * Complexity: O(n)
	 */
	public String[] infoToArray()
	{
		if (empty() == true) {return new String[] {};}
		infoArr = new String[size()];
		infoArrIndex = 0;

		/*
		 * This method is an envelope function for inOrderInfo recursion function.
		 * inOrderInfo changes infoArr to a sorted array of all info in the tree.(sorted by the keys)
		 */
		inOrderInfo(this.getRoot());

		return infoArr.clone();
	}

	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 *
	 * precondition: none
	 * postcondition: none
	 * 
	 * Complexity: O(1)
	 */
	public int size()
	{
		return this.rootNode.getSubtreeSize();
	}

	/**
	 * public int less(int i)
	 *
	 * Returns the sum of all keys which are less or equal to i
	 * i is not neccessarily a key in the tree 	
	 *
	 * precondition: none
	 * postcondition: none
	 */   
	public int less(int i)
	{
		if(empty()) { return 0; } //If the tree is empty then the sum must be zero.

		/* If i is less than the minimum key / is the minimum key / is greater or equal to the maximum key, then the sum is known */
		if (i < this.minNode.getKey()) {return 0;}
		if (i == this.minNode.getKey()) {return this.minNode.getKey();}
		if (i >= this.maxNode.getKey()) {return this.getRoot().getSum();}

		// Get the sum of keys which are less than or equal to i.
		return treeLess(i);
	}

	/**
	 * public string select(int i)
	 *
	 * Returns the value of the i'th smallest key (return null if tree is empty)
	 * Example 1: select(1) returns the value of the node with minimal key 
	 * Example 2: select(size()) returns the value of the node with maximal key 
	 * Example 3: select(2) returns the value 2nd smallest minimal node, i.e the value of the node minimal node's successor 	
	 *
	 * precondition: size() >= i > 0
	 * postcondition: none
	 * 
	 * Complexity: O(log k)
	 */   
	public String select(int i)
	{
		if (empty() == true) {return null;}
		if (i < 1) {return null;}
		if (i == 1) {return min();}
		if (i == size()) {return max();}
		if (i > size()) {return null;}

		Double temp = Math.ceil(Math.log(i)/Math.log((1+Math.sqrt(5))/2));
		int top = temp.intValue();
		IAVLNode curNode = this.minNode;
		for (int j = 1; j <= top; j++) {
			if (curNode.getKey()!=this.rootNode.getKey()) {
				curNode = curNode.getParent();
			}
		}
		return treeSelect(curNode, i); 
	}

	/**
	 * public int getRoot()
	 *
	 * Returns the root AVL node, or null if the tree is empty
	 *
	 * precondition: none
	 * postcondition: none
	 * 
	 * Complexity: O(1)
	 */
	public IAVLNode getRoot()
	{
		if(!empty())
			return this.rootNode;
		else 
			return null;
	}


	/*/*************************************************
	 * 					Rotations Methods			   *
	 ***************************************************/

	/*
	 * Additional values such as size, height and sum are updated only in the following function.
	 */

	/**
	 * 
	 * @param node is the top node of the two that will get rotated
	 * precondition: node has a right child
	 * 
	 * performs a left rotation and update additional node values.
	 * 
	 * Complexity: O(1)
	 */
	private void rotateLeft(IAVLNode node) {
		IAVLNode parent = node.getParent();
		IAVLNode right = node.getRight(); //x
		IAVLNode left = right.getLeft(); //B

		/* Update reference of the nodes children and parent */
		right.setLeft(node);
		right.setParent(parent);
		node.setParent(right);
		node.setRight(left);

		left.setParent(node);

		if(parent==null) { //The node is root
			rootNode = right;
			rootNode.setParent(null);
		}
		else {
			if(parent.getLeft().getKey() == node.getKey()) //node is the left child
				parent.setLeft(right);
			else if(parent.getRight().getKey() == node.getKey()) //node is the right child
				parent.setRight(right);
			else //node has no parent but is not the root - error
				throw new RuntimeException("Has no parent. key:" + node.getKey());
		}

		/* Update size, height and sum of the two rotated nodes */
		node.setSubtreeSize(1+node.getLeft().getSubtreeSize()+node.getRight().getSubtreeSize());
		node.setHeight(1+Math.max(node.getLeft().getHeight(), node.getRight().getHeight()));

		right.setSubtreeSize(1+right.getLeft().getSubtreeSize()+right.getRight().getSubtreeSize());
		right.setHeight(1+Math.max(right.getLeft().getHeight(), right.getRight().getHeight()));

		((AVLNode) node).setSum(((AVLNode) node.getLeft()).getSum()+((AVLNode) node.getRight()).getSum()+node.getKey());
		((AVLNode) right).setSum(((AVLNode) right.getLeft()).getSum()+((AVLNode) right.getRight()).getSum()+right.getKey());
	}

	/**
	 * 
	 * @param node is the top node of the two that will get rotated
	 * precondition: node has a left child
	 * 
	 * performs a right rotation and update additional node values.
	 * 
	 * Complexity: O(1)
	 */
	private void rotateRight(IAVLNode node) {
		IAVLNode parent = node.getParent();
		IAVLNode left = node.getLeft(); //y
		IAVLNode right = left.getRight(); //B

		/* Update reference of the nodes children and parent */
		left.setRight(node);
		left.setParent(parent);
		node.setParent(left);
		node.setLeft(right);

		right.setParent(node);

		if(parent==null) { //node is root
			rootNode = left;
			rootNode.setParent(null);
		}
		else {
			if(parent.getLeft().getKey() == node.getKey()) //node is the left child
				parent.setLeft(left);
			else if(parent.getRight().getKey() == node.getKey()) //node is the right child
				parent.setRight(left);
			else //node has no parent but is not the root - error
				throw new RuntimeException("Has no parent. key:" + node.getKey());
		}

		/* Update size, height and sum of the two rotated nodes */
		node.setSubtreeSize(1+node.getLeft().getSubtreeSize()+node.getRight().getSubtreeSize());
		node.setHeight(1+Math.max(node.getLeft().getHeight(), node.getRight().getHeight()));

		left.setSubtreeSize(1+left.getLeft().getSubtreeSize()+left.getRight().getSubtreeSize());
		left.setHeight(1+Math.max(left.getLeft().getHeight(), left.getRight().getHeight()));

		((AVLNode) node).setSum(((AVLNode) node.getLeft()).getSum()+((AVLNode) node.getRight()).getSum()+node.getKey());
		((AVLNode) left).setSum(((AVLNode) left.getLeft()).getSum()+((AVLNode) left.getRight()).getSum()+left.getKey());
	}


	/**
	 * 
	 * @param newNode - A node which was just inserted to the tree.
	 * @return Number of rotations.
	 * 
	 * Complexity: O(h)=O(log(n))
	 */
	private int rotateAndUpdateAfterInsert(IAVLNode newNode) {
		/* Update until a rotation is needed and then rotate (only if needed) */
		int numOfRot = 0;

		IAVLNode newNodeParent = newNode.getParent();

		while(newNodeParent!=null) {
			newNodeParent.setSubtreeSize(newNodeParent.getSubtreeSize()+1);
			boolean hasHeightUpdated = updateHeightIfNeeded(newNode, newNodeParent);
			((AVLNode) newNodeParent).setSum(((AVLNode) newNodeParent.getLeft()).getSum()+((AVLNode) newNodeParent.getRight()).getSum()+newNodeParent.getKey());

			int parentBF = BF(newNodeParent);
			if(parentBF<2 && parentBF>-2) {//|BF|<2
				if(hasHeightUpdated) { //parent height has changed
					newNode = newNodeParent;
					newNodeParent = newNode.getParent();
				}
				else { //parent height is the same
					numOfRot= 0; //No rotation were made;
					break;
				}
			}
			else { //|BF|=2

				if(parentBF==2) {
					if(BF(newNodeParent.getLeft())==1) { //LL
						rotateRight(newNodeParent);
						numOfRot = 1;
						break;
					}
					else if(BF(newNodeParent.getLeft())==-1) { //LR
						rotateLeft(newNode);
						rotateRight(newNodeParent);
						numOfRot = 2;
						break;
					}
				}
				else if(parentBF==-2) {
					if(BF(newNodeParent.getRight())==-1) { //RR
						rotateLeft(newNodeParent);
						numOfRot = 1;
						break;
					}
					else if(BF(newNodeParent.getRight())==1) { //RL
						rotateRight(newNode);
						rotateLeft(newNodeParent);
						numOfRot = 2;
						break;
					}
				}
			}
		}

		/* Update the rest of the tree path(to root) size, height and sum */
		if(newNodeParent!=null && newNodeParent.getParent()!=null) {
			newNode = newNodeParent;
			newNodeParent = newNode.getParent();

			while(newNodeParent!=null) {
				newNodeParent.setSubtreeSize(newNodeParent.getLeft().getSubtreeSize()+newNodeParent.getRight().getSubtreeSize()+1);
				updateHeightIfNeeded(newNode, newNodeParent);
				((AVLNode) newNodeParent).setSum(((AVLNode) newNodeParent.getLeft()).getSum()+((AVLNode) newNodeParent.getRight()).getSum()+newNodeParent.getKey());

				newNode = newNodeParent;
				newNodeParent = newNode.getParent();
			}
		}

		return numOfRot;
	}

	/**
	 * 
	 * @param parent - The parent of the node which was just deleted.
	 * @return Number of rotations.
	 * 
	 * Complexity: O(h)=O(log(n))
	 */
	private int rotateAndUpdateAfterDelete(IAVLNode parent) {
		IAVLNode firstParent = parent;

		/* Update and rotate as needed */
		int numOfRot = 0;

		if(parent!=null) {
			if(parent.getLeft().isRealNode()) {
				parent.getLeft().setSubtreeSize(parent.getLeft().getLeft().getSubtreeSize()+parent.getLeft().getRight().getSubtreeSize()+1);
				updateHeightIfNeeded_Deletion(parent.getLeft());
				((AVLNode) parent.getLeft()).setSum(((AVLNode) parent.getLeft().getLeft()).getSum()+((AVLNode) parent.getLeft().getRight()).getSum()+parent.getLeft().getKey());
			}
			if(parent.getRight().isRealNode()) {
				parent.getRight().setSubtreeSize(parent.getRight().getLeft().getSubtreeSize()+parent.getRight().getRight().getSubtreeSize()+1);
				updateHeightIfNeeded_Deletion(parent.getRight());
				((AVLNode) parent.getRight()).setSum(((AVLNode) parent.getRight().getLeft()).getSum()+((AVLNode) parent.getRight().getRight()).getSum()+parent.getRight().getKey());
			}
		}
		else
			return 0;

		if(BF(parent)<2 && BF(parent)>-2) {
			parent.setSubtreeSize(parent.getLeft().getSubtreeSize()+parent.getRight().getSubtreeSize()+1);
			updateHeightIfNeeded_Deletion(parent);
			((AVLNode) parent).setSum(((AVLNode) parent.getLeft()).getSum()+((AVLNode) parent.getRight()).getSum()+parent.getKey());

			parent = parent.getParent();
		}

		while(parent!=null) {
			parent.setSubtreeSize(parent.getLeft().getSubtreeSize()+parent.getRight().getSubtreeSize()+1);
			boolean hasHeightUpdated = updateHeightIfNeeded_Deletion(parent);
			((AVLNode) parent).setSum(((AVLNode) parent.getLeft()).getSum()+((AVLNode) parent.getRight()).getSum()+parent.getKey());

			int parentBF = BF(parent);

			if(parentBF<2 && parentBF>-2) {//|BF|<2
				if(hasHeightUpdated) { //parent height has changed
					parent = parent.getParent();
				}
				else { //parent height is the same
					//No rotation were made;
					break;
				}
			}
			else { //|BF|=2
				if(parentBF==2) {
					if(BF(parent.getLeft())==1 || BF(parent.getLeft())==0) { //LL
						rotateRight(parent);
						numOfRot += 1;
					}
					else if(BF(parent.getLeft())==-1) { //LR
						rotateLeft(parent.getLeft());
						rotateRight(parent);
						numOfRot += 2;
					}
				}
				else if(parentBF==-2) {
					if(BF(parent.getRight())==-1  || (BF(parent.getRight())==0)) { //RR
						rotateLeft(parent);
						numOfRot += 1;
					}
					else if(BF(parent.getRight())==1) { //RL
						rotateRight(parent.getRight());
						rotateLeft(parent);
						numOfRot += 2;
					}
				}

				if(parent.getParent()!=null)
					parent = parent.getParent().getParent();
				else
					break;
			}
		}

		/* If no rotation was needed then update the children of the parent of originally deleted node */
		if(firstParent!=null && numOfRot==0) {
			if(firstParent.getLeft().isRealNode()) {
				firstParent.getLeft().setSubtreeSize(firstParent.getLeft().getLeft().getSubtreeSize()+firstParent.getLeft().getRight().getSubtreeSize()+1);
				updateHeightIfNeeded_Deletion(firstParent.getLeft());
				((AVLNode) firstParent.getLeft()).setSum(((AVLNode) firstParent.getLeft().getLeft()).getSum()+((AVLNode) firstParent.getLeft().getRight()).getSum()+firstParent.getLeft().getKey());
			}
			if(firstParent.getRight().isRealNode()) {
				firstParent.getRight().setSubtreeSize(firstParent.getRight().getLeft().getSubtreeSize()+firstParent.getRight().getRight().getSubtreeSize()+1);
				updateHeightIfNeeded_Deletion(firstParent.getRight());
				((AVLNode) firstParent.getRight()).setSum(((AVLNode) firstParent.getRight().getLeft()).getSum()+((AVLNode) firstParent.getRight().getRight()).getSum()+firstParent.getRight().getKey());
			}
		}

		/* Update the rest of the tree path(to root) size, height and sum */
		while(parent!=null) {
			parent.setSubtreeSize(parent.getLeft().getSubtreeSize()+parent.getRight().getSubtreeSize()+1);
			updateHeightIfNeeded_Deletion(parent);
			((AVLNode) parent).setSum(((AVLNode) parent.getLeft()).getSum()+((AVLNode) parent.getRight()).getSum()+parent.getKey());

			parent = parent.getParent();
		}

		return numOfRot;
	}
	/*/*************************************************/


	/*/*************************************************
	 * 				Methods Used By insert 			   *
	 ***************************************************/
	/**
	 * 
	 * @param root - The root of the subtree.
	 * @param newNode - The new node to be inserted to the tree.
	 * @return True - inserted successfully. False - error.
	 * 
	 * Complexity: O(h)=O(log(n))
	 */
	private static boolean treeInsert(IAVLNode root, IAVLNode newNode) {
		IAVLNode y = treePosition(root, newNode.getKey());

		if(newNode.getKey()==y.getKey())  //if y.key==newNode.key then the key is already in the tree -> "error"
			return false;

		newNode.setParent(y);
		if(newNode.getKey()<y.getKey())
			y.setLeft(newNode);
		else
			y.setRight(newNode);

		return true;
	}

	/**
	 * @return True if the height of Parent has changed, False otherwise.
	 * 
	 * precondition: node is in path of the new node inserted.
	 * precondition: ( Parent.getLeft().getKey() == node.getKey() ) || ( Parent.getRight().getKey() == node.getKey() ).
	 * 
	 * Complexity: O(1)
	 */
	private static boolean updateHeightIfNeeded(IAVLNode node, IAVLNode Parent) {

		if(Parent.getHeight()>node.getHeight()) //No need to update, other child is higher or equal.
			return false;
		if(Parent.getHeight()==node.getHeight()) { //Update parent height
			Parent.setHeight(node.getHeight()+1);
			return true;
		}
		return false;
	}
	/*/*************************************************/


	/*/*************************************************
	 * 				Methods Used By delete 			   *
	 ***************************************************/
	/**
	 * @param node - The node to be deleted
	 * 
	 * precondition: node is a leaf.
	 * precondition: node has a parent.
	 * 
	 * Deletes node from the tree
	 * 
	 * Complexity: O(1)
	 */
	private void justDelete(IAVLNode node) {
		IAVLNode parent = node.getParent();
		if(parent.getLeft().getKey() == node.getKey())
			node.getParent().setLeft(new AVLNode());
		else if(parent.getRight().getKey() == node.getKey())
			node.getParent().setRight(new AVLNode());
		else
			throw new RuntimeException("Not the node's parent. key:" + node.getKey());

	}

	/**
	 * @param node - The node to be deleted
	 * 
	 * precondition: node has one child.
	 * precondition: node has a parent.
	 * 
	 * Deletes node from the tree.
	 * 
	 * Complexity: O(1)
	 */
	private void bypassDelete(IAVLNode node) {
		IAVLNode parent = node.getParent();
		if(parent == null) { //Delete root with one children
			if(node.getLeft().getKey()!=-1)
				rootNode = node.getLeft();
			else 
				rootNode = node.getRight();
			rootNode.setParent(null);
		}
		else if(parent.getLeft().getKey() == node.getKey()) {
			if(node.getLeft().isRealNode() && !node.getRight().isRealNode()) {
				parent.setLeft(node.getLeft());
				node.getLeft().setParent(parent);
			}
			else if(!node.getLeft().isRealNode() && node.getRight().isRealNode()){
				parent.setLeft(node.getRight());
				node.getRight().setParent(parent);
			}
		}
		else if(parent.getRight().getKey() == node.getKey()) {
			if(node.getLeft().isRealNode() && !node.getRight().isRealNode()) {
				parent.setRight(node.getLeft());
				node.getLeft().setParent(parent);
			}
			else if(!node.getLeft().isRealNode() && node.getRight().isRealNode()){
				parent.setRight(node.getRight());
				node.getRight().setParent(parent);
			}
		}
		else
			throw new RuntimeException("Not the node's parent. key:" + node.getKey());

	}

	/**
	 * @param node - the node to be deleted
	 * @return the parent of the deleted node (the one that was actually deleted)
	 * 
	 * precondition: node does not have two children then it must have a parent.
	 * 
	 * Deletes node from the tree.
	 * 
	 * Complexity: O(h)=O(log(n))
	 */
	private IAVLNode bstDelete(IAVLNode node) {
		if(isLeaf(node)) {
			justDelete(node);
			return node.getParent();
		}
		else if(isOneChild(node)) {
			bypassDelete(node);
			return node.getParent();
		}
		else { //Has two children
			IAVLNode suc =  successor(node);  //successor of node
			IAVLNode sucParent;
			if(node.getRight().getKey()==suc.getKey())
				sucParent = suc;
			else
				sucParent = suc.getParent();

			if(sucParent!=null && sucParent.getKey()==node.getKey())
				sucParent = sucParent.getParent();
			/*
			 * suc is from the subtree of the right children of node (node has two child).
			 * Therefore suc has no left children.
			 * Remove suc and Replace node with suc;
			 */
			if(isLeaf(suc))
				justDelete(suc);
			else if(isOneChild(suc))
				bypassDelete(suc);

			if(node.getParent()==null) {
				rootNode = suc;
				rootNode.setParent(null);
			}
			else {
				IAVLNode parent = node.getParent();
				if(parent.getLeft().getKey() == node.getKey()) //node is left child
					parent.setLeft(suc);
				else if(parent.getRight().getKey() == node.getKey())
					parent.setRight(suc);
				suc.setParent(parent);
			}


			suc.setLeft(node.getLeft());
			suc.setRight(node.getRight());
			suc.getLeft().setParent(suc);
			suc.getRight().setParent(suc);


			return sucParent;
		}
	}

	/**
	 * @param parent - A node on the path from the node actually deleted.
	 * @return True if the height of parent has changed, False otherwise.
	 * 
	 * Complexity: O(1)
	 */
	private static boolean updateHeightIfNeeded_Deletion(IAVLNode parent) {

		if(parent.getHeight()==parent.getLeft().getHeight()+1 && parent.getHeight()==parent.getRight().getHeight()+1) //Equal heights, No need to update.
			return false;
		else if(parent.getLeft().getHeight()>parent.getRight().getHeight() && parent.getHeight()==parent.getLeft().getHeight()+1) { //No need to update.
			return false;
		}
		else if(parent.getRight().getHeight()>parent.getLeft().getHeight() && parent.getHeight()==parent.getRight().getHeight()+1) { //No need to update.
			return false;
		}
		else {
			parent.setHeight(1+Math.max(parent.getLeft().getHeight(), parent.getRight().getHeight()));
			return true;
		}
	}
	/*/*************************************************/


	/*/*************************************************
	 * 					General Methods 			   *
	 ***************************************************/

	/**
	 * 
	 * @param node - Is the root node of the sub tree.
	 * @param k - the key which his position is wanted.
	 * 
	 * precondition: node.isRealNode() == true
	 * 
	 * Complexity: O(h)=O(log(n))
	 */
	private static IAVLNode treePosition(IAVLNode node, int k) {
		IAVLNode y = null; //Supposed to be replaced by node anyway.
		while(node.isRealNode()) {
			y = node;
			if(k == node.getKey())
				return node;
			else if(k < node.getKey())
				node = node.getLeft();
			else
				node = node.getRight();
		}
		return y;
	}

	/**
	 * Balance Factor
	 * @return the node's balance factor
	 * 
	 * Complexity: O(1)
	 */
	private static int BF(IAVLNode node) {
		return node.getLeft().getHeight() - node.getRight().getHeight();
	}

	/**
	 * @return Whether the node is a leaf or not
	 */
	private static boolean isLeaf(IAVLNode node) {
		return ( !node.getLeft().isRealNode() && !node.getRight().isRealNode() );
	}

	/**
	 * @return Whether the node has one child or not
	 */
	private static boolean isOneChild(IAVLNode node) {
		return ( node.getLeft().isRealNode() && !node.getRight().isRealNode() ) || ( !node.getLeft().isRealNode() && node.getRight().isRealNode() );
	}

	/**
	 * @param node - The root of the sub tree.
	 * @return The node minimum node in the sub tree.
	 */
	private static IAVLNode minInSubtree(IAVLNode node) {
		while(node.getLeft().isRealNode()) {
			node = node.getLeft();
		}
		return node;
	}

	/**
	 * @param node - A node in the tree.
	 * @return The successor of node. Returns null if node is the maximum node.
	 */
	private static IAVLNode successor(IAVLNode node) {
		if(node.getRight().isRealNode()) {
			return minInSubtree(node.getRight());
		}

		IAVLNode parent = node.getParent();
		while(parent!=null && node.getKey() == parent.getRight().getKey()) {
			node = parent;
			parent = node.getParent();
		}
		return parent;
	}

	/**
	 * @param node - The root of the sub tree.
	 * @return The node maximum node in the sub tree.
	 */
	private static IAVLNode maxInSubtree(IAVLNode node) {
		while(node.getRight().isRealNode()) {
			node = node.getRight();
		}
		return node;
	}

	/**
	 * @param node - A node in the tree.
	 * @return The predecessor of node. Returns null if node is the minimum node.
	 */
	private static IAVLNode predecessor(IAVLNode node) {
		if(node.getLeft().isRealNode()) {
			return maxInSubtree(node.getLeft());
		}

		IAVLNode parent = node.getParent();
		while(parent!=null && node.getKey() == parent.getLeft().getKey()) {
			node = parent;
			parent = node.getParent();
		}
		return parent;
	}
	/*/*************************************************/


	/*/*************************************************
	 * 			Method enveloped by keyToArray 		   *
	 ***************************************************/
	/**
	 * @param root - A subtree root.
	 * 
	 * precondition: keysArr and keysArrIndex are initialized.
	 * 
	 * When inOrder is finished the array keysArr will contain a sorted list of the keys that are in the tree.
	 */
	private void inOrderKeys(IAVLNode root) {
		if (root.isRealNode() == false) {return;}
		inOrderKeys(root.getLeft());
		keysArr[keysArrIndex] = root.getKey();
		keysArrIndex++;
		inOrderKeys(root.getRight());
	}
	/*/*************************************************/
	/*/*************************************************
	 * 			Method enveloped by keyToArray 		   *
	 ***************************************************/
	/**
	 * @param root - A subtree root.
	 * 
	 * precondition: keysArr and keysArrIndex are initialized.
	 * 
	 * When inOrder is finished the array keysArr will contain a sorted list of the keys that are in the tree.
	 */
	private void inOrderInfo(IAVLNode root) {
		if (root.isRealNode() == false) {return;}
		inOrderInfo(root.getLeft());
		infoArr[infoArrIndex] = root.getValue();
		infoArrIndex++;
		inOrderInfo(root.getRight());
	}
	/*/*************************************************/

	/*/*************************************************
	 * 				Method enveloped by select 		   *
	 ***************************************************/
	/**
	 * 
	 * @param root
	 * @param i
	 * @return
	 */
	private String treeSelect(IAVLNode root, int i) {
		int rootI = (root.getLeft().getSubtreeSize()) + 1;	
		if (i==rootI) {return root.getValue();}
		else if (i < rootI) {
			return treeSelect(root.getLeft(), i);
		}
		else {
			return treeSelect(root.getRight(), (i-rootI));
		}
	}
	/*/*************************************************/


	/*/*************************************************
	 * 				Method Used By less 			   *
	 ***************************************************/
	/**
	 * @param i - A number between 1 and size()
	 * @return the sum of all keys which are less or equal to i.
	 */
	private int treeLess(int i) {
		IAVLNode node = this.getRoot(); //Start from the root.
		int lessSum = 0; //Initialize the sum of keys which are less or equal to i.

		/*
		 * Start a the top and get down towards the supposed location of i.
		 * Sum the node with keys which are less or equal to i along the way.
		 */
		while(node.isRealNode()) { //Stop at the bottom
			/* If the node's key is larger than i continue left. Otherwise, continue right and sum the smaller/equal keys. */
			if(node.getKey()>i) {
				node = node.getLeft();
			}
			else {
				lessSum += node.getLeft().getSum() + node.getKey();
				node = node.getRight();
			}
		}

		return lessSum;
	}
	/*/*************************************************/

	/**
	 * public interface IAVLNode
	 * ! Do not delete or modify this - otherwise all tests will fail !
	 */
	public interface IAVLNode{	
		public int getKey(); //returns node's key (for virtual node return -1)
		public String getValue(); //returns node's value [info] (for virtual node return null)
		public void setLeft(IAVLNode node); //sets left child
		public IAVLNode getLeft(); //returns left child (if there is no left child return null)
		public void setRight(IAVLNode node); //sets right child
		public IAVLNode getRight(); //returns right child (if there is no right child return null)
		public void setParent(IAVLNode node); //sets parent
		public IAVLNode getParent(); //returns the parent (if there is no parent return null)
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node
		public void setSubtreeSize(int size); // sets the number of real nodes in this node's subtree
		public int getSubtreeSize(); // Returns the number of real nodes in this node's subtree (Should be implemented in O(1))
		public void setHeight(int height); // sets the height of the node
		public int getHeight(); // Returns the height of the node (-1 for virtual nodes)
		public int getSum(); // Returns the sum of all node's keys in the node's subtree.
	}

	/**
	 * public class AVLNode
	 *
	 * If you wish to implement classes other than AVLTree
	 * (for example AVLNode), do it in this file, not in 
	 * another file.
	 * This class can and must be modified.
	 * (It must implement IAVLNode)
	 */
	public class AVLNode implements IAVLNode{
		private int key = -1; // Contains the node's key.
		private String info = null; // Contains the node's info.

		private IAVLNode parentNode = null; // Contains the node's parent node.
		private IAVLNode leftNode = null; // Contains the node's left node.
		private IAVLNode rightNode = null; // Contains the node's right node.

		private int size = 0; // Contains the number of nodes in the node's subtree.
		private int height = -1; // Contains the height of the node in the AVLTree.
		private int sum = 0; // Contains the sum of all node's keys in the node's subtree.

		//Use only if creating a virtual node.(default)
		public AVLNode() {

		}
		//Use only if creating a leaf. (real node)
		public AVLNode(int k, String i, IAVLNode parent) {
			this.key = k;
			this.info = i;

			this.parentNode = parent;
			this.leftNode = new AVLNode(); //new virtual node
			this.rightNode = new AVLNode();//new virtual node

			this.height = 0;
			this.size = 1;
			this.sum = k;
		}
		/**
		 * @return the node key.
		 */
		public int getKey()
		{
			return this.key;
		}
		/**
		 * @return the node value.
		 */
		public String getValue()
		{
			return this.info;
		}
		/**
		 * @param node the reference of the node to be set as the left node.
		 */
		public void setLeft(IAVLNode node)
		{
			this.leftNode = node;
		}
		/**
		 * @return the left node reference.
		 */
		public IAVLNode getLeft()
		{
			return this.leftNode;
		}
		/**
		 * @param node the reference of the node to be set as the right node.
		 */
		public void setRight(IAVLNode node)
		{
			this.rightNode = node;
		}
		/**
		 * @return the right node reference.
		 */
		public IAVLNode getRight()
		{
			return this.rightNode;
		}
		/**
		 * @param node the reference of the node to be set as the parent node.
		 */
		public void setParent(IAVLNode node)
		{
			this.parentNode = node;
		}
		/**
		 * @return the parent node reference.
		 */
		public IAVLNode getParent()
		{
			return this.parentNode;
		}
		/**
		 *  @return True if this is a non-virtual AVL node, Flase otherwise.
		 */
		public boolean isRealNode()
		{
			if(this.key == -1)
				return false;
			else
				return true;
		}
		/**
		 * @param size is the value to be set as the node's subtree size.
		 */
		public void setSubtreeSize(int size)
		{
			this.size = size;
		}
		/**
		 * @return the nodes's subtree size.
		 */
		public int getSubtreeSize()
		{
			return this.size;
		}
		/**
		 * @param height is the value to be set as the node's height.
		 */
		public void setHeight(int height)
		{
			this.height = height;
		}
		/**
		 * @return the nodes's height.
		 */
		public int getHeight()
		{
			return height;
		}
		/**
		 * @param sum is the value to be set as the node's subtree sum.
		 */
		public void setSum(int sum) {
			this.sum = sum;
		}
		/**
		 * @return the nodes's subtree sum.
		 */
		public int getSum() {
			return this.sum;
		}
	}

}


