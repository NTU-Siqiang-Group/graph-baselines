#META;
all_id_file_path = System.getenv("GRAPHMAPPING");
println("get all id from ${all_id_file_path}");

allIds= [];
if (all_id_file_path.contains("janusgraph")) {
  allIds = get_long_ids_from_files(all_id_file_path);
  is_janus = true;
} else {
  allIds = get_ids_from_files(all_id_file_path);
}
println("all ids size: ${allIds.size()}");

rand = new Random();

for (int i = 0; i < 1; i++) {
  srcId = allIds[Integer.parseInt(System.getenv("SOURCE_VERTEX"))];
  dstId = allIds[Integer.parseInt(System.getenv("DST_VERTEX"))];
  dstNode = g.v(dstId);
  srcNode = g.v(srcId);
  paths = []
  visited = [] as Set
  t = System.nanoTime();
  srcNode.as('x').out().except(visited).store(visited).loop('x'){ !it.object.equals(dstNode) && it.loops <= 10 }.retain([dstNode]).path().fill(paths);
  exec_time = System.nanoTime() - t;
  shortest_length = -1;
  if (paths.size() > 0) {
    shortest_length = paths[0].size();
  }
  println("Shortest Path from ${srcId} to ${dstId} having ${shortest_length} path in ${exec_time} ns");
}