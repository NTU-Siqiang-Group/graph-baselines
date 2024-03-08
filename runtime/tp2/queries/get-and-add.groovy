#META;

import java.util.Random;

random = new Random();
tl = "new-edge";

p = g.V;
println(g);

for (int i = 0; i < 100; i++) {
  if (i % 2 == 0) {
    cur = p.next();
    v1 = g.v(cur.id);
    t = System.nanoTime();
    cnt = v1.out().count().next();
    exec_time = System.nanoTime() - t;
    println("Vertex " + cur + " has " + cnt + " out neighbors in " + exec_time + " ns");
  } else {
    v1 = p.next();
    v2 = p.next();
    v1 = g.v(v1.id);
    v2 = g.v(v2.id);
    t = System.nanoTime();
    v1.addEdge(tl, v2);
    exec_time = System.nanoTime() - t;
    println("Edge added between " + v1 + " and " + v2 + " in " + exec_time + " ns");
  }
}
println("final edge size: " + g.E.count());
result_row = [ DATABASE, DATASET, QUERY,"0", ITERATION, "0", "0"];
println result_row.join(',');