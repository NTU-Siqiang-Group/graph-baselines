#META;

depth = 10;

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

println allIds.size();

for (int i = 0; i < 1; i++) {
  vid = allIds[Integer.parseInt(System.getenv("BFS_SOURCE_VERTEX"))];
  v = g.v(vid);
	visited = [v] as Set;
  t = System.nanoTime();
  count = v.as('x').out().except(visited).store(visited).loop('x'){ it.loops <= depth}{true}.count();
  exec_time = System.nanoTime() - t;
  println("BFS start from " + vid + " finished in " + exec_time + " ns");
}
