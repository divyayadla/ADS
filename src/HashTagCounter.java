/*
Divya Yadla
11.15.2016
Advanced Data Structures Project - Fall 2016
*/
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/* HashTagCounter counts the number of hashtags appearing on social media sites.
 * It retrieves the n most popular hashtags.
 * A hashtable is used to store the hashtags and pointer to fibonacci heap nodes.
 * We use a max fibonacci heap to keep track of the frequencies of the hashtags.
 * Only the operations of Fibonacci heap required to implement HashTagCounter are given.
 */
public class HashTagCounter {
	
	//This class defines a node in Fibonacci Heap
	 class FiboHeapNode{

		String element;//Hashtag value
		
		FiboHeapNode child,parent;//Parent and child nodes
		
		//left and right tell how nodes are connected in a doubly linked list
		FiboHeapNode left,right;
		
		int degree;//Number of child nodes
		int key;//Fibonacci node frequency
		boolean childCut;//It defines whether a child has been removed or not 
		
		//Constructor of the class
		FiboHeapNode(String tag,int frequency){
			this.element = tag;
			this.key = frequency;
			right = this;
			left = this;
			this.degree = 0;
			this.parent = null;
			this.child = null;
		}
		
		//Returns the value of the node
		int getKey(){
			return key;
		}
		
		//Returns the hashtag
		String getTag(){
			return element;
		}
		 
	}
	 
	//This class contains all the operations performed on the Fibonacci heap
	class FiboHeap {
			
		FiboHeapNode node = null;//Max node is initialized to null
		int n;//Number of nodes in the heap
			
		//insert operation is performed to add a new node to the heap
		FiboHeapNode insert(FiboHeapNode newNode){
			n++;
				
			//check if a heap exists or not and add the new node as max node if false
			if(node == null){
				node = newNode;
				return newNode;
			}
				
			//If a heap exists, add new node to the root level and adjust pointers
			newNode.right = node.right;
			newNode.left = node;
			node.right.left = newNode;
			node.right = newNode;
		
			//Check if the key of new node is greater than the key of max node
			//If true make the new node as the max node
			if(node.key < newNode.key)
				node = newNode;
				
			return newNode;
		}	 
		//insert
			
			
		//increaseKey operation increases the key of the given node to a certain value
		void increaseKey(FiboHeapNode p,int k){
				
			//If there is no node, return
			if (null == node) {
				return;
			}
					
			//check if the node is same as the max node
			p.key = p.key + k;
					
			if (node == p) {
				return;
			}
					
			//Check if the new node's key becomes more than the parent
			//If yes, remove it and insert at the root level and adjust pointers to its neighbors
			FiboHeapNode tParent = p.parent;
			
			if((tParent != null)&&(p.key > tParent.key)){
				cut(p,tParent);
				cascadingCut(tParent);
			} else {
				p.right.left = p.left;
				p.left.right = p.right;
				p.right = p;
				p.left = p;
				insert(p);
			}
		}
		//increaseKey
			
			
		//cascadingCut checks the childCut value and performs the necessary changes 
		void cascadingCut(FiboHeapNode tParent){
			FiboHeapNode t = tParent.parent;
			if(t != null){
				//if the node loses a child the childCut is set to true
				if(!tParent.childCut){
					tParent.childCut = true;
				}
				//Repeat this operation until we find a node with childCut value false
				else{
					cut(tParent,t);
					cascadingCut(t);
				}
			}
		}
		//cascadingCut
			
		//cut removes a node from the fibonacci heap
		void cut(FiboHeapNode c,FiboHeapNode tParent){
			
			//Remove the node and decrement the parent's degree
			c.left.right = c.right;
			c.right.left = c.left;
			tParent.degree--;
			
			//Add it to the root level
			if(tParent.child == c){
				tParent.child = c.right;
			}
				
			if(tParent.degree == 0){
				tParent.child = null;
			}
				
			//change it's left and right links
			c.left = node;
			c.right = node.right;
			node.right = c;
			c.right.left = c;
			c.parent = null;
			c.childCut = false;
		}
		//cut
			
		//removeMax removes the node with maximum value from the heap
		FiboHeapNode removeMax(){
			
			//When the heap is empty return null
			if(n == 0)
				return null;
					
			FiboHeapNode t = this.node;
			if(t != null){
				int kids = t.degree;//Stores the degree of the node
				FiboHeapNode m = t.child;
				FiboHeapNode tChild;
				t.degree = 0;
				//Add the children to the top level until all child nodes are exhausted
				while(kids != 0){
					tChild = m.right;
					
					m.left.right = m.right;
					m.right.left = m.left;
					m.parent = null;
					
					//Update the links of the nodes moved
					m.left = node;
					m.right = node.right;
					t.right.left = m;
					t.right = m;
					
					m = tChild;
					kids--;
				}
				node = t.right;
					
				//If there exists a single node in the heap 
				if(t == t.right){
					node = null;
				}
					
				//Remove max node and adjust its left and right links
				else{
					t.left.right = t.right;
					t.right.left = t.left;
					t.right = t;
					t.left = t;
					t.child = null;
						
					pairWiseCombine();
					setNewMaxPointer();
				}
			}
				
			return t;
		}
		//removeMax
			
		//setNewMaxPointer updates the pointer to the max node after removeMax is performed
		void setNewMaxPointer(){
			
			FiboHeapNode tNode = node;
			FiboHeapNode iNode = node.right;
			
			//Iterate for all the nodes at the top level
			while(iNode != node){
				//Check if the key of any node is greater than that of max node
				if(iNode.key > tNode.key){
					tNode = iNode;//if yes make that node as the max node
			 	}
				iNode = iNode.right;
			 }
			node = tNode;
		}
		//setNewMaxPointer
			
		//pairWiseCombine combines the nodes of the same degree into a single heap
		void pairWiseCombine(){
			
			//Hashtable stores the degrees of the nodes with degree as key and node as value
			Hashtable<Integer,FiboHeapNode> d = new Hashtable<Integer, FiboHeapNode>();
			FiboHeapNode nd = node;
			FiboHeapNode parent,child;
			
			do{
				FiboHeapNode currNode = node;//stores the current node 
				int currDegree = node.degree;//stores the degree of the current node
				boolean done = false;
				
				//If there is only one node
				if(currDegree < 1){
					break;
				}
				do{
					//if the hashtable contains a key with the current node's degree
					if(d.containsKey(currDegree)){
						FiboHeapNode prevNode = d.get(currDegree);
						/*Compare the key values of both nodes and make the node with greater
						key value the parent and the node with lesser key value the child*/
						if(prevNode.key >= currNode.key){
							parent = prevNode;
							child = currNode;
						}
						else{
							parent = currNode;
							child = prevNode;
						}
								
						//Link the two nodes
						link(child,parent);
						d.remove(currDegree);//remove the entry from the hashtable
						currNode = parent;
					}
					else{
						d.put(currDegree, currNode);//insert the node into the hashtable if no degree matches
						done = true;
					}
				}while(!done);//Iterate this loop until all hashtable entries are checked
				node = node.right;
			}while(nd != node);//Iterate till all the root level nodes are covered
		}
		//pairWiseCombine
			
		//link is used to update the right and left links of the two nodes 
		void link(FiboHeapNode p,FiboHeapNode q){
				
			p.left.right = p.right;
			p.right.left = p.left;
			p.parent = q;
			
			//if the node doesn't have a child, add p as child
			if(q.child == null){
				q.child = p;
				p.right = p;
				p.left = p;
			}
			//else adjust the child nodes to add p as child
			else{
				p.left = q.child;
				p.right = q.child.right;
				q.child.right = p;
				p.right.left = p;
				q.child = p;
			}
				
			q.degree++;//Increase the number of children of q
			p.childCut = false;//Set it to false since it hasn't lost any children
		}
	}
	//link

	//main function
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		//Check if command line arguments are passed or not
		if(args.length == 1){
			
			//Read input from a file
			String content;
			List<String> list = new ArrayList<String>();
			File f = new File(args[0]);//Creates a new file with the filename given as argument
			try{
				BufferedReader br = new BufferedReader(new FileReader(f));//Creates an object of BufferedReader class to read input
				while((content = br.readLine())!=null){
					list.add(content);//Adds text to list
				}
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
			String[] a = list.toArray(new String[0]);
			String[] b = new String[a.length];//String array to store hashtags
			int c[] = new int[a.length];//Integer array to store frequencies
		
			HashTagCounter x = new HashTagCounter();//Object of the HashTagCounter class
		
			//Hashtable to store the hashtag as key and pointer to node as value
			Hashtable<String, FiboHeapNode> hashTags = new Hashtable<String, FiboHeapNode>();
			FiboHeapNode nd;
			FiboHeap heap = x.new FiboHeap();//Object to access methods of FiboHeap
		
			//Writing Output to file
			BufferedWriter writer = null;
			try{
				writer = new BufferedWriter(new FileWriter("output_file.txt"));
			}catch(Exception e){
				System.out.println("Error while creating output file");
			}
        
			//Process all inputs of the array a
			for(int i = 0; i < a.length; i++){
				//Check if the string is a hashtag or not
				if(a[i].startsWith("#")){
					String[] parts = a[i].split(" ");//Split the input into hashtag and frequency
					String s = parts[0];//stores hashtag
					String tag = s.substring(1, s.length());
					int value = Integer.parseInt(parts[1]);//stores frequency
				
					//check for the existence of key in hashTag
					if(hashTags.containsKey(tag)){
						FiboHeapNode n = hashTags.get(tag);
						heap.increaseKey(n, value);//increases the key of the hashtag
					}else {
						nd = x.new FiboHeapNode(tag,value);//creates a new node in the heap
						heap.insert(nd);//inserts the node into the heap
						hashTags.put(tag, nd);
					}
				}
				//if the input is stop the program stops
				else if (a[i].startsWith("stop")||a[i].startsWith("STOP")) {
					break;
				}
				//when it encounters a query
				else{
					StringBuilder sb = new StringBuilder();
					int value = Integer.parseInt(a[i]);//stores query value
					FiboHeapNode rmNode;
					//Arraylist to store the removed nodes for re-insertion at a later point of time
					ArrayList<FiboHeapNode> arr = new ArrayList<FiboHeapNode>();
					for(int j = 0; j < value; j++){
						rmNode = heap.removeMax();//removes the max node from heap
						arr.add(rmNode);
						if(j != 0) {
							sb.append(",");
						}
						sb.append(rmNode.getTag());
//						sb.append(rmNode.key);
					}
					for(int k = 0; k < value; k++){
						heap.insert(arr.get(k));//reinserts the removed nodes into the heap
					}
					try{
						//Writing output to file
						writer.append(sb.toString());
						writer.newLine();
						writer.flush();
					}catch(Exception e){
						System.out.println("Error appending to output file");
					}
				}
			}
			try{
				writer.close();
			}catch(Exception e){
				System.out.println("Error closing");
			}
		}
		else{
			System.out.println("No file found");
		}
	}//main
}
//HashTagCounter