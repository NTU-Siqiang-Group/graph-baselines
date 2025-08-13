all_id_file_path = System.getenv("GRAPHMAPPING");
println("get all id from ${all_id_file_path}");

allIds= [];
if (all_id_file_path.contains("janusgraph")) {
  allIds = f.get_long_ids_from_files(all_id_file_path);
  is_janus = true;
} else {
  allIds = f.get_ids_from_files(all_id_file_path);
}
println("all ids size: ${allIds.size()}");

rand = new Random();

depth = 5;

for (int i = 0; i < 1; i++) {
  startId = allIds[Integer.parseInt(System.getenv("BFS_SOURCE_VERTEX"))];
  v = g.V(startId);
  t = System.nanoTime();
  count = v.repeat(out()).emit().times(depth).dedup().toList();
  exec_time = System.nanoTime() - t;
  println("BFS start from " + startId + " finished in " + exec_time + " ns, count: " + count.size());
}