import re
with open('test_wikipedia.log', 'r') as f:
  line = f.readline()
  rops = 0
  wops = 0
  num_re = re.compile(r'\d+')
  while line:
    if 'rops=' in line:
      start = line.find('rops=')
      content = line[start:]
      rops = int(num_re.findall(content.split(', ')[0])[0])
      wops = int(num_re.findall(content.split(', ')[1])[0])
    elif 'get avg' in line:
      get_avg_content = line.split(', ')[0]
      add_avg_content = line.split(', ')[2]

      start = get_avg_content.find('get avg: ') + len('get avg: ')
      get_avg = float(get_avg_content[start:])

      start = add_avg_content.find('add avg: ') + len('add avg: ')
      add_avg = float(add_avg_content[start:])
      # print(get_avg, add_avg)
      # print(rops, wops)

      print(round((get_avg * rops + add_avg * wops) / 1000, 2))
    
    line = f.readline()