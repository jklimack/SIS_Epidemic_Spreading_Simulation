
"""
Simple script used to generate a series of complex networks (graphs) using the networkx library,
and saving as pajek files.

EXECUTING THIS SCRIPT WILL OVERWRITE ANY EXISTING FILES THAT SHARE A
NAME WITH THE FILES THAT WILL BE GENERATED.

PROCEDE WITH CAUTION!

"""


import networkx as nx

def generate_networks_ER():
    p = 0.01
    for n in [500, 1000, 10000]:
        G = nx.erdos_renyi_graph(n,p)
        nx.write_pajek(G, "networks/ER(N{},P{}).net".format(n,p))
    n = 500
    for p in [0.01, 0.1, 0.8]:
        G = nx.erdos_renyi_graph(n, p)
        nx.write_pajek(G, "networks/ER(N{},P{}).net".format(n, p))

def generate_type_test_nets():
    # tidy the airports_UW file
    filename = "networks/airports_UW.net"
    G = nx.read_pajek(filename)
    print(degree(G))
    nx.write_pajek(G, "networks/airports_UW2.net")

    G = nx.scale_free_graph(3618, alpha=0.15, beta=0.75, gamma=0.1)
    print(degree(G))
    nx.write_pajek(G, "networks/SFType.net")

    G = nx.erdos_renyi_graph(3618, 0.0022)
    print(degree(G))
    nx.write_pajek(G, "networks/ERType.net")

def degree(G):
    degs = G.degree()
    degs = [d[1] for d in degs]
    return sum(degs)/len(degs)
################################################################ MAIN

# Networks
generate_networks_ER()
generate_type_test_nets()





