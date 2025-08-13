import random
import queue
graph_path = 'runtime/data/twitter-2010.json3'

adj_graph = {}

with open(graph_path, 'r') as f:
  line = f.readline()
  while line:
    words = line.split(' ')
    start, end = int(words[0]), int(words[1])
    if start not in adj_graph:
      adj_graph[start] = []
    adj_graph[start].append(end)
    line = f.readline()

out = open('graph.cluster', 'w+')

print(f"len of adj_graph: {len(adj_graph)}")
idx = 10
while len(adj_graph) > 0:
  # random start
  keys = list(adj_graph.keys())
  key = keys[random.randint(0, len(keys)-1)]
  adjs = adj_graph[key]
  adj_graph.pop(key)
  q = queue.Queue()
  depths = queue.Queue()
  for v in adjs:
    q.put(v)
    depths.put(2)
  cluster = [key]
  cluster_dep = [1]
  while not q.empty():
    v = q.get()
    dep = depths.get()
    cluster.append(v)
    cluster_dep.append(dep)
    if v in adj_graph:
      adjs = adj_graph[v]
      adj_graph.pop(v)
      for n in adjs:
        q.put(n)
        depths.put(dep + 1)
  if 6765 in cluster:
    out.write(f'cluster: {cluster}, depth: {cluster_dep}\n')
  # print(f"len of adj_graph: {len(adj_graph)}")
  