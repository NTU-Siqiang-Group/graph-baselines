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
  return np.mean(get_stat), np.std(get_stat), np.mean(add_stat), np.std(add_stat)

commands = {
  'get_and_add': get_stat_for_get_and_add,
}

if __name__ == "__main__":
  parser = argparse.ArgumentParser(description='process test result')
  parser.add_argument("-p", "--path", default="")
  parser.add_argument("-w", "--workload", default="get_and_add")

  args = parser.parse_args()
  print(commands[args.workload](args.path))