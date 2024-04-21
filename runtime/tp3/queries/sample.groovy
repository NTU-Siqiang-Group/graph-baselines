#META;

// sample the first 10000 vertices
p = g.V().id();
for (int i = 0; i < 10000; i++) {
  vid = p.next();
  ov = g.V(vid).out().count().next();
  println("id: ${vid}, out V: ${ov}")
}