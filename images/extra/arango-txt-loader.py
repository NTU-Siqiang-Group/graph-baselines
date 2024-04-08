import argparse

parser = argparse.ArgumentParser(description='txt loader')
parser.add_argument("-p", "--path", default="")
parser.add_argument("-v", "--vertex", default=200)
parser.add_argument("-u", "--undirected", default=False)
args = parser.parse_args()

output_vertex = 'nodes.json'
output_edge = 'edges.json'


with open(output_vertex, 'w+') as f:
  for i in range(int(args.vertex)):
    v_str = "{" + f'"_id":"V/{i}","_type":"vertex","_key":"{i}"' + "}\n"
    f.write(v_str)

eid = 0

with open(output_edge, 'w+') as f:
  with open(args.path, 'r') as f1:
    line = f1.readline()
    while line:
      v1, v2 = line.split()
      v1 = int(v1)
      v2 = int(v2)
      e_str = "{" + f'"_type":"edge","_from":"V/{v1}","_to":"V/{v2}","_id":"E/{eid}","_key":"{eid}"' + "}\n"
      f.write(e_str)
      eid += 1
      if args.undirected:
        e_str = "{" + f'"_type":"edge","_from":"V/{v2}","_to":"V/{v1}","_id":"E/{eid}","_key":"{eid}"' + "}\n"
        f.write(e_str)
        eid += 1
      line = f1.readline()