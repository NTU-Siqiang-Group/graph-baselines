import argparse

parser = argparse.ArgumentParser(description='txt loader')
parser.add_argument('--input', type=str, required=True, help='Input txt file')
parser.add_argument('--output', type=str, required=True, help='Output json file')
args = parser.parse_args()

vs = {}
cur_id = 0

edges = []

with open(args.input, 'r') as f:
  for line in f:
    tmp = line.strip().split()
    id1, id2 = int(tmp[0]), int(tmp[1])
    if id1 not in vs:
      vs[id1] = cur_id
      cur_id += 1
    if id2 not in vs:
      vs[id2] = cur_id
      cur_id += 1
    
    edges.append(f'{vs[id1]} {vs[id2]}')
  
  with open(args.output, 'w') as out_f:
    out_f.write('\n'.join(edges))