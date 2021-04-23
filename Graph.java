
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;

/* 
The Graph object is used to represent a complex network consisting of a set
of nodes, with connections between them (edges). The functions included allow
for more nodes/edges to be added to the network, as well as the retrieval of 
the degree of each node, and the average degree for each node in the network. 
Additionally, a simulate function is used to simulate the spreading of an 
epidemic using a SIS (susceptible-infected-susceptible) model. 
*/
public class Graph{
    Node[] nodes;

	// ------------------ Constructors
    public Graph(){}

    public Graph(int numNodes){
        nodes = new Node[numNodes];
        
    }//end constructor

	// ----------------- Setters

    public void addNode(int i, String label){
        nodes[i] = new Node(i, label);
    }//end method

    public void addEdge(int i, int j){
        nodes[i].addEdge(j);
    }//end method

	// ----------------- Getters

	public double avgDegree(){
		/*
		Compute the average degree of the entire graph
		*/
		double sum = 0;
		for(Node node:nodes){
			sum+=node.degree();
		}
		return sum / nodes.length;
	}//end method

    //public void clearNodeLabels(){
    //    for(int i=0;i<nodes.length;i++){
    //        nodes[i].setLabel("");
    //    }
    //}//end method

	public int[] degrees(){
		/*
		return an int array of the degree values for each node in the graph
		*/
		int[] degs = new int[nodes.length];
		for(int i=0;i<nodes.length;i++)
			degs[i] = nodes[i].degree();
		return degs;
	}//end method

	public void printAdjList(){
		System.out.println("");
		for(int i=0;i<nodes.length;i++){
			ArrayList<Integer> ns = nodes[i].getConnections();
			System.out.print(""+i+": ");
			for(Integer n:ns){
				System.out.print(" "+n+" ");
			}
			System.out.print("\n");
		}
	}//end method

    public double[] simulate(double rhoInit, double beta, double mu, int Tmax, int Ttrans){
		/*
		Function simulate: performs a single and complete simulation of the SIS model. 
		
		First, it initializes each node in the graph to either I or S, based on probability
		rhoInit. 

		Second, it performs the simulation by iterating through each of the Tmax time steps. 
		During the first Ttrans time steps, rho is not computed. The remaining Tmax-Ttrans
		time steps, rho is computed and stored in an array, which is returned. 

		mu: spontaneous recovery probability
		beta: infection probability when in contact with an infected node
		*/
        Random rand = new Random();
        double[] rho = new double[Tmax-Ttrans];
		
        //initialize each node to either S or I (with probability rhoInit)
        for(int i=0;i<nodes.length;i++){
            double r = Math.abs(rand.nextDouble());
            if(r<rhoInit)
                nodes[i].setLabel("I");
            else
                nodes[i].setLabel("S");
        }//end loop

        for(int t=0;t<Tmax;t++){
            int numInfected = 0; //track the number of infected nodes in the next time step
            String[] labels = new String[nodes.length]; //labels for each node in the next time step
            for(int i=0;i<nodes.length;i++){
                if(nodes[i].getLabel().equals("I")){ //infected node
                    double r = Math.abs(rand.nextDouble());
                    if(r<mu){
                        //spontaneous recovery
                        labels[i] = "S";
                    }
                    else{
                        numInfected++;
                        labels[i] = "I";
                    }
                }
                else{ //susceptible node
					labels[i] = "J";
					ArrayList<Integer> neighbors = nodes[i].getConnections();
					for(Integer n:neighbors){ //for each neighbour, check if the infection is contracted
						if(nodes[n].getLabel().equals("I")){ //if the neighbour is infected
							double r = Math.abs(rand.nextDouble());
							if(r < beta){ //infection probability
								numInfected++;
                        		labels[i] = "I";
								break;
							}
						}
					}
                }
            }
			//if the time step is passed the transitory stages, compute rho
			if(t >=Ttrans){ 
				rho[t-Ttrans] = ((double)numInfected)/nodes.length;
			}
			//set the labels of the graph for the next time step
			for(int i=0;i<nodes.length;i++){
				nodes[i].setLabel(labels[i]);
			}
        } //end time step
		return rho;
    }//end method

    public int size(){
        return nodes.length;
    }//end method
}//end class


class Node{
    int index;
    String label;
    ArrayList<Integer> connections = new ArrayList<Integer>();

    public Node(int i, String label){
        this.index = i;
        this.label = label;
    }//end constructor

    public void addEdge(int i){
        connections.add(i);
    }//end method

    public ArrayList<Integer> getConnections(){
        return connections;
    }//end method

    public String getLabel(){
        return this.label;
    }

    public int degree(){
        return connections.size();
    }//end method

    public void setLabel(String label){
        this.label = label;
    }
}//end class