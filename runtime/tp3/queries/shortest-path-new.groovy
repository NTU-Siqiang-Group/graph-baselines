#META;

p = g.V().id();

id_file_path = System.getenv("METAPATH");
println("get sampled id from ${id_file_path}");

allIds = f.get_ids_from_files(id_file_path);
// println(allIds);
rand = new Random();

for (int i = 0; i < 10; i++) {
  srcId = allIds[rand.nextInt() % allIds.size()];
  dstId = allIds[rand.nextInt() % allIds.size()];
  srcV = g.V(srcId);
  t = System.nanoTime();
  l = srcV.repeat(both().where(without("x")).aggregate("x")).until(hasId(dstId)).limit(1).path().count(local);
  if (l.hasNext()) {
    x = l.next();
  } else {
    x = -1;
  }
  exec_time = System.nanoTime() - t;
  println("Shortest Path from ${srcId} to ${dstId} having ${x} path in ${exec_time} ns");
}