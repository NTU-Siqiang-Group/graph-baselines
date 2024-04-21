#META;

p = g.V;

id_file_path = System.getenv("METAPATH");
println("get sampled id from ${id_file_path}");

allIds = get_ids_from_files(id_file_path);
rand = new Random();

for (int i = 0; i < 10; i++) {
  srcId = allIds[rand.nextInt() % allIds.size()];
  dstId = allIds[rand.nextInt() % allIds.size()];
  dstNode = g.v(dstId);
  srcNode = g.v(srcId);
  paths = []
  visited = [] as Set
  t = System.nanoTime();
  srcNode.as('x').both().except(visited).store(visited).loop('x'){ !it.object.equals(dstNode) && it.loops <= 10 }.retain([dstNode]).path().fill(paths);
  exec_time = System.nanoTime() - t;
  shortest_length = -1;
  if (paths.size() > 0) {
    shortest_length = paths[0].size();
  }
  println("Shortest Path from ${srcId} to ${dstId} having ${shortest_length} path in ${exec_time} ns");
}