import java.util.concurrent.atomic.AtomicInteger;

public monitor class PQueue {
	
	private int MAXSIZE;
	private AtomicInteger currentSize;
	Node head;
		
	public PQueue(int m){
		MAXSIZE = m;
		currentSize.set(0);
	}
	
	public int insert(String name, int priority){
		waituntil(currentSize.get() < MAXSIZE);
		int index = 0;
		Node newNode = new Node(name, priority);
		Node n1 = head;
		while(n1.checkNext()){
			Node n2 = n1.getNext();
			if(n2.checkText(name)){
				return -1;
			}			
			if(n2.checkPriority() < priority){
				n1.setNext(newNode);
				newNode.setNext(n2);
				currentSize.incrementAndGet();
				return index;
			}
			n1 = n1.getNext();
			index++;
		}
		n1.setNext(newNode);
		currentSize.incrementAndGet();
		return index;
	}
	
	public int search(String name){
		int index = 0;
		Node n = head;
		while(n.checkNext()){
			n = n.getNext();
			if(n.checkText(name)){
				return index;
			}
			index++;
		}
		return -1;
	}
	
	public String getFirst(){
		waituntil(currentSize.get() > 0);
		return head.next.returnName();
	}

	private class Node{
		private String name;
		private int priority;
		private Node next;
		
		Node(String name, int priority){
			this.name = name;
			this.priority = priority;
			next = null;
		}
		
		void setNext(Node n){
			next = n;		
		}
		
		boolean checkNext(){
			if(next != null) return true;
			else return false;
		}
		
		Node getNext(){
			return next;
		}
		
		boolean checkText(String s){
			if(s.equals(name)) return true;
			else return false;
		}
		
		String returnName(){
			return name;
		}
		
		int checkPriority(){
			return priority;
		}
	}
}
