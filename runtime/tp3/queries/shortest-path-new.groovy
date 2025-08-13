#META;

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

for (int i = 0; i < 1; i++) {
  srcId = allIds[Integer.parseInt(System.getenv("SOURCE_VERTEX"))];
  dstId = allIds[Integer.parseInt(System.getenv("DST_VERTEX"))];
  srcV = g.V(srcId);
  t = System.nanoTime();
  l = srcV.repeat(out().where(without("x")).aggregate("x")).until(hasId(dstId)).limit(1).path().count(local);
  if (l.hasNext()) {
    x = l.next();
  } else {
    x = -1;
  }
  exec_time = System.nanoTime() - t;
  println("Shortest Path from ${srcId} to ${dstId} having ${x} path in ${exec_time} ns");
}