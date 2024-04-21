#META;

id_file_path = System.getenv("METAPATH");
println("get sampled id from ${id_file_path}");

allIds = f.get_ids_from_files(id_file_path);
// println(allIds);
rand = new Random();

depth = 3;

for (int i = 0; i < 10; i++) {
  startId = allIds[rand.nextInt() % allIds.size()];
  v = g.V(startId);
  t = System.nanoTime();
  count = v.repeat(both().where(without("x")).aggregate("x")).times(depth).cap("x").next().size();
  exec_time = System.nanoTime() - t;
  println("BFS start from " + startId + " finished in " + exec_time + " ns");
}