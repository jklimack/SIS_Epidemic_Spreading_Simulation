/*
Complex Networks: Assignment 4
Jason Klimack
May 21, 2020

This work is aimed at simulating the spread of an epidemic through
the use of a Monte Carlo simulation using a SIS (susceptible-infected-
susceptible) model. The goal of the work is to determine the effect that 
different parameters have on the ratio of infected nodes in the network during 
a series of stationary time steps. 
*/

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;


public class CN_A4{

    public static void main(String[] args){

        //networks
        String path = "networks/";
        //Erdos-Renyi networks of different degrees
        String[] ERNets = {"ER(N500,P0.01).net", "ER(N500,P0.1).net", "ER(N500,P0.8).net"};
        //Erdos-Renyi networks of different sizes
        String[] sizeNets = {ERNets[0], "ER(N1000,P0.01).net", "ER(N10000,P0.01).net"};
        // 1 scale-free network, 1 Erdos-Renyi network, and 1 real network
        String[] networks = {"SFType.net", "ERType.net", "airports_UW2.net"};

        //standard parameters
        double rhoInit = 0.2; //initial probability of each node set to infected
        int Nrep = 50; //number of repetitions of the simulation for each trial
        int Tmax = 1000; //number of time steps
        int Ttrans = 900; //number of transitory time steps
        int numBeta = 51; //number of different beta values to test
        double deltaBeta = Math.round(100.0/(double)numBeta) / 100.0;
        double[] beta = new double[numBeta]; //list of different beta values to test
        for(int i=0;i<numBeta;i++){
            beta[i] = Math.floor(i*deltaBeta*100.0)/100.0;
        }
        double[] muList = {0.1, 0.5, 0.9}; //list of different mu  values to test

        //variable declarations
        String title; //used for creating output files
        String network; //the network in use for the current simulation
        Graph G; //the current graph
        double[][] testList; //array of rho values: 1 col for each beta, 1 row for each trial in the experiment
        String[] labels; //labels of the trials in the current experiment
        double mu; //current value of mu for the experiment

        // Rest of 'main' is for computing the 4 experiments, divided into sets

        //======================== recovery rate comparison
        System.out.println("\nRR COMPARISON");
        network = ERNets[0]; 
        G = readPajek(path+network); 
        testList = new double[muList.length][numBeta]; 
        labels = new String[muList.length];

        //loop through each value being tested
        for(int j=0;j<muList.length;j++){
            mu = muList[j];
            System.out.println(".................. RR test: mu="+mu);
            labels[j] = "mu="+mu;

            //repeat for each value of beta
            for(int i=0;i<numBeta;i++){
                System.out.println("Computing rho("+beta[i]+")");
                testList[j][i] = simulate(G, rhoInit, beta[i], mu, Tmax, Ttrans, Nrep);
            }//end for beta
        }//end for mu
        title = String.format("RR_NET(%s)", network);
        output2CSV(testList, beta, labels, title); 

        //======================== network size comparison
        System.out.println("\nSIZE COMPARISON");
        mu = muList[1]; //fix mu
        testList = new double[sizeNets.length][numBeta];
        labels = new String[sizeNets.length];

        for(int j=0;j<sizeNets.length;j++){
            network = sizeNets[j];
            G = readPajek(path+network);
            System.out.println(".................. Size test: network="+network);
            labels[j] = network;

            for(int i=0;i<numBeta;i++){
                System.out.println("Computing rho("+beta[i]+")");
                testList[j][i] = simulate(G, rhoInit, beta[i], mu, Tmax, Ttrans, Nrep);
            }//end for beta
        }//end for
        title = String.format("SIZE_MU(%f)", mu);
        output2CSV(testList, beta, labels, title); 

        //======================== network type comparison
        System.out.println("\nTYPE COMPARISON");
        mu = muList[1]; //fix mu
        testList = new double[networks.length][numBeta];
        labels = new String[networks.length];

        for(int j=0;j<networks.length;j++){
            network = networks[j];
            G = readPajek(path+network);
            System.out.println(".................. Type test: network="+network);
            labels[j] = network;

            for(int i=0;i<numBeta;i++){
                System.out.println("Computing rho("+beta[i]+")");
                testList[j][i] = simulate(G, rhoInit, beta[i], mu, Tmax, Ttrans, Nrep);
            }//end for beta
        }//end for
        title = String.format("TYPE_MU(%f)", mu);
        output2CSV(testList, beta, labels, title); 

        //======================== degree comparison
        System.out.println("\nDEGREE COMPARISON");
        mu = muList[1]; //fix mu
        testList = new double[ERNets.length][numBeta];
        labels = new String[ERNets.length];

        for(int j=0;j<ERNets.length;j++){
            network = ERNets[j];
            G = readPajek(path+network);
            System.out.println(".................. Deg test: avgDeg="+G.avgDegree());
            labels[j] = network+ " Deg="+G.avgDegree();

            for(int i=0;i<numBeta;i++){
                System.out.println("Computing rho("+beta[i]+")");
                testList[j][i] = simulate(G, rhoInit, beta[i], mu, Tmax, Ttrans, Nrep);
            }//end for beta
        }//end for
        title = String.format("DEG_MU(%f)", mu);
        output2CSV(testList, beta, labels, title);     

    }//end main


    public static void output2CSV(double[][] yVals, double[] xVals, String[] yLabels, String title){
    /*
    Function output2CSV: outputs the list of yVals to a CSV file, where each row r begins with 
    xVals[r], and then is followed by the contents of yVals[c][r] for c in length of yVals
    */
        try{
            File file = new File(title+".csv");
            for(int i=1;file.exists();i++){
                file = new File(title+"("+i+").csv");
            }
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write("xaxis");
            for(String label:yLabels){
                writer.write("," + label);
            }
            writer.write("\n");
            for(int i=0;i<xVals.length;i++){
                writer.write(""+xVals[i]);
                for(int j=0;j<yVals.length;j++){
                    double v = Math.floor(yVals[j][i]*10000.0)/10000.0;
                    writer.write("," + v);
                }
                writer.write("\n");
            }
            writer.close();
        }catch(IOException e){
            System.out.println("ERROR: Error occurred while trying to write CSV file.");
        }
    }//end method

    public static double simulate(Graph G, double rhoInit, double beta, double mu, int Tmax, int Ttrans, int Nrep){
        /*
        Compute the average value of rho for all N repetitions
        */
        double sum = 0.0;
        int count = 0;
        for(int i=0;i<Nrep;i++){
            //rhoTime is an array containing the value of rho for all time steps
            double[] rhoTime = G.simulate(rhoInit, beta, mu, Tmax, Ttrans);
            for(int t=0;t<rhoTime.length;t++){
                sum+=rhoTime[t];
                count++;
            }
        }
        return sum/count;
    }//end method

    public static Graph readPajek(String filename){
        /*
        Function readPajek: create a Graph object from a Pajek file, specified 
            by filename. 

        The pajek file must meet certain requirements:
        - empty lines are allowed
        - 'spaces' at the beginning of each line are allowed
        - double 'spaces' NOT at the beginning of the line are NOT allowed
        - list of vertices must begin with a line containing "*vertices #", 
            where # represents an integer number corresponding to the number 
            of vertices. The following # lines must contain a leading int
        - list of edges must begin with a line containing "*edges" or "*arcs"
                - edges: the graph is undirected
                - arcs: the graph is directed
        - the remaining lines must contain 2 integers, seperated by a single 
            'space'
        */
        try{
            File file = new File(filename);
            FileReader reader = new FileReader(file);
            Scanner input = new Scanner(reader);
            Graph G = new Graph();
            boolean readVertices = false; //if currently reading vertices
            boolean readEdges = false; //if currently reading edges
            boolean directed = false; //if the edges being read are directed or undirected
            while(input.hasNext()){
                String line = input.nextLine();
                if(line.length()>0){ //confirm that the line is not empty
                    while(line.charAt(0) == ' '){ //remove leading spaces
                        line = line.substring(1,line.length());
                    }
                    String[] chunks = line.split(" ");
                    if(chunks.length > 0){
                        if((chunks[0].toLowerCase()).equals("*vertices")){
                            readVertices = true;
                            readEdges = false;
                            int numVertices = Integer.parseInt(chunks[1]);
                            G = new Graph(numVertices);
                        }
                        else if((chunks[0].toLowerCase()).equals("*edges")){
                            readVertices = false;
                            readEdges = true;
                            directed = false;
                        }
                        else if ((chunks[0].toLowerCase()).equals("*arcs")){
                            readVertices = false;
                            readEdges = true;
                            directed = true;
                        }
                        else{
                            if(readVertices){
                                int i = Integer.parseInt(chunks[0]) -1;
                                String label = "";
                                G.addNode(i, label);
                            }
                            else if(readEdges){
                                int i = Integer.parseInt(chunks[0]) -1;
                                int j = Integer.parseInt(chunks[1]) -1;
                                G.addEdge(i, j);
                                if(!directed)
                                    G.addEdge(j, i);
                            }
                        }
                    }
                    else{
                        System.out.println("ERROR: Something went wrong while reading Pajek.");
                        System.exit(1);
                    }
                }
            }//end while
            return G;
        }catch(IOException e){
            System.out.println("ERROR: Could not read Pajek file.");
        }
        return null;
    }//end method
}//end class