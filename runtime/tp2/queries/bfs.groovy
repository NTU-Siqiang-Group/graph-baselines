#META;

depth = 3;

id_file_path = System.getenv("METAPATH");
println("get sampled id from ${id_file_path}");

allIds = get_ids_from_files(id_file_path);
rand = new Random();

println allIds.size();

for (int i = 0; i < 10; i++) {
  vid = allIds[rand.nextInt() % allIds.size()];
  v = g.v(vid);
	visited = [v] as Set;
  t = System.nanoTime();
  count = v.as('x').both().except(visited).store(visited).loop('x'){ it.loops <= depth}{true}.count();
  exec_time = System.nanoTime() - t;
  println("BFS start from " + vid + " finished in " + exec_time + " ns");
}
