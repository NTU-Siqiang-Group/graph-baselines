#META;
import java.util.Random;

tl = "new-edge";

p = g.V().id();

for (int i = 0; i < 100; i++) {
  if (i % 2 == 0) {
    vid = p.next();
    v1 = g.V(vid);
    t = System.nanoTime();
    cnt = v1.out().count().next();
    exec_time = System.nanoTime() - t;
    println("Vertex " + vid + " has " + cnt + " out neighbors in " + exec_time + " ns");
  } else {
    vid1 = p.next();
    vid2 = p.next();
    v1 = g.V(vid1).next();
    v2 = g.V(vid2).next();
    t = System.nanoTime();
    v1.addEdge(tl, v2);
    exec_time = System.nanoTime() - t;
    println("Edge added between " + vid1 + " and " + vid2 + " in " + exec_time + " ns");
  }
}

println("final edge size: " + g.E().count().next());
result_row = [ DATABASE, DATASET, QUERY,"0", ITERATION, "0","0"];
println result_row.join(',');