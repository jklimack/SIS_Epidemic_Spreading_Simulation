# SIS_Epidemic_Spreading_Simulation
The goal of this project was to simulate an epidemic using a SIS (Susceptible-Infected-Susceptible) model. 

An SIS epidemic spreading model requires two parameters: infection probability (beta), and recovery probability (mu). For a node to become infected, it must be neighbors with at least one other infected node. For each infected neighbor, there is a probability beta that the neighbor will pass on the infection to the current node. Alternatively, each infected node will spontaneously recover from the infection with probability mu. 

Using complex networks (graphs), each node represents an individual that can either be infected (I) or susceptible (S) at any given time. Each edge between nodes represents a connection between two individuals, which means that they are in contact at each time step. 

Iteratively, 1000 time steps are performed, where each individual may either change states (S-I, or I-S), or remain in its current state, depending on the probabilities beta and mu. 

To initialize the network a number of the nodes must be set as infected. This is done by having a 20% chance for each node to be infected at the start of the simulation. 

There were four different experiments performed with this model in order to determine the effects that different parameters have on the spreading of an epidemic. Each experiment tests how a particular parameter effects the spreading of the epidemic. The parameters tested were: 1) recovery probability, 2) number of nodes, 3) network type, and 4) network degree (average number of connections per node). 

In short, the results of the experiments were as follows: 
- lower recovery probability results in a higher number of infected nodes, while a higher probability has fewer infected nodes. 
- having a higher population of people (number of nodes) allows the epidemic to spread more easily through a network. 
- having directed edges (1-way connections) for the network significantly decreases the overall spreading of the epidemic. 
- a network with a greater number of connections between nodes is more susceptible to an epidemic than one with fewer connection. 

Python code was used in order to generate sample networks and save them in Pajek format, with the help of the NetworkX library. Java was then used to read the Pajek networks and simulate the SIS epidemic. 
