#META;
import java.util.Random;

tl = "new-edge";

all_id_file_path = System.getenv("ALLIDPATH");
println("get all id from ${all_id_file_path}");

allIds = []
if (all_id_file_path.contains("janusgraph")) {
  allIds = f.get_long_ids_from_files(all_id_file_path);
} else {
  allIds = f.get_ids_from_files(all_id_file_path);
}

rand = new Random();

rops = System.getenv("rops") as Integer;
wops = System.getenv("wops") as Integer;
op_arr = [];

println("rops: ${rops}, wops: ${wops}");

for (int i = 0; i < rops; i++) {
  op_arr.add(1);
}

onehop = (rops * 0.75) as Integer;
twohop = rops - onehop;

for (int i = 0; i < rops; i++) {
  if (i <= onehop) {
    op_arr.add(1);
  } else {
    op_arr.add(2);
  }
}

for (int i = 0; i < wops; i++) {
  op_arr.add(0);
}

Collections.shuffle(op_arr);

for (int i = 0; i < op_arr.size(); i++) {
  if (op_arr[i] > 0) {
    vid = allIds[rand.nextInt() % allIds.size()];
    v1 = g.V(vid);
    t = System.nanoTime();
    if (op_arr[i] == 1) {
      cnt = v1.out().count().next();
    } else {
      cnt = v1.out().count().next();
    }
    exec_time = System.nanoTime() - t;
    println("Vertex " + vid + " has " + cnt + " out neighbors (${op_arr[i]}) in " + exec_time + " ns");
  } else {
    vid1 = allIds[rand.nextInt() % allIds.size()];
    vid2 = allIds[rand.nextInt() % allIds.size()];
    v1 = g.V(vid1).next();
    v2 = g.V(vid2).next();
    t = System.nanoTime();
    v1.addEdge(tl, v2);
    exec_time = System.nanoTime() - t;
    println("Edge added between " + vid1 + " and " + vid2 + " in " + exec_time + " ns");
  }
}
