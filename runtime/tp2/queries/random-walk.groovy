#META;

decayFactor = 0.2;

p = g.V;

id_file_path = System.getenv("METAPATH");
println("get sampled id from ${id_file_path}");

allIds = get_ids_from_files(id_file_path);
rand = new Random();

for (int i = 0; i < 100; i++) {
  t = System.nanoTime();
  // cur = p.next();
  // vid1 = cur.id;
  vid1 = allIds[rand.nextInt() % allIds.size()];
  step = 0;
  // start random walk
  outIds = []
  g.v(vid1).out().id().sideEffect{outIds.add(it)}.iterate();
  // println(outIds);
  // println("vertex " + vid1 + " has " + outIds.size() + " outE");
  startOutIds = outIds;
  while (outIds.size() > 0) {
    def random_out = Math.random();
    if (random_out <= decayFactor) {
      break;
    }
    step += 1;
    rand = new Random();
    idx = (rand.nextInt() % outIds.size()) as Integer;
    next_vid = outIds[idx];
    outIds = []
    g.v(vid1).out().id().sideEffect{outIds.add(it)}.iterate();
    if (outIds.size() == 0) {
      outIds = startOutIds;
    }
  }
  exec_time = System.nanoTime() - t;
  println("Random Walk step: " + step + " finished in " + exec_time + " ns");
}