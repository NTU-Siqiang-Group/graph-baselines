#META;

import java.util.Random;

new_edge_label = "new-edge";
new_vertex_label = "new-vertex"

total_ops = 100000;

all_id_file_path = System.getenv("ALLIDPATH");
println("get all id from ${all_id_file_path}");

allIds = []
is_janus = false;
if (all_id_file_path.contains("janusgraph")) {
  allIds = f.get_long_ids_from_files(all_id_file_path);
  is_janus = true;
} else {
  allIds = f.get_ids_from_files(all_id_file_path);
}

rand = new Random();

// test get neighbors
for (int i = 0; i < total_ops; i++) {
  vid = allIds[rand.nextInt() % allIds.size()];
  t = System.nanoTime();
  g.V(vid).out().count().next();
  exec_time = System.nanoTime() - t;
  println("Get Vertex in " + exec_time + " ns");
}

// test add vertex
for (int i = 0; i < total_ops; i++) {
  t = System.nanoTime();
  graph.addVertex(new_vertex_label);
  if (i % 10000 == 0 || i == total_ops - 1) {
    try {
      graph.tx().commit();
    } catch (Exception e) {
      println("Not support commit");
    }
  }
  exec_time = System.nanoTime() - t;
  println("Add Vertex in " + exec_time + " ns");
}

// test add edge
edge_to_del = [];
for (int i = 0; i < total_ops; i++) {
  vid1 = allIds[rand.nextInt() % allIds.size()];
  vid2 = allIds[rand.nextInt() % allIds.size()];
  v1 = g.V(vid1).next();
  v2 = g.V(vid2).next();
  t = System.nanoTime();
  edge_to_del.add(v1.addEdge(new_edge_label, v2));
  if (i % 10000 == 0 || i == total_ops - 1) {
    try {
      graph.tx().commit();
    } catch (Exception e) {
      println("Not support commit");
    }
  }
  exec_time = System.nanoTime() - t;
  println("Edge added in " + exec_time + " ns");
}

// test delete edge
for (int i = 0; i < total_ops; i++) {
  vid = allIds[rand.nextInt() % allIds.size()];
  t = System.nanoTime();
  // edge_to_del[i].remove();
  edge_to_del[i].remove();
  if (i % 10000 == 0 || i == total_ops - 1) {
    try {
      graph.tx().commit();
    } catch (Exception e) {
      println("Not support commit");
    }
  }
  exec_time = System.nanoTime() - t;
  println("Edge deleted in " + exec_time + " ns");
}
