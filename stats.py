import argparse
import numpy as np
import re

def get_stat_for_get_and_add(path):
  get_stat = []
  add_stat = []
  with open(path, 'r') as f:
    get_re = re.compile(r'in \d+ ns')
    num_re = re.compile(r'\d+')
    line = f.readline()
    while line:
      if 'out neighbors' in line:
        ns = num_re.findall(get_re.findall(line)[0])[0]
        get_stat.append(int(ns) / 1000)
      elif 'Edge added between' in line:
        ns = num_re.findall(get_re.findall(line)[0])[0]
        add_stat.append(int(ns) / 1000)

      line = f.readline()
  get_stat = np.array(get_stat)
  add_stat = np.array(add_stat)
  db_name = path.split('/')[-1].split('_')[0]
  return f'{db_name},{np.mean(get_stat):.2f},{np.mean(add_stat):.2f}'
  # return f'get avg: {np.mean(get_stat)}, get std: {np.std(get_stat)}, add avg: {np.mean(add_stat)}, add std: {np.std(add_stat)}'

def get_stat_normal(keyword, algm):
  def analyze_stat(path):
    with open(path, 'r') as f:
      num_re = re.compile(r'\d+')
      bfs_re = re.compile(r'in \d+ ns')
      line = f.readline()
      bfs_stat = []
      while line:
        if keyword in line:
          ns = num_re.findall(bfs_re.findall(line)[0])[0]
          bfs_stat.append(int(ns) / 1000) # convert to us
        line = f.readline()
      bfs_stat = np.array(bfs_stat)
      return f'{algm} avg: {np.mean(bfs_stat) if len(bfs_stat) > 0 else "nan"}, {algm} std: {np.std(bfs_stat) if len(bfs_stat) > 0 else "nan" }'
  return analyze_stat

def parse_info_line(line):
  words = line.split(' ')
  image = words[1]
  algm = words[3]
  dataset = words[5]
  return image, algm, dataset

def display_result(db):
  imgs = ['dbtrento/gremlin-neo4j-tp3', 'dbtrento/gremlin-orientdb', 'dbtrento/gremlin-arangodb', 'dbtrento/gremlin-pg', 'dbtrento/gremlin-janusgraph']
  for algm in db:
    print(algm)
    for dataset in db[algm]:
      print(f"  {dataset}")
      for img in imgs:
        if img not in db[algm][dataset]:
          print(f"    nan")
        else:
          print(f"    {db[algm][dataset][img][0]}")

def get_stat_log(path):
  db = {}
  with open(path, 'r') as f:
    avg_re = re.compile(r'avg: [0-9]+\.[0-9]*|nan')
    std_re = re.compile(r'std: [0-9]+\.[0-9]*|nan')
    num_re = re.compile(r'[0-9]+\.[0-9]*')
    line = f.readline()
    prev_line = ''
    while line:
      if 'avg: ' in line:
        # data line
        print(avg_re.findall(line))
        avg = num_re.findall(avg_re.findall(line)[0])
        if len(avg) == 0:
          avg = 'nan'
        else:
          avg = float(avg[0])
        std = num_re.findall(std_re.findall(line)[0])
        if len(std) == 0:
          std = 'nan'
        else:
          std = float(std[0])
        img, algm, dataset = parse_info_line(prev_line)
        if algm not in db:
          db[algm] = {}
        if dataset not in db[algm]:
          db[algm][dataset] = {}
        db[algm][dataset][img] = [avg, std]
      prev_line = line
      line = f.readline()
    
    display_result(db)
    return None

def printPath(path):
  # with open(path, 'r') as f:
  #   print(f.read())
  # do nothing
  pass

def gen_sample(path):
  # 1. extract db name from path
  db_name = path.split('/')[-1].split('_')[0]
  # 2. extract dataset
  start = path.find('_')
  ppath = path[start:]
  json_re = re.compile(r'json[2|3]')
  end = ppath.find(json_re.findall(ppath)[0]) + len('json3')
  dataset = ppath[:end]
  # 3. read and sort
  vset = {}
  with open(path, 'r') as f:
    line = f.readline()
    while line:
      if 'out V: ' in line:
        first_comma = line.find(',')
        start = len('id: ')
        id_str = line[start:first_comma]
        outv_str = 'out V: '
        start = line.find(outv_str) + len(outv_str)
        out_d = int(line[start:])
        vset[id_str] = out_d
      line = f.readline()
  vset = {k: v for k, v in sorted(vset.items(), key=lambda item: item[1])}
  # 4. gen sample meta
  sample_path = f'runtime/meta/metaid/{db_name}{dataset}_sampleid.txt'
  cnts = [0 for _ in range(11)]
  with open(sample_path, 'w+') as f:
    for key in vset:
      if vset[key] > 10 or vset[key] < 5:
        continue
      if cnts[vset[key]] >= 10:
        continue
      cnts[vset[key]] += 1
      f.write(f"{key}\n")

def get_all_id(path):
  # 1. extract db name from path
  db_name = path.split('/')[-1].split('_')[0]
  # 2. extract dataset
  start = path.find('_')
  ppath = path[start:]
  json_re = re.compile(r'json[2|3]')
  end = ppath.find(json_re.findall(ppath)[0]) + len('json3')
  dataset = ppath[:end]
  # 3. read and write
  vset = {}
  sample_path = f'runtime/meta/metaid/{db_name}{dataset}_allid.txt'
  with open(path, 'r') as f:
    with open(sample_path, 'w+') as f1:
      line = f.readline()
      while line:
        if 'vertex id: ' in line:
          s = len('vertex id: ')
          f1.write(f'{line[s:]}')
        line = f.readline()

def get_stat_for_basics(path):
  get_neighbors_times = []
  add_vertex_times = []
  add_edge_times = []
  del_edge_times = []
  with open(path, 'r') as f:
    for line in f:
      if 'Get Vertex in ' in line:
        t = int(line.split(' ')[-2])
        get_neighbors_times.append(t / 1000)
      elif 'Add Vertex in ' in line:
        t = int(line.split(' ')[-2])
        add_vertex_times.append(t / 1000)
      elif 'Edge added in ' in line:
        t = int(line.split(' ')[-2])
        add_edge_times.append(t / 1000)
      elif 'Edge deleted in ' in line:
        t = int(line.split(' ')[-2])
        del_edge_times.append(t / 1000)
  db_name = path.split('/')[-1].split('_')[0]
  return f'{db_name},{np.mean(get_neighbors_times):.2f},{np.mean(add_vertex_times):.2f},{np.mean(add_edge_times):.2f},{np.mean(del_edge_times):.2f}'

def get_property_stats(alg, key):

  def get_specific_property(path):
    times = []
    db_name = path.split('/')[-1].split('_')[0]
    with open(path, 'r') as f:
      for line in f:
        if key in line:
          times.append(int(line.split(' ')[-2])/ 1000)
    return f'{db_name},{alg},{np.mean(times):.2f}'

  return get_specific_property

properties = {
  "delete-edge-property.groovy": "delete edge property used time ",
  "delete-node-property.groovy": "delete node used time ",
  "edge-specific-property-search.groovy": "edge property search used time ",
  "insert-edge-property.groovy": "insert-edge-property used time ",
  "insert-node-property.groovy": "insert-node-property used time: ",
  "node-property-search.groovy": "node property search used time ",
  "update-edge-property.groovy": "update edge property used time ",
  "update-node-property.groovy": "update node property used time "
}

commands = {
  'get-and-add.groovy': get_stat_for_get_and_add,
  'bfs.groovy': get_stat_normal('BFS start from ', 'bfs'),
  'random-walk.groovy': get_stat_normal('Random Walk step:', 'RW'),
  'shortest-path-new.groovy': get_stat_normal('Shortest Path from ', 'SP'),
  'test_all': get_stat_log,
  'sample.groovy': printPath,
  'allids.groovy': printPath,
  'gen_sample': gen_sample,
  'get_all_ids': get_all_id,
  'ppr.groovy': get_stat_normal('PPR finished ', 'PPR'),
  'basics.groovy': get_stat_for_basics,
}

for alg in properties.keys():
  commands[alg] = get_property_stats(alg, properties[alg])
  

if __name__ == "__main__":
  parser = argparse.ArgumentParser(description='process test result')
  parser.add_argument("-p", "--path", default="")
  parser.add_argument("-w", "--workload", default="get_and_add")

  args = parser.parse_args()
  print(commands[args.workload](args.path))