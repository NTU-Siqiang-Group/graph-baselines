#META;

import java.util.Random;

rand = new Random();
tl = "new-edge";

all_id_file_path = System.getenv("ALLIDPATH");
println("get all id from ${all_id_file_path}");

allIds = []
if (all_id_file_path.contains("janusgraph")) {
  allIds = get_long_ids_from_files(all_id_file_path);
} else {
  allIds = get_ids_from_files(all_id_file_path);
}

rops = System.getenv("rops") as Integer;
wops = System.getenv("wops") as Integer;
op_arr = [];

println("rops: ${rops}, wops: ${wops}");
onehop = (rops * 0.75) as Integer;
twohop = (rops * 0.2) as Integer;
threehop = rops - onehop - twohop;

for (int i = 0; i < rops; i++) {
  op_arr.add(1);
}


for (int i = 0; i < wops; i++) {
  op_arr.add(0);
}

Collections.shuffle(op_arr);

for (int i = 0; i < op_arr.size(); i++) {
  if (i > 0) {
    vid = allIds[rand.nextInt() % allIds.size()];
    v1 = g.v(vid);
    t = System.nanoTime();
    cnt = v1.out().count().next();
    exec_time = System.nanoTime() - t;
    println("Vertex " + vid + " has " + cnt + " out neighbors in " + exec_time + " ns");
  } else {
    vid1 = allIds[rand.nextInt() % allIds.size()];
    vid2 = allIds[rand.nextInt() % allIds.size()];
    v1 = g.v(vid1);
    v2 = g.v(vid2);
    t = System.nanoTime();
    v1.addEdge(tl, v2);
    exec_time = System.nanoTime() - t;
    println("Edge added between " + v1 + " and " + v2 + " in " + exec_time + " ns");
  }
}
// println("final edge size: " + g.E.count());
result_row = [ DATABASE, DATASET, QUERY,"0", ITERATION, "0", "0"];
println result_row.join(',');